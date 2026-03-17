package frc.robot.subsystems.climber;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.MotorAlignmentValue;

/**
 * Interfaz de hardware del subsistema Climber.
 *
 * Gestiona los motores TalonFX del elevador (derecho e izquierdo sincronizados)
 * y la garra, incluyendo su inicialización, sincronización y lectura de telemetría.
 */
public class ClimberIO {
    
    /** Motor TalonFX de la garra. */
    private final TalonFX clawMotor;
    /** Motor TalonFX del elevador derecho (principal). */
    private final TalonFX elevatorR;
    /** Motor TalonFX del elevador izquierdo (seguidor). */
    private final TalonFX elevatorL;

    /**
     * Crea la interfaz de hardware del Climber.
     *
     * Inicializa los motores TalonFX del elevador y la garra, configurando
     * el motor izquierdo del elevador como seguidor del motor derecho.
     */
    public ClimberIO() {
        this.elevatorL = new TalonFX(ClimberConstants.ELEVATOR_L_ID);

        this.elevatorR = new TalonFX(ClimberConstants.ELEVATOR_R_ID);

        this.clawMotor = new TalonFX(ClimberConstants.CLAW_MOTOR_ID);

        elevatorL.setControl(new Follower(elevatorR.getDeviceID(), MotorAlignmentValue.Aligned));
    }

    /**
     * Obtiene la posición actual del elevador derecho.
     *
     * @return Posición en rotaciones
     */
    public double getElevatorPosition() {
        return elevatorR.getPosition().getValueAsDouble();
    }

    /**
     * Obtiene la posición actual de la garra.
     *
     * @return Posición en rotaciones
     */
    public double getClawPosition() {
        return clawMotor.getPosition().getValueAsDouble();
    }
    
    /**
     * Detiene todos los motores del subsistema.
     */
    public void stopMotors() {
        elevatorR.stopMotor();
        clawMotor.stopMotor();
    }

    /**
     * Obtiene una referencia al motor del elevador derecho.
     *
     * @return Motor TalonFX del elevador derecho
     */
    public TalonFX getElevatorRight() {
        return elevatorR;
    }

    /**
     * Obtiene una referencia al motor de la garra.
     *
     * @return Motor TalonFX de la garra
     */
    public TalonFX getClawMotor() {
        return clawMotor;
    }
}
