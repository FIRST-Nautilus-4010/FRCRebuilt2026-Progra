package frc.robot.subsystems.shooter.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterController;
import frc.robot.subsystems.shooter.ShooterIO;

public class SetVel extends Command {
    private final ShooterController controller;
    private final ShooterIO io;
    private final double velocity;

    public SetVel(Shooter shooter, ShooterController controller, ShooterIO io, double velocity) {
        this.controller = controller;
        this.io = io;
        this.velocity = velocity;
        addRequirements(shooter);
    }

    @Override
    public void execute() {
        controller.setVelocity(velocity);
    }

    @Override
    public boolean isFinished() {
        return velocity == 0 || Math.abs(io.getSpinVelocityRPS() - velocity) < 0.1;
    }    
}
