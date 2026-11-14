package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

final class ScoringHardware {
    // Bench tuning values
    static final double INTAKE_POWER = 0.5;
    static final double TRANSFER_POWER = 0.4;
    static final double REVERSE_POWER = -0.25;
    static final double SHOOTER_POWER = 0.5;

    private final DcMotor intake;
    private final DcMotor transfer;
    private final DcMotor shooter;

    ScoringHardware(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, "intake");
        transfer = hardwareMap.get(DcMotor.class, "transfer");
        shooter = hardwareMap.get(DcMotor.class, "shooter");

        stop();
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    void update(boolean intakeHeld, boolean spinHeld, boolean transferHeld, boolean clearHeld) {
        if (clearHeld) {
            setPowers(REVERSE_POWER, REVERSE_POWER, 0);
            return;
        }

        setPowers(intakeHeld ? INTAKE_POWER : 0,
                transferHeld ? TRANSFER_POWER : 0,
                spinHeld ? SHOOTER_POWER : 0);
    }

    void setPowers(double intakePower, double transferPower, double shooterPower) {
        shooter.setPower(shooterPower);
        intake.setPower(intakePower);
        transfer.setPower(transferPower);
    }

    void stop() {
        setPowers(0, 0, 0);
    }
}
