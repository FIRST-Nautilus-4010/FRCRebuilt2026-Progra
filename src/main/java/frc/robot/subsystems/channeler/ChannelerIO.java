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
    private final TalonFX secondaryMotor;
    private final TalonFX thirdMotor;

    /**
     * Crea la interfaz de hardware del Channeler.
     *
     * Inicializa los motores TalonFX y configura el motor secundario como
     * seguidor del motor principal con alineación.
     */
    public ChannelerIO() {
        this.spinMotor = new TalonFX(ChannelerConstants.SPIN_TALONFX_ID);
        this.secondaryMotor = new TalonFX(ChannelerConstants.SPIN_SECONDARY_TALONFX_ID);
        this.thirdMotor = new TalonFX(33);
        secondaryMotor.setControl(new Follower(spinMotor.getDeviceID(), MotorAlignmentValue.Aligned));
        thirdMotor.setControl(new Follower(spinMotor.getDeviceID(), MotorAlignmentValue.Opposed));
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
