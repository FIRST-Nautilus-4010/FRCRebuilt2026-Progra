package frc.robot.subsystems.intake;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.intake.commands.Move;

// TODO comentarios de la clase.

public class Intake extends SubsystemBase{
    private final IntakeIO io;
    private final IntakeController controller;

    public Intake() {
        this.io = new IntakeIO();
        this.controller = new IntakeController(io.getspinBackMotor(), io.getspinFrontMotor(), io.getPivotMotor());
    }

    public Command grabCommand() {
        return new Move(IntakeConstants.GRAB_ANGLE_RAD, IntakeConstants.GRAB_SPIN_RPS, controller, io, this);
    }

    public Command releaseCommand() {
        return new Move(IntakeConstants.RELEASE_ANGLE_RAD, IntakeConstants.RELEASE_SPIN_RPS, controller, io, this);
    }

    public Command stowCommand() {
        return new Move(IntakeConstants.STOW_ANGLE_RAD, IntakeConstants.STOW_SPIN_RPS, controller, io, this);
    }

    public Command stopCommand() {
        return new InstantCommand(() -> io.stopMotors(), this);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Intake position (RAD)", io.getPivotPositionRad());
        SmartDashboard.putNumber("Intake velocity (RPS)", io.getSpinVelocityRPS());
    }
}
