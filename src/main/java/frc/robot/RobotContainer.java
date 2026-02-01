// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

/**
 * Clase central de configuración del robot.
 *
 * Para este proyecto, solo se configura lo necesario para mover el chasis
 * swerve con un control Xbox.
 */
public class RobotContainer {

  /** Joystick del driver (puerto 0). */
  private final XboxController driverJoystick;

  /** Gestor de subsistemas y estados del robot. */
  private final SubsystemManager subsystemManager;

  /**
   * Crea el contenedor del robot.
   *
   * Configura los subsistemas, comandos y bindings.
   */
  public RobotContainer() {

    this.driverJoystick = new XboxController(0);
    this.subsystemManager = new SubsystemManager();

    configureBindings();

    // Inicializar el estado del robot
    subsystemManager.initialize();
  }

  /**
   * Configura los bindings de comandos:
   * <ul>
   *   <li>Controles de conducción para el estado {@link RobotState#TRAVEL}.</li>
   * </ul>
   */
  private void configureBindings() {
    // Configurar controles para el estado TRAVEL
    subsystemManager.configureTravelControls(
        () -> -driverJoystick.getLeftY(),
        () -> -driverJoystick.getLeftX(),
        () -> -driverJoystick.getRightX(),
        () -> !driverJoystick.getRightBumperButton(),
        () -> driverJoystick.getAButton()
    );
  }

  /**
   * Comando autónomo por defecto.
   * <p>
   * De momento no se ha configurado un autónomo real, así que devuelve
   * un {@link InstantCommand} vacío.
   */
  public Command getAutonomousCommand() {
    return new InstantCommand();
  }

  /**
   * Debe ser llamado desde Robot.robotPeriodic()
   * 
   * Actualiza el subsystem manager y la estimación de pose.
   */
  public void periodic() {
    subsystemManager.periodic();
  }
}
