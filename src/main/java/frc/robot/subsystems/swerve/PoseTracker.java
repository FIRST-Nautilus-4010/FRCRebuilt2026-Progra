package frc.robot.subsystems.swerve;

import java.util.Optional;
import java.util.function.Supplier;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.AutonomousConstants;
import frc.robot.Constants.ChassisConstants;
import frc.robot.subsystems.swerve.commands.Drive;
import frc.robot.utils.PoseConfidenceTracker;
import frc.robot.utils.CollisionDetector;
import frc.robot.utils.LimelightHelpers;
import frc.robot.utils.LimelightHelpers.PoseEstimate;

/**
 * Encapsula toda la lógica de estimación de pose del robot.
 *
 * Fuentes de información:
 * <ul>
 *   <li>Odometry de los módulos swerve + gyro</li>
 *   <li>Medidas de visión (Limelight / AprilTags)</li>
 *   <li>Detección de colisión y skid para ajustar la confianza</li>
 * </ul>
 *
 * También expone comandos de alto nivel como {@link #driveTo(Pose2d)}.
 */
public class PoseTracker {

    // --- Constantes internas ---

    /** Nombre de la cámara Limelight usada para odometría. */
    private static final String LIMELIGHT_2 = "limelight-comosea";
    private static final String LIMELIGHT_3 = "limelight-three";

    // --- Estimador de pose y publicación ---

    /** Estimador de pose basado en modelo swerve + mediciones de visión. */
    private final SwerveDrivePoseEstimator poseEstimator;

    /** Publicador de pose al NetworkTables para dashboards externos. */
    private final StructPublisher<Pose2d> posePublisher =
            NetworkTableInstance.getDefault()
                    .getStructTopic("Robot position", Pose2d.struct)
                    .publish();

    // --- Dependencias del subsistema ---

    /** Referencia al subsistema de swerve, del que se obtienen módulos y gyro. */
    private final Swerve swerve;
    

    // --- Utilidades auxiliares ---

    /** Rastrea confianza en la odometría (detección de skid, etc.). */
    private final PoseConfidenceTracker confidenceTracker = new PoseConfidenceTracker();

    /** Detecta impactos/colisiones que puedan invalidar la odometría. */
    private final CollisionDetector collisionDetector = new CollisionDetector();

    /** Indica si ya se inicializó la pose usando visión (Limelight). */
    private boolean initialPoseSetFromVision = false;


    /**
     * Crea un {@link PoseTracker} asociado a un subsistema swerve.
     *
     * Inicializa el {@link SwerveDrivePoseEstimator} usando:
     * <ul>
     *   <li>Cinemática del chasis</li>
     *   <li>Rotación inicial (0 rad)</li>
     *   <li>Posiciones iniciales de módulos</li>
     *   <li>Pose inicial definida en {@link AutonomousConstants#initialPose}</li>
     * </ul>
     *
     * @param swerve subsistema swerve del robot.
     */
    public PoseTracker() {
        this.swerve = new Swerve(true);

        this.poseEstimator = new SwerveDrivePoseEstimator(
                ChassisConstants.KINEMATICS,
                new Rotation2d(0),
                swerve.getSwerveModulePos(),
                AutonomousConstants.initialPose
        );
    }

    // --------------------------------------------------------------------
    // API PRINCIPAL
    // --------------------------------------------------------------------

    public Swerve getSwerve() {
        return swerve;
    }

    public void configureDefaultCommands(
        Supplier<Double> xInput,
        Supplier<Double> yInput,
        Supplier<Double> omegaInput,
        Supplier<Boolean> resetYaw,

        Supplier<Boolean> assistX,
        Supplier<Boolean> assistY,
        Supplier<Boolean> assistTheta,
        Supplier<Boolean> aimEnabled,

        Supplier<Pose2d> targetPose,
        Supplier<Pose2d> aimPose    
    ) {
        swerve.setDefaultCommand(
            new Drive(
                swerve,
                this,

                xInput,
                yInput,
                omegaInput,
                resetYaw,

                assistX,
                assistY,
                assistTheta,
                aimEnabled,

                targetPose,
                aimPose
            )
        
        );
    }

    /** Devuelve la pose estimada actual del robot. */
    public Pose2d getPose() {
        return poseEstimator.getEstimatedPosition();
    }

    /**
     * Resetea la odometría a una pose dada.
     *
     * @param pose nueva pose de referencia.
     */
    public void resetOdometry(Pose2d pose) {
        poseEstimator.resetPosition(
                swerve.getRotation2d(),
                swerve.getSwerveModulePos(),
                pose
        );
    }

    // --------------------------------------------------------------------
    // VISIÓN (LIMELIGHT)
    // --------------------------------------------------------------------

    private Optional<PoseEstimate> getVisionEstimate(String limelight) {

        if (!LimelightHelpers.getTV(limelight))
            return Optional.empty();

            LimelightHelpers.SetRobotOrientation(
                    limelight,
                    swerve.getHeading(),
                    swerve.getGyroRate(), swerve.getPitch(),
                    swerve.getPitchRate(), swerve.getRoll(),
                    swerve.getRollRate()
            );

        PoseEstimate estimate;

        estimate = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(limelight);
        

        
        if (estimate == null || estimate.tagCount == 0)
            return Optional.empty();

        return Optional.of(estimate);
    }

    private boolean visionGate(PoseEstimate estimate) {

        if (estimate.avgTagDist > 6.0)
            return false;

        if (Math.abs(swerve.getGyroRate()) > 720)
            return false;

        Pose2d current = getPose();
        Pose2d vision = estimate.pose;

        double dx = current.getX() - vision.getX();
        double dy = current.getY() - vision.getY();

        double error = Math.hypot(dx, dy);
        if (initialPoseSetFromVision) {
            return error < 2.0;
        }

        return true;
    }

    private void applyDynamicVisionStdDevs(PoseEstimate estimate) {

        double distanceFactor = Math.max(estimate.avgTagDist, 1.0);

        double tagFactor;
        if (estimate.tagCount >= 3) tagFactor = 0.4;
        else if (estimate.tagCount == 2) tagFactor = 0.7;
        else tagFactor = 1.2;

        double gyroFactor = 1.0 + Math.abs(swerve.getGyroRate()) / 500.0;

        double xyStd = 0.03 * distanceFactor * tagFactor * gyroFactor;
        double thetaStd = 0.05 * distanceFactor * gyroFactor;

        poseEstimator.setVisionMeasurementStdDevs(
                VecBuilder.fill(xyStd, xyStd, thetaStd)
        );
    }

    private void processVision(String limelight) {

        Optional<PoseEstimate> vision = getVisionEstimate(limelight);

        if (vision.isEmpty())
            return;

        PoseEstimate estimate = vision.get();

        if (!visionGate(estimate))
            return;

        applyDynamicVisionStdDevs(estimate);

        Pose2d pose = new Pose2d(estimate.pose.getTranslation(), swerve.getRotation2d());
        double timestamp = estimate.timestampSeconds;

        if (!initialPoseSetFromVision) {

            resetOdometry(pose);
            initialPoseSetFromVision = true;

            SmartDashboard.putString(
                    "Init Pose Source",
                    limelight
            );
        }

        if (confidenceTracker.shouldTrustVision(pose, getPose())) {

            poseEstimator.addVisionMeasurement(
                    pose,
                    timestamp
            );
        }
    }

    private void applySmartCrop(String limelight) {

        if (!LimelightHelpers.getTV(limelight)) {

            LimelightHelpers.setCropWindow(
                    limelight,
                    -1,1,-1,1
            );

            return;
        }

        double tx = LimelightHelpers.getTX(limelight);

        double center = tx / 29.8;

        double width = 0.35;

        double xmin = Math.max(-1, center - width);
        double xmax = Math.min(1, center + width);

        LimelightHelpers.setCropWindow(
                limelight,
                xmin,
                xmax,
                -1,
                1
        );
    }

    // --------------------------------------------------------------------
    // CICLO PERIÓDICO
    // --------------------------------------------------------------------

    /**
     * Debe ser llamado periódicamente (por ejemplo, desde {@code Swerve.periodic()}).
     *
     * Flujo general:
     * <ol>
     *   <li>Detectar colisiones y decidir si actualizar solo por odometría.</li>
     *   <li>Actualizar confianza (skid) y ajustar std dev de visión.</li>
     *   <li>Procesar mediciones de Limelight (inicialización + fusión).</li>
     *   <li>Publicar la pose a SmartDashboard y NetworkTables.</li>
     * </ol>
     */
    public void periodic() {
        // ======= 1. Actualización por odometría / colisión =======
        if (collisionDetector.detectImpact(swerve)) {
            // Se ha detectado una colisión fuerte: congela temporalmente odometría.
            collisionDetector.freeze();
        } else {
            // Actualiza pose usando solo gyro + módulos swerve.
            poseEstimator.update(
                    swerve.getRotation2d(),
                    swerve.getSwerveModulePos()
            );
        }

        // ======= 2. Skid detection / confianza en visión =======
        confidenceTracker.update(swerve);

        // ======= 3. Actualizaciones de visión (AprilTags / Limelight) =======

        applySmartCrop(LIMELIGHT_2);
        applySmartCrop(LIMELIGHT_3);

        processVision(LIMELIGHT_2);
        processVision(LIMELIGHT_3);

        // ======= 4. Publicación a Dashboard / NT =======
        Pose2d estimatedPose = getPose();
        SmartDashboard.putString("Robot Pose", estimatedPose.toString());
        posePublisher.set(estimatedPose);
    }
}