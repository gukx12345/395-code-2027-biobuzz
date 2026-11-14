package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

final class ScoringHardware {
    // Starting values for bench tuning. Velocity is measured in encoder ticks per second.
    static final double TARGET_TICKS_PER_SECOND = 800;
    private static final double INTAKE_POWER = 0.5;
    private static final double TRANSFER_POWER = 0.4;
    private static final double REVERSE_POWER = -0.25;

    private final DcMotor intake;
    private final DcMotor transfer;
    private final DcMotorEx shooter;

    ScoringHardware(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, "intake");
        transfer = hardwareMap.get(DcMotor.class, "transfer");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        stop();
    }

    double update(boolean intakeHeld, boolean spin, boolean feed, boolean clearJam) {
        double velocity = shooter.getVelocity();
        if (clearJam) {
            shooter.setPower(0);
            transfer.setPower(REVERSE_POWER);
            intake.setPower(REVERSE_POWER);
            return velocity;
        }

        if (spin) {
            shooter.setVelocity(TARGET_TICKS_PER_SECOND);
        } else {
            shooter.setPower(0);
        }

        boolean readyToFeed = spin && feed && atSpeed(velocity);
        transfer.setPower(readyToFeed ? TRANSFER_POWER : 0);
        intake.setPower(intakeHeld ? INTAKE_POWER : 0);
        return velocity;
    }

    static boolean atSpeed(double velocity) {
        return Math.abs(velocity - TARGET_TICKS_PER_SECOND) <= 0.05 * TARGET_TICKS_PER_SECOND;
    }

    void stop() {
        transfer.setPower(0);
        intake.setPower(0);
        shooter.setPower(0);
    }
}
