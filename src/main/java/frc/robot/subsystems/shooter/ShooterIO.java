package frc.robot.subsystems.shooter;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.MotorAlignmentValue;

/**
 * Interfaz de hardware del subsistema Shooter.
 *
 * Gestiona los motores TalonFX del shooter (principal y secundario sincronizados),
 * incluyendo su inicialización, sincronización y lectura de telemetría.
 */
public class ShooterIO {
    
    /** Motor TalonFX principal del shooter. */
    private final TalonFX spinMotor;
    /** Motor TalonFX secundario (seguidor del principal con alineación opuesta). */
    private final TalonFX spinMotorSecondary;
    
    /**
     * Crea la interfaz de hardware del Shooter.
     *
     * Inicializa los motores TalonFX y configura el motor secundario como
     * seguidor del motor principal con alineación opuesta.
     */
    public ShooterIO() {
        this.spinMotor = new TalonFX(ShooterConstants.SPIN_TALONFX_ID);
        this.spinMotorSecondary = new TalonFX(ShooterConstants.SPIN_SECONDARY_TALONFX_ID);

        this.spinMotorSecondary.setControl(new Follower(spinMotor.getDeviceID(), MotorAlignmentValue.Opposed));
    }

    /**
     * Obtiene la velocidad actual del motor de lanzamiento.
     *
     * @return Velocidad en rotaciones por segundo (RPS)
     */
    public double getSpinVelocityRPS() {
        return spinMotor.getVelocity().getValueAsDouble();
    }

    /**
     * Calcula la velocidad de salida estimada del fuel basado en la velocidad de rotación del motor.
     *
     * @return Velocidad de salida estimada en metros por segundo (m/s)
     */
    public double getEstimatedExitVelocity(double distance) {
        double rps = ShooterConstants.VEL_TABLE.get(distance);

        double omega = 2 * Math.PI * rps;

        double linearVelocity = omega * ShooterConstants.FLYWHEEL_RADIUS_METERS;

        return linearVelocity * ShooterConstants.FLYWHEEL_EFFICIENCY;
    }
    
    /**
     * Detiene el motor de lanzamiento.
     */
    public void stopMotors() {
        spinMotor.stopMotor();
    }

    /**
     * Obtiene una referencia al motor de lanzamiento principal.
     *
     * @return Motor TalonFX principal del shooter
     */
    public TalonFX getSpinMotor() {
        return spinMotor;
    }

    /**
     * Obtiene una referencia al motor de lanzamiento secundario.
     *
     * @return Motor TalonFX secundario del shooter
     */
    public TalonFX getSpinMotorSecondary() {
        return spinMotorSecondary;
    }
}
