package frc.robot.subsystems.intake;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.MotorAlignmentValue;

/**
 * Interfaz de hardware del subsistema Intake.
 *
 * Gestiona los motores TalonFX del pivote (derecho e izquierdo sincronizados)
 * y los spinners (frontal y trasero), incluyendo su inicialización, sincronización
 * y lectura de telemetría.
 */
public class IntakeIO {
    
    /** Motor TalonFX del spinner. */
    private final TalonFX spinMotor;
    /** Motor TalonFX del pivote derecho (principal). */
    private final TalonFX pivotMotor;

    /**
     * Crea la interfaz de hardware del Intake.
     *
     * Inicializa todos los motores TalonFX y configura el pivote izquierdo
     * como seguidor del pivote derecho con alineación opuesta.
     */
    public IntakeIO() {
        this.spinMotor = new TalonFX(IntakeConstants.SPIN_TALONFX_ID);
        this.pivotMotor = new TalonFX(IntakeConstants.PIVOT_TALONFX_ID);
        TalonFX pivot2 = new TalonFX(10);
        pivot2.setControl(new Follower(pivotMotor.getDeviceID(), MotorAlignmentValue.Aligned));
        TalonFX pivot3 = new TalonFX(9);
        pivot3.setControl(new Follower(pivotMotor.getDeviceID(), MotorAlignmentValue.Opposed));
        TalonFX pivot4 = new TalonFX(8);
        pivot4.setControl(new Follower(pivotMotor.getDeviceID(), MotorAlignmentValue.Opposed));


        this.pivotMotor.setPosition(0.332275);
    }

    /**
     * Obtiene la posición actual del pivote derecho.
     *
     * @return Posición en radianes
     */
    public double getPivotPositionRad() {
        return pivotMotor.getPosition().getValueAsDouble() * IntakeConstants.ROT_2_RAD;
    }

    /**
     * Obtiene la velocidad actual del spinner trasero.
     *
     * @return Velocidad en rotaciones por segundo (RPS)
     */
    public double getSpinVelocityRPS() {
        return spinMotor.getVelocity().getValueAsDouble();
    }
    
    /**
     * Detiene todos los motores del subsistema.
     */
    public void stopMotors() {
        spinMotor.stopMotor();
        pivotMotor.stopMotor();
    }

    /**
     * Obtiene una referencia al motor del spinner frontal.
     *
     * @return Motor TalonFX del spinner frontal
     */
    public TalonFX getspinMotor() {
        return spinMotor;
    }

    /**
     * Obtiene una referencia al motor del pivote derecho.
     *
     * @return Motor TalonFX del pivote derecho
     */
    public TalonFX getPivotMotor() {
        return pivotMotor;
    }
}
