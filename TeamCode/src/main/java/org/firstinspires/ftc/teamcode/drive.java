package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "drive", group = "Robot")
public class drive extends OpMode {
    // This declares the four motors needed
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;

    // This declares the IMU needed to get the current direction the robot is facing
    IMU imu;

    private ScoringHardware scoring;
    boolean shooterOn = false;
    boolean robotCentric = false;
    boolean halfSpeed=false;

    @Override
    public void init() {
        frontLeftDrive = hardwareMap.get(DcMotor.class, "driveFrontLeft");
        frontRightDrive = hardwareMap.get(DcMotor.class, "driveFrontRight");
        backLeftDrive = hardwareMap.get(DcMotor.class, "driveBackLeft");
        backRightDrive = hardwareMap.get(DcMotor.class, "driveBackRight");

        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);

        // This uses RUN_USING_ENCODER to be more accurate.   If you don't have the encoder
        // wires, you should remove these
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        imu = hardwareMap.get(IMU.class, "imu");
        // This needs to be changed to match the orientation on your robot
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD;

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        scoring = new ScoringHardware(hardwareMap);


    }

    @Override
    public void loop() {


        // If you press the A button, then you reset the Yaw to be zero from the way
        // the robot is currently pointing
        if (gamepad1.a) {
            imu.resetYaw();
        }
        // If you press the left bumper, you get a drive from the point of view of the robot
        // (much like driving an RC vehicle)

        if (gamepad1.leftBumperWasPressed()){
            robotCentric = !robotCentric;
        }

        if (robotCentric) {
            driveBot(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            telemetry.addLine("ROBOT centric");
        } else {
            driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            telemetry.addLine("FIELD centric");
        }


        //makes the shooter a toggle based on the Y button
//        if (shooterOn){
//            if (gamepad1.yWasPressed() || gamepad2.yWasPressed()){
//                shooterOn=false;
//            }
//        }else {
//            if (gamepad1.yWasPressed() || gamepad2.yWasPressed()) {
//                shooterOn = true;
//            }
//        }

        if (gamepad1.y || gamepad2.y){
            shooterOn=true;
        }

        if (gamepad1.b || gamepad2.b){
            shooterOn=false;
        }


        // Scoring controls
        scoring.update(
                gamepad1.left_trigger_pressed || gamepad2.left_trigger_pressed,
                shooterOn,
                gamepad1.right_trigger_pressed || gamepad2.left_trigger_pressed,
                gamepad1.x || gamepad2.x);

        if (gamepad1.dpad_down || gamepad2.dpad_down){
            scoring.flowerIntake();
        } else if (gamepad1.dpad_right || gamepad2.dpad_right){
            scoring.flowerBlock();
        } else if (gamepad1.dpad_up || gamepad2.dpad_up){
            scoring.flowerUp();
        }

    }

    @Override
    public void stop() {
        if (scoring != null) {
            scoring.stop();
        }
    }

    // This routine drives the robot field relative
    private void driveFieldRelative(double forward, double right, double rotate) {
        // First, convert direction being asked to drive to polar coordinates
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        // Second, rotate angle by the angle the robot is pointing
        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        // Third, convert back to cartesian
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        driveBot(newForward, newRight, rotate);
    }

    // Thanks to FTC16072 for sharing this code!!
    public void driveBot(double forward, double right, double rotate) {
        // This calculates the power needed for each wheel based on the amount of forward,
        // strafe right, and rotate
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;


        if (gamepad1.rightBumperWasPressed()){
            halfSpeed = !halfSpeed;
        }
        double maxPower = 1.0;
        double maxSpeed = 1;
        if(halfSpeed){
            maxSpeed=0.5;
            telemetry.addLine("HALF speed");
        }else{
            telemetry.addLine("FULL speed");
        }



        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should
        // be and not get clipped
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.
        frontLeftDrive.setPower(maxSpeed * (frontLeftPower / maxPower));
        frontRightDrive.setPower(maxSpeed * (frontRightPower / maxPower));
        backLeftDrive.setPower(maxSpeed * (backLeftPower / maxPower));
        backRightDrive.setPower(maxSpeed * (backRightPower / maxPower));
    }
}
