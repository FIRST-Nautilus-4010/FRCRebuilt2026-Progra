// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.swerve.commands;

import java.util.function.Supplier;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.AutonomousConstants;
import frc.robot.subsystems.swerve.PoseTracker;
import frc.robot.subsystems.swerve.Swerve;

/**
 * Comando que conduce el robot desde su pose actual hasta una pose objetivo
 * usando {@link HolonomicDriveController}.
 *
 * Controla X, Y y ángulo (theta) con PID, respetando velocidades máximas y
 * tolerancias definidas en {@link AutonomousConstants}.
 */
public class RotateTo extends Command {

  /** Pose objetivo a alcanzar. */
  private final Pose2d target;

  /** Subsistema swerve que se controla. */
  private final Swerve swerve;

  /** Fuente de la pose actual del robot (odometría + visión). */
  private final PoseTracker poseTracker;

  /** Controlador holonómico (X, Y, theta). */
  private final HolonomicDriveController controller;

  /**
   * Crea un comando para ir a una pose concreta.
   *
   * @param target      pose objetivo (X, Y, θ)
   * @param swerve      subsistema swerve
   * @param poseTracker proveedor de la pose actual del robot
   */
  public RotateTo(Rotation2d rotation, Swerve swerve, PoseTracker poseTracker) {
    this.target = new Pose2d(poseTracker.getPose().getX(), poseTracker.getPose().getY(), rotation);
    this.swerve = swerve;
    this.poseTracker = poseTracker;

    // Controlador de rotación con límites de velocidad/aceleración (ProfiledPID).
    ProfiledPIDController thetaController =
        new ProfiledPIDController(
            AutonomousConstants.P_Z,
            AutonomousConstants.I_Z,
            AutonomousConstants.D_Z,
            AutonomousConstants.Z_CONTROLER);

    // Permite que el ángulo "envuelva" de -π a π sin saltos grandes.
    thetaController.enableContinuousInput(-Math.PI, Math.PI);

    // Controladores para X, Y y theta.
    this.controller = new HolonomicDriveController(
        new PIDController(
            0,
            0,
            0),
        new PIDController(
            0,
            0,
            0),
        thetaController);

    // Declara que este comando usa el subsistema swerve.
    addRequirements(swerve);
  }

  @Override
  public void execute() {
    // Calcula las velocidades de chasis necesarias para ir desde la pose actual a la objetivo.
    var currentPose = poseTracker.getPose();

    var desiredPose = new Pose2d(
        target.getX(),
        target.getY(),
        target.getRotation());

    var chassisSpeeds = controller.calculate(
        currentPose,
        desiredPose,
        AutonomousConstants.MAX_SPD,
        target.getRotation());
    
    // Comando de conducción en coordenadas de campo (normal).
    swerve.driveFieldRelative(
        0,
        0,
        chassisSpeeds.omegaRadiansPerSecond
      );
  }

  @Override
  public void end(boolean interrupted) {
    // Al terminar (o ser interrumpido), detener los módulos.
    swerve.stopModules();
  }

  @Override
  public boolean isFinished() {
    // Termina cuando estamos dentro de las tolerancias en posición y ángulo.
    var currentPose = poseTracker.getPose();

    boolean angleOk =
        Math.abs(currentPose.getRotation().getRadians() - target.getRotation().getRadians())
            < AutonomousConstants.ANG_TOLERANCE;

    boolean velOk = swerve.getAverageWheelSpeed() < 1;
    
    return false;
  }
}
