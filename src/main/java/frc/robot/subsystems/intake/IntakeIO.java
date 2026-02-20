package frc.robot.subsystems.intake;

import com.ctre.phoenix6.hardware.TalonFX;

// TODO comentarios de la clase.

/**
 * Gestiona el hardware del intake:
 * <ul>
 *   <li>Motor de giro front (spinFront)</li>
 *   <li>Motor de giro back (spinBack)</li>
 *   <li>Motor de pivote (pivot)</li>
 * </ul>
 *
 * Esta clase se encarga de:
 * <ul>
 *   <li>Inicializar los dispositivos CAN</li>
 *   <li>Proveer métodos de acceso a posición y velocidad del motor de giro y pivote</li>
 * </ul>
 */
public class IntakeIO {
    private final TalonFX spinBackMotor;
    private final TalonFX spinFrontMotor;
    private final TalonFX pivotMotor;
    /**
     * Crea un nuevo IntakeIO.
     *
     * @param spinBackTalonFxId ID CAN del TalonFX de giro back
     * @param pivotTalonFxId    ID CAN del TalonFX del pivote
     */
    
    public IntakeIO() {
        this.spinBackMotor = new TalonFX(IntakeConstants.SPIN_BACK_TALONFX_ID);
        this.pivotMotor = new TalonFX(IntakeConstants.PIVOT_TALONFX_ID);
        this.spinFrontMotor = new TalonFX(IntakeConstants.SPIN_FRONT_TALONFX_ID);
    }

    /** Obtiene la posición actual del motor del pivote en radianes. */
    public double getPivotPositionRad() {
        return pivotMotor.getPosition().getValueAsDouble() * IntakeConstants.ROT_2_RAD;
    }

    public double getSpinVelocityRPS() {
        return spinBackMotor.getVelocity().getValueAsDouble();
    }
    
    public void stopMotors() {
        spinBackMotor.stopMotor();
        pivotMotor.stopMotor();
    }

    public TalonFX getspinBackMotor() {
        return spinBackMotor;
    }

    public TalonFX getspinFrontMotor() {
        return spinFrontMotor;
    }

    public TalonFX getPivotMotor() {
        return pivotMotor;
    }
}
