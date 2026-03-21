package frc.robot.subsystems.shooter;

import java.util.function.Supplier;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.shooter.commands.SetVel;

/**
 * Subsistema de lanzamiento de fuel del robot.
 *
 * Gestiona los motores de disparo para lanzar fuel hacia el objetivo.
 * Proporciona comandos para establecer velocidades específicas y comandos
 * predefinidos como release y stop.
 */
public class Shooter extends SubsystemBase{
    
    /** Interfaz I/O del subsistema. */
    private final ShooterIO io;
    /** Controlador del motor de lanzamiento. */
    private final ShooterController controller;

    private double vel = 0;
    private final NetworkTable table;

    private final NetworkTableEntry velEntry;

    /**
     * Crea el subsistema Shooter.
     *
     * Inicializa la interfaz de hardware y el controlador con referencias
     * a los motores de disparo (principal y secundario).
     */
    public Shooter() {
        this.io = new ShooterIO();
        this.controller = new ShooterController(io.getSpinMotor(), io.getSpinMotorSecondary());

        table = NetworkTableInstance.getDefault().getTable("SmartDashboard");

        velEntry = table.getEntry("debug/DesiredShooterVel");

        velEntry.setDefaultDouble(0.0);
    }

    /**
     * Comando para disparar una nota.
     * 
     * Calcula la velocidad necesaria basada en la distancia al objetivo y
     * ejecuta el disparo a esa velocidad.
     *
     * @param distance proveedor de la distancia al objetivo (metros)
     * @return Comando para ejecutar el disparo
     */
    public Command shootCommand(Supplier<Double> distance) {
        //return new SetVel(this, controller, io, distance);
        return new SetVel(this, controller, io, distance);
    }

    /**
     * Comando para liberar/lanzar una nota a velocidad predeterminada.
     * 
     * Usa la velocidad definida en {@link ShooterConstants#RELEASE_VELOCITY}.
     *
     * @return Comando instantáneo que ejecuta el lanzamiento
     */
    public Command releaseCommand() {
        return new SetVel(this, controller, io, ShooterConstants.RELEASE_VELOCITY);
    }

    /**
     * Comando para detener el motor de lanzamiento.
     *
     * @return Comando que establece velocidad a cero
     */
    public Command stopCommand() {
        return new SetVel(this, controller, io, 0);
    }

    /**
     * Obtiene la interfaz de hardware del subsistema.
     *
     * @return Interfaz ShooterIO
     */
    public ShooterIO getIO() {
        return io;
    }

    /**
     * Actualiza el subsistema periódicamente.
     * 
     * Publica la velocidad actual del motor a SmartDashboard para telemetría
     * y lee la velocidad deseada desde SmartDashboard si el usuario la cambia.
     */
    @Override
    public void periodic() {
        // Publica la velocidad actual de lectura del motor
        SmartDashboard.putNumber("debug/Shooter velocity (RPS)", io.getSpinVelocityRPS());

        vel = velEntry.getDouble(0.0);
    }
}
