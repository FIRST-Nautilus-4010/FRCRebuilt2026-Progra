package frc.robot;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.ChassisConstants;
import frc.robot.subsystems.channeler.Channeler;
import frc.robot.subsystems.climber.Climber;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.swerve.PoseTracker;
import frc.robot.utils.TejuinoBoard;

/**
 * Gestor simple de subsistemas/estados del robot.
 *
 * En esta versión reducida, solo se mantiene:
 * <ul>
 *   <li>El subsistema swerve (a través de {@link PoseTracker})</li>
 *   <li>El estado {@link RobotState#TRAVEL}</li>
 * </ul>
 *
 * El objetivo es centrarse únicamente en mover el chasis.
 */
public final class SubsystemManager {

    /** Rastreador de pose del robot (odometría + visión). */
    private final PoseTracker poseTracker;
    private final Intake intake;
    private final Channeler channeler;
    private final Shooter shooter;
    private final TejuinoBoard tejuino;
    private final Climber climber;

    /** Estado actual del robot. Solo se usa TRAVEL en esta versión. */
    private RobotState robotState = RobotState.TRAVEL;

    // --- Suppliers para controles de TRAVEL ---
    private Supplier<Double> travelVxSupplier;

    // --- Suppliers para las flags de las asistencias ---
    public boolean assistX = false;
    public boolean assistY = false;
    public boolean assistTheta = false;
    public boolean aimEnabled = false;

    public Pose2d targetPose = new Pose2d(0, 0, new Rotation2d(0));
    Pose2d aimPose = new Pose2d(0, 0, new Rotation2d(0));
    
    StructPublisher<Pose2d> aimPosePublisher = 
        NetworkTableInstance.getDefault()
                    .getStructTopic("Aim Pose", Pose2d.struct)
                    .publish();

    /**
     * Crea el gestor de subsistemas usando el subsistema swerve.
     */ 
    public SubsystemManager() {
        this.poseTracker = new PoseTracker();
        this.intake = new Intake();
        this.channeler = new Channeler();
        this.shooter = new Shooter();
        this.tejuino = new TejuinoBoard();
        this.climber = new Climber();
    }

    /**
     * Configura los controles para el estado TRAVEL.
     * 
     * Debe ser llamado desde {@link RobotContainer} después de crear
     * el SubsystemManager.
     *
     * @param vx velocidad X del robot (m/s)
     * @param vy velocidad Y del robot (m/s)
     * @param omega velocidad angular del robot (rad/s)
     * @param fieldRelative si la conducción es relativa al campo
     * @param resetYaw si se debe resetear el yaw (gyro)
     */
    public void configureTravelControls(
            Supplier<Double> vx,
            Supplier<Double> vy,
            Supplier<Double> omega,
            Supplier<Boolean> resetYaw
    ) {
        this.travelVxSupplier = vx;

        poseTracker.configureDefaultCommands(
            vx, 
            vy, 
            omega, 
            resetYaw,

            () -> assistX,
            () -> assistY,
            () -> assistTheta,
            () -> aimEnabled,

            () -> targetPose,
            this::calculateAimPose
            
        );
    }

    private Pose2d calculateAimPose() {
        Pose2d pose = poseTracker.getPose();

        var alliance = DriverStation.getAlliance();

        if (alliance.get() == DriverStation.Alliance.Blue) {
            if (pose.getX() <= 4.625) {
                aimPose = new Pose2d(4.625, 4.033, new Rotation2d(180));
            } else if (pose.getY() >= 4.033) {
                aimPose = new Pose2d(4.625, 6.0495, new Rotation2d(180));
            } else{
                aimPose = new Pose2d(4.625, 2.0165, new Rotation2d(0));
            }
        } else {
            if (pose.getX() >= 16.54 - 4.625) {
                aimPose = new Pose2d(16.54 - 4.625, 4.033, new Rotation2d(180));
            } else if (pose.getY() >= 4.033) {
                aimPose = new Pose2d(16.54 - 4.625, 6.0495, new Rotation2d(180));
            } else{
                aimPose = new Pose2d(16.54 - 4.625, 2.0165, new Rotation2d(0));
            }
        }

        return aimPose;
    }


    public PoseTracker getPoseTracker() {
        return poseTracker;
    }

    /**
     * Inicializa el estado del robot.
     * 
     * Debe ser llamado después de configurar los controles.
     */
    public void initialize() {
        // Estado inicial: TRAVEL (conducción normal del chasis).
        executeState(RobotState.TRAVEL);
        tejuino.init(40);
    }

    public void disable() {
        assistX = false;
        assistY = false;
        assistTheta = false;
        aimEnabled = false;
        ChassisConstants.MAX_VELOCITY = 0;
        tejuino.all_leds_purple(1);
        tejuino.all_leds_purple(2);
    }

    /**
     * Cambia el estado actual sin lanzar comandos adicionales.
     *
     * @param state nuevo estado del robot
     */
    private void setState(RobotState state) {
        robotState = state;
    }

    /**
     * Ejecuta un cambio explícito de estado:
     * <ul>
     *   <li>Cancela todos los comandos actuales</li>
     *   <li>Programa el nuevo estado</li>
     * </ul>
     *
     * En esta versión, solo existe un comportamiento para TRAVEL.
     */
    public void executeState(RobotState state) {
        CommandScheduler.getInstance().cancelAll();
        scheduleState(state);
    }
    
    /**
     * Programa el comportamiento asociado a un estado.
     * <p>
     * Actualmente, solo se maneja {@link RobotState#TRAVEL} y el resto
     * de estados se redirigen a TRAVEL.
     */
    public void scheduleState(RobotState state) {
        setState(state);

        switch (state) {
            case TRAVEL:
                // Verifica que los controles estén configurados
                if (travelVxSupplier == null) {
                    throw new IllegalStateException(
                        "Travel controls not configured. Call configureTravelControls() first."
                    );
                }
                
                CommandScheduler.getInstance().schedule(
                    new ParallelCommandGroup(
                        new InstantCommand(() -> {
                            assistX = false;
                            assistY = false;
                            assistTheta = false;
                            aimEnabled = false;
                            ChassisConstants.MAX_VELOCITY = 3.77952;
                            tejuino.all_leds_blue(1);
                            tejuino.all_leds_blue(2);
                        }),
                        //intake.stopCommand(),
                        intake.stopCommand(),
                        channeler.stopCommand(),
                        shooter.stopCommand()//,
                        //climber.stowCommand()

                        
                    )
                );
                break;
            case INTAKE:
                CommandScheduler.getInstance().schedule(
                    new InstantCommand(() -> {
                            assistX = false;
                            assistY = false;
                            assistTheta = false;
                            aimEnabled = false;
                            ChassisConstants.MAX_VELOCITY = 3.77952 / 2;
                            tejuino.all_leds_yellow(1);
                            tejuino.all_leds_yellow(2);
                        }),
                    intake.grabCommand(),
                    //intake.testRollersCommand(),
                    channeler.stopCommand(),
                    shooter.stopCommand()//,
                    //climber.stowCommand()

                );
                break;
            case SHOOT:
                CommandScheduler.getInstance().schedule(
                    new InstantCommand(() -> {
                            assistX = false;
                            assistY = false;
                            assistTheta = true;//true
                            aimEnabled = true;//true
                            ChassisConstants.MAX_VELOCITY = 3.77952;
                            tejuino.all_leds_red(1);
                            tejuino.all_leds_red(2);
                        }),
                    intake.stopCommand(),
                    //intake.stopCommand(),
                    shooter.shootCommand(
                        () -> calculateAimPose().getTranslation().getDistance(poseTracker.getPose().getTranslation())
                    ).andThen(channeler.feedCommand())//,
                    //climber.stowCommand()

                    //poseTracker.rotateTo(Rotation2d.fromDegrees(180)),
                );
                break;
            case CLIMB:
                CommandScheduler.getInstance().schedule(
                    new InstantCommand(() -> {
                            assistX = false;
                            assistY = false;
                            assistTheta = false;
                            aimEnabled = false;
                            ChassisConstants.MAX_VELOCITY = 3.77952;
                            tejuino.all_leds_green(1);
                            tejuino.all_leds_green(2);
                        }),
                    intake.stowCommand(),
                    channeler.stopCommand(),
                    //intake.stopCommand(),
                    shooter.stopCommand()/* ,
                    new SequentialCommandGroup(
                        climber.riseCommand(),
                        climber.extendCommand(),
                        climber.pullCommand()
                    )*/
                );
                break;
            case TEST:
                CommandScheduler.getInstance().schedule(
                     intake.stowCommand()
                );
                break;
            default:
                // Todos los demás estados se redirigen a TRAVEL.
                setState(RobotState.TRAVEL);
                break;
        }
    }

    /**
     * Debe llamarse periódicamente desde {@code Robot.periodic()}.
     *
     * Actualiza la estimación de pose y publica el estado actual a
     * SmartDashboard.
     */
    public void periodic() {
        poseTracker.periodic();
        SmartDashboard.putString("Robot State", robotState.toString());

        SmartDashboard.putString("Aim Pose", calculateAimPose().toString());
        aimPosePublisher.set(aimPose);
    }
}