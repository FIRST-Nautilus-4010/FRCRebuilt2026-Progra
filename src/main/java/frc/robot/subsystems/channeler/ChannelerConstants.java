package frc.robot.subsystems.channeler;

/**
 * Constantes de configuración del subsistema Channeler.
 *
 * Define IDs de dispositivos CAN, parámetros de Motion Magic, ganancias PID
 * y velocidades predefinidas para el control del motor de conducción de fuel.
 */
public class ChannelerConstants {
    
    private ChannelerConstants() {
        // Clase de solo constantes: no instanciable.
    }

    // ====================================================================
    // IDS DE DISPOSITIVOS CAN
    // ====================================================================

    /** ID del motor TalonFX principal del spinner. */
    public static final int SPIN_TALONFX_ID = 6;
    /** ID del motor TalonFX secundario del spinner. */
    public static final int SPIN_SECONDARY_TALONFX_ID = 7;

    // ====================================================================
    // MOTION MAGIC - CONTROL DE VELOCIDAD (SPIN)
    // ====================================================================
    
    /** Aceleración de Motion Magic para el spin (rot/s²). */
    public static final double MAGIC_MOTION_VELOCITY_ACCELERATION_STR = 950;

    /** Jerk de Motion Magic para el spin (rot/s³). */
    public static final double MAGIC_MOTION_VELOCITY_JERK_STR = 9500;

    // ====================================================================
    // GANANCIAS PID - CONTROL DE VELOCIDAD (SPIN)
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
    // VELOCIDADES PREDEFINIDAS
    // ====================================================================

    /** Velocidad de liberación/rechazo de fuel (rotaciones/segundo). */
    public static final double RELEASE_VELOCITY = 30.0;
}
