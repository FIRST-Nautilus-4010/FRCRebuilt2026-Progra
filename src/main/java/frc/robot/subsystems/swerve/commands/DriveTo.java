package frc.robot.subsystems.swerve.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.SubsystemManager;

public class DriveTo extends Command {

    private final SubsystemManager manager;
    private final Pose2d target;

    public DriveTo(SubsystemManager manager, Pose2d target) {
        this.manager = manager;
        this.target = target;
    }

    @Override
    public void initialize() {

        manager.targetPose = target;

        manager.assistX = true;
        manager.assistY = true;
        manager.assistTheta = true;
    }

    @Override
    public boolean isFinished() {

        var pose = manager.getPoseTracker().getPose();

        double error =
            pose.getTranslation().getDistance(target.getTranslation());

        return error < 0.15; // 15 cm
    }

    @Override
    public void end(boolean interrupted) {

        manager.assistX = false;
        manager.assistY = false;
        manager.assistTheta = false;
    }
}