package frc.robot.subsystems.climber.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.climber.ClimberController;
import frc.robot.subsystems.climber.ClimberIO;
import frc.robot.subsystems.climber.Climber;

// TODO Comentarios de la clase.

public class Move extends Command {
  ClimberController controller;
  ClimberIO io;
  double elevatorPosition;
  double clawPosition;

  public Move(double elevatorPosition, double clawPosition, ClimberController controller, ClimberIO io, Climber climber) {
    this.elevatorPosition = elevatorPosition;
    this.clawPosition = clawPosition;
    this.controller = controller;
    this.io = io;

    addRequirements(climber);
  }

  @Override
  public void execute() {
    controller.setPositionClaw(clawPosition);
    controller.setPositionElevator(elevatorPosition);
  }

  @Override
  public boolean isFinished() {
    return (io.getElevatorPosition() - elevatorPosition) < 0.05 &&
            Math.abs(io.getClawPosition() - clawPosition) < 0.05;
  }
}
