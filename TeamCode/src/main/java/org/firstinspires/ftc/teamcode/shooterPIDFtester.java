package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Shooter PIDF Tuner")
public class shooterPIDFtester extends LinearOpMode {
    DcMotorEx shooter;
    public double highVelocity=1100;
    public double lowVelocity=800;
    double currentTargetVelocity=highVelocity;

    public double P=500;
    public double I=0;
    public double D=0;
    public double F=0;

    public boolean isFlywheelOn=true;

    public double increment=0.01;

    double flyWheelSpeed=1100;



    @Override public void runOpMode(){

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");

        shooter.setDirection(DcMotor.Direction.REVERSE);

        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P,I,D,F);
        shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pidfCoefficients);


        telemetry.addLine("init done");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()){

            if (gamepad1.xWasPressed()){
                if (currentTargetVelocity==highVelocity){
                    currentTargetVelocity=lowVelocity;
                }else{
                    currentTargetVelocity=highVelocity;
                }
            }

            pidfCoefficients = new PIDFCoefficients(P,I,D,F);
            shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pidfCoefficients);


            if ((gamepad1.b)||(gamepad2.b)){
                isFlywheelOn=false;
            }

            if ((gamepad1.a)||(gamepad2.a)){
                isFlywheelOn=true;
            }

            if (isFlywheelOn){
                shooter.setVelocity(currentTargetVelocity);
            }else{
                shooter.setVelocity(0);
            }
            if (gamepad1.dpadUpWasPressed()){
                increment=increment*10;
            }else if (gamepad1.dpadDownWasPressed()){
                increment=increment/10;
            }


            if (gamepad1.dpadRightWasPressed()){
                F=F+increment;
            }else if (gamepad1.dpadLeftWasPressed()){
                F=F-increment;
            }







            double currentVelocity=shooter.getVelocity();
            double error =currentTargetVelocity-currentVelocity;

            telemetry.addData("target velocity",currentTargetVelocity);
            telemetry.addData("current velocity", "%.2f",currentVelocity);
            telemetry.addData("error", "%.2f",error);
            telemetry.addLine("-------------------------------------");
            telemetry.addData("P",P);
            telemetry.addData("I",I);
            telemetry.addData("D",D);
            telemetry.addData("F",F);
            telemetry.addData("increment",increment);
            telemetry.update();


        }
    }
}
