package frc.robot.subsystems.intake.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.intake.IntakeController;
import frc.robot.subsystems.intake.IntakeIO;

// TODO Comentarios de la clase.

public class Move extends Command {
  IntakeController controller;
  IntakeIO io;
  double angle;
  double spinVelocity;

  public Move(double angle, double spinVelocity, IntakeController controller, IntakeIO io, Intake intake) {
    this.angle = angle;
    this.spinVelocity = spinVelocity;
    this.controller = controller;
    this.io = io;

    addRequirements(intake);
  }

  @Override
  public void execute() {
    controller.setAngle(angle);
    controller.setVelocity(spinVelocity);
  }

  @Override
  public boolean isFinished() {
    return (io.getPivotPositionRad() - angle) < 0.05 &&
            Math.abs(io.getSpinVelocityRPS() - spinVelocity) < 0.1;
  }
}
