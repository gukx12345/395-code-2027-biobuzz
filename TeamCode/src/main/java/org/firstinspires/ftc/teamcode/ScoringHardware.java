package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

final class ScoringHardware {
    // Bench tuning values
    private static final double INTAKE_POWER = 0.5;
    private static final double TRANSFER_POWER = 0.4;
    private static final double REVERSE_POWER = -0.25;
    private static final double TARGET_TICKS_PER_SECOND = 800;

    private final DcMotor intake;
    private final DcMotor transfer;
    private final DcMotorEx shooter;

    ScoringHardware(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, "intake");
        transfer = hardwareMap.get(DcMotor.class, "transfer");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");

        stop();
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    void update(boolean intakeHeld, boolean spinHeld, boolean transferHeld, boolean clearHeld) {
        if (clearHeld) {
            shooter.setPower(0);
            intake.setPower(REVERSE_POWER);
            transfer.setPower(REVERSE_POWER);
            return;
        }

        intake.setPower(intakeHeld ? INTAKE_POWER : 0);
        transfer.setPower(transferHeld ? TRANSFER_POWER : 0);

        if (spinHeld) {
            shooter.setVelocity(TARGET_TICKS_PER_SECOND);
        } else {
            shooter.setPower(0);
        }
    }

    void stop() {
        intake.setPower(0);
        transfer.setPower(0);
        shooter.setPower(0);
    }
}
