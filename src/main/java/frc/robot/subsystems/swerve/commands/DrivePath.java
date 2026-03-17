package frc.robot.subsystems.swerve.commands;

import java.util.List;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.SubsystemManager;

public class DrivePath extends Command {

    private final SubsystemManager manager;
    private final List<Pose2d> path;
    private final boolean velocityHeadingEnabled;

    private int currentIndex = 0;

    private static final double TOLERANCE = 0.15; // 10 cm
    private static final double LOOKAHEAD_DISTANCE = 0.25; // meters, used for projection-based lookahead
    private static final double PROJECTION_ADVANCE_THRESHOLD = 0.99; // fraction along segment to advance index

    public DrivePath(SubsystemManager manager, List<Pose2d> path, boolean velocityHeadingEnabled) {
        this.manager = manager;
        this.path = path;
        this.velocityHeadingEnabled = velocityHeadingEnabled;
    }

    public DrivePath(SubsystemManager manager, List<Pose2d> path) {
        this.manager = manager;
        this.path = path;
        this.velocityHeadingEnabled = false;
    }

    @Override
    public void initialize() {

        currentIndex = 0;

        Drive.assistX = true;
        Drive.assistY = true;
        Drive.assistTheta = true;
        Drive.velocityHeadingEnabled = velocityHeadingEnabled;

        Drive.targetPose = path.get(0);
    }

    @Override
    public void execute() {

        Pose2d currentPose = manager.getPoseTracker().getPose();

        Pose2d currentTarget = path.get(currentIndex);
        Pose2d nextTarget = path.get(Math.min(currentIndex + 1, path.size() - 1));

        Translation2d curT = currentPose.getTranslation();
        Translation2d a = currentTarget.getTranslation();
        Translation2d b = nextTarget.getTranslation();

        double abx = b.getX() - a.getX();
        double aby = b.getY() - a.getY();
        double apx = curT.getX() - a.getX();
        double apy = curT.getY() - a.getY();

        double abLen2 = abx * abx + aby * aby;
        double tProj = 0.0;
        if (abLen2 > 1e-6) {
            tProj = (abx * apx + aby * apy) / abLen2;
        }
        tProj = Math.max(0.0, Math.min(1.0, tProj));

        double segmentLength = Math.sqrt(abLen2);
        double lookahead = LOOKAHEAD_DISTANCE;
        double tLook = tProj;
        if (segmentLength > 1e-6) {
            tLook = tProj + lookahead / segmentLength;
        }
        tLook = Math.max(0.0, Math.min(1.0, tLook));

        Pose2d interpolatedTarget = currentTarget.interpolate(nextTarget, tLook);
        Drive.targetPose = interpolatedTarget;

        // Advance to next waypoint when projected past almost the end of the segment
        // or when physically close to the next waypoint.
        double distToNext = curT.getDistance(b);
        if ((tProj > PROJECTION_ADVANCE_THRESHOLD || distToNext < TOLERANCE) && currentIndex < path.size() - 1) {
            currentIndex++;
        }
    }

    @Override
    public boolean isFinished() {

        Pose2d currentPose = manager.getPoseTracker().getPose();
        Pose2d finalTarget = path.get(path.size() - 1);

        double error = currentPose.getTranslation()
                .getDistance(finalTarget.getTranslation());

        return error < TOLERANCE && currentIndex == path.size() - 1;
    }

    @Override
    public void end(boolean interrupted) {

        Drive.assistX = false;
        Drive.assistY = false;
        Drive.assistTheta = false;
        Drive.velocityHeadingEnabled = false;
    }
}
