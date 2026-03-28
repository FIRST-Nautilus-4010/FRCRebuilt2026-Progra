package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import frc.robot.Constants.AutonomousConstants;
import frc.robot.subsystems.swerve.PoseTracker;

public class Auto {
    RobotConfig config;
    PoseTracker poseTracker;

    public Auto (SubsystemManager subsystemManager) {
        this.poseTracker = subsystemManager.getPoseTracker();

        try{
            config = RobotConfig.fromGUISettings();
        } catch (Exception e) {
            // Handle exception as needed
            e.printStackTrace();
        }

        AutoBuilder.configure(
            poseTracker::getPose, // Robot pose supplier
            poseTracker::resetOdometry, // Method to reset odometry (will be called if your auto has a starting pose)
            poseTracker.getSwerve()::getChassisSpeed, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
            (speeds, feedforwards) -> poseTracker.getSwerve().drive(speeds), // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds. Also optionally outputs individual module feedforwards
            new PPHolonomicDriveController( // PPHolonomicController is the built in path following controller for holonomic drive trains
                    new PIDConstants(AutonomousConstants.P_X, AutonomousConstants.I_X, AutonomousConstants.D_X), // Translation PID constants
                    new PIDConstants(360, AutonomousConstants.I_Z, AutonomousConstants.D_Z) // Rotation PID constants
            ),
            config,
            () -> {

              var alliance = DriverStation.getAlliance();
              if (alliance.isPresent()) {
                return alliance.get() == DriverStation.Alliance.Red;
              }
              return false;
            },
            poseTracker.getSwerve()
        );
      
      NamedCommands.registerCommand("Travel", new InstantCommand(() -> subsystemManager.scheduleState(RobotState.TRAVEL)));
      NamedCommands.registerCommand("Intake", new PrintCommand("Intake inicializado (No se mueve porque se rompe)"));
      NamedCommands.registerCommand("Shoot", new InstantCommand(() -> subsystemManager.scheduleState(RobotState.SHOOT)));
      NamedCommands.registerCommand("Climb", new InstantCommand(() -> subsystemManager.scheduleState(RobotState.CLIMB)));
    }

    public Command getCommand(String autoName) {
        return new PathPlannerAuto(autoName);
    }
    
}