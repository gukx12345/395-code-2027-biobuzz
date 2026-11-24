package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

final class ScoringHardware {
    // Bench tuning values
    static final double rightOffset=0.04;
    static final double INTAKE_POWER = 0.8;
    static final double TRANSFER_POWER = 0.4; // UNTESTED, may work best if we just use intake value, this is an alternative for slower fire rate
    static final double REVERSE_POWER = -0.5; // UNTESTED
    static final double SHOOTER_POWER = 1100; // UNTESTED NEEDS TUNING

    static final double FLOWER_LEFT_UP = .2; // UNTESTED
    static final double FLOWER_RIGHT_UP = .8+rightOffset; // UNTESTED
    static final double FLOWER_LEFT_BLOCK = 0.4; // UNTESTED
    static final double FLOWER_RIGHT_BLOCK = 0.6+rightOffset; // UNTESTED
    static final double FLOWER_LEFT_INTAKE = .775; // UNTESTED
    static final double FLOWER_RIGHT_INTAKE = .225+rightOffset; // UNTESTED
    static final double OUTTAKE_BLOCKER_BLOCKING = 0.5; // UNTESTED
    static final double OUTTAKE_BLOCKER_SHOOTING = 0.5; // UNTESTED

    private final HardwareMap hardwareMap;
    private Servo flowerLeft;
    private Servo flowerRight;
    private Servo outtakeBlocker;
    final DcMotor intake;
//    private final DcMotor transfer;
    private final DcMotorEx shooter;

    ScoringHardware(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        intake = hardwareMap.get(DcMotor.class, "intake");
//        transfer = hardwareMap.get(DcMotor.class, "transfer");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");

        stop();
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); we don't have a transfer motor
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(500,0,0,10);
        shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pidfCoefficients);

        shooter.setDirection(DcMotor.Direction.REVERSE);
        intake.setDirection(DcMotor.Direction.REVERSE);
    }

    void update(boolean intakeHeld, boolean spinHeld, boolean transferHeld, boolean clearHeld) {
        if (clearHeld) {
            setPowers(REVERSE_POWER, REVERSE_POWER, spinHeld ? SHOOTER_POWER : 0);
            return;
        }

        setPowers(intakeHeld ? INTAKE_POWER : 0,
                transferHeld ? TRANSFER_POWER : 0,
                spinHeld ? SHOOTER_POWER : 0);
    }

    void setPowers(double intakePower, double transferPower, double shooterPower) {
        shooter.setVelocity(shooterPower);
        intake.setPower(intakePower);
        if (transferPower != 0){
            intake.setPower(transferPower);
        }
//        intake.setPower(transferPower); //set to intake for now, used to be transfer
    }

    private void initFlower() {
        if (flowerLeft == null || flowerRight == null) {
            flowerLeft = hardwareMap.get(Servo.class, "flowerLeft");
            flowerRight = hardwareMap.get(Servo.class, "flowerRight");
        }
    }

    private void initOuttakeBlocker() {
        if (outtakeBlocker == null) {
            outtakeBlocker = hardwareMap.get(Servo.class, "outtakeBlocker");
        }
    }

    void flowerUp() {
        initFlower();
        flowerLeft.setPosition(FLOWER_LEFT_UP);
        flowerRight.setPosition(FLOWER_RIGHT_UP);
    }

    void flowerBlock() {
        initFlower();
        flowerLeft.setPosition(FLOWER_LEFT_BLOCK);
        flowerRight.setPosition(FLOWER_RIGHT_BLOCK);
    }

    void flowerIntake() {
        initFlower();
        flowerLeft.setPosition(FLOWER_LEFT_INTAKE);
        flowerRight.setPosition(FLOWER_RIGHT_INTAKE);
    }

    void blockerBlock() {
        initOuttakeBlocker();
        outtakeBlocker.setPosition(OUTTAKE_BLOCKER_BLOCKING);
    }

    void blockerShoot() {
        initOuttakeBlocker();
        outtakeBlocker.setPosition(OUTTAKE_BLOCKER_SHOOTING);
    }

    void stop() {
        setPowers(0, 0, 0);
    }
}
