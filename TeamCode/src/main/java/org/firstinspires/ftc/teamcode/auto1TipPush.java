package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.pedropathing.paths.interpolator.Interpolator;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "1tip+push - middle of tile", group = "Autonomous")
public class auto1TipPush extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();
    private ScoringHardware scoring;

    private final Pose start = poseFactory.of(58.9203, 9.3274, 270);
    private final Pose path1 = poseFactory.of(12.1957, 68.4047, 360);
    private final Pose path1Control1 = poseFactory.of(24.114, 28.1834, 0);
    private final Pose path1Control2 = poseFactory.of(18.0118, 56.9881, 0);
    private final Pose path1Segment1Start = poseFactory.of(12.1957, 68.4047, 270);
    private final Pose path1Segment1End = poseFactory.of(12.1957, 68.4047, 0);
    private final Pose point2 = poseFactory.of(12.3138, 73.712, 0);
    private final Pose point3 = poseFactory.of(30.6956, 73.9485, 0.7369);
    private final Pose point4 = poseFactory.of(9.1967, 115, 0);
    private final Pose point4Control1 = poseFactory.of(39.068, 141.5, 0);

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                follow(follower, path1()),
                follow(follower, path2()),
                follow(follower, path3()),
                follow(follower, path4())
        );
    }



    @Override
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        scoring = new ScoringHardware(hardwareMap);
        follower.setPose(start);
        follower.update();


        waitForStart();

        //shoots balls stupidly
        scoring.flowerUp();
        scoring.update(false,true,false,false);
        sleep(5000);
        scoring.update(true,true,false,false);
        sleep(2000);
        scoring.update(false,false,false,false);


        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            telemetry.update();
        }
    }

    public Path path1() {
        return Paths.curve(start, path1Control1, path1Control2, path1).heading(Interpolator.piecewise().until(1, Interpolator.linear(path1Segment1Start, path1Segment1End)));
    }

    public Path path2() {
        return Paths.line(path1, point2).constant(point2);
    }

    public Path path3() {
        return Paths.line(point2, point3).tangent();
    }

    public Path path4() {
        return Paths.curve(point3, point4Control1, point4).constant(point4);
    }
}
