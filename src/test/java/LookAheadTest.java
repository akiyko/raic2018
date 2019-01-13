import model.Action;
import model.Rules;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;


/**
 * @author akiyko
 * @since 12/29/2018.
 */
public class LookAheadTest {
    private Rules rules = TestUtils.standardRules();

    public static final Random rnd = new Random(0);


    @Test
    public void testRobotGroundMove() throws Exception {
        MyRobot r = TestUtils.robotInTheAir(new Position(0, Constants.ROBOT_RADIUS, 0));
        r.velocity = Vector3d.of(-10, 0, -20).clamp(Constants.ROBOT_MAX_GROUND_SPEED);

        MyAction action = new MyAction();
        action.target_velocity = Vector3d.of(50, 0, 10);

        r.action = action;
        r.touch = true;
        r.touch_normal = Vector3d.of(0, 1, 0);
        MyRobot mrm = r.clone();

        int ticks = 100;
        int jumpTick = 10;
        double jumpSpeed = 15;

        for (int i = 1; i < ticks; i++) {
            if (i == jumpTick) {
                r.action.jump_speed = jumpSpeed;
            }
            Simulator.tick(rules, Collections.singletonList(mrm), TestUtils.ballInTheAir(new Position(20, 10, 10)), 100, Collections.emptyList());
            MyRobot mrMath = LookAhead.robotGroundMoveAndJump(r, action.target_velocity, i, jumpTick, jumpSpeed);

            System.out.println(i + "=============================");
            System.out.println("Sim:" + mrm.position + "/" + mrm.velocity);
            System.out.println("Math:" + mrMath.position + "/" + mrMath.velocity);

            System.out.println("Position diff: " + mrm.position.minus(mrMath.position).length());
            System.out.println("Velo diff: " + mrm.velocity.minus(mrMath.velocity).length());

        }


    }

    @Test
    public void testBallTracePerfMathNoColl() throws Exception {
        long goals = 0;

        int iterations = 2000;
        int tickDepth = 300;
        int mpt = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            MyBall mb = randomBall();

            BallGoal bg = LookAhead.ballFlyUntouched(rules, mb);


            if (bg.goalScoredTick > 0) {
                goals++;
            }
        }

        System.out.println("Goals" + goals);
        System.out.println("Dan count: " + Dan.DAN_COUNT * 0.000001);
        System.out.println(System.currentTimeMillis() - start + "ms");
        System.out.println(((double) (System.currentTimeMillis() - start)) / iterations + "ms per tick");
    }

    @Test
    public void testBallTracePerf() throws Exception {
        long goals = 0;

        int iterations = 2000;
        int tickDepth = 300;
        int mpt = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            MyBall mb = randomBall();
            BallTrace bt1 = LookAhead.ballUntouchedTraceOptimized(rules, mb, tickDepth, mpt);

            if (bt1.goalScoredTick > 0) {
                goals++;
            }
        }

        System.out.println("Goals" + goals);
        System.out.println("Dan count: " + Dan.DAN_COUNT * 0.000001);
        System.out.println(System.currentTimeMillis() - start + "ms");
        System.out.println(((double) (System.currentTimeMillis() - start)) / iterations + "ms per tick");
    }


    @Test
    public void testBallTraceOptimizedCorrect() throws Exception {
        long goals = 0;

        int iterations = 200;
        int tickDepth = 300;
        int mpt = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            MyBall mb = randomBall();
            MyBall mb2 = mb.clone();
            MathUtils.r = new Random(0);
            BallTrace bt1 = LookAhead.ballUntouchedTraceSimulator(rules, mb, tickDepth, mpt);
            MathUtils.r = new Random(0);
            BallTrace bt2 = LookAhead.ballUntouchedTraceOptimized(rules, mb2, tickDepth, mpt / 10);

            if (bt1.goalScoredTick > 0) {
                goals++;
            }

            assertEquals(String.valueOf(i), bt1.ballTrace.size(), bt2.ballTrace.size());
            for (int j = 0; j < bt2.ballTrace.size(); j++) {

                MyBall b1 = bt1.ballTrace.get(j);
                MyBall b2 = bt2.ballTrace.get(j);
//                assertTrue(i + "/" + j,b1.position.doubleEquals(b2.position));
            }

            System.out.println(bt1.ballTrace.get(bt1.ballTrace.size() - 1).position);
            System.out.println(bt2.ballTrace.get(bt2.ballTrace.size() - 1).position);

        }

        System.out.println("Goals" + goals);
        System.out.println(System.currentTimeMillis() - start + "ms");
        System.out.println((System.currentTimeMillis() - start) / iterations + "ms per tick");
    }

    @Test
    public void testFlyPrecision() throws Exception {

        MyBall ball = new MyBall();

        ball.velocity = Vector3d.of(random(-10.0, 10.0), random(0, 10.0), random(30.0, 50.0));
        ball.position = new Position(random(-5.0, 5.0), random(3.0, 5.0), random(30, 35.0));

        ball.arena_e = Constants.BALL_ARENA_E;

        ball.radiusChangeSpeed = 0;

        ball.mass = Constants.BALL_MASS;
        ball.radius = Constants.BALL_RADIUS;

        int ticks = 50;

        BallTrace bt1 = LookAhead.ballUntouchedTraceSimulator(rules, ball.clone(), ticks, 300);

        for (int i = 0; i < ticks - 1; i++) {
            Position bapprox = LookAhead.ballPositionFly(ball, (i + 1) / Constants.TICKS_PER_SECOND);

            System.out.println("============");
            System.out.println(bt1.ballTrace.get(i).position);
            System.out.println(bapprox);
            System.out.println("Diff len: " + bt1.ballTrace.get(i).position.minus(bapprox).length());
        }


    }

    public static MyBall randomBall() {
        MyBall ball = new MyBall();

//        ball.velocity = of(random(-10.0, 10.0), random(-10.0, 10.0),random(-10.0, 10.0));
//        ball.position = new Position(random(-25.0, 25.0), random(3.0, 5.0), random(-35.0, 35.0));

        ball.velocity = Vector3d.of(random(-10.0, 10.0), random(0, 10.0), random(30.0, 50.0));
        ball.position = new Position(random(-5.0, 5.0), random(3.0, 5.0), random(30, 35.0));


        ball.arena_e = Constants.BALL_ARENA_E;

        ball.radiusChangeSpeed = 0;

        ball.mass = Constants.BALL_MASS;
        ball.radius = Constants.BALL_RADIUS;

        return ball;
    }

    public static double random(double low, double hi) {
        return low + rnd.nextDouble() * (hi - low);
    }

    @Test
    public void testKickFirst() throws Exception {
        MyRobot r1 = TestUtils.robotInTheAir(new Position(-25, 1.5, -35));


//        LookAhead


    }

    @Test
    public void testGroundBallFindMath() throws Exception {
        MyRobot r1 = TestUtils.robotOnTheGround(new Position(0, 1.0, -35));
//        MyRobot r1 = TestUtils.robotOnTheGround(new Position(0, 1.0, -35));
        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, 0));


        double minAngle = -Math.PI;
        double maxAngle = Math.PI;
        int steps = 80000;
        int ticks = 300;
        int mpt = 100;

        BallTrace bt = LookAhead.ballUntouchedTraceOptimized(rules, myBall, ticks, mpt);
        long start = System.currentTimeMillis();

        BestMoveDouble bmd = LookAhead.robotSeekForBallOnGround(rules, r1, bt, -Math.PI, Math.PI, steps, 3);

        System.out.println( bmd);

        System.out.println("Total: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void testKickFirstMath() throws Exception {
        MyRobot r1 = TestUtils.robotOnTheGround(new Position(0, 1.0, -35));
//        MyRobot r1 = TestUtils.robotOnTheGround(new Position(0, 1.0, -35));
        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, -20));


        double minAngle = -Math.PI;
        double maxAngle = Math.PI;
        int steps = 80000;
        int ticks = 300;
        int mpt = 100;
        double dangle = (maxAngle - minAngle) / steps;
        double jumpSpeed = 15;

        BallTrace bt = LookAhead.ballUntouchedTraceOptimized(rules, myBall, ticks, mpt);
        long start = System.currentTimeMillis();

        for (int i = 0; i <= steps; i++) {
            double x = Math.cos(minAngle + dangle * i);
            double z = Math.sin(minAngle + dangle * i);
            Vector3d targetVelo = Vector3d.of(x, 0, z).multiply(Constants.ROBOT_MAX_GROUND_SPEED);


            GamePlanResult gmp = LookAhead.predictRobotBallFutureMath(rules, bt, r1, targetVelo, 32, jumpSpeed, mpt);

            if(gmp.minToBall.length() < 3.1  /* gmp.goalScoredTick > 0*/) {
                System.out.println(i + ": " + x + "/" + z + gmp);
            }

        }

        System.out.println("Total: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void testFindGoalPerformance() throws Exception {
        StrategyParams strategyParams = new StrategyParams();
        strategyParams.usePotentialGoals = true;
        MyRobot r1 = TestUtils.robotOnTheGround(new Position(-10, 1.0, -35));
        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, -1));
        myBall.velocity = Vector3d.of(-10, 0, 0);

        long start = System.currentTimeMillis();

        BallTrace bt = LookAhead.ballUntouchedTraceOptimized(rules, myBall.clone(), 300, 100);

        int repeatCount = 10000;

        for (int i = 0; i < repeatCount; i++) {
            List<RobotMoveJumpPlan> rmjp = LookAhead.robotMoveJumpGoalOptions(rules, r1, bt, strategyParams);
        }
        long total = System.currentTimeMillis() - start;
        System.out.println("Average: " + (total / repeatCount) + "ms");
    }

    @Test
    public void testFindGoal() throws Exception {
        StrategyParams strategyParams = new StrategyParams();
        strategyParams.usePotentialGoals = true;

        MyRobot r1 = TestUtils.robotOnTheGround(new Position(-10, 1.0, -35));
        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 4, -1));
        myBall.velocity = Vector3d.of(-10, 0,0);

        long start = System.currentTimeMillis();
        BallTrace bt = LookAhead.ballUntouchedTraceOptimized(rules, myBall.clone(), 300, 100);

        List<RobotMoveJumpPlan> rmjp = LookAhead.robotMoveJumpGoalOptions(rules, r1, bt, strategyParams);

        System.out.println("Total rmjp: " + (System.currentTimeMillis() - start) + "ms");

        if(!rmjp.isEmpty()) {
            RobotMoveJumpPlan rmjplan = rmjp.get(0);
            System.out.println(rmjplan);

            //check same with simulate
            for (int i = 1; i < 300; i++) {
                try {
                    MyAction action = new MyAction();
                    if(i >= rmjplan.jumpTick) {
                        action.jump_speed = rmjplan.jumpSpeed;
                    }
                    action.target_velocity = rmjplan.targetVelocity;
                    r1.action = action;

                    Simulator.tick(rules, Collections.singletonList(r1), myBall);

                } catch (GoalScoredException e) {
                    System.out.println("Goal at " + i + ", pos: " + myBall.position);
                    break;

                }
            }

        } else {
            //no goals :(
        }
    }

    @Test
    public void testFindGoalSeekFirst() throws Exception {
        StrategyParams strategyParams = new StrategyParams();
        MyRobot r1 = TestUtils.robotOnTheGround(new Position(-10, 1.0, -35));
        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, -1));
        myBall.velocity = Vector3d.of(0, 0,0);

        double jumpSpeed = 15;
        int jumpTickOffset = -1;

        long start = System.currentTimeMillis();
        BallTrace bt = LookAhead.ballUntouchedTraceOptimized(rules, myBall.clone(), 300, 100);

        BestMoveDouble bmd = LookAhead.robotSeekForBallOnGround(rules, r1, bt, -Math.PI, Math.PI, 80, 3);
        System.out.println(bmd);
        List<RobotMoveJumpPlan> rmjp = LookAhead.robotMoveJumpGooalOptions(rules, r1, bt, bmd, 40, jumpSpeed, jumpTickOffset, strategyParams);

        System.out.println("Total ball search: " + (System.currentTimeMillis() - start) + "ms");

        if(!rmjp.isEmpty()) {
            RobotMoveJumpPlan rmjplan = rmjp.get(0);
            System.out.println(rmjplan);

            //check same with simulate
            for (int i = 1; i < 300; i++) {
                try {
                    MyAction action = new MyAction();
                    if(i >= rmjplan.jumpTick) {
                        action.jump_speed = rmjplan.jumpSpeed;
                    }
                    action.target_velocity = rmjplan.targetVelocity;
                    r1.action = action;

                    Simulator.tick(rules, Collections.singletonList(r1), myBall);

                } catch (GoalScoredException e) {
                    System.out.println("Goal at " + i + ", pos: " + myBall.position);
                    break;

                }

            }


        } else {
            //no goals :(
        }
    }

    @Test
    public void testGoalSimulate() throws Exception {
//        53088: 0.5165931063274437/0.8562310216845466GamePlanResult{goalScoredTick=144, oppGoalScored=-1, minToBall={dx=0.3694619595571389, dy=1.187908630000209, dz=2.4632211759872256}, minToBallTick=85, minBallToOppGateCenter={dx=9.223372036854776E18, dy=9.223372036854776E18, dz=9.223372036854776E18}, ballFinalPosition={x=8.495376713949636, y=2.2122067135335257, z=42.0}, beforeTouchTick=84}
//        53089: 0.5165258565070227/0.8562715921713663GamePlanResult{goalScoredTick=144, oppGoalScored=-1, minToBall={dx=0.37201745273313946, dy=1.187908630000209, dz=2.4616794974880847}, minToBallTick=85, minBallToOppGateCenter={dx=9.223372036854776E18, dy=9.223372036854776E18, dz=9.223372036854776E18}, ballFinalPosition={x=8.564028955330194, y=2.236547847395073, z=42.0}, beforeTouchTick=84}

        int jumpTick = 96;

        MyRobot r1 = TestUtils.robotOnTheGround(new Position(0, 1.0, -35));
        MyRobot r1_0 = r1.clone();
        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, 0));

//        59896: 0.00816805007191672/0.9999666409225973GamePlanResult{goalScoredTick=137, oppGoalScored=-1, minToBall={dx=-0.363478228200294, dy=0.5282059135834578, dz=2.501484478944416}, minToBallTick=98, minBallToOppGateCenter={dx=9.223372036854776E18, dy=9.223372036854776E18, dz=9.223372036854776E18}, ballFinalPosition={x=-3.6838100012816413, y=2.023014241818273, z=42.0}, beforeTouchTick=97}

        Vector3d targetVelo = Vector3d.of(0.00816805007191672, 0, 0.9999666409225973).multiply(Constants.ROBOT_MAX_GROUND_SPEED);

        BallTrace bt = LookAhead.ballUntouchedTraceOptimized(rules, myBall.clone(), 300, 100);
        GamePlanResult gmp = LookAhead.predictRobotBallFutureMath(rules, bt, r1, targetVelo, jumpTick + 1, 15, 100);

        for (int i = 0; i < 150; i++) {
            try {
                MyAction action = new MyAction();
                if(i >= jumpTick) {
                    action.jump_speed = 15;
                }
                action.target_velocity = targetVelo;
                r1.action = action;

                Simulator.tick(rules, Collections.singletonList(r1), myBall);


                System.out.println(i + "==============");
                MyRobot rapprox = LookAhead.robotGroundMoveAndJump(r1_0, targetVelo, i + 1, jumpTick + 1, 15);
                System.out.println(rapprox);
                System.out.println(r1);

                System.out.println(i + ":Ball :" + myBall);
                System.out.println(i + ":Ball Pos Diff:" + myBall.position.minus(bt.ballTrace.get(i).position).length());
                System.out.println(i + ":Rob pos diff:" + r1.position.minus(rapprox.position).length());
                System.out.println(i + ":Rob Velo diff:" + r1.velocity.minus(rapprox.velocity).length());

//                Ball before call sim:MyBall{p={x=0.0, y=2.212034213333523, z=0.0}, v={dx=0.0, dy=-1.1975349999998215, dz=0.0}} beforeTouchTick: 84
//                Ball after col sim:MyBall{p={x=0.13572315494419418, y=2.388330332298954, z=0.6405033872765454}, v={dx=8.946372727291507, dy=11.513528743250749, dz=42.219634800154836}}


//                if(r1.position.minus(myBall.position).length() < 4) {
//                    System.out.println(i + ": len = " + r1.position.minus(myBall.position).length());
//                    System.out.println("ball:" + myBall);
//                }
            } catch (GoalScoredException e) {
                System.out.println("Goal at " + i + ", pos: " + myBall.position);
                break;

            }
        }

    }

    @Test
    public void ballFlyWhat() throws Exception {
        System.out.println(MathUtils.whenHitGround(2.388330332298954, 11.513528743250749) * 60);
//        MyBall mb = TestUtils.ballInTheAir(new Position(0.13570986826334336, 2.389343734592452, 0.6413302779300499));
//        mb.velocity = of(8.932707744799497, 11.561345447617931, 42.21370202440609);
        MyBall mb = TestUtils.ballInTheAir(new Position(0.13572315494419418, 2.388330332298954, 0.6405033872765454));
        mb.velocity = Vector3d.of(8.946372727291507, 11.513528743250749, 42.219634800154836);
        MyBall mbcopy = mb.clone();

        for (int i = 1; i < 160; i++) {

            try {
                Simulator.tick(rules, Collections.emptyList(), mb);
                System.out.println(i + "Ball:" + mb);
                System.out.println(i + "Math:" + LookAhead.ballPositionFly(mbcopy, i / Constants.TICKS_PER_SECOND));
            } catch(GoalScoredException e) {
                System.out.println("Goals scored at: " + i);
                break;
            }
        }


        BallGoal bg = LookAhead.ballFlyUntouched(rules, mbcopy);
        System.out.println(bg);

    }

    @Test
    public void testKickFirstLahStraight() throws Exception {
        MyRobot r1 = TestUtils.robotInTheAir(new Position(-20, 1.5, -35));
        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, 0));

        JumpCondition jc = (r, b) -> {
            if (r.position.minus(b.position).length() < 4) {
                return 15;
            } else {
                return 0;
            }
        };

        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {

            BestMoveDouble bmd = LookAhead.singleRobotKickGoalGroundOld(rules, r1, myBall, jc, -Math.PI, Math.PI, 72,
                    (Constants.ROBOT_MAX_RADIUS + Constants.BALL_RADIUS) + 4, false, 150, 300, 5);

            System.out.println(bmd);

        }
        System.out.println(Dan.DAN_COUNT + " " + (System.currentTimeMillis() - start) + "ms");

//        LookAhead


    }

    @Test
    public void testKickFirstLaGoal() throws Exception {
        MyRobot r1 = TestUtils.robotInTheAir(new Position(-20, 1.5, -35));
        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, 0));

        JumpCondition jc = (r, b) -> {
            if (r.position.minus(b.position).length() < 5) {
                return 15;
            } else {
                return 0;
            }
        };

        long start = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            BestMoveDouble bmd = LookAhead.singleRobotKickGoalGroundOld(rules, r1, myBall, jc, 0.95993, 1.1344, 30,
                    (Constants.ROBOT_MAX_RADIUS + Constants.BALL_RADIUS) + 4, true, 150, 300, 100);

            System.out.println(bmd);

        }
        System.out.println(Dan.DAN_COUNT + " " + (System.currentTimeMillis() - start) + "ms");

//        LookAhead


    }


    @Test
    public void testGroundMove() throws Exception {



    }
}