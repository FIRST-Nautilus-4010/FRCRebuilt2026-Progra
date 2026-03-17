package frc.robot.subsystems.intake;

/**
 * Constantes de configuración del subsistema Intake.
 *
 * Define IDs de dispositivos CAN, límites de software, parámetros de Motion Magic,
 * ganancias PID y velocidades predefinidas para el control del pivote e intake spinners.
 */
public class IntakeConstants {
    
    private IntakeConstants() {
        // Clase de solo constantes: no instanciable.
    }

    // ====================================================================
    // IDS DE DISPOSITIVOS CAN
    // ====================================================================

    /** ID del motor TalonFX del pivote izquierdo. */
    public static final int PIVOT_L_TALONFX_ID = 1;
    /** ID del motor TalonFX del pivote derecho. */
    public static final int PIVOT_R_TALONFX_ID = 2;
    /** ID del motor TalonFX del spinner frontal. */
    public static final int SPIN_FRONT_TALONFX_ID = 3;
    /** ID del motor TalonFX del spinner trasero. */
    public static final int SPIN_BACK_TALONFX_ID = 4;

    // ====================================================================
    // LIMITES DE SOFTWARE
    // ====================================================================
    
    /** Límite superior de posición del pivote (radianes). */
    public static final double PIVOT_SOFT_LIMIT_FORWARD = 0;
    
    /** Límite inferior de posición del pivote (radianes). */
    public static final double PIVOT_SOFT_LIMIT_REVERSE = -2.9;

    // ====================================================================
    // GEOMETRÍA Y CONVERSIONES
    // ====================================================================

    /** Factor de conversión de rotaciones del motor a radianes. */
    public static final double ROT_2_RAD = 0.209439510239319549;

    // ====================================================================
    // MOTION MAGIC EXPO - CONTROL DE POSICIÓN (PIVOTE)
    // ====================================================================

    /** Velocidad de crucero de Motion Magic (rot/s). */
    public static final double MAGIC_MOTION_VELOCITY_STR = 9;

    /** Aceleración de Motion Magic (rot/s²). */
    public static final double MAGIC_MOTION_ACCELERATION_STR = 10;

    /** Jerk de Motion Magic (rot/s³). */
    public static final double MAGIC_MOTION_JERK_STR = 9500;

    /** Ganancia kV del modo Motion Magic Expo (escala de velocidad). */
    public static final double MAGIC_MOTION_EXPO_KV_STR = 0.12;

    /** Ganancia kA del modo Motion Magic Expo (escala de aceleración). */
    public static final double MAGIC_MOTION_EXPO_KA_STR = 0.10;

    // ====================================================================
    // GANANCIAS PID - CONTROL DE POSICIÓN (PIVOTE)
    // ====================================================================

    /** kG: Compensación de gravedad/rozamiento (output). */
    public static final double POS_KG = 0.20;

    /** kS: Salida estática para vencer fricción (output). */
    public static final double POS_KS = 0.25;

    /** kV: Ganancia de velocidad (output / rps). */
    public static final double POS_KV = 0.12;

    /** kA: Ganancia de aceleración (output / (rps/s)). */
    public static final double POS_KA = 0.01;

    /** kP: Ganancia proporcional de posición (output / rotación de error). */
    public static final double POS_KP = 48;

    /** kI: Ganancia integral de posición (output / rotación integrada). */
    public static final double POS_KI = 0.0;

    /** kD: Ganancia derivativa de posición (output / (rps/s) de error). */
    public static final double POS_KD = 0.01;

    // ====================================================================
    // MOTION MAGIC - CONTROL DE VELOCIDAD (SPINNERS)
    // ====================================================================

    /** Aceleración de Motion Magic para velocidad (rot/s²). */
    public static final double MAGIC_MOTION_VELOCITY_ACCELERATION_STR = 950;

    /** Jerk de Motion Magic para velocidad (rot/s³). */
    public static final double MAGIC_MOTION_VELOCITY_JERK_STR = 9500;

    // ====================================================================
    // GANANCIAS PID - CONTROL DE VELOCIDAD (SPINNERS)
    // ====================================================================

    /** kS: Salida estática para vencer fricción (output). */
    public static final double VEL_KS = 0.10442;

    /** kV: Ganancia de velocidad (output / rps). */
    public static final double VEL_KV = 0.10882;

    /** kA: Ganancia de aceleración (output / (rps/s)). */
    public static final double VEL_KA = 0.001647;

    /** kP: Ganancia proporcional de velocidad (output / rps de error). */
    public static final double VEL_KP = 0.4;

    /** kI: Ganancia integral de velocidad (output / rps integrado). */
    public static final double VEL_KI = 0.00;

    /** kD: Ganancia derivativa de velocidad (output / (rps/s) de error). */
    public static final double VEL_KD = 0.001;

    // ====================================================================
    // POSICIONES Y VELOCIDADES PREDEFINIDAS
    // ====================================================================

    /** Ángulo del pivote para grabbing (radianes). */
    public static final double GRAB_ANGLE_RAD = -2.850;
    /** Velocidad del spinner frontal para grabbing (RPS). */
    public static final double GRAB_SPIN_RPS = -70.0;
    /** Velocidad del spinner trasero para grabbing (RPS). */
    public static final double GRAB_SPIN_RPS_SECONDARY = -30.0;

    /** Ángulo del pivote para release (radianes). */
    public static final double RELEASE_ANGLE_RAD = -2.850;
    /** Velocidad del spinner frontal para release (RPS). */
    public static final double RELEASE_SPIN_RPS = 30.0;
    /** Velocidad del spinner trasero para release (RPS). */
    public static final double RELEASE_SPIN_RPS_SECONDARY = 30.0;

    /** Ángulo del pivote en reposo (radianes). */
    public static final double STOW_ANGLE_RAD = 0.0;
    /** Velocidad del spinner frontal en reposo (RPS). */
    public static final double STOW_SPIN_RPS = 0.0;
    /** Velocidad del spinner trasero en reposo (RPS). */
    public static final double STOW_SPIN_RPS_SECONDARY = 0.0;
}
