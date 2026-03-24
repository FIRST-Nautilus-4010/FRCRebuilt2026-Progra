package frc.robot.subsystems.swerve;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.ChassisConstants;

/**
 * Subsistema de conducción swerve del robot.
 *
 * Gestiona los cuatro módulos swerve, los sensores de orientación (Pigeon2/NavX)
 * y convierte velocidades de chasis en comandos para los módulos individuales.
 * Proporciona funcionalidad de odometría, control field-relative y asistencia
 * de conducción automática.
 */
public class Swerve extends SubsystemBase {

    // ====================================================================
    // MÓDULOS DE RUEDA
    // ====================================================================

    /** Módulo swerve delantero izquierdo. */
    private final SwerveModule frontLeft =
            new SwerveModule(SwerveConstants.FL_PWR, SwerveConstants.FL_STR, SwerveConstants.FL_ENC);

    /** Módulo swerve delantero derecho. */
    private final SwerveModule frontRight =
            new SwerveModule(SwerveConstants.FR_PWR, SwerveConstants.FR_STR, SwerveConstants.FR_ENC);

    /** Módulo swerve trasero izquierdo. */
    private final SwerveModule backLeft =
            new SwerveModule(SwerveConstants.BL_PWR, SwerveConstants.BL_STR, SwerveConstants.BL_ENC);

    /** Módulo swerve trasero derecho. */
    private final SwerveModule backRight =
            new SwerveModule(SwerveConstants.BR_PWR, SwerveConstants.BR_STR, SwerveConstants.BR_ENC);

    // ====================================================================
    // SENSORES DE ORIENTACIÓN
    // ====================================================================

    /** NavX (sensor de orientación alternativo o backup). */
    private final AHRS gyro = new AHRS(NavXComType.kMXP_SPI);

    /** Pigeon2 (IMU principal para lectura de orientación). */
    private final Pigeon2 pigeon = new Pigeon2(SwerveConstants.PIGEON, new CANBus("cleopatra"));

    /** Flag para seleccionar la fuente primaria de orientación. */
    private boolean usePigeon = true;

    // ====================================================================
    // PUBLICADORES A NETWORKTABLES
    // ====================================================================

    /** Estados medidos actuales de los módulos (para telemetría). */
    private final StructArrayPublisher<SwerveModuleState> swervePublisher =
            NetworkTableInstance.getDefault()
                    .getStructArrayTopic("Detected module states", SwerveModuleState.struct)
                    .publish();

    /** Estados deseados de los módulos (comandos). */
    private final StructArrayPublisher<SwerveModuleState> swerveDesiredStatePublisher =
            NetworkTableInstance.getDefault()
                    .getStructArrayTopic("desiredStates", SwerveModuleState.struct)
                    .publish();

    /**
     * Crea el subsistema Swerve.
     *
     * @param usePigeon si {@code true}, usa Pigeon2 como IMU principal;
     *                  si {@code false}, usa NavX. Resetea el heading al inicializar.
     */
    public Swerve(boolean usePigeon) {
        this.usePigeon = usePigeon;
        zeroHeading();
    }

    // ====================================================================
    // CICLO PERIÓDICO
    // ====================================================================

    /**
     * Actualiza el subsistema periódicamente.
     * 
     * Publica los estados actuales de los módulos a NetworkTables y telemetría
     * básica a SmartDashboard.
     */
    @Override
    public void periodic() {
        swervePublisher.set(getSwerveModuleStates());
        SmartDashboard.putNumber("Robot Heading", getHeading());
    }

    // ====================================================================
    // ESTADOS DE MÓDULO Y CHASIS
    // ====================================================================

    /**
     * Obtiene las posiciones actuales de los cuatro módulos.
     * 
     * Utilizado para cálculos de odometría.
     *
     * @return Array de posiciones de módulo [FL, FR, BL, BR]
     */
    public SwerveModulePosition[] getSwerveModulePos() {
        return new SwerveModulePosition[] {
                frontLeft.getPosition(),
                frontRight.getPosition(),
                backLeft.getPosition(),
                backRight.getPosition()
        };
    }

    /**
     * Obtiene los estados actuales de los cuatro módulos.
     * 
     * Incluye velocidad y ángulo de orientación de cada rueda.
     *
     * @return Array de estados de módulo [FL, FR, BL, BR]
     */
    public SwerveModuleState[] getSwerveModuleStates() {
        return new SwerveModuleState[] {
                frontLeft.getState(),
                frontRight.getState(),
                backLeft.getState(),
                backRight.getState()
        };
    }

    /**
     * Obtiene la orientación actual del robot.
     *
     * @return Rotation2d con el heading actual
     */
    public Rotation2d getRotation2d() {
        return Rotation2d.fromDegrees(getHeading());
    }

    // ====================================================================
    // ACELERACIONES
    // ====================================================================

    /**
     * Obtiene la aceleración lineal en X (m/s²).
     * 
     * Lee desde el sensor configurado (Pigeon2 o NavX).
     *
     * @return Aceleración en X en el marco del robot
     */
    public double getAccelX() {
        if (usePigeon) {
            return pigeon.getAccelerationX().getValue().magnitude();
        } else {
            return gyro.getWorldLinearAccelX();
        }
    }

    /**
     * Obtiene la aceleración lineal en Y (m/s²).
     * 
     * Lee desde el sensor configurado (Pigeon2 o NavX).
     *
     * @return Aceleración en Y en el marco del robot
     */
    public double getAccelY() {
        if (usePigeon) {
            return pigeon.getAccelerationY().getValue().magnitude();
        } else {
            return gyro.getWorldLinearAccelY();
        }
    }

    /**
     * Obtiene la aceleración lineal en Z (m/s²).
     * 
     * Lee desde el sensor configurado (Pigeon2 o NavX).
     *
     * @return Aceleración en Z
     */
    public double getAccelZ() {
        if (usePigeon) {
            return pigeon.getAccelerationZ().getValue().magnitude();
        } else {
            return gyro.getWorldLinearAccelZ();
        }
    }

    /**
     * Calcula el módulo total de la aceleración lineal.
     *
     * @return Magnitud de aceleración (m/s²)
     */
    public double getLinearAcceleration() {
        double ax = getAccelX();
        double ay = getAccelY();
        double az = getAccelZ();
        return Math.sqrt(ax * ax + ay * ay + az * az);
    }

    /**
     * Calcula la velocidad promedio de las cuatro ruedas.
     *
     * @return Velocidad promedio (m/s)
     */
    public double getAverageWheelSpeed() {
        SwerveModuleState[] states = getSwerveModuleStates();
        double sum = 0.0;
        for (SwerveModuleState state : states) {
            sum += state.speedMetersPerSecond;
        }
        return sum / states.length;
    }

    /**
     * Calcula la velocidad lineal del chasis.
     * 
     * Se calcula a partir de los estados de módulos usando la cinemática swerve.
     *
     * @return Velocidad del chasis (m/s)
     */
    public ChassisSpeeds getChassisSpeed() {
        ChassisSpeeds speeds =
                ChassisConstants.KINEMATICS.toChassisSpeeds(getSwerveModuleStates());

        return speeds;
    }

    public double getChassisAngularSpeed() {
        ChassisSpeeds speeds =
                ChassisConstants.KINEMATICS.toChassisSpeeds(getSwerveModuleStates());

        return speeds.omegaRadiansPerSecond;
    }

    public double getXChassisSpeed() {
        ChassisSpeeds speeds =
                ChassisConstants.KINEMATICS.toChassisSpeeds(getSwerveModuleStates());

        return speeds.vxMetersPerSecond;
    }

    public double getYChassisSpeed() {
        ChassisSpeeds speeds =
                ChassisConstants.KINEMATICS.toChassisSpeeds(getSwerveModuleStates());

        return speeds.vyMetersPerSecond;
    }

    public double getChassisSpeedMagnitude() {
        ChassisSpeeds speeds =
                ChassisConstants.KINEMATICS.toChassisSpeeds(getSwerveModuleStates());

        return Math.hypot(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond);
    }

    // ====================================================================
    // SENSORES DE ORIENTACIÓN (GYRO)
    // ====================================================================

    /**
     * Resetea el heading (yaw) del sensor de orientación.
     * 
     * Establece 0° para alianza azul y 180° para alianza roja.
     */
    public void zeroHeading() {
        final double resetHeading = DriverStation.getAlliance().get() == DriverStation.Alliance.Red ? 180.0 : 0.0;

        if (usePigeon) {
            pigeon.setYaw(resetHeading);
        } else {
            gyro.setAngleAdjustment(resetHeading);
        }
    }
    
    /**
     * Obtiene el heading (yaw) actual del robot.
     *
     * @return Ángulo de yaw en grados
     */
    public double getHeading() {
        if (usePigeon) {
            return pigeon.getYaw().getValueAsDouble();
        } else {
            return -gyro.getAngle();
        }
    }

    /**
     * Obtiene el pitch actual del robot.
     *
     * @return Ángulo de pitch en grados
     */
    public double getPitch() {
        if (usePigeon) {
            return pigeon.getPitch().getValueAsDouble();
        } else {
            return gyro.getPitch();
        }
    }

    /**
     * Obtiene el roll actual del robot.
     *
     * @return Ángulo de roll en grados
     */
    public double getRoll() {
        if (usePigeon) {
            return pigeon.getRoll().getValueAsDouble();
        } else {
            return gyro.getRoll();
        }
    }

    /**
     * Obtiene la velocidad angular en Z (yaw).
     *
     * @return Velocidad angular en rad/s
     */
    public double getGyroRate() {
        if (usePigeon) {
            return pigeon.getAngularVelocityZWorld().getValueAsDouble();
        } else {
            return gyro.getRate();
        }
    }

    /**
     * Obtiene la velocidad angular en Y (pitch).
     *
     * @return Velocidad angular en rad/s
     */
    public double getPitchRate() {
        if (usePigeon) {
            return pigeon.getAngularVelocityYWorld().getValueAsDouble();
        } else {
            return gyro.getRawGyroY();
        }
    }

    /**
     * Obtiene la velocidad angular en X (roll).
     *
     * @return Velocidad angular en rad/s
     */
    public double getRollRate() {
        if (usePigeon) {
            return pigeon.getAngularVelocityXWorld().getValueAsDouble();
        } else {
            return gyro.getRawGyroX();
        }
    }

    // ====================================================================
    // CONTROL DE MÓDULOS Y CONDUCCIÓN
    // ====================================================================

    /**
     * Detiene todos los módulos swerve.
     * 
     * Establece velocidad y ángulo a cero en todos los módulos.
     */
    public void stopModules() {
        frontLeft.stop();
        frontRight.stop();
        backLeft.stop();
        backRight.stop();
    }

    /**
     * Conduce el robot en modo field-relative (relativo al campo).
     * 
     * Convierte velocidades del marco del campo al marco del robot usando
     * el heading actual, luego aplica los estados a los módulos.
     *
     * @param xSpeed velocidad en X del campo (m/s)
     * @param ySpeed velocidad en Y del campo (m/s)
     * @param rot    velocidad angular (rad/s)
     */
    public void driveFieldRelative(double xSpeed, double ySpeed, double rot) {
        ChassisSpeeds fieldRelativeSpeeds =
                ChassisSpeeds.fromFieldRelativeSpeeds(
                        xSpeed,
                        ySpeed,
                        rot,
                        getRotation2d());

        drive(fieldRelativeSpeeds);
    }

    /**
     * Conduce el robot en modo field-relative con asistencia de conducción.
     * 
     * Similar a {@link #driveFieldRelative(double, double, double)} pero permite
     * aplicar un vector objetivo para asistencias automáticas.
     *
     * @param xSpeed        velocidad en X del campo (m/s)
     * @param ySpeed        velocidad en Y del campo (m/s)
     * @param rot           velocidad angular (rad/s)
     * @param targetVector  array [ángulo (rad), distancia (m)] para asistencia
     */
    public void driveFieldRelative(double xSpeed, double ySpeed, double rot, double[] targetVector) {
        ChassisSpeeds fieldRelativeSpeeds =
                ChassisSpeeds.fromFieldRelativeSpeeds(
                        xSpeed,
                        ySpeed,
                        rot,
                        getRotation2d());

        drive(fieldRelativeSpeeds, targetVector);
    }

    /**
     * Conduce el robot con asistencia de conducción automática.
     * 
     * Calcula un vector de asistencia perpendicular al vector de movimiento
     * para ayudar a mantener la orientación hacia un objetivo.
     *
     * @param speeds        velocidades de chasis deseadas (marco del robot)
     * @param targetVector  array [ángulo (rad), distancia (m)] del objetivo
     */
    public void drive(ChassisSpeeds speeds, double[] targetVector) {
        double speedsAngle = Math.atan2(speeds.vyMetersPerSecond, speeds.vxMetersPerSecond);

        double angleToTarget = speedsAngle - targetVector[0];

        if (Math.abs(angleToTarget) >= Math.PI / 2) {
            // Sin objetivo válido, conducción normal
            drive(speeds);
            return;
        }

        double assistModule = Math.sin(speedsAngle - targetVector[0]) * targetVector[1];

        ChassisSpeeds asistedVector = new ChassisSpeeds(
            speeds.vyMetersPerSecond * assistModule * SwerveConstants.ASSIST_STRAFE_FACTOR,
            -speeds.vxMetersPerSecond * assistModule * SwerveConstants.ASSIST_STRAFE_FACTOR,
            speeds.omegaRadiansPerSecond
        );

        drive(asistedVector);
    }

    /**
     * Conduce el robot con velocidades en el marco del robot.
     * 
     * Convierte velocidades de chasis a estados de módulo y los aplica
     * a cada rueda. Publica telemetría a NetworkTables.
     *
     * @param speeds velocidades de chasis (vx, vy, ω)
     */
    public void drive(ChassisSpeeds speeds) {
        SwerveModuleState[] moduleStates =
                ChassisConstants.KINEMATICS.toSwerveModuleStates(speeds);
        
        setStates(moduleStates);
        swerveDesiredStatePublisher.set(moduleStates);
    }

    /**
     * Aplica estados deseados a los módulos con desaturación.
     * 
     * Asegura que ningún módulo exceda la velocidad máxima permitida y aplica
     * compensación basada en inclinación (roll y pitch) del robot.
     *
     * @param desiredStates array de estados de módulo en orden [FL, FR, BL, BR]
     */
    public void setStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(
                desiredStates,
                ChassisConstants.MAX_VELOCITY);

        double roll = getRoll();
        double pitch = getPitch();

        frontLeft.setDesiredState(desiredStates[0], roll, pitch);
        frontRight.setDesiredState(desiredStates[1], roll, pitch);
        backLeft.setDesiredState(desiredStates[2], roll, pitch);
        backRight.setDesiredState(desiredStates[3], roll, pitch);
    }
}
