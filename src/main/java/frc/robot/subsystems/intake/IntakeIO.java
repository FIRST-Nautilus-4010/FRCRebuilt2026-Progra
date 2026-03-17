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
    
    /** Motor TalonFX del spinner trasero. */
    private final TalonFX spinBackMotor;
    /** Motor TalonFX del spinner frontal. */
    private final TalonFX spinFrontMotor;
    /** Motor TalonFX del pivote derecho (principal). */
    private final TalonFX pivotRMotor;
    /** Motor TalonFX del pivote izquierdo (seguidor con alineación opuesta). */
    private final TalonFX pivotLMotor;

    /**
     * Crea la interfaz de hardware del Intake.
     *
     * Inicializa todos los motores TalonFX y configura el pivote izquierdo
     * como seguidor del pivote derecho con alineación opuesta.
     */
    public IntakeIO() {
        this.spinBackMotor = new TalonFX(IntakeConstants.SPIN_BACK_TALONFX_ID);
        this.pivotRMotor = new TalonFX(IntakeConstants.PIVOT_R_TALONFX_ID);
        this.spinFrontMotor = new TalonFX(IntakeConstants.SPIN_FRONT_TALONFX_ID);

        this.pivotLMotor = new TalonFX(IntakeConstants.PIVOT_L_TALONFX_ID);

        pivotLMotor.setControl(new Follower(pivotRMotor.getDeviceID(), MotorAlignmentValue.Opposed));
    }

    /**
     * Obtiene la posición actual del pivote derecho.
     *
     * @return Posición en radianes
     */
    public double getPivotPositionRad() {
        return pivotRMotor.getPosition().getValueAsDouble() * IntakeConstants.ROT_2_RAD;
    }

    /**
     * Obtiene la velocidad actual del spinner trasero.
     *
     * @return Velocidad en rotaciones por segundo (RPS)
     */
    public double getSpinVelocityRPS() {
        return spinBackMotor.getVelocity().getValueAsDouble();
    }
    
    /**
     * Detiene todos los motores del subsistema.
     */
    public void stopMotors() {
        spinBackMotor.stopMotor();
        spinFrontMotor.stopMotor();
        pivotRMotor.stopMotor();
    }

    /**
     * Obtiene una referencia al motor del spinner trasero.
     *
     * @return Motor TalonFX del spinner trasero
     */
    public TalonFX getspinBackMotor() {
        return spinBackMotor;
    }

    /**
     * Obtiene una referencia al motor del spinner frontal.
     *
     * @return Motor TalonFX del spinner frontal
     */
    public TalonFX getspinFrontMotor() {
        return spinFrontMotor;
    }

    /**
     * Obtiene una referencia al motor del pivote derecho.
     *
     * @return Motor TalonFX del pivote derecho
     */
    public TalonFX getPivotMotor() {
        return pivotRMotor;
    }
}
