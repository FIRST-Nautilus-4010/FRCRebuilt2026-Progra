package frc.robot.subsystems.climber;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.climber.commands.Move;

// TODO comentarios de la clase.

public class Climber extends SubsystemBase{
    private final ClimberIO io;
    private final ClimberController controller;

    public Climber() {
        this.io = new ClimberIO();
        this.controller = new ClimberController(io.getElevatorRight(),io.getClawMotor());
    }

    public Command riseCommand() {
        return new Move(ClimberConstants.RISE_ELEVATOR_POSITION, ClimberConstants.RISE_CLAW_POSITION, controller, io, this);
    }

    public Command extendCommand() {
        return new Move(ClimberConstants.EXTEND_ELEVATOR_POSITION, ClimberConstants.EXTEND_CLAW_POSITION, controller, io, this);
    }

    public Command stowCommand() {
        return new Move(ClimberConstants.STOW_ELEVATOR_POSITION, ClimberConstants.STOW_CLAW_POSITION, controller, io, this);
    }

    public Command pullCommand() {
        return new Move(ClimberConstants.PULL_ELEVATOR_POSITION, ClimberConstants.PULL_CLAW_POSITION, controller, io, this);
    }

    public Command stopCommand() {
        return new InstantCommand(() -> io.stopMotors(), this);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Elevator position", io.getElevatorPosition());
        SmartDashboard.putNumber("Claw position", io.getClawPosition());
    }
}
