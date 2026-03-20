package frc.robot.subsystems.swerve.commands;

import java.util.function.Supplier;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.Constants.AutonomousConstants;
import frc.robot.Constants.ChassisConstants;
import frc.robot.subsystems.swerve.PoseTracker;
import frc.robot.subsystems.swerve.Swerve;

public class Drive extends Command {

    private static final double LIN_DEADZONE =
            0.1 * ChassisConstants.MAX_VELOCITY;

    private static final double ANG_DEADZONE =
            0.1 * ChassisConstants.MAX_ANG_SPD;

    private final Swerve swerve;
    private final PoseTracker poseTracker;

    /* ---------- Joystick ---------- */

    private final Supplier<Double> xInput;
    private final Supplier<Double> yInput;
    private final Supplier<Double> omegaInput;
    private final Supplier<Boolean> resetYaw;

    /* ---------- Assist flags ---------- */

    public static boolean assistX;
    public static boolean assistY;
    public static boolean assistTheta;
    public static boolean aimEnabled;
    public static boolean velocityHeadingEnabled;

    /* ---------- Targets ---------- */

    // Target de traslación (cuando assistX o assistY están activos)
    public static Pose2d targetPose;

    // Punto al que se quiere apuntar (solo usado si aimEnabled = true)
    private final Supplier<Pose2d> aimPose;

    private final HolonomicDriveController controller;

    public Drive(
            Swerve swerve,
            PoseTracker poseTracker,

            Supplier<Double> xInput,
            Supplier<Double> yInput,
            Supplier<Double> omegaInput,
            Supplier<Boolean> resetYaw,

            Supplier<Pose2d> aimPose

    ) {

        this.swerve = swerve;
        this.poseTracker = poseTracker;

        this.xInput = xInput;
        this.yInput = yInput;
        this.omegaInput = omegaInput;
        this.resetYaw = resetYaw;
        this.aimPose = aimPose;

        ProfiledPIDController thetaController =
                new ProfiledPIDController(
                        AutonomousConstants.P_Z,
                        AutonomousConstants.I_Z,
                        AutonomousConstants.D_Z,
                        AutonomousConstants.Z_CONTROLER);

        thetaController.enableContinuousInput(-Math.PI, Math.PI);

        controller = new HolonomicDriveController(
                new PIDController(
                        AutonomousConstants.P_X,
                        AutonomousConstants.I_X,
                        AutonomousConstants.D_X),
                new PIDController(
                        AutonomousConstants.P_Y,
                        AutonomousConstants.I_Y,
                        AutonomousConstants.D_Y),
                thetaController
        );

        addRequirements(swerve);
    }

    @Override
    public void execute() {

        Pose2d currentPose = poseTracker.getPose();
        Pose2d baseTarget = targetPose;

        // Si no hay objetivo de traslación, usa la pose actual
        if (baseTarget == null) {
            baseTarget = currentPose;
        }

        /* ---------------- Rotación deseada para el controlador ---------------- */

        Rotation2d desiredRotation = new Rotation2d();

        if (aimEnabled) {
            desiredRotation = computeAimRotation(currentPose);
        } else if (assistTheta) {
            desiredRotation = baseTarget.getRotation();
        } else if (velocityHeadingEnabled) {
            desiredRotation = computeVelocityHeading(currentPose);
        }

        Pose2d poseForController =
                new Pose2d(baseTarget.getTranslation(), desiredRotation);

        /* ---------------- Salida automática ---------------- */

        ChassisSpeeds auto = controller.calculate(
                currentPose,
                poseForController,
                AutonomousConstants.MAX_SPD,
                desiredRotation
        );

        /* ---------------- Entradas manuales ---------------- */

        double manVx = xInput.get() * ChassisConstants.MAX_VELOCITY;
        double manVy = yInput.get() * ChassisConstants.MAX_VELOCITY;
        double manOmega = omegaInput.get() * ChassisConstants.MAX_ANG_SPD;

        manVx = applyDeadzone(manVx, LIN_DEADZONE);
        manVy = applyDeadzone(manVy, LIN_DEADZONE);
        manOmega = applyDeadzone(manOmega, ANG_DEADZONE);

        /* ---------------- Mezcla por asistencia ---------------- */

        double vx = assistX
                ? auto.vxMetersPerSecond
                : manVx;

        double vy = assistY
                ? auto.vyMetersPerSecond
                : manVy;

        double omega;

        if (assistTheta) {
            omega = auto.omegaRadiansPerSecond;

        } else {
            omega = manOmega;
        }

        /* ---------------- Drive (siempre field-relative) ---------------- */

        swerve.driveFieldRelative(vx, vy, omega);

        // Opción para resetear yaw del gyro (por ejemplo, botón en el joystick).
        if (resetYaw.get()) {
            swerve.zeroHeading();
        }
    }

    @Override
    public void end(boolean interrupted) {
        swerve.stopModules();
    }

    /* ------------------------------------------------------------------ */

    private Rotation2d computeAimRotation(Pose2d currentPose) {

        if (aimPose.get() == null || aimPose.get() == null) {
            return currentPose.getRotation();
        }

        Pose2d aim = aimPose.get();

        double dx = aim.getX() - currentPose.getX();
        double dy = aim.getY() - currentPose.getY();

        return new Rotation2d(Math.atan2(dy, dx));
    }

    private Rotation2d computeVelocityHeading(Pose2d currentPose) {
        return new Rotation2d(Math.atan2(targetPose.getY() - currentPose.getY(), targetPose.getX() - currentPose.getX()));
    }

    private static double applyDeadzone(double value, double deadzone) {
        return Math.abs(value) > deadzone ? value : 0.0;
    }
}