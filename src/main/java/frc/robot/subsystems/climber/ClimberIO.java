package frc.robot.subsystems.climber;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.MotorAlignmentValue;

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
public class ClimberIO {
    
    private final TalonFX clawMotor;
    private final TalonFX elevatorR;
    private final TalonFX elevatorL;

    /**
     * Crea un nuevo IntakeIO.
     * @param elevatorRId    ID CAN del TalonFX del pivote
     */
    
    public ClimberIO() {
        this.elevatorL = new TalonFX(ClimberConstants.ELEVATOR_L_ID);

        this.elevatorR = new TalonFX(ClimberConstants.ELEVATOR_R_ID);

        this.clawMotor = new TalonFX(ClimberConstants.CLAW_MOTOR_ID);


        elevatorL.setControl(new Follower(elevatorR.getDeviceID(), MotorAlignmentValue.Aligned));
    }

    /** Obtiene la posición actual del motor del pivote en radianes. */
    public double getElevatorPosition() {
        return elevatorR.getPosition().getValueAsDouble();
    }

    public double getClawPosition() {
        return clawMotor.getPosition().getValueAsDouble();
    }
    
    public void stopMotors() {
        elevatorR.stopMotor();
        clawMotor.stopMotor();
    }


    public TalonFX getElevatorRight() {
        return elevatorR;
    }

    public TalonFX getClawMotor() {
        return clawMotor;
    }
}
