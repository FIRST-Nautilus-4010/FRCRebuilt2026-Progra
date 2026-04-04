package frc.robot.subsystems.intake;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.intake.commands.Move;

/**
 * Subsistema de ingesta de fuel del robot.
 *
 * Gestiona el pivote del intake y los motores de giro (spinners) para
 * recoger y expulsar fuel. Proporciona comandos predefinidos para las
 * operaciones de grabbing, release y stow.
 */
public class Intake extends SubsystemBase{
    
    /** Interfaz I/O del subsistema. */
    private final IntakeIO io;
    /** Controlador del pivote y spinners. */
    private final IntakeController controller;

    /**
     * Crea el subsistema Intake.
     *
     * Inicializa la interfaz de hardware y el controlador con referencias
     * a los motores de giro (principal y secundario) y el pivote.
     */
    public Intake() {
        this.io = new IntakeIO();
        this.controller = new IntakeController(io.getspinMotor(), io.getPivotMotor());
    }

    /**
     * Comando para recoger una nota (grab).
     * 
     * Posiciona el pivote y configura velocidades según 
     * {@link IntakeConstants#GRAB_ANGLE_RAD} y {@link IntakeConstants#GRAB_SPIN_RPS}.
     *
     * @return Comando de movimiento a posición de recogida
     */
    public Command grabCommand() {
        return new Move(IntakeConstants.GRAB_ANGLE_RAD, IntakeConstants.GRAB_SPIN_RPS, controller, io, this);
    }

    /**
     * Comando para expulsar una nota (release).
     * 
     * Posiciona el pivote y configura velocidades según 
     * {@link IntakeConstants#RELEASE_ANGLE_RAD} y {@link IntakeConstants#RELEASE_SPIN_RPS}.
     *
     * @return Comando de movimiento a posición de expulsión
     */
    public Command releaseCommand() {
        return new Move(IntakeConstants.RELEASE_ANGLE_RAD, IntakeConstants.RELEASE_SPIN_RPS, controller, io, this);
    }

    /**
     * Comando para establecer la velocidad máxima del intake.
     * 
     * Ajusta el limitador de velocidad del controlador.
     *
     * @param maxVel velocidad máxima permitida para el pivote
     * @return Comando instantáneo que aplica el límite
     */
    public Command setMaxVelocityCommand(double maxVel) {
        return new InstantCommand(() -> controller.setMaxVelocity(maxVel), this);
    }

    /**
     * Comando para guardar/retraer el intake (stow).
     * 
     * Posiciona el pivote y detiene los spinners según 
     * {@link IntakeConstants#STOW_ANGLE_RAD} y {@link IntakeConstants#STOW_SPIN_RPS}.
     *
     * @return Comando de movimiento a posición de reposo
     */
    public Command stowCommand() {
        return new Move(IntakeConstants.STOW_ANGLE_RAD, IntakeConstants.STOW_SPIN_RPS, controller, io, this);
    }

    /**
     * Comando de prueba para los motores de giro.
     * 
     * Mantiene el pivote en su posición actual y ejecuta los spinners
     * a velocidad de grabbing para testing.
     *
     * @return Comando de movimiento para prueba de spinners
     */
    public Command testRollersCommand() {
        return new Move(io.getPivotPositionRad(), IntakeConstants.GRAB_SPIN_RPS, controller, io, this);
    }

    /**
     * Comando para detener todos los motores del subsistema.
     *
     * @return Comando instantáneo que detiene pivote y spinners
     */
    public Command stopCommand() {
        return new InstantCommand(() -> io.stopMotors(), this);
    }

    /**
     * Comando para resetear el limitador de velocidad al máximo.
     *
     * @return Comando instantáneo que reinicia el límite de velocidad
     */
    public Command resetMaxVelocityCommand() {
        return new InstantCommand(() -> controller.resetMaxVelocity(), this);
    }

    /**
     * Actualiza el subsistema periódicamente.
     * 
     * Publica la posición del pivote y velocidad de los spinners a SmartDashboard
     * para telemetría y debugging.
     */
    @Override
    public void periodic() {
        SmartDashboard.putNumber("Intake position (RAD)", io.getPivotPositionRad());
        SmartDashboard.putNumber("Intake velocity (RPS)", io.getSpinVelocityRPS());
    }
}
