package frc.robot.subsystems.channeler;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.MotorAlignmentValue;

/**
 * Interfaz de hardware del subsistema Channeler.
 *
 * Gestiona los motores TalonFX del spinner (principal y secundario), incluyendo
 * su inicialización, sincronización y lectura de telemetría.
 */
public class ChannelerIO {
    
    /** Motor TalonFX principal del spinner. */
    private final TalonFX spinMotor;
    /** Motor TalonFX secundario (seguidor del principal). */
    private final TalonFX indexerMotor;
    private final TalonFX indexerSecondary;

    /**
     * Crea la interfaz de hardware del Channeler.
     *
     * Inicializa los motores TalonFX y configura el motor secundario como
     * seguidor del motor principal con alineación.
     */
    public ChannelerIO() {
        this.spinMotor = new TalonFX(ChannelerConstants.SPIN_TALONFX_ID);
        this.indexerMotor = new TalonFX(ChannelerConstants.SPIN_INDEXER_TALONFX_ID);
        this.indexerSecondary = new TalonFX(ChannelerConstants.SPIN_SECONDARY_INDEXER_TALONFX_ID);
        indexerMotor.setControl(new Follower(spinMotor.getDeviceID(), MotorAlignmentValue.Opposed));
        indexerSecondary.setControl(new Follower(spinMotor.getDeviceID(), MotorAlignmentValue.Aligned));
    }

    /**
     * Obtiene la velocidad actual del motor de giro.
     *
     * @return Velocidad en rotaciones por segundo (RPS)
     */
    public double getSpinVelocityRPS() {
        return spinMotor.getVelocity().getValueAsDouble();
    }
    
    /**
     * Detiene ambos motores.
     */
    public void stopMotors() {
        spinMotor.stopMotor();
    }

    /**
     * Obtiene una referencia al motor de giro principal.
     *
     * @return Motor TalonFX del spinner
     */
    public TalonFX getSpinMotor() {
        return spinMotor;
    }
}
