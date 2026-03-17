package frc.robot.subsystems.climber;

/**
 * Constantes de configuración del subsistema Climber.
 *
 * Define IDs de dispositivos CAN, límites de software, parámetros de Motion Magic,
 * ganancias PID y posiciones predefinidas para las etapas de escalada.
 */
public class ClimberConstants {
    
    private ClimberConstants() {
        // Clase de solo constantes: no instanciable.
    }

    // ====================================================================
    // IDS DE DISPOSITIVOS CAN
    // ====================================================================

    /** ID del motor TalonFX de la garra. */
    public static final int CLAW_MOTOR_ID = 10;
    /** ID del motor TalonFX del elevador izquierdo. */
    public static final int ELEVATOR_L_ID = 11;
    /** ID del motor TalonFX del elevador derecho. */
    public static final int ELEVATOR_R_ID = 12;

    // ====================================================================
    // LIMITES DE SOFTWARE
    // ====================================================================
    
    /** Límite superior de posición del elevador (rotaciones). */
    public static final double ELEVATOR_SOFT_LIMIT_FORWARD = 0;
    
    /** Límite inferior de posición del elevador (rotaciones). */
    public static final double ELEVATOR_SOFT_LIMIT_REVERSE = -2.584483556353203234;

    /** Límite superior de posición de la garra (rotaciones). */
    public static final double CLAW_SOFT_LIMIT_FORWARD = 0;
    
    /** Límite inferior de posición de la garra (rotaciones). */
    public static final double CLAW_SOFT_LIMIT_REVERSE = -2.584483556353203234;

    // ====================================================================
    // GEOMETRÍA Y CONVERSIONES
    // ====================================================================

    /** Factor de conversión de rotaciones del motor a radianes. */
    public static final double ROT_2_RAD = 0.209439510239319549;

    // ====================================================================
    // MOTION MAGIC EXPO - CONTROL DE POSICIÓN
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
    // GANANCIAS PID - CONTROL DE POSICIÓN
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
    // MOTION MAGIC - CONTROL DE VELOCIDAD
    // ====================================================================

    /** Aceleración de Motion Magic para velocidad (rot/s²). */
    public static final double MAGIC_MOTION_VELOCITY_ACCELERATION_STR = 950;

    /** Jerk de Motion Magic para velocidad (rot/s³). */
    public static final double MAGIC_MOTION_VELOCITY_JERK_STR = 9500;

    // ====================================================================
    // GANANCIAS PID - CONTROL DE VELOCIDAD
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
    // POSICIONES PREDEFINIDAS - ETAPAS DE ESCALADA
    // ====================================================================

    /** Posición del elevador en etapa de subida (rotaciones). */
    public static final double RISE_ELEVATOR_POSITION = -2.584483556353203234;
    /** Posición de la garra en etapa de subida (rotaciones). */
    public static final double RISE_CLAW_POSITION = -30.0;

    /** Posición del elevador en etapa de extensión (rotaciones). */
    public static final double EXTEND_ELEVATOR_POSITION = -2.584483556353203234;
    /** Posición de la garra en etapa de extensión (rotaciones). */
    public static final double EXTEND_CLAW_POSITION = 30.0;

    /** Posición del elevador en reposo (rotaciones). */
    public static final double STOW_ELEVATOR_POSITION = 0.0;
    /** Posición de la garra en reposo (rotaciones). */
    public static final double STOW_CLAW_POSITION = 0.0;

    /** Posición del elevador en etapa de tracción (rotaciones). */
    public static final double PULL_ELEVATOR_POSITION = 0.0;
    /** Posición de la garra en etapa de tracción (rotaciones). */
    public static final double PULL_CLAW_POSITION = 0.0;

}
