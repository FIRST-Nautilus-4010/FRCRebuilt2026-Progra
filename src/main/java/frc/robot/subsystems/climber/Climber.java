package frc.robot.subsystems.climber;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.climber.commands.Move;

/**
 * Subsistema de escalada del robot.
 *
 * Gestiona el ascenso y manipulación del robot en las cadenas de escalada
 * mediante control de motor del elevador y la garra. Proporciona comandos
 * predefinidos para las etapas de escalada: rise, extend, pull y stow.
 */
public class Climber extends SubsystemBase{
    
    /** Interfaz I/O del subsistema. */
    private final ClimberIO io;
    /** Controlador del elevador y la garra. */
    private final ClimberController controller;

    /**
     * Crea el subsistema Climber.
     *
     * Inicializa la interfaz de hardware y el controlador con referencias
     * a los motores del elevador y la garra.
     */
    public Climber() {
        this.io = new ClimberIO();
        this.controller = new ClimberController(io.getElevatorRight(), io.getClawMotor());
    }

    /**
     * Comando para la primera etapa de escalada (subida).
     * 
     * Posiciona el elevador y la garra según {@link ClimberConstants#RISE_ELEVATOR_POSITION}
     * y {@link ClimberConstants#RISE_CLAW_POSITION}.
     *
     * @return Comando de movimiento a posición de subida
     */
    public Command riseCommand() {
        return new Move(ClimberConstants.RISE_ELEVATOR_POSITION, ClimberConstants.RISE_CLAW_POSITION, controller, io, this);
    }

    /**
     * Comando para la segunda etapa de escalada (extensión).
     * 
     * Posiciona el elevador y la garra según {@link ClimberConstants#EXTEND_ELEVATOR_POSITION}
     * y {@link ClimberConstants#EXTEND_CLAW_POSITION}.
     *
     * @return Comando de movimiento a posición extendida
     */
    public Command extendCommand() {
        return new Move(ClimberConstants.EXTEND_ELEVATOR_POSITION, ClimberConstants.EXTEND_CLAW_POSITION, controller, io, this);
    }

    /**
     * Comando para retraer y guardar el subsistema de escalada.
     * 
     * Retorna el elevador y la garra a posición de reposo según
     * {@link ClimberConstants#STOW_ELEVATOR_POSITION} y {@link ClimberConstants#STOW_CLAW_POSITION}.
     *
     * @return Comando de movimiento a posición de reposo
     */
    public Command stowCommand() {
        return new Move(ClimberConstants.STOW_ELEVATOR_POSITION, ClimberConstants.STOW_CLAW_POSITION, controller, io, this);
    }

    /**
     * Comando para la etapa final de escalada (tracción).
     * 
     * Posiciona el elevador y la garra según {@link ClimberConstants#PULL_ELEVATOR_POSITION}
     * y {@link ClimberConstants#PULL_CLAW_POSITION}.
     *
     * @return Comando de movimiento a posición de tracción
     */
    public Command pullCommand() {
        return new Move(ClimberConstants.PULL_ELEVATOR_POSITION, ClimberConstants.PULL_CLAW_POSITION, controller, io, this);
    }

    /**
     * Comando para detener todos los motores del subsistema.
     *
     * @return Comando instantáneo que detiene el elevador y la garra
     */
    public Command stopCommand() {
        return new InstantCommand(() -> io.stopMotors(), this);
    }

    /**
     * Actualiza el subsistema periódicamente.
     * 
     * Publica las posiciones actuales del elevador y la garra a SmartDashboard
     * para telemetría y debugging.
     */
    @Override
    public void periodic() {
        SmartDashboard.putNumber("Elevator position", io.getElevatorPosition());
        SmartDashboard.putNumber("Claw position", io.getClawPosition());
    }
}
