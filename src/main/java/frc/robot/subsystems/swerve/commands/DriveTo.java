package frc.robot.subsystems.swerve.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.SubsystemManager;
import frc.robot.subsystems.swerve.PoseTracker;

public class DriveTo extends Command {

    private final SubsystemManager manager;
    private final PoseTracker poseTracker;
    private final Pose2d target;
    private final boolean velocityHeadingEnabled;

    public DriveTo(SubsystemManager manager, Pose2d target, boolean velocityHeadingEnabled) {
        this.manager = manager;
        this.poseTracker = null;
        this.target = target;
        this.velocityHeadingEnabled = velocityHeadingEnabled;
    }

    public DriveTo(SubsystemManager manager, Pose2d target) {
        this.manager = manager;
        this.poseTracker = null;
        this.target = target;
        this.velocityHeadingEnabled = false;
    }

    public DriveTo(PoseTracker poseTracker, Pose2d target) {
        this.poseTracker = poseTracker;
        this.manager = null;
        this.target = target;
        this.velocityHeadingEnabled = false;
    }

    @Override
    public void initialize() {

        Drive.targetPose = target;

        Drive.assistX = true;
        Drive.assistY = true;
        Drive.assistTheta = true;
        Drive.velocityHeadingEnabled = velocityHeadingEnabled;
    }

    @Override
    public boolean isFinished() {
        Pose2d pose;
        if (manager == null) {
            pose = poseTracker.getPose();
        } else {
            pose = manager.getPoseTracker().getPose();
        }

        double error =
            pose.getTranslation().getDistance(target.getTranslation());

        return error < 0.15; // 15 cm
    }

    @Override
    public void end(boolean interrupted) {

        Drive.assistX = false;
        Drive.assistY = false;
        Drive.assistTheta = false;
        Drive.velocityHeadingEnabled = false;
    }
}