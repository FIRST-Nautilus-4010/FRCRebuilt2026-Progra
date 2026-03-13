package frc.robot.subsystems.shooter.commands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterController;
import frc.robot.subsystems.shooter.ShooterIO;

public class SetVel extends Command {
    private final ShooterController controller;
    private final ShooterIO io;
    private Double velocity;
    private final Supplier<Double> distance;


    public SetVel(Shooter shooter, ShooterController controller, ShooterIO io, double velocity) {
        this.controller = controller;
        this.io = io;
        this.velocity = velocity;
        distance = null;
        addRequirements(shooter);
    }

    public SetVel(Shooter shooter, ShooterController controller, ShooterIO io, Supplier<Double> distance) {
        this.controller = controller;
        this.io = io;
        this.velocity = 0.0;
        this.distance = distance;
        addRequirements(shooter);
    }

    @Override
    public void execute() {
        if (distance != null) {
            double x = distance.get();

            velocity = 2.11384 * Math.pow(x, 2) - 9.66687 * x + 60.05155;
        }

        controller.setVelocity(velocity);
    }

    @Override
    public boolean isFinished() {
        return velocity == 0 || Math.abs(io.getSpinVelocityRPS() - velocity) < 0.8;
    }    
}
