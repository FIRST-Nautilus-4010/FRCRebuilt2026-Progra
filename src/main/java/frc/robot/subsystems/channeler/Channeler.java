package frc.robot.subsystems.channeler;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// TODO comentarios de la clase.

public class Channeler extends SubsystemBase{
    private final ChannelerIO io;
    private final ChannelerController controller;

    public Channeler() {
        this.io = new ChannelerIO();
        this.controller = new ChannelerController(io.getSpinMotor(), io.getSpinMotorSecondary());
    }

    public Command feedCommand() {
        return new InstantCommand(() -> controller.setVelocity(ChannelerConstants.FEED_VELOCITY), this);
    }

    public Command releaseCommand() {
        return new InstantCommand(() -> controller.setVelocity(ChannelerConstants.RELEASE_VELOCITY), this);
    }

    public Command stopCommand() {
        return new InstantCommand(() -> io.stopMotors(), this);
    }
}
