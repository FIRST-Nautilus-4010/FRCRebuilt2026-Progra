package frc.robot.subsystems.intake.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.intake.IntakeController;
import frc.robot.subsystems.intake.IntakeIO;

/**
 * Comando para mover el pivote del intake y controlar los spinners.
 *
 * Posiciona el pivote a un ángulo específico y configura las velocidades
 * de los motores de giro (principal y secundario). El comando finaliza cuando
 * el pivote alcanza su posición y los spinners alcanzan su velocidad objetivo.
 */
public class Move extends Command {
  
  /** Controlador del subsistema Intake. */
  IntakeController controller;
  /** Interfaz de hardware del subsistema Intake. */
  IntakeIO io;
  /** Posición objetivo del pivote (radianes). */
  double angle;
  /** Velocidad objetivo del spinner principal (RPS). */
  double spinVelocity;

  /**
   * Crea un comando para posicionar el pivote y controlar los spinners.
   *
   * @param angle posición objetivo del pivote (radianes)
   * @param spinVelocity velocidad objetivo del spinner principal (RPS)
   * @param spinVelocitySecondary velocidad objetivo del spinner secundario (RPS)
   * @param controller controlador del subsistema
   * @param io interfaz de hardware del subsistema
   * @param intake subsistema Intake (para requirements)
   */
  public Move(double angle, double spinVelocity, IntakeController controller, IntakeIO io, Intake intake) {
    this.angle = angle;
    this.spinVelocity = spinVelocity;
    this.controller = controller;
    this.io = io;

    addRequirements(intake);
  }

  /**
   * Ejecuta el comando enviando el ángulo y velocidades al controlador.
   */
  @Override
  public void execute() {
    controller.setAngle(angle);
    controller.setVelocity(spinVelocity);
  }

  @Override
  public void end(boolean interrupted) {
    //controller.keepPos();
    //io.getPivotMotor().stopMotor();
  }

  /**
   * Verifica si el comando ha finalizado.
   * 
   * El comando termina cuando el pivote ha alcanzado su posición dentro de
   * una tolerancia de 0.05 radianes Y el spinner principal está dentro de
   * 0.1 RPS de su velocidad objetivo.
   *
   * @return true si pivote y spinner están en objetivo, false si no
   */
  @Override
  public boolean isFinished() {
    return Math.abs(io.getPivotPositionRad() - angle) < 0.01 &&
            Math.abs(io.getSpinVelocityRPS() - spinVelocity) < 0.1;
  }
}
