package frc.robot.subsystems.intake;

// TODO Comentarios de las constantes.

/**
 * Conjunto de constantes específicas del sistema intake.
 *
 * Solo se incluyen las que realmente se usan en las clases actuales
 * (módulos, controlador, IO, etc.).
 */
public class IntakeConstants {
    
    private IntakeConstants() {
        // Clase de solo constantes: no instanciable.
    }

    // --------------------------------------------------------------------
    // IDs DE DISPOSITIVOS (CTR)
    // --------------------------------------------------------------------

    //TODO Asignar IDs correctos de los TalonFX del intake.
    public static final int PIVOT_L_TALONFX_ID = 1;
    public static final int PIVOT_R_TALONFX_ID = 2;
    public static final int SPIN_FRONT_TALONFX_ID = 3;
    public static final int SPIN_BACK_TALONFX_ID = 4;

    // --------------------------------------------------------------------
    // LIMITES DE SOFTWARE
    // --------------------------------------------------------------------
    
    /** Límite superior de posición del pivote (radianes). */
    public static final double PIVOT_SOFT_LIMIT_FORWARD = 0;
    
    /** Límite inferior de posición del pivote (radianes). */
    public static final double PIVOT_SOFT_LIMIT_REVERSE = -2.584483556353203234;

    // --------------------------------------------------------------------
    // GEOMETRÍA Y CONVERSIONES
    // --------------------------------------------------------------------

    /**
     * Factor de conversión de rotaciones del motor de giro a radianes de ángulo
     * del módulo.
     *
     * rotaciones_motor * ROT_2_RAD = radianes
     * 
     * TODO Conseguir la relacion correcta de rotaciones a radianes del intake.
     * 
     */
    public static final double ROT_2_RAD = 0.209439510239319549;

    // --------------------------------------------------------------------
    // MOTION MAGIC EXPO - PIVOT (POSICIÓN)
    // --------------------------------------------------------------------

    /** Velocidad de crucero de Motion Magic para el pivot (rot/s). */
    public static final double MAGIC_MOTION_VELOCITY_STR = 9;

    /** Aceleración de Motion Magic para el pivot (rot/s²). */
    public static final double MAGIC_MOTION_ACCELERATION_STR = 10;

    /** Jerk de Motion Magic para el pivot (rot/s³). */
    public static final double MAGIC_MOTION_JERK_STR = 9500;

    /**
     * Ganancia kV del modo Motion Magic Expo para el pivot.
     * <p>
     * Escala la contribución de la velocidad en el perfil de movimiento.
     */
    public static final double MAGIC_MOTION_EXPO_KV_STR = 0.12;

    /**
     * Ganancia kA del modo Motion Magic Expo para el pivot.
     * <p>
     * Escala la contribución de la aceleración en el perfil de movimiento.
     */
    public static final double MAGIC_MOTION_EXPO_KA_STR = 0.10;

    // --------------------------------------------------------------------
    // GANANCIAS DE CONTROL - POSICIÓN (PIVOT)
    // --------------------------------------------------------------------

    /**
     * kG: salida para compensar gravedad (en este caso, torque/rozamiento
     * del módulo).
     */
    public static final double POS_KG = 0.20;

    /** kS: salida para vencer fricción estática (offset inicial). */
    public static final double POS_KS = 0.25;

    /** kV: salida por unidad de velocidad objetivo (output / rps). */
    public static final double POS_KV = 0.12;

    /** kA: salida por unidad de aceleración objetivo (output / (rps/s)). */
    public static final double POS_KA = 0.01;

    /** kP: salida por unidad de error de posición (output / rotación). */
    public static final double POS_KP = 48;

    /** kI: salida por unidad de error integrado de posición. */
    public static final double POS_KI = 0.0;

    /** kD: salida por unidad de error de velocidad (derivada). */
    public static final double POS_KD = 0.01;

    // --------------------------------------------------------------------
    // MOTION MAGIC - SPIN (VELOCIDAD) 
    // --------------------------------------------------------------------

    // TODO Ajustar valores de Motion Magic para el spin.
    
    /** Aceleración de Motion Magic para el spin (rot/s²). */
    public static final double MAGIC_MOTION_VELOCITY_ACCELERATION_STR = 950;

    /** Jerk de Motion Magic para el spin (rot/s³). */
    public static final double MAGIC_MOTION_VELOCITY_JERK_STR = 9500;

    // -------------------------------------------------------------------
    // GANANCIAS DE CONTROL - VELOCIDAD (SPIN)
    // --------------------------------------------------------------------

    /** kS: salida para vencer fricción estática en el spin. */
    public static final double VEL_KS = 0.10442;

    /** kV: salida por unidad de velocidad objetivo (output / rps). */
    public static final double VEL_KV = 0.10882;

    /** kA: salida por unidad de aceleración objetivo (output / (rps/s)). */
    public static final double VEL_KA = 0.001647;

    /** kP: salida por unidad de error de velocidad (output / rps). */
    public static final double VEL_KP = 0.4;

    /** kI: salida por unidad de error integrado de velocidad. */
    public static final double VEL_KI = 0.00;

    /** kD: salida por unidad de derivada del error de velocidad. */
    public static final double VEL_KD = 0.001;

    // --------------------------------------------------------------------
    // POSICIONES Y VELOCIDADES PREDEFINIDAS
    // --------------------------------------------------------------------

    // TODO Ajustar valores de ángulo y velocidad del intake.

    public static final double GRAB_ANGLE_RAD = -2.584483556353203234;
    public static final double GRAB_SPIN_RPS = 100.0;

    public static final double RELEASE_ANGLE_RAD = -2.584483556353203234;
    public static final double RELEASE_SPIN_RPS = -3.0;

    public static final double STOW_ANGLE_RAD = 0.0;
    public static final double STOW_SPIN_RPS = 0.0;
}
