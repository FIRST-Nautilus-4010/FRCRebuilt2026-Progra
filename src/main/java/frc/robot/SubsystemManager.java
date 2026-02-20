package frc.robot;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.swerve.PoseTracker;

/**
 * Gestor simple de subsistemas/estados del robot.
 *
 * En esta versión reducida, solo se mantiene:
 * <ul>
 *   <li>El subsistema swerve (a través de {@link PoseTracker})</li>
 *   <li>El estado {@link RobotState#TRAVEL}</li>
 * </ul>
 *
 * El objetivo es centrarse únicamente en mover el chasis.
 */
public final class SubsystemManager {

    /** Rastreador de pose del robot (odometría + visión). */
    private final PoseTracker poseTracker;
    private final Intake intake;

    /** Estado actual del robot. Solo se usa TRAVEL en esta versión. */
    private RobotState robotState = RobotState.TRAVEL;

    // --- Suppliers para controles de TRAVEL ---
    private Supplier<Double> travelVxSupplier;
    private Supplier<Double> travelVySupplier;
    private Supplier<Double> travelOmegaSupplier;
    private Supplier<Boolean> travelFieldRelativeSupplier;
    private Supplier<Boolean> travelResetYawSupplier;

    /**
     * Crea el gestor de subsistemas usando el subsistema swerve.
     */ 
    public SubsystemManager() {
        this.poseTracker = new PoseTracker();
        this.intake = new Intake();
    }

    /**
     * Configura los controles para el estado TRAVEL.
     * 
     * Debe ser llamado desde {@link RobotContainer} después de crear
     * el SubsystemManager.
     *
     * @param vx velocidad X del robot (m/s)
     * @param vy velocidad Y del robot (m/s)
     * @param omega velocidad angular del robot (rad/s)
     * @param fieldRelative si la conducción es relativa al campo
     * @param resetYaw si se debe resetear el yaw (gyro)
     */
    public void configureTravelControls(
            Supplier<Double> vx,
            Supplier<Double> vy,
            Supplier<Double> omega,
            Supplier<Boolean> fieldRelative,
            Supplier<Boolean> resetYaw
    ) {
        this.travelVxSupplier = vx;
        this.travelVySupplier = vy;
        this.travelOmegaSupplier = omega;
        this.travelFieldRelativeSupplier = fieldRelative;
        this.travelResetYawSupplier = resetYaw;
    }

    /**
     * Inicializa el estado del robot.
     * 
     * Debe ser llamado después de configurar los controles.
     */
    public void initialize() {
        // Estado inicial: TRAVEL (conducción normal del chasis).
        scheduleState(RobotState.TRAVEL);
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
     * Ejecuta un cambio explícito de estado:
     * <ul>
     *   <li>Cancela todos los comandos actuales</li>
     *   <li>Programa el nuevo estado</li>
     * </ul>
     *
     * En esta versión, solo existe un comportamiento para TRAVEL.
     */
    public void executeState(RobotState state) {
        CommandScheduler.getInstance().cancelAll();
        scheduleState(state);
    }

    /**
     * Programa el comportamiento asociado a un estado.
     * <p>
     * Actualmente, solo se maneja {@link RobotState#TRAVEL} y el resto
     * de estados se redirigen a TRAVEL.
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
                            poseTracker.configureDefaultCommands(
                                travelVxSupplier, 
                                travelVySupplier, 
                                travelOmegaSupplier, 
                                travelFieldRelativeSupplier, 
                                travelResetYawSupplier, 
                                false
                            );
                        }),
                        intake.stopCommand()
                    )
                );
                break;
            case INTAKE:
                CommandScheduler.getInstance().schedule(
                    new InstantCommand(() -> {
                            poseTracker.configureDefaultCommands(
                                travelVxSupplier, 
                                travelVySupplier, 
                                travelOmegaSupplier, 
                                travelFieldRelativeSupplier, 
                                travelResetYawSupplier, 
                                false
                            );
                        }),
                    intake.grabCommand()
                );
                break;
            case SHOOT:
                CommandScheduler.getInstance().schedule(
                    new InstantCommand(() -> {
                            poseTracker.configureDefaultCommands(
                                travelVxSupplier, 
                                travelVySupplier, 
                                travelOmegaSupplier, 
                                travelFieldRelativeSupplier, 
                                travelResetYawSupplier, 
                                false
                            );
                        }),
                    intake.stowCommand(),
                    poseTracker.rotateTo(Rotation2d.fromDegrees(180))
                );
                break;
            case CLIMB:
                CommandScheduler.getInstance().schedule(
                    new InstantCommand(() -> {
                            poseTracker.configureDefaultCommands(
                                travelVxSupplier, 
                                travelVySupplier, 
                                travelOmegaSupplier, 
                                travelFieldRelativeSupplier, 
                                travelResetYawSupplier, 
                                false
                            );
                        }),
                    intake.stowCommand()
                );
                break;
            case TEST:
                CommandScheduler.getInstance().schedule(
                    new InstantCommand(() -> {
                            poseTracker.configureDefaultCommands(
                                travelVxSupplier, 
                                travelVySupplier, 
                                travelOmegaSupplier, 
                                travelFieldRelativeSupplier, 
                                travelResetYawSupplier, 
                                true
                            );
                        }),
                    intake.grabCommand(),
                    poseTracker.rotateTo(Rotation2d.fromDegrees(90))
                );
                break;
            default:
                // Todos los demás estados se redirigen a TRAVEL.
                setState(RobotState.TRAVEL);
                break;
        }
    }

    /**
     * Debe llamarse periódicamente desde {@code Robot.periodic()}.
     *
     * Actualiza la estimación de pose y publica el estado actual a
     * SmartDashboard.
     */
    public void periodic() {
        poseTracker.periodic();
        SmartDashboard.putString("Robot State", robotState.toString());
    }
}