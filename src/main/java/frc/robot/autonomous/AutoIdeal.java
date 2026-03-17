package frc.robot.autonomous;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;

import frc.robot.SubsystemManager;
import frc.robot.subsystems.swerve.commands.DriveTo;
import frc.robot.RobotState;

public class AutoIdeal extends SequentialCommandGroup {

    public AutoIdeal(SubsystemManager manager) {

        Pose2d initialPose = manager.getPoseTracker().getPose();

        // Determinar si estamos en el lado superior del campo (y > 4.033)
        boolean isUpperSide = initialPose.getY() > 4.033;
        
        // Si estamos en el lado superior, reflejamos alrededor del centro (4.033 * 2 = 8.066)
        // Si estamos en el lado inferior, no hay offset
        double sideOffset = isUpperSide ? -8.066 : 0;
        
        // Multiplicador para reflejar la rotación simétricamente
        // Si estamos arriba, multiplicamos por -1 para invertir el ángulo
        double rotationMultiplier = isUpperSide ? -1 : 1;

        Pose2d bumpPoseOut = new Pose2d(5.869, 2.355 + sideOffset, Rotation2d.fromDegrees(0 * rotationMultiplier));
        Pose2d bumpPoseIn = new Pose2d(3.477, 2.355 + sideOffset, Rotation2d.fromDegrees(0 * rotationMultiplier));

        Pose2d trenchPoseOut = new Pose2d(5.765, 0.613 + sideOffset, Rotation2d.fromDegrees(0 * rotationMultiplier));
        Pose2d trenchPoseIn = new Pose2d(3.426, 0.613 + sideOffset, Rotation2d.fromDegrees(0 * rotationMultiplier));

        Pose2d humanPose = new Pose2d(0.694, 0.675 + sideOffset, Rotation2d.fromDegrees(180 * rotationMultiplier));

        Pose2d prepareForIntakePos = new Pose2d(6.909, 1.262 + sideOffset, Rotation2d.fromDegrees(90 * rotationMultiplier));
        Pose2d intakePos = new Pose2d(8.043, 2.417 + sideOffset, Rotation2d.fromDegrees(90 * rotationMultiplier));
        
        addCommands(
            // Disparar los 8 fuel iniciales
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL)),
            new DriveTo(manager, new Pose2d(3.230, initialPose.getY(), initialPose.getRotation())),
            new InstantCommand(() -> manager.scheduleState(RobotState.SHOOT)),
            new WaitCommand(1.0),

            // Regresar al estado travel
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL)),

            // Salir del la aliance zone
            (initialPose.getY() > 4.033 && initialPose.getY() < 6.77) 
                ? new DriveTo(manager, bumpPoseIn)
                    .andThen(new DriveTo(manager, bumpPoseOut))
                : new DriveTo(manager, trenchPoseIn)
                    .andThen(new DriveTo(manager, trenchPoseOut)),
            

            // Recoger los fuel de la neutral zone
            new InstantCommand(() -> manager.scheduleState(RobotState.INTAKE)),
            new DriveTo(manager, prepareForIntakePos),
            new DriveTo(manager, intakePos),

            // Regresar al estado travel
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL)),

            // Ir a la zona de human para disparar los fuel recogidos
            new DriveTo(manager, trenchPoseOut),
            new DriveTo(manager, trenchPoseIn),
            new InstantCommand(() -> manager.scheduleState(RobotState.SHOOT)),
            new WaitCommand(3.0),

            // Regresar al estado travel
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL)),

            //--------------- Repetir el proceso ---------------

            // Salir del la aliance zone
            new DriveTo(manager, trenchPoseIn),
            new DriveTo(manager, trenchPoseOut),
            

            // Recoger los fuel de la neutral zone
            new InstantCommand(() -> manager.scheduleState(RobotState.INTAKE)),
            new DriveTo(manager, prepareForIntakePos),
            new DriveTo(manager, intakePos),

            // Regresar al estado travel
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL)),

            // Ir a la zona de human para disparar los fuel recogidos y los que ingrese el human
            new DriveTo(manager, trenchPoseOut),
            new DriveTo(manager, trenchPoseIn),
            new DriveTo(manager, humanPose),
            new InstantCommand(() -> manager.scheduleState(RobotState.SHOOT)),
            new WaitCommand(3.0),

            // Regresar al estado travel
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL))
        );
    }
}