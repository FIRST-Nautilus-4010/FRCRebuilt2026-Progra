package frc.robot.subsystems.channeler;

import com.ctre.phoenix6.hardware.TalonFX;

// TODO comentarios de la clase.

/**
 * Gestiona el hardware del channeler:
 * <ul>
 *   <li>Motor de giro (spin)</li>
 *   <li>Motor secundario (secondary)</li>
 * </ul>
 *
 * Esta clase se encarga de:
 * <ul>
 *   <li>Inicializar los dispositivos CAN</li>
 *   <li>Proveer métodos de acceso a posición y velocidad del motor de giro y motor secundario</li>
 * </ul>
 */
public class ChannelerIO {
    private final TalonFX spinMotor;
    /**
     * Crea un nuevo ChannelerIO.
     *
     * @param spinTalonFxId   ID CAN del TalonFX de giro
     */
    
    public ChannelerIO() {
        this.spinMotor = new TalonFX(ChannelerConstants.SPIN_TALONFX_ID);
    }

    public double getSpinVelocityRPS() {
        return spinMotor.getVelocity().getValueAsDouble();
    }
    
    public void stopMotors() {
        spinMotor.stopMotor();
    }

    public TalonFX getSpinMotor() {
        return spinMotor;
    }
}
