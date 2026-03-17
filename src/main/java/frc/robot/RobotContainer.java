// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.autonomous.AutoIdeal;

/**
 * Contenedor central de configuración del robot.
 *
 * Gestiona la inicialización de subsistemas, configuración de controles del operador
 * y bindings de comandos. Coordina la comunicación entre los inputs del joystick y
 * las transiciones de estado del robot.
 */
public class RobotContainer {

  /** Controlador Xbox del driver (puerto 0). */
  private final XboxController driverJoystick;

  /** Gestor centralizado de subsistemas y estados del robot. */
  private final SubsystemManager subsystemManager;

  /**
   * Crea e inicializa el contenedor del robot.
   *
   * Instancia el gestor de subsistemas, configura los bindings de comandos
   * y establece el estado inicial del robot.
   */
  public RobotContainer() {

    this.driverJoystick = new XboxController(0);
    this.subsystemManager = new SubsystemManager();

    configureBindings();

    subsystemManager.initialize();
  }

  /**
   * Inicializa el robot en modo teleoperado.
   * 
   * Establece el estado inicial a TRAVEL para permitir la conducción normal.
   */
  public void initializeTeleOp() {
    subsystemManager.executeState(RobotState.TRAVEL);
  }

  /**
   * Configura los bindings de comandos del operador.
   * 
   * Mapea los inputs del controlador Xbox a transiciones de estado y controles
   * de conducción:
   * <ul>
   *   <li><b>Joystick izquierdo:</b> Velocidades X e Y (TRAVEL)</li>
   *   <li><b>Joystick derecho X:</b> Velocidad angular (TRAVEL)</li>
   *   <li><b>Botón A:</b> Reset del giroscopio</li>
   *   <li><b>Botón Y:</b> Estado TEST</li>
   *   <li><b>Botón X:</b> Estado TRAVEL</li>
   *   <li><b>Botón B:</b> Estado CLIMB</li>
   *   <li><b>Trigger izquierdo:</b> Estado INTAKE</li>
   *   <li><b>Trigger derecho:</b> Estado SHOOT</li>
   * </ul>
   * 
   * Los triggers se invierten según la alianza para una conducción intuitiva.
   */
  private void configureBindings() {
    final double sideMultiplier = DriverStation.getAlliance().get() == DriverStation.Alliance.Red ? -1.0 : 1.0;
    
    subsystemManager.configureTravelControls(
        () -> -driverJoystick.getLeftY() * sideMultiplier,
        () -> -driverJoystick.getLeftX() * sideMultiplier,
        () -> -driverJoystick.getRightX(),
        () -> driverJoystick.getAButton()
    );

    Trigger testTrigger = new Trigger(() -> driverJoystick.getYButton());
    testTrigger.onTrue(new InstantCommand(() -> subsystemManager.executeState(RobotState.TEST)));

    Trigger intakeTrigger = new Trigger(() -> driverJoystick.getLeftTriggerAxis() > 0.5);
    intakeTrigger.onTrue(new InstantCommand(() -> subsystemManager.executeState(RobotState.INTAKE)));

    Trigger shootTrigger = new Trigger(() -> driverJoystick.getRightTriggerAxis() > 0.5);
    shootTrigger.onTrue(new InstantCommand(() -> subsystemManager.executeState(RobotState.SHOOT)));

    Trigger climbTrigger = new Trigger(() -> driverJoystick.getBButton());
    climbTrigger.onTrue(new InstantCommand(() -> subsystemManager.executeState(RobotState.CLIMB)));

    Trigger travelTrigger = new Trigger(() -> driverJoystick.getXButton());
    travelTrigger.onTrue(new InstantCommand(() -> subsystemManager.executeState(RobotState.TRAVEL)));
  }  /**
   * Retorna el comando autónomo a ejecutar.
   * 
   * Proporciona la rutina de autonomía del robot para la fase autónoma
   * de la competencia.
   *
   * @return Comando autónomo a ejecutar
   */
  public Command getAutonomousCommand() {
    return new AutoIdeal(subsystemManager);
  }

  /**
   * Actualiza el estado periódico del robot.
   * 
   * Debe ser llamado desde {@code Robot.robotPeriodic()}. Actualiza todos
   * los subsistemas y la estimación de pose del robot.
   */
  public void periodic() {
    subsystemManager.periodic();
  }

  /**
   * Desactiva el robot y detiene todos los subsistemas.
   * 
   * Debe ser llamado desde {@code Robot.disabledInit()}. Cancela comandos
   * activos y desactiva los sistemas de conducción.
   */
  public void disable() {
    subsystemManager.disable();
  }

}
