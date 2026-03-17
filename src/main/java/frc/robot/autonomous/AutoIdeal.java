package frc.robot.autonomous;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;

import java.util.List;

import frc.robot.SubsystemManager;
import frc.robot.subsystems.swerve.commands.DrivePath;
import frc.robot.utils.PathUtil;
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

        List<Pose2d> circularIntakePath = PathUtil.generateSemiCircle(intakePos.getX(), intakePos.getY(), 20, 0.5);
        circularIntakePath.set(0, prepareForIntakePos);
        
        addCommands(
            // Disparar los 8 fuel iniciales
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL)),
            new DrivePath(manager, List.of(new Pose2d(3.230, initialPose.getY(), initialPose.getRotation()))),
            new InstantCommand(() -> manager.scheduleState(RobotState.SHOOT)),
            new WaitCommand(1.0),

            // Regresar al estado travel
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL)),

            // Salir del la aliance zone
            // Añadimos prepareForIntakePos al final del camino para continuidad
            (initialPose.getY() > 4.033 && initialPose.getY() < 6.77) 
                ? new DrivePath(manager, List.of(bumpPoseIn, bumpPoseOut, prepareForIntakePos))
                : new DrivePath(manager, List.of(trenchPoseIn, trenchPoseOut, prepareForIntakePos)),
            

            // Recoger los fuel de la neutral zone
            new InstantCommand(() -> manager.scheduleState(RobotState.INTAKE)),
            // Combinamos el camino de preparación, la entrada y añadimos el siguiente punto (trenchPoseOut)
            // para mantener continuidad al cambiar a TRAVEL después de la toma.
            new DrivePath(manager, List.of(prepareForIntakePos, intakePos, trenchPoseOut)),

            // Regresar al estado travel
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL)),

            // Ir a la zona de human para disparar los fuel recogidos
            new DrivePath(manager, List.of(trenchPoseOut, trenchPoseIn)),
            new InstantCommand(() -> manager.scheduleState(RobotState.SHOOT)),
            new WaitCommand(3.0),

            // Regresar al estado travel
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL)),

            //--------------- Repetir el proceso ---------------

            // Salir del la aliance zone
            new DrivePath(manager,
                List.of(
                    trenchPoseIn,
                    trenchPoseOut,
                    prepareForIntakePos
                )
            ),

            // Recoger los fuel de la neutral zone
            new InstantCommand(() -> manager.scheduleState(RobotState.INTAKE)),
            new DrivePath(manager, circularIntakePath, true),

            // Regresar al estado travel
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL)),

            // Ir a la zona de human para disparar los fuel recogidos y los que ingrese el human
            // Agrupamos las poses en un solo DrivePath (no añadimos la siguiente pose después de SHOOT)
            new DrivePath(manager, List.of(trenchPoseOut, trenchPoseIn, humanPose)),
            new InstantCommand(() -> manager.scheduleState(RobotState.SHOOT)),
            new WaitCommand(3.0),

            // Regresar al estado travel
            new InstantCommand(() -> manager.scheduleState(RobotState.TRAVEL))
        );
    }
}