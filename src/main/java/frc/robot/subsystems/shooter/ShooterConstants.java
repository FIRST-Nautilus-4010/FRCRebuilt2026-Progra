package frc.robot.subsystems.shooter;

// TODO Comentarios de las constantes.

/**
 * Conjunto de constantes específicas del sistema shooter.
 *
 * Solo se incluyen las que realmente se usan en las clases actuales
 * (módulos, controlador, IO, etc.).
 */
public class ShooterConstants {
    
    private ShooterConstants() {
        // Clase de solo constantes: no instanciable.
    }

    // --------------------------------------------------------------------
    // IDs DE DISPOSITIVOS (CTR)
    // --------------------------------------------------------------------

    //TODO Asignar IDs correctos de los TalonFX del shooter.

    public static final int SPIN_TALONFX_ID = 20;
    public static final int SPIN_SECONDARY_TALONFX_ID = 21;

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

    // TODO Ajustar valores de velocidad del shooter.

    public static final double SHOOT_VELOCITY = 15.0; // rotaciones por segundo

    public static final double RELEASE_VELOCITY = -10.0; // rotaciones por segundo
}
