package frc.robot.subsystems.channeler;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Subsistema de conducción de fuel hacia el shooter.
 *
 * Gestiona el motor de conducción (channeler) que transporta las fuel desde
 * la rueda de intake hasta el shooter. Proporciona comandos para alimentar,
 * liberar y detener el movimiento de fuel.
 */
public class Channeler extends SubsystemBase{
    
    /** Interfaz I/O del subsistema. */
    private final ChannelerIO io;
    /** Controlador del motor de conducción. */
    private final ChannelerController controller;

    /** Flag para indicar si se debe alimentar fuel hacia el shooter. */
    private boolean isFeeding = false;

    /** Proveedor de velocidad del shooter para sincronización. */
    private final Supplier<Double> velocitySupplier;

    /**
     * Crea el subsistema Channeler.
     *
     * @param velocitySupplier proveedor de velocidad del shooter para sincronizar
     *                         la alimentación de fuel
     */
    public Channeler(Supplier<Double> velocitySupplier) {
        this.velocitySupplier = velocitySupplier;
        this.io = new ChannelerIO();
        this.controller = new ChannelerController(io.getSpinMotor());
    }

    /**
     * Comando para comenzar a alimentar fuel hacia el shooter.
     *
     * @return Comando instantáneo que activa el flag de alimentación
     */
    public Command feedCommand() {
        return new InstantCommand(() -> isFeeding = true, this);
    }

    /**
     * Comando para liberar/rechazar fuel.
     * 
     * Ejecuta el motor a la velocidad definida en {@link ChannelerConstants#RELEASE_VELOCITY}.
     *
     * @return Comando instantáneo que activa velocidad de liberación
     */
    public Command releaseCommand() {
        return new InstantCommand(() -> controller.setVelocity(ChannelerConstants.RELEASE_VELOCITY), this);
    }

    /**
     * Comando para detener la conducción de fuel.
     *
     * @return Comando instantáneo que desactiva la alimentación
     */
    public Command stopCommand() {
        return new InstantCommand(() -> isFeeding = false, this);
    }

    /**
     * Actualiza el subsistema periódicamente.
     * 
     * Si está alimentando, calcula la velocidad sincronizada con el shooter.
     * En caso contrario, detiene todos los motores.
     */
    @Override
    public void periodic() {
        if (isFeeding) {
            // Calcula velocidad de conducción sincronizada con shooter
            double velocity = Math.max(-Math.abs(velocitySupplier.get() * 2), -100);

            controller.setVelocity(-velocity);
        } else {
            io.stopMotors();
        }

        SmartDashboard.putNumber("Channeler velocity (RPS)", io.getSpinVelocityRPS());
    }
}
