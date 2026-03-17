package frc.robot.subsystems.climber.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.climber.ClimberController;
import frc.robot.subsystems.climber.ClimberIO;
import frc.robot.subsystems.climber.Climber;

/**
 * Comando para mover el elevador y la garra a posiciones específicas.
 *
 * Ejecuta los comandos de posición en el controlador y verifica que ambos
 * motores hayan alcanzado sus posiciones objetivo dentro de una tolerancia.
 */
public class Move extends Command {
  
  /** Controlador del subsistema Climber. */
  ClimberController controller;
  /** Interfaz de hardware del subsistema Climber. */
  ClimberIO io;
  /** Posición objetivo del elevador (rotaciones). */
  double elevatorPosition;
  /** Posición objetivo de la garra (rotaciones). */
  double clawPosition;

  /**
   * Crea un comando para mover el elevador y la garra.
   *
   * @param elevatorPosition posición objetivo del elevador (rotaciones)
   * @param clawPosition posición objetivo de la garra (rotaciones)
   * @param controller controlador del subsistema
   * @param io interfaz de hardware del subsistema
   * @param climber subsistema Climber (para requirements)
   */
  public Move(double elevatorPosition, double clawPosition, ClimberController controller, ClimberIO io, Climber climber) {
    this.elevatorPosition = elevatorPosition;
    this.clawPosition = clawPosition;
    this.controller = controller;
    this.io = io;

    addRequirements(climber);
  }

  /**
   * Ejecuta el comando enviando las posiciones objetivo al controlador.
   */
  @Override
  public void execute() {
    controller.setPositionClaw(clawPosition);
    controller.setPositionElevator(elevatorPosition);
  }

  /**
   * Verifica si el comando ha finalizado.
   * 
   * El comando termina cuando ambos motores han alcanzado sus posiciones
   * objetivo dentro de una tolerancia de 0.05 rotaciones.
   *
   * @return true si ambos motores están en posición, false si no
   */
  @Override
  public boolean isFinished() {
    return (io.getElevatorPosition() - elevatorPosition) < 0.05 &&
            Math.abs(io.getClawPosition() - clawPosition) < 0.05;
  }
}
