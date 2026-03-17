package frc.robot.subsystems.shooter;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

/**
 * Encapsula la configuración y el control del shooter:
 * <ul>
 *   <li>Motor de giro en modo Motion Magic Velocity</li>
 *   <li>Motor de giro secundario en modo Follower</li>
 * </ul>
 *
 * Esta clase:
 * <ul>
 *   <li>Aplica las ganancias de los slots desde {@link ShooterConstants}</li>
 *   <li>Configura los parámetros de Motion Magic para ambos motores</li>
 *   <li>Provee métodos simples para setear velocidad lineal</li>
 * </ul>
 */
public class ShooterController {

    // --- Motores físicos ---

    /** Motor de giro. */
    private final TalonFX spinMotor;

    /** Motor del pivote. */
    private final TalonFX spinMotorSecondary;

    // --- Configuración Phoenix 6 ---

    /** Configuración del TalonFX de giro. */
    private final TalonFXConfiguration spinConfig;

    // --- Demandos (requests) de control ---

    /**
     * Request de control para velocidad del motor de giro.
     * Usa el Slot0 de la configuración.
     */
    private final MotionMagicVelocityVoltage velocityRequest;


    /**
     * Crea un controlador para un shooter.
     *
     * @param spinMotor   TalonFX usado como spin (giro)
     * @param spinMotorSecondary TalonFX usado como spinMotorSecondary (motor secundario)
     */
    public ShooterController(TalonFX spinMotor, TalonFX spinMotorSecondary) {
        this.spinMotor = spinMotor;
        this.spinMotorSecondary = spinMotorSecondary;

        // Instancia configuraciones vacías que luego llenamos con nuestras constantes.
        this.spinConfig = new TalonFXConfiguration();

        // Configura límites de corriente y modo neutral.
        configureMotors();

        // Requests de control iniciales (valor 0, slot 0).
        this.velocityRequest = new MotionMagicVelocityVoltage(0.0).withSlot(0);

        // Configura gains de slots y parámetros de Motion Magic.
        configureSpinGains();
        configureMotionMagic();

        // Aplica las configuraciones a los TalonFX.
        this.spinMotor.getConfigurator().apply(spinConfig);
        this.spinMotorSecondary.getConfigurator().apply(spinConfig);
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
    }

    /**
     * Configura las ganancias del slot 0 del motor de giro (velocidad).
     * <p>
     * Valores tomados de {@link ShooterConstants}:
     * kS, kV, kA, kP, kI, kD.
     */
    private void configureSpinGains() {
        var slot0 = spinConfig.Slot0;
        slot0.kS = ShooterConstants.VEL_KS;
        slot0.kV = ShooterConstants.VEL_KV;
        slot0.kA = ShooterConstants.VEL_KA;
        slot0.kP = ShooterConstants.VEL_KP;
        slot0.kI = ShooterConstants.VEL_KI;
        slot0.kD = ShooterConstants.VEL_KD;
    }


    /**
     * Configura los parámetros de Motion Magic para el motor de giro.
     * <ul>
     *   <li>Motor de giro: vel. crucero, aceleración, jerk y parámetros Expo</li>
     * </ul>
     */
    private void configureMotionMagic() {
        // Motion Magic en el motor de giro.
        var spinMM = spinConfig.MotionMagic;
        spinMM.MotionMagicAcceleration = ShooterConstants.MAGIC_MOTION_VELOCITY_ACCELERATION_STR;
        spinMM.MotionMagicJerk = ShooterConstants.MAGIC_MOTION_VELOCITY_JERK_STR;
    }

    // --------------------------------------------------------------------
    // FUNCIONES
    // --------------------------------------------------------------------

    /**
     * Establece la velocidad lineal deseada del shooter.
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
}
