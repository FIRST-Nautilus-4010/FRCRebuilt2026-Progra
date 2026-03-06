package frc.robot.subsystems.shooter;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// TODO comentarios de la clase.

public class Shooter extends SubsystemBase{
    private final ShooterIO io;
    private final ShooterController controller;

    public Shooter() {
        this.io = new ShooterIO();
        this.controller = new ShooterController(io.getSpinMotor(), io.getSpinMotorSecondary());
    }

    public Command shootCommand() {
        return new InstantCommand(() -> controller.setVelocity(ShooterConstants.SHOOT_VELOCITY), this);
    }

    public Command releaseCommand() {
        return new InstantCommand(() -> controller.setVelocity(ShooterConstants.RELEASE_VELOCITY), this);
    }

    public Command stopCommand() {
        return new InstantCommand(() -> io.stopMotors(), this);
    }
}
