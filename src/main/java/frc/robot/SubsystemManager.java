package frc.robot;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.subsystems.channeler.Channeler;
import frc.robot.subsystems.climber.Climber;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.swerve.PoseTracker;
import frc.robot.utils.TejuinoBoard;

/**
 * Gestor centralizado de subsistemas y estados del robot.
 *
 * Coordina el funcionamiento de todos los subsistemas (swerve, intake, shooter,
 * channeler, climber) y administra las transiciones entre estados operacionales
 * (TRAVEL, INTAKE, SHOOT, CLIMB, TEST).
 * 
 * Mantiene el estado actual del robot y gestiona las asistencias de conducción
 * (assist X, Y, theta) así como el aiming automático hacia los objetivos.
 */
public final class SubsystemManager {

    /** Rastreador de pose del robot (odometría + visión). */
    private final PoseTracker poseTracker;
    /** Subsistema de ingesta de notas. */
    private final Intake intake;
    /** Subsistema de conducción de notas hacia el shooter. */
    private final Channeler channeler;
    /** Subsistema de lanzamiento de notas. */
    private final Shooter shooter;
    /** Controlador de la placa Tejuino para LEDs y feedback visual. */
    private final TejuinoBoard tejuino;
    /** Subsistema de escalada. */
    private final Climber climber;

    /** Estado operacional actual del robot. */
    private RobotState robotState = RobotState.TRAVEL;

    /** Supplier para la velocidad X del robot en modo TRAVEL. */
    private Supplier<Double> travelVxSupplier;

    /** Flags de asistencia para controles de conducción automática. */
    public boolean assistX = false;
    public boolean assistY = false;
    public boolean assistTheta = false;
    /** Flag para habilitar el aiming automático hacia objetivos. */
    public boolean aimEnabled = false;

    /** Pose objetivo para comandos de navegación automática. */
    public Pose2d targetPose = new Pose2d(0, 0, new Rotation2d(0));
    /** Pose calculada para aiming automático. */
    Pose2d aimPose = new Pose2d(0, 0, new Rotation2d(0));
    
    /** Publisher de NetworkTables para la pose de aiming. */
    StructPublisher<Pose2d> aimPosePublisher = 
        NetworkTableInstance.getDefault()
                    .getStructTopic("Aim Pose", Pose2d.struct)
                    .publish();

    /**
     * Crea el gestor de subsistemas e inicializa todos los subsistemas
     * del robot.
     */ 
    public SubsystemManager() {
        this.poseTracker = new PoseTracker();
        this.intake = new Intake();
        this.shooter = new Shooter();
        this.channeler = new Channeler(() -> shooter.getIO().getSpinVelocityRPS());
        this.tejuino = new TejuinoBoard();
        this.climber = new Climber();
    }

    /**
     * Configura los controles para el estado TRAVEL.
     * 
     * Debe ser llamado desde {@link RobotContainer} después de crear
     * el SubsystemManager. Conecta los inputs del operador con el PoseTracker
     * y los sistemas de asistencia.
     *
     * @param vx velocidad X del robot (m/s), típicamente del joystick izquierdo
     * @param vy velocidad Y del robot (m/s), típicamente del joystick izquierdo
     * @param omega velocidad angular del robot (rad/s), típicamente del joystick derecho
     * @param resetYaw proveedor para resetear el yaw del giroscopio
     */
    public void configureTravelControls(
            Supplier<Double> vx,
            Supplier<Double> vy,
            Supplier<Double> omega,
            Supplier<Boolean> resetYaw
    ) {
        this.travelVxSupplier = vx;

        poseTracker.configureDefaultCommands(
            vx, 
            vy, 
            omega, 
            resetYaw,

            () -> assistX,
            () -> assistY,
            () -> assistTheta,
            () -> aimEnabled,

            () -> targetPose,
            this::calculateAimPose
            
        );
    }

    /**
     * Calcula la pose de aiming óptima según la posición actual del robot
     * y la alianza. Selecciona el objetivo disponible más cercano.
     *
     * @return Pose2d con la posición y rotación recomendada para el shooter
     */
    private Pose2d calculateAimPose() {
        Pose2d pose = poseTracker.getPose();

        var alliance = DriverStation.getAlliance();

        if (alliance.get() == DriverStation.Alliance.Blue) {
            if (pose.getX() <= 4.625) {
                aimPose = new Pose2d(4.625, 4.033, new Rotation2d(180));
            } else if (pose.getY() >= 4.033) {
                aimPose = new Pose2d(4.625, 6.0495, new Rotation2d(180));
            } else{
                aimPose = new Pose2d(4.625, 2.0165, new Rotation2d(0));
            }
        } else {
            if (pose.getX() >= 16.54 - 4.625) {
                aimPose = new Pose2d(16.54 - 4.625, 4.033, new Rotation2d(180));
            } else if (pose.getY() >= 4.033) {
                aimPose = new Pose2d(16.54 - 4.625, 6.0495, new Rotation2d(180));
            } else{
                aimPose = new Pose2d(16.54 - 4.625, 2.0165, new Rotation2d(0));
            }
        }

        return aimPose;
    }


    /**
     * Retorna el rastreador de pose del robot.
     */
    public PoseTracker getPoseTracker() {
        return poseTracker;
    }

    /**
     * Inicializa el estado del robot a la configuración operacional por defecto.
     * 
     * Debe ser llamado después de configurar los controles. Establece el estado
     * inicial a TRAVEL e inicializa la placa Tejuino.
     */
    public void initialize() {
        executeState(RobotState.TRAVEL);
        tejuino.init(40);
    }

    /**
     * Desactiva todos los sistemas de conducción y asistencia.
     * 
     * Deshabilita los flags de asistencia y setea los LEDs a púrpura como
     * indicador visual de estado deshabilitado.
     */
    public void disable() {
        assistX = false;
        assistY = false;
        assistTheta = false;
        aimEnabled = false;
        tejuino.all_leds_purple(1);
        tejuino.all_leds_purple(2);
    }

    /**
     * Cambia el estado actual sin lanzar comandos adicionales.
     *
     * @param state nuevo estado del robot
     */
    private void setState(RobotState state) {
        robotState = state;
    }

    /**
     * Ejecuta un cambio explícito de estado.
     *
     * Cancela todos los comandos programados actualmente y luego programa
     * el nuevo estado. Útil para transiciones inmediatas entre estados.
     *
     * @param state nuevo estado a ejecutar
     */
    public void executeState(RobotState state) {
        CommandScheduler.getInstance().cancelAll();
        scheduleState(state);
    }
    
    /**
     * Programa el comportamiento asociado a un estado operacional.
     * <p>
     * Gestiona la transición a cada estado configurando los subsistemas,
     * asistencias y comandos correspondientes:
     * <ul>
     *   <li><b>TRAVEL:</b> Conducción normal con todos los subsistemas inactivos</li>
     *   <li><b>INTAKE:</b> Activación del intake</li>
     *   <li><b>SHOOT:</b> Preparación del shooter con aiming automático habilitado</li>
     *   <li><b>CLIMB:</b> Preparación del subsistema de escalada</li>
     *   <li><b>TEST:</b> Modo de prueba para subsistemas individuales</li>
     * </ul>
     *
     * @param state estado a programar
     */
    public void scheduleState(RobotState state) {
        setState(state);

        switch (state) {
            case TRAVEL:
                // Verifica que los controles estén configurados
                if (travelVxSupplier == null) {
                    throw new IllegalStateException(
                        "Travel controls not configured. Call configureTravelControls() first."
                    );
                }
                
                CommandScheduler.getInstance().schedule(
                    new ParallelCommandGroup(
                        new InstantCommand(() -> {
                            assistX = false;
                            assistY = false;
                            assistTheta = false;
                            aimEnabled = false;
                            tejuino.all_leds_blue(1);
                            tejuino.all_leds_blue(2);
                        }),
                        intake.stopCommand(),
                        channeler.stopCommand(),
                        shooter.stopCommand()
                    )
                );
                break;
            case INTAKE:
                CommandScheduler.getInstance().schedule(
                    new InstantCommand(() -> {
                            assistX = false;
                            assistY = false;
                            assistTheta = false;
                            aimEnabled = false;
                            tejuino.all_leds_yellow(1);
                            tejuino.all_leds_yellow(2);
                        }),
                    intake.resetMaxVelocityCommand(),
                    intake.grabCommand(),
                    channeler.stopCommand(),
                    shooter.stopCommand()
                );
                break;
            case SHOOT:
                CommandScheduler.getInstance().schedule(
                    new InstantCommand(() -> {
                            assistX = false;
                            assistY = false;
                            assistTheta = true;
                            aimEnabled = true;
                            tejuino.all_leds_red(1);
                            tejuino.all_leds_red(2);
                        }),
                    intake.setMaxVelocityCommand(1),
                    intake.stowCommand(),
                    shooter.shootCommand(
                        () -> calculateAimPose().getTranslation().getDistance(poseTracker.getPose().getTranslation())
                    ).andThen(channeler.feedCommand())
                );
                break;
            case CLIMB:
                CommandScheduler.getInstance().schedule(
                    new InstantCommand(() -> {
                            assistX = false;
                            assistY = false;
                            assistTheta = false;
                            aimEnabled = false;
                            tejuino.all_leds_green(1);
                            tejuino.all_leds_green(2);
                    }),
                    intake.resetMaxVelocityCommand(),
                    intake.stowCommand(),
                    channeler.stopCommand(),
                    shooter.stopCommand()
                );
                break;
            case TEST:
                CommandScheduler.getInstance().schedule(
                     intake.stowCommand()
                );
                break;
            default:
                // Todos los demás estados se redirigen a TRAVEL.
                setState(RobotState.TRAVEL);
                break;
        }
    }

    /**
     * Actualiza periódicamente el estado del robot.
     * 
     * Debe ser llamado desde {@code Robot.periodic()}. Actualiza la estimación
     * de pose y publica el estado actual a SmartDashboard para debugging.
     */
    public void periodic() {
        poseTracker.periodic();
        SmartDashboard.putString("Robot State", robotState.toString());

        SmartDashboard.putString("Aim Pose", calculateAimPose().toString());
        aimPosePublisher.set(aimPose);
    }
}