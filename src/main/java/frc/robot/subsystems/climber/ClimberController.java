package frc.robot.subsystems.climber;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicExpoVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

/**
 * Encapsula la configuración y el control del intake:
 * <ul>
 *   <li>Motor de giro en modo Motion Magic Velocity</li>
 *   <li>Motor del pivote en modo Motion Magic Expo (posición)</li>
 * </ul>
 *
 * Esta clase:
 * <ul>
 *   <li>Aplica las ganancias de los slots desde {@link ClimberConstants}</li>
 *   <li>Configura los parámetros de Motion Magic para ambos motores</li>
 *   <li>Provee métodos simples para setear velocidad lineal y ángulo</li>
 * </ul>
 */
public class ClimberController {

    // --- Motores físicos ---

    /** Motor de claw. */

    private final TalonFX clawMotor;

    /** Motor del elevator. */
    
    private final TalonFX elevatorR;
    // --- Configuración Phoenix 6 ---

    /** Configuración del TalonFX de claw. */
    private final TalonFXConfiguration clawConfig;

    /** Configuración del TalonFX del elevator. */
    private final TalonFXConfiguration elevatorConfig;

    // --- Demandos (requests) de control ---


    /**
     * Request de control para posición (Motion Magic Expo) del motor del Claw.
     * Usa el Slot0 de la configuración.
     */
    private final MotionMagicExpoVoltage positionClawRequest;

        /**
     * Request de control para posición (Motion Magic Expo) del motor del Elevator.
     * Usa el Slot0 de la configuración.
     */
    private final MotionMagicExpoVoltage positionElevatorRequest;

    /**
     * Crea un controlador para un intake.
     *
     * @param clawMotor   TalonFX usado como claw (claw)
     * @param elevatorR TalonFX usado como elevator (elevator)
     */
    public ClimberController(TalonFX elevatorR, TalonFX clawMotor) {
        this.clawMotor = clawMotor;
        this.elevatorR = elevatorR;

        // Instancia configuraciones vacías que luego llenamos con nuestras constantes.
        this.clawConfig = new TalonFXConfiguration();
        this.elevatorConfig = new TalonFXConfiguration();

        // Configura límites de corriente y modo neutral.
        configureMotors();

        // Requests de control iniciales (valor 0, slot 0).
        this.positionElevatorRequest = new MotionMagicExpoVoltage(0.0).withSlot(0);
        this.positionClawRequest = new MotionMagicExpoVoltage(0.0).withSlot(0);


        // Configura gains de slots y parámetros de Motion Magic.
  
        configurePivotGains();
        configureMotionMagic();

        // Configura soft limits antes de aplicar las configuraciones al hardware.
        configureClawSoftLimits();
        configureElevatorSoftLimits();

        // Aplica las configuraciones a los TalonFX.
        this.clawMotor.getConfigurator().apply(clawConfig);
        this.elevatorR.getConfigurator().apply(elevatorConfig);
    }

    // --------------------------------------------------------------------
    // CONFIGURACIÓN
    // --------------------------------------------------------------------
    
    /**
     * Configura los límites de corriente y el modo neutral de ambos motores.
     */
    private void configureMotors() {
        //spinFrontMotor.setControl(new Follower(spinBackMotor.getDeviceID(), MotorAlignmentValue.Aligned));

        clawConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        clawConfig.CurrentLimits.SupplyCurrentLimit = 40;
        clawConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        clawConfig.CurrentLimits.StatorCurrentLimit = 120;
        clawConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        elevatorConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        elevatorConfig.CurrentLimits.SupplyCurrentLimit = 40;
        elevatorConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        elevatorConfig.CurrentLimits.StatorCurrentLimit = 120;
        elevatorConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    }



    /**
     * Configura las ganancias del slot 0 del motor del pivote (posición).
     * <p>
     * Valores tomados de {@link ClimberConstants}:
     * kG, kS, kV, kA, kP, kI, kD.
     */
    private void configurePivotGains() {
        var slot0Elevator = elevatorConfig.Slot0;
        slot0Elevator.kG = ClimberConstants.POS_KG;
        slot0Elevator.kS = ClimberConstants.POS_KS;
        slot0Elevator.kV = ClimberConstants.POS_KV;
        slot0Elevator.kA = ClimberConstants.POS_KA;
        slot0Elevator.kP = ClimberConstants.POS_KP;
        slot0Elevator.kI = ClimberConstants.POS_KI;
        slot0Elevator.kD = ClimberConstants.POS_KD;

        var slot0Claw = clawConfig.Slot0;
        slot0Claw.kG = ClimberConstants.POS_KG;
        slot0Claw.kS = ClimberConstants.POS_KS;
        slot0Claw.kV = ClimberConstants.POS_KV;
        slot0Claw.kA = ClimberConstants.POS_KA;
        slot0Claw.kP = ClimberConstants.POS_KP;
        slot0Claw.kI = ClimberConstants.POS_KI;
        slot0Claw.kD = ClimberConstants.POS_KD;
    }

    /**
     * Configura los parámetros de Motion Magic para el pivote.
     * <ul>
     *   <li>Pivote: vel. crucero, aceleración, jerk y parámetros Expo</li>
     * </ul>
     */
    private void configureMotionMagic() {
        // Motion Magic Expo en el motor del claw.
        var clawMM = clawConfig.MotionMagic;
        clawMM.MotionMagicCruiseVelocity = ClimberConstants.MAGIC_MOTION_VELOCITY_STR;
        clawMM.MotionMagicAcceleration = ClimberConstants.MAGIC_MOTION_ACCELERATION_STR;
        clawMM.MotionMagicJerk = ClimberConstants.MAGIC_MOTION_JERK_STR;
        clawMM.MotionMagicExpo_kV = ClimberConstants.MAGIC_MOTION_EXPO_KV_STR;
        clawMM.MotionMagicExpo_kA = ClimberConstants.MAGIC_MOTION_EXPO_KA_STR;

        // Motion Magic Expo en el motor del elevator.
        var elevatorMM = elevatorConfig.MotionMagic;
        elevatorMM.MotionMagicCruiseVelocity = ClimberConstants.MAGIC_MOTION_VELOCITY_STR;
        elevatorMM.MotionMagicAcceleration = ClimberConstants.MAGIC_MOTION_ACCELERATION_STR;
        elevatorMM.MotionMagicJerk = ClimberConstants.MAGIC_MOTION_JERK_STR;
        elevatorMM.MotionMagicExpo_kV = ClimberConstants.MAGIC_MOTION_EXPO_KV_STR;
        elevatorMM.MotionMagicExpo_kA = ClimberConstants.MAGIC_MOTION_EXPO_KA_STR;
    }

    /**
     * Configura los soft limits del pivote.
     */
    private void configureClawSoftLimits() {
        // Configura los soft limits del claw.
        clawConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        clawConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold = ClimberConstants.CLAW_SOFT_LIMIT_FORWARD;
        clawConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
        clawConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold = ClimberConstants.CLAW_SOFT_LIMIT_REVERSE;
    }

        /**
     * Configura los soft limits del pivote.
     */
    private void configureElevatorSoftLimits() {
        // Configura los soft limits del claw.
        elevatorConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        elevatorConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold = ClimberConstants.ELEVATOR_SOFT_LIMIT_FORWARD;
        elevatorConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
        elevatorConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold = ClimberConstants.ELEVATOR_SOFT_LIMIT_REVERSE;
    }
    // --------------------------------------------------------------------
    // FUNCIONES
    // --------------------------------------------------------------------



    /**
     * Establece la posición deseado del módulo.
     *
     * @param setPositionClaw posición objetivo en radianes.
     */
    public void setPositionClaw(double position) {
        if (Math.abs(position - clawMotor.getPosition().getValueAsDouble()) > 0.05){
            // Convierte de radianes a rotaciones del eje (considerando relación de transmisión).

            clawMotor.setControl(positionClawRequest.withPosition(position));
        } else {
            clawMotor.stopMotor();
        }
    }

    
    /**
     * Establece la posición deseado del módulo.
     *
     * @param setPositionElevator posición objetivo en radianes.
     */
    public void setPositionElevator(double position) {
        if (Math.abs(position - elevatorR.getPosition().getValueAsDouble()) > 0.05){
            // Convierte de radianes a rotaciones del eje (considerando relación de transmisión).

            elevatorR.setControl(positionElevatorRequest.withPosition(position));
        } else {
            elevatorR.stopMotor();
        }
    }
}
