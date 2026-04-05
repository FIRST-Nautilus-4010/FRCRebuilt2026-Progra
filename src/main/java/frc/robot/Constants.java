package frc.robot;

// TODO Mover constantes específicas de swerve a SwerveConstants.

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

/**
 * Conjunto de constantes globales del proyecto.
 */
public final class Constants {

  private Constants() {
    // Clase de solo constantes: no instanciable.
  }

  // ------------------------------------------------------------------------
  // CHASSIS / SWERVE
  // ------------------------------------------------------------------------
  public static final class ChassisConstants {

    private ChassisConstants() {}

    /** Distancia entre ruedas derecha e izquierda (m). */
    public static final double TRACKWIDTH = 0.502;

    /** Distancia entre ruedas delanteras y traseras (m). */
    public static final double WHEELBASE = 0.61;

    /**
     * Cinemática del chasis swerve.
     * <p>
     * Define la posición de cada módulo respecto al centro del robot.
     */
    public static final SwerveDriveKinematics KINEMATICS = new SwerveDriveKinematics(
        new Translation2d(TRACKWIDTH / 2.0,  WHEELBASE / 2.0),   // Front Left
        new Translation2d(TRACKWIDTH / 2.0, -WHEELBASE / 2.0),   // Front Right
        new Translation2d(-TRACKWIDTH / 2.0, WHEELBASE / 2.0),   // Back Left
        new Translation2d(-TRACKWIDTH / 2.0, -WHEELBASE / 2.0));  // Back Right

    /** Velocidad lineal máxima del chasis (m/s). */
    public static final double MAX_VELOCITY = 5;

    /** Velocidad angular máxima del chasis (rad/s). */
    public static final double MAX_ANG_SPD = 11.34;

    /** Aceleración lineal máxima esperada del chasis (m/s²). */
    public static final double MAX_ACCEL = 11.9;

    /** Aceleración angular máxima esperada del chasis (rad/s²). */
    public static final double MAX_ANG_ACCEL = 50.71;
  }

  // ------------------------------------------------------------------------
  // AUTÓNOMO / POSE ESTIMATOR
  // ------------------------------------------------------------------------
  public static final class AutonomousConstants {

    private AutonomousConstants() {}

    /** Pose inicial del robot en el campo. */
    public static final Pose2d initialPose = new Pose2d();

    // Ganancias PID para control de posición X.
    public static final double P_X = 80.0;
    public static final double I_X = 0.0;
    public static final double D_X = 0.0;

    // Ganancias PID para control de posición Y.
    public static final double P_Y = 80.0;
    public static final double I_Y = 0.0;
    public static final double D_Y = 0.0;

    // Ganancias PID para control de ángulo (theta).
    public static final double P_Z = 10;
    //public static final double P_Z = 10000;
    public static final double I_Z = 0.0;
    public static final double D_Z = 0.0;

    /** Velocidad lineal máxima permitida en auton (m/s). */
    public static final double MAX_SPD = ChassisConstants.MAX_VELOCITY;

    /** Aceleración lineal máxima permitida en auton (m/s²). */
    public static final double MAX_ACCEL = ChassisConstants.MAX_ACCEL;

    /** Velocidad angular máxima permitida en auton (rad/s). */
    public static final double MAX_ANG_SPD = ChassisConstants.MAX_ANG_SPD;

    /** Aceleración angular máxima permitida en auton (rad/s²). */
    public static final double MAX_ANG_ACCEL = ChassisConstants.MAX_ANG_ACCEL;

    /**
     * Constraints para el ProfiledPIDController de theta (rotación),
     * usados por el {@link edu.wpi.first.math.controller.HolonomicDriveController}.
     */
    public static final TrapezoidProfile.Constraints Z_CONTROLER =
        new TrapezoidProfile.Constraints(
            MAX_ANG_SPD,
            MAX_ANG_ACCEL);

    /** Tolerancia de posición para finalizar un movimiento auton (m). */
    public static final double POS_TOLERANCE = 0.05;

    /** Tolerancia de ángulo para finalizar un movimiento auton (rad). */
    public static final double ANG_TOLERANCE = Math.toRadians(1.0);
    }
}
