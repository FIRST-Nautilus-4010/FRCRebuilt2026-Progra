package frc.robot.subsystems.intake;

import com.ctre.phoenix6.hardware.TalonFX;

// TODO comentarios de la clase.

/**
 * Gestiona el hardware del intake:
 * <ul>
 *   <li>Motor de giro (spin)</li>
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
    private final TalonFX spinMotor;
    private final TalonFX pivotMotor;
    /**
     * Crea un nuevo IntakeIO.
     *
     * @param spinTalonFxId   ID CAN del TalonFX de giro
     * @param pivotTalonFxId  ID CAN del TalonFX del pivote
     */
    
    public IntakeIO() {
        this.spinMotor = new TalonFX(IntakeConstants.SPIN_TALONFX_ID);
        this.pivotMotor = new TalonFX(IntakeConstants.PIVOT_TALONFX_ID);
    }

    /** Obtiene la posición actual del motor del pivote en radianes. */
    public double getPivotPositionRad() {
        return pivotMotor.getPosition().getValueAsDouble() * IntakeConstants.ROT_2_RAD;
    }

    public double getSpinVelocityRPS() {
        return spinMotor.getVelocity().getValueAsDouble();
    }
    
    public void stopMotors() {
        spinMotor.stopMotor();
        pivotMotor.stopMotor();
    }

    public TalonFX getSpinMotor() {
        return spinMotor;
    }

    public TalonFX getPivotMotor() {
        return pivotMotor;
    }
}
