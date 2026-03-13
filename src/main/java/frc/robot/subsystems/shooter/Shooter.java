package frc.robot.subsystems.shooter;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.shooter.commands.SetVel;

// TODO comentarios de la clase.

public class Shooter extends SubsystemBase{
    private final ShooterIO io;
    private final ShooterController controller;

    public Shooter() {
        this.io = new ShooterIO();
        this.controller = new ShooterController(io.getSpinMotor(), io.getSpinMotorSecondary());
    }

    public Command shootCommand(Supplier<Double> distance) {
        return new SetVel(this, controller, io, distance);
    }

    public Command releaseCommand() {
        return new SetVel(this, controller, io, ShooterConstants.RELEASE_VELOCITY);
    }

    public Command stopCommand() {
        return new SetVel(this, controller, io, 0);
    }
}
