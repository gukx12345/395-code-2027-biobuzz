package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@TeleOp(name = "servo tester", group = "Test")
public class ServoTester extends OpMode {
    public static int selectedServo = 0;
    public static boolean flowerPair = false;
    public static double targetPosition = 0.5; // UNTESTED
    public static double flowerRightTarget = 0.5; // UNTESTED

    private Servo[] servos;
    private final String[] names = {"flowerLeft", "flowerRight", "outtakeBlocker"};
    private String lastCommand = "None";
    private boolean wasHeld;

    @Override
    public void init() {
        servos = new Servo[] {
                hardwareMap.get(Servo.class, "flowerLeft"),
                hardwareMap.get(Servo.class, "flowerRight"),
                hardwareMap.get(Servo.class, "outtakeBlocker")
        };
    }

    @Override
    public void loop() {
        if (gamepad1.a && !wasHeld) {
            if (flowerPair && validPos(targetPosition) && validPos(flowerRightTarget)) {
                servos[0].setPosition(targetPosition);
                servos[1].setPosition(flowerRightTarget);
                lastCommand = "Flower pair: " + targetPosition + ", " + flowerRightTarget;
            } else if (!flowerPair && selectedServo >= 0
                    && selectedServo < servos.length && validPos(targetPosition)) {
                servos[selectedServo].setPosition(targetPosition);
                lastCommand = names[selectedServo] + ": " + targetPosition;
            }
        }
        wasHeld = gamepad1.a;

        String selectedName = "Invalid selection (0-2)";
        if (flowerPair) {
            selectedName = "Flower pair";
        } else if (selectedServo >= 0 && selectedServo < names.length) {
            selectedName = names[selectedServo];
        }
        if (!validPos(targetPosition) || (flowerPair && !validPos(flowerRightTarget))) {
            telemetry.addLine("Invalid target");
        }
        telemetry.addData("Next selection", selectedName);
        telemetry.addData("Next target / flower left", targetPosition);
        telemetry.addData("Next flower right target", flowerRightTarget);
        telemetry.addData("Last command", lastCommand);
        telemetry.addLine("Press A sends commands");
        telemetry.update();
    }

    private boolean validPos(double pos) {
        return Double.isFinite(pos) && pos >= 0.0 && pos <= 1.0;
    }
}
