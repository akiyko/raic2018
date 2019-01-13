import model.Rules;
import org.junit.Test;

import java.util.Collections;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * By no one on 12.01.2019.
 */
public class RobotLookAheadTest {

    Rules rules = TestUtils.standardRules();

    RobotPrecalcPhysics phys = RobotPrecalcPhysics.calculate(rules);

    @Test
    public void testGroundBallFindMath() throws Exception {
        MyRobot r1 = TestUtils.robotOnTheGround(new Position(0, 1.0, -35));
//        MyRobot r1 = TestUtils.robotOnTheGround(new Position(0, 1.0, -35));
        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, 0));


        double minAngle = -Math.PI;
        double maxAngle = Math.PI;
        int steps = 80;
        int ticks = 300;
        int mpt = 100;

        BallTrace bt = LookAhead.ballUntouchedTraceOptimized(rules, myBall.clone(), ticks, mpt);
//        BallTrace bt = LookAhead.ballUntouchedTraceSimulator(rules, myBall.clone(), ticks, mpt);
        long start = System.currentTimeMillis();

        BestMoveDouble bmd = RobotLookAhead.robotSeekForBallOnGround(rules, phys, r1.pv(), bt, -Math.PI, Math.PI, steps, 3, false);

        System.out.println( bmd);


        //simulate

        PV pvStart = r1.pv();

        r1.action = new MyAction();
        Vector3d targetVeloGround = bmd.middleTargetVelocityAngleGround();

        r1.action.target_velocity = targetVeloGround;
        r1.nitro = 100;

        int jumpTick = 71;
        GamePlanResult gpr = RobotLookAhead.predictRobotBallFutureJump(rules, phys, bt, pvStart, targetVeloGround, false, false, jumpTick, 77);

        for (int i = 1; i < 75; i++) {
            r1.action.use_nitro = false;
//            if (i < jumpTick + 1) {
//                r.action.use_nitro = nitroOnground;
//            }


            if (i >= jumpTick + 1) {
                r1.action.jump_speed = Constants.ROBOT_MAX_JUMP_SPEED;
                r1.action.target_velocity = r1.velocity;//tang
                r1.action.use_nitro = false;
            }


            Simulator.tick(rules, Collections.singletonList(r1), myBall, Collections.emptyList());


            if(i > 70) {

//            PV pvPredictred = RobotLookAhead.findRobotPvTang(phys, pvStart, targetVeloGround, false, nitroOonFly,
//                    i, jumpTick);
                System.out.println(i + "=============");

                System.out.println(r1.position + " / " + r1.velocity);
//                System.out.println(bt.ballTrace.get(i));
                System.out.println(myBall);
//            System.out.println(pvPredictred.p + " / " + pvPredictred.v);

//            System.out.println("Position diff: " + pvPredictred.p.minus(r.position).length());
//            System.out.println("Velocity diff: " + pvPredictred.v.minus(r.velocity).length());
            }
        }



    }

    @Test
    public void testGroundMoveJumpPerf() throws Exception {

        MyRobot r = TestUtils.robotOnTheGround(new Position(0, 1, 0));

        r.velocity = Vector3d.of(10, 0, 20);
        MyBall b = TestUtils.ballInTheAir(new Position(-25, 10, 35));


        PV pvStart = r.pv();

        r.action = new MyAction();
//        r.action.target_velocity = Vector3d.of(0, 0, 1).normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);
        Vector3d targetVeloGround = r.velocity.normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);

        r.action.target_velocity = targetVeloGround;
        r.nitro = 100;

        int jumpTick = 10;
        boolean nitroOonFly = true;
        boolean nitroOnground = true;


        int cnt = 1_00;

        long start = System.currentTimeMillis();

        for (int j = 0; j < cnt; j++) {
            for (int i = 0; i < 80; i++) {
                PV pvPredictred = RobotLookAhead.findRobotPvTang(phys, pvStart, targetVeloGround, nitroOnground, nitroOonFly,
                        i, jumpTick);
            }

        }


        System.out.println(System.currentTimeMillis() - start + "ms");


    }

    @Test
    public void testGroundMaxSpeedJumpTangPrecision() throws Exception {

        MyRobot r = TestUtils.robotOnTheGround(new Position(0, 1, 0));

        r.velocity = Vector3d.of(1, 0, 5);
        MyBall b = TestUtils.ballInTheAir(new Position(-25, 10, 35));


        PV pvStart = r.pv();

        r.action = new MyAction();
//        r.action.target_velocity = Vector3d.of(0, 0, 1).normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);
        Vector3d targetVeloGround = r.velocity.normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);

        r.action.target_velocity = targetVeloGround;
        r.nitro = 100;

        int jumpTick = 0;
        boolean nitroOonFly = true;
        boolean nitroOnground = true;

        for (int i = 1; i < 70; i++) {
            r.action.use_nitro = false;
            if (i < jumpTick + 1) {
                r.action.use_nitro = nitroOnground;
            }


            if (i >= jumpTick + 1) {
                r.action.jump_speed = Constants.ROBOT_MAX_JUMP_SPEED;
                r.action.target_velocity = r.velocity;//tang
                r.action.use_nitro = nitroOonFly;
            }


            Simulator.tick(rules, Collections.singletonList(r), b, Collections.emptyList());

            PV pvPredictred = RobotLookAhead.findRobotPvTang(phys, pvStart, targetVeloGround, nitroOnground, nitroOonFly,
                    i, jumpTick);
            System.out.println(i + "=============");

            System.out.println(r.position + " / " + r.velocity);
            System.out.println(pvPredictred.p + " / " + pvPredictred.v);

            System.out.println("Position diff: " + pvPredictred.p.minus(r.position).length());
            System.out.println("Velocity diff: " + pvPredictred.v.minus(r.velocity).length());
        }
    }


    @Test
    public void testGroundPrecistion() throws Exception {
        MyRobot r = TestUtils.robotOnTheGround(new Position(0, 1, 0));

        r.velocity = Vector3d.of(-10, 0, -5);
        MyBall b = TestUtils.ballInTheAir(new Position(-10, 10, 0));


        PV pvStart = r.pv();

        r.action = new MyAction();
        r.action.target_velocity = Vector3d.of(1, 0, 1).normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);

        for (int i = 1; i < 50; i++) {
            Simulator.tick(rules, Collections.singletonList(r), b, Collections.emptyList());

            PV pvPredictred = LookAhead.robotGroundMove(pvStart, r.action.target_velocity, i, false);
            System.out.println(i + "=============");

            System.out.println(r.position + " / " + r.velocity);
            System.out.println(pvPredictred.p + " / " + pvPredictred.v);

            System.out.println("Position diff: " + pvPredictred.p.minus(r.position).length());
            System.out.println("Velocity diff: " + pvPredictred.v.minus(r.velocity).length());
        }
    }

    @Test
    public void testGroundPrecistionNitro() throws Exception {
        MyRobot r = TestUtils.robotOnTheGround(new Position(0, 1, 0));

        r.velocity = Vector3d.of(-10, 0, -5);
        MyBall b = TestUtils.ballInTheAir(new Position(-10, 10, 0));


        PV pvStart = r.pv();

        r.action = new MyAction();
        r.action.target_velocity = Vector3d.of(1, 0, 1).normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);
        r.action.use_nitro = true;
        r.nitro = 100;

        for (int i = 1; i < 50; i++) {
            Simulator.tick(rules, Collections.singletonList(r), b, Collections.emptyList());

            PV pvPredictred = LookAhead.robotGroundMove(pvStart, r.action.target_velocity, i, true);
            System.out.println(i + "=============");

            System.out.println(r.position + " / " + r.velocity);
            System.out.println(pvPredictred.p + " / " + pvPredictred.v);

            System.out.println("Position diff: " + pvPredictred.p.minus(r.position).length());
            System.out.println("Velocity diff: " + pvPredictred.v.minus(r.velocity).length());
        }
    }

    @Test
    public void testGroundPrecistionOld() throws Exception {
        MyRobot r = TestUtils.robotOnTheGround(new Position(0, 1, 0));

        r.velocity = Vector3d.of(-10, 0, -5);
        MyBall b = TestUtils.ballInTheAir(new Position(-10, 10, 0));


        PV pvStart = r.pv();

        r.action = new MyAction();
        r.action.target_velocity = Vector3d.of(1, 0, 1).normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);

        for (int i = 1; i < 50; i++) {
            MyRobot rPredictred = LookAhead.robotGroundMove(r.clone(), r.action.target_velocity, i);
            Simulator.tick(rules, Collections.singletonList(r), b, Collections.emptyList());

            System.out.println(i + "=============");

            System.out.println(r.position + " / " + r.velocity);
            System.out.println(rPredictred.position + " / " + rPredictred.velocity);

            System.out.println("Position diff: " + rPredictred.position.minus(r.position).length());
            System.out.println("Velocity diff: " + rPredictred.velocity.minus(r.velocity).length());
        }

    }
}