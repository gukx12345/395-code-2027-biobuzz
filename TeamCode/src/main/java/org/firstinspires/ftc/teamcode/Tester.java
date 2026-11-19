package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "tester", group = "Test")
public class Tester extends OpMode {
    private final String[] names = {"Intake", "Transfer", "Shooter", "Jam reverse"};
    private final double[] powers = {
        ScoringHardware.INTAKE_POWER, ScoringHardware.TRANSFER_POWER,
        ScoringHardware.SHOOTER_POWER, -ScoringHardware.REVERSE_POWER
    };

    private ScoringHardware scoring;
    private int selected;

    @Override
    public void init() {
        scoring = new ScoringHardware(hardwareMap);
    }

    @Override
    public void loop() {
        boolean next = gamepad1.dpadRightWasPressed();
        boolean previous = gamepad1.dpadLeftWasPressed();
        boolean increase = gamepad1.dpadUpWasPressed();
        boolean decrease = gamepad1.dpadDownWasPressed();

        if (gamepad1.a) {
            double power = powers[selected];
            switch (selected) {
                case 0: scoring.setPowers(power, 0, 0); break;
                case 1: scoring.setPowers(0, power, 0); break;
                case 2: scoring.setPowers(0, 0, power); break;
                case 3: scoring.setPowers(-power, -power, 0); break;
            }
        } else {
            scoring.stop();
            if (next) selected = (selected + 1) % names.length;
            if (previous) selected = (selected + names.length - 1) % names.length;
            if (increase) powers[selected] = Range.clip(powers[selected] + 0.05, -1.0, 1.0);
            if (decrease) powers[selected] = Range.clip(powers[selected] - 0.05, -1.0, 1.0);
        }

        telemetry.addLine("D-pad left/right: select | up/down: adjust | hold A: run");
        double selectedPower = selected == 3 ? -powers[selected] : powers[selected];
        telemetry.addData("Selected", names[selected]);
        telemetry.addData("Selected power", "%.2f", selectedPower);
        telemetry.addData("Commanded power", "%.2f", gamepad1.a ? selectedPower : 0.0);
        telemetry.update();
    }

    @Override
    public void stop() {
        if (scoring != null) {
            scoring.stop();
        }
    }
}
