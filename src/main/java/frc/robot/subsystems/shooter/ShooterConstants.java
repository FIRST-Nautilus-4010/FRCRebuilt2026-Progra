package frc.robot.subsystems.shooter;

import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;

/**
 * Constantes de configuración del subsistema Shooter.
 *
 * Define IDs de dispositivos CAN, parámetros de Motion Magic, ganancias PID
 * y velocidades predefinidas para el control del motor de lanzamiento.
 */
public class ShooterConstants {
    
    private ShooterConstants() {
        // Clase de solo constantes: no instanciable.
    }
    
    public static final double FLYWHEEL_RADIUS_METERS = 0.0508;
    public static final double FLYWHEEL_EFFICIENCY = 0.185;

    // ====================================================================
    // IDS DE DISPOSITIVOS CAN
    // ====================================================================

    /** ID del motor TalonFX principal del shooter. */
    public static final int SPIN_TALONFX_ID = 7;
    /** ID del motor TalonFX secundario del shooter. */
    public static final int SPIN_SECONDARY_TALONFX_ID = 4;

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
    public static final double VEL_KS = 0.18;

    /** kV: Ganancia de velocidad (output / rps). */
    public static final double VEL_KV = 0.12;

    /** kA: Ganancia de aceleración (output / (rps/s)). */
    public static final double VEL_KA = 6;

    /** kP: Ganancia proporcional de velocidad (output / rps de error). */
    public static final double VEL_KP = 1.6;

    /** kI: Ganancia integral de velocidad (output / rps integrado). */
    public static final double VEL_KI = 1.00;

    /** kD: Ganancia derivativa de velocidad (output / (rps/s) de error). */
    public static final double VEL_KD = 0.12;

    // ====================================================================
    // VELOCIDADES PREDEFINIDAS
    // ====================================================================

    /** Velocidad de liberación/lanzamiento de fuel (rotaciones/segundo). */
    public static final double RELEASE_VELOCITY = -10.0;

    // ====================================================================
    // INTERPOLACIÓN
    // ====================================================================

    public static final InterpolatingDoubleTreeMap VEL_TABLE = new InterpolatingDoubleTreeMap();

    static {
        VEL_TABLE.put(1.5, 46.0);
        VEL_TABLE.put(2.0, 46.0);
        VEL_TABLE.put(2.5, 49.0);
        VEL_TABLE.put(3.0, 52.0);
        VEL_TABLE.put(3.5, 54.0);
        VEL_TABLE.put(4.0, 56.5);
        VEL_TABLE.put(4.5, 59.2);
        VEL_TABLE.put(5.0, 61.3);

    }
}
