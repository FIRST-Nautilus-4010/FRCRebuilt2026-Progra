// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import choreo.Choreo;
import choreo.auto.AutoChooser;
import choreo.auto.AutoFactory;
import choreo.auto.AutoRoutine;
import choreo.auto.AutoTrajectory;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
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

  private AutoChooser autoChooser;

  private StructArrayPublisher<Pose2d> tArrayPublisher = NetworkTableInstance.getDefault()
                    .getStructArrayTopic("Auto Trajectory", Pose2d.struct)
                    .publish();

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

    configureAutoChooser();
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
   * Los triggers se invierten según la alianza (Red vs Blue) para una
   * conducción intuitiva en ambos lados del campo.
   */
  private void configureBindings() {
    // ====================================================================
    // CONFIGURACIÓN DE ALIANZA Y MULTIPLICADOR DE LADO
    // ====================================================================
    // Obtiene la alianza con manejo seguro de Optional
    boolean isRed = DriverStation.getAlliance()
        .map(alliance -> alliance == DriverStation.Alliance.Red)
        .orElse(false); // Default: Blue si no hay alianza disponible
    
    final double sideMultiplier = isRed ? -1.0 : 1.0;
    
    subsystemManager.configureTravelControls(
        () -> -driverJoystick.getLeftY() * sideMultiplier,
        () -> -driverJoystick.getLeftX() * sideMultiplier,
        () -> -driverJoystick.getRightX(),
        () -> driverJoystick.getAButton()
    );

    // ====================================================================
    // BOTONES DE TRANSICIÓN DE ESTADO
    // ====================================================================
    
    // Botón Y → Estado TEST (diagnóstico y prueba)
    Trigger testTrigger = new Trigger(() -> driverJoystick.getYButton());
    testTrigger.onTrue(new InstantCommand(() -> subsystemManager.executeState(RobotState.TEST)));

    // Trigger Izquierdo → Estado INTAKE (recolección de fuel)
    Trigger intakeTrigger = new Trigger(() -> driverJoystick.getLeftTriggerAxis() > 0.5);
    intakeTrigger.onTrue(new InstantCommand(() -> subsystemManager.executeState(RobotState.INTAKE)));

    // Trigger Derecho → Estado SHOOT (lanzamiento de fuel)
    Trigger shootTrigger = new Trigger(() -> driverJoystick.getRightTriggerAxis() > 0.5);
    shootTrigger.onTrue(new InstantCommand(() -> subsystemManager.executeState(RobotState.SHOOT)));

    // Botón B → Estado CLIMB (escalada)
    Trigger climbTrigger = new Trigger(() -> driverJoystick.getBButton());
    climbTrigger.onTrue(new InstantCommand(() -> subsystemManager.executeState(RobotState.CLIMB)));

    // Botón X → Estado TRAVEL (conducción normal)
    Trigger travelTrigger = new Trigger(() -> driverJoystick.getXButton());
    travelTrigger.onTrue(new InstantCommand(() -> subsystemManager.executeState(RobotState.TRAVEL)));
  }  
  
  private void configureAutoChooser() {
    // ====================================================================
    // CONFIGURACIÓN DE AUTONOMÍA CON CHOREO
    // ====================================================================
    
    // Obtiene la alianza con manejo seguro de Optional
    boolean isRed = DriverStation.getAlliance()
        .map(alliance -> alliance == DriverStation.Alliance.Red)
        .orElse(false); // Default: Blue si no hay alianza disponible

    // Crea el factory para rutas autónomas con trayectorias de Choreo
    final AutoFactory autoFactory = new AutoFactory(
      subsystemManager.getPoseTracker()::getPose,              // Supplier de pose actual
      subsystemManager.getPoseTracker()::resetOdometry,       // Consumer para reset de odometría
      subsystemManager.getPoseTracker()::followTrajectory,    // Consumer para seguimiento de trayectoria
      isRed,                                                    // Flag de alianza (Red=true, Blue=false)
      subsystemManager.getPoseTracker().getSwerve()            // Subsistema swerve para comandos
    );

    autoChooser = new AutoChooser();

    // Agrega rutinas autónomas disponibles
    autoChooser.addRoutine("Human Side", () -> getRoutine(autoFactory, "HumanSide"));
    autoChooser.addRoutine("Human Opposed", () -> getRoutine(autoFactory, "HumanOpposed"));
    autoChooser.addRoutine("Center", () -> getRoutine(autoFactory, "Center"));
    autoChooser.addRoutine("Test", () -> getRoutine(autoFactory, "Test"));
    
    // ====================================================================
    // PUBLICACIÓN EN SMARTDASHBOARD/SHUFFLEBOARD
    // ====================================================================
    // Publica el chooser para que aparezca en Shuffleboard
    SmartDashboard.putData("Auto Chooser", autoChooser);
    System.out.println("[RobotContainer] AutoChooser publicado en SmartDashboard/Shuffleboard");
  }

  /**
   * Retorna el comando autónomo a ejecutar.
   * 
   * Proporciona la rutina de autonomía del robot seleccionada en el chooser
   * para la fase autónoma de la competencia. Si no hay rutina seleccionada,
   * retorna un comando vacío.
   *
   * @return Comando autónomo a ejecutar (o comando vacío si no hay selección)
   */
  public Command getAutonomousCommand() {
    Command selectedCommand = autoChooser.selectedCommandScheduler();
    
    // Validación: si no hay comando seleccionado, retorna un comando vacío
    if (selectedCommand == null) {
      System.err.println("[RobotContainer] ADVERTENCIA: No hay rutina autónoma seleccionada.");
      return Commands.none(); // Retorna comando vacío
    }
    
    return selectedCommand;
    //return new AutoIdeal(subsystemManager);
  }

  private AutoRoutine getRoutine(AutoFactory autoFactory, String trajectoryName) {
    // ====================================================================
    // CONFIGURACIÓN DE RUTINA AUTÓNOMA CON CHOREO
    // ====================================================================
    
    AutoRoutine routine = autoFactory.newRoutine("autoRoutine");

    // Carga la trayectoria desde el archivo de Choreo
    AutoTrajectory trajectory = routine.trajectory(trajectoryName);
    
    // ====================================================================
    // PUBLICACIÓN DE TRAYECTORIA EN ADVANTAGE SCOPE
    // ====================================================================
    // Publica la trayectoria para visualizarla en Advantage Scope
    publishTrajectoryToAdvantageScope(trajectory, trajectoryName);

    
    // Cuando la rutina se activa, resetea la odometría y comienza a seguir la trayectoria
    routine.active().onTrue(
        trajectory.resetOdometry().andThen(
          trajectory.cmd()
        )  // Resetea pose al inicio de la trayectoria
                 // Ejecuta el seguimiento de la trayectoria
    );

    // ====================================================================
    // ZONA DE ACTIVACIÓN: INTAKE
    // ====================================================================
    // Se activa intake cuando la trayectoria pasa cerca de zonas de recolección
    trajectory.atPose("IntakeActivationZone1", 1, 1).onTrue(
      new InstantCommand(() -> subsystemManager.executeState(RobotState.INTAKE))
    );
    trajectory.atPose("IntakeActivationZone2", 1, 1).onTrue(
      new InstantCommand(() -> subsystemManager.executeState(RobotState.INTAKE))
    );
    trajectory.atPose("IntakeActivationZone3", 1, 1).onTrue(
      new InstantCommand(() -> subsystemManager.executeState(RobotState.INTAKE))
    );

    // ====================================================================
    // ZONA DE ACTIVACIÓN: SHOOTER
    // ====================================================================
    // Se activa shooter en zonas de disparo con tiempo de espera variable
    trajectory.atPose("ShooterActivationZone1", 0.2, 0.2).onTrue(
      Commands.sequence(
        new InstantCommand(() -> subsystemManager.executeState(RobotState.SHOOT)),
        new WaitCommand(1.0),   // Espera 1 segundo para que acelere
        new InstantCommand(() -> subsystemManager.executeState(RobotState.TRAVEL))
      )
    );
    trajectory.atPose("ShooterActivationZone2", 0.2, 0.2).onTrue(
      Commands.sequence(
        new InstantCommand(() -> subsystemManager.executeState(RobotState.SHOOT)),
        new WaitCommand(4.0),   // Espera 4 segundos
        new InstantCommand(() -> subsystemManager.executeState(RobotState.TRAVEL))
      )
    );
    trajectory.atPose("ShooterActivationZone3", 0.2, 0.2).onTrue(
      Commands.sequence(
        new InstantCommand(() -> subsystemManager.executeState(RobotState.SHOOT)),
        new WaitCommand(5.0),   // Espera 5 segundos
        new InstantCommand(() -> subsystemManager.executeState(RobotState.TRAVEL))
      )
    );

    return routine;
  }

  /**
   * Actualiza el estado periódico del robot.
   * 
   * Debe ser llamado desde {@code Robot.robotPeriodic()}. Actualiza todos
   * los subsistemas y la estimación de pose del robot. Mantiene sincronizado
   * el AutoChooser en SmartDashboard/Shuffleboard.
   */
  public void periodic() {
    subsystemManager.periodic();
    
    // Mantiene el AutoChooser sincronizado en el dashboard
    // Esto asegura que siempre esté disponible en Shuffleboard
    if (autoChooser != null) {
      SmartDashboard.putData("Auto Chooser", autoChooser);
    }
  }

  /**
   * Publica la trayectoria en NetworkTables para visualizarla en Advantage Scope.
   *
   * Carga la trayectoria de Choreo y la envía a NetworkTables en el topic
   * "/Advantage Scope/Trajectories/{trajectoryName}" para que pueda ser
   * visualizada en Advantage Scope.
   *
   * @param trajectory Trayectoria de Choreo a publicar
   * @param trajectoryName Nombre de la trayectoria (utilizado como identificador)
   */
  private void publishTrajectoryToAdvantageScope(AutoTrajectory trajectory, String trajectoryName) {
    Pose2d[] trajectoryPoints = trajectory.getRawTrajectory().getPoses();


    tArrayPublisher.set(trajectoryPoints);
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
