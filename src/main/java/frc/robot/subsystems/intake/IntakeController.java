package frc.robot.subsystems.intake;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicExpoVoltage;
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
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
 *   <li>Aplica las ganancias de los slots desde {@link IntakeConstants}</li>
 *   <li>Configura los parámetros de Motion Magic para ambos motores</li>
 *   <li>Provee métodos simples para setear velocidad lineal y ángulo</li>
 * </ul>
 */
public class IntakeController {

    // --- Motores físicos ---

    /** Motor de giro. */
    private final TalonFX spinMotor;

    /** Motor del pivote. */
    private final TalonFX pivotMotor;

    // --- Configuración Phoenix 6 ---

    /** Configuración del TalonFX de giro. */
    private final TalonFXConfiguration spinConfig;

    /** Configuración del TalonFX del pivote. */
    private final TalonFXConfiguration pivotConfig;

    // --- Demandos (requests) de control ---

    /**
     * Request de control para velocidad del motor de giro.
     * Usa el Slot0 de la configuración.
     */
    private final MotionMagicVelocityVoltage velocityRequest;

    /**
     * Request de control para posición (Motion Magic Expo) del motor del pivote.
     * Usa el Slot0 de la configuración.
     */
    private final MotionMagicExpoVoltage positionRequest;

    /**
     * Crea un controlador para un intake.
     *
     * @param spinMotor   TalonFX usado como spin (giro)
     * @param pivotMotor TalonFX usado como pivot (pivote)
     */
    public IntakeController(TalonFX spinMotor, TalonFX pivotMotor) {
        this.spinMotor = spinMotor;
        this.pivotMotor = pivotMotor;

        // Instancia configuraciones vacías que luego llenamos con nuestras constantes.
        this.spinConfig = new TalonFXConfiguration();
        this.pivotConfig = new TalonFXConfiguration();

        // Configura límites de corriente y modo neutral.
        configureMotors();

        // Requests de control iniciales (valor 0, slot 0).
        this.velocityRequest = new MotionMagicVelocityVoltage(0.0).withSlot(0);
        this.positionRequest = new MotionMagicExpoVoltage(0.0).withSlot(0);

        // Configura gains de slots y parámetros de Motion Magic.
        configureSpinGains();
        configurePivotGains();
        configureMotionMagic();

        // Configura soft limits antes de aplicar las configuraciones al hardware.
        configureSoftLimits();

        // Aplica las configuraciones a los TalonFX.
        this.spinMotor.getConfigurator().apply(spinConfig);
        this.pivotMotor.getConfigurator().apply(pivotConfig);
    }

    // --------------------------------------------------------------------
    // CONFIGURACIÓN
    // --------------------------------------------------------------------
    
    /**
     * Configura los límites de corriente y el modo neutral de ambos motores.
     */
    private void configureMotors() {
        spinConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        spinConfig.CurrentLimits.SupplyCurrentLimit = 40;
        spinConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        spinConfig.CurrentLimits.StatorCurrentLimit = 120;
        spinConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        pivotConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        pivotConfig.CurrentLimits.SupplyCurrentLimit = 40;
        pivotConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        pivotConfig.CurrentLimits.StatorCurrentLimit = 120;
        pivotConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    }

    /**
     * Configura las ganancias del slot 0 del motor de giro (velocidad).
     * <p>
     * Valores tomados de {@link IntakeConstants}:
     * kS, kV, kA, kP, kI, kD.
     */
    private void configureSpinGains() {
        var slot0 = spinConfig.Slot0;
        slot0.kS = IntakeConstants.VEL_KS;
        slot0.kV = IntakeConstants.VEL_KV;
        slot0.kA = IntakeConstants.VEL_KA;
        slot0.kP = IntakeConstants.VEL_KP;
        slot0.kI = IntakeConstants.VEL_KI;
        slot0.kD = IntakeConstants.VEL_KD;
    }

    /**
     * Configura las ganancias del slot 0 del motor del pivote (posición).
     * <p>
     * Valores tomados de {@link IntakeConstants}:
     * kG, kS, kV, kA, kP, kI, kD.
     */
    private void configurePivotGains() {
        var slot0 = pivotConfig.Slot0;
        slot0.kG = IntakeConstants.POS_KG;
        slot0.kS = IntakeConstants.POS_KS;
        slot0.kV = IntakeConstants.POS_KV;
        slot0.kA = IntakeConstants.POS_KA;
        slot0.kP = IntakeConstants.POS_KP;
        slot0.kI = IntakeConstants.POS_KI;
        slot0.kD = IntakeConstants.POS_KD;
    }

    /**
     * Configura los parámetros de Motion Magic para el pivote.
     * <ul>
     *   <li>Pivote: vel. crucero, aceleración, jerk y parámetros Expo</li>
     * </ul>
     */
    private void configureMotionMagic() {
        // Motion Magic Expo en el motor del pivote.
        var pivotMM = pivotConfig.MotionMagic;
        pivotMM.MotionMagicCruiseVelocity = IntakeConstants.MAGIC_MOTION_VELOCITY_STR;
        pivotMM.MotionMagicAcceleration = IntakeConstants.MAGIC_MOTION_ACCELERATION_STR;
        pivotMM.MotionMagicJerk = IntakeConstants.MAGIC_MOTION_JERK_STR;
        pivotMM.MotionMagicExpo_kV = IntakeConstants.MAGIC_MOTION_EXPO_KV_STR;
        pivotMM.MotionMagicExpo_kA = IntakeConstants.MAGIC_MOTION_EXPO_KA_STR;

        // Motion Magic en el motor de giro.
        var spinMM = spinConfig.MotionMagic;
        spinMM.MotionMagicAcceleration = IntakeConstants.MAGIC_MOTION_VELOCITY_ACCELERATION_STR;
        spinMM.MotionMagicJerk = IntakeConstants.MAGIC_MOTION_VELOCITY_JERK_STR;
    }

    /**
     * Configura los soft limits del pivote.
     */
    private void configureSoftLimits() {
        // Configura los soft limits del pivote.
        pivotConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        pivotConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold = IntakeConstants.PIVOT_SOFT_LIMIT_FORWARD / IntakeConstants.ROT_2_RAD;
        pivotConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
        pivotConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold = IntakeConstants.PIVOT_SOFT_LIMIT_REVERSE / IntakeConstants.ROT_2_RAD;
    }

    // --------------------------------------------------------------------
    // FUNCIONES
    // --------------------------------------------------------------------

    /**
     * Establece la velocidad lineal deseada del intake.
     *
     * @param velocityMps velocidad objetivo en rotaciones por segundo.
     */
    public void setVelocity(double velocityRps) {
        if (Math.abs(velocityRps) > 0.1) {
            double motorVelocity = velocityRps;
            spinMotor.setControl(velocityRequest.withVelocity(motorVelocity));
        } else {
            spinMotor.stopMotor();
        }
    }

    /**
     * Establece el ángulo deseado del módulo.
     *
     * @param angleRad ángulo objetivo en radianes.
     *                 Se convierte a rotaciones del motor de giro usando
     *                 {@link IntakeConstants#ROT_2_RAD}.
     */
    public void setAngle(double angleRad) {
        if (Math.abs(angleRad - pivotMotor.getPosition().getValueAsDouble()) > Math.toRadians(1)){
            // Convierte de radianes a rotaciones del eje (considerando relación de transmisión).
            double rotations = angleRad / IntakeConstants.ROT_2_RAD;
            pivotMotor.setControl(positionRequest.withPosition(rotations));
        } else {
            pivotMotor.stopMotor();
        }
    }
}
