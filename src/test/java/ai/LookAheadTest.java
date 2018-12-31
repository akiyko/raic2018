package ai;

import ai.model.*;
import ai.plan.BallTrace;
import ai.plan.BestMoveDouble;
import ai.plan.JumpCondition;
import model.Arena;
import model.Rules;
import org.junit.Test;

import java.util.Random;

import static ai.model.Vector3d.of;
import static org.junit.Assert.*;

/**
 * @author akiyko
 * @since 12/29/2018.
 */
public class LookAheadTest {
    private Rules rules = TestUtils.standardRules();

    public static final Random rnd = new Random(0);


    @Test
    public void testBallTracePerf() throws Exception {
        long goals = 0;

        int iterations = 20000;
        int tickDepth = 300;
        int mpt = 100;
        long start=System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            MyBall mb = randomBall();
            BallTrace bt1 =  LookAhead.ballUntouchedTraceOptimized(rules, mb, tickDepth, mpt);

            if(bt1.goalScoredTick > 0 ) {
                goals ++;
            }
        }

        System.out.println("Goals" + goals);
        System.out.println("Dan count: " + Dan.DAN_COUNT * 0.000001);
        System.out.println(System.currentTimeMillis() - start + "ms");
        System.out.println(((double)(System.currentTimeMillis() - start)) / iterations + "ms per tick");
    }


    @Test
    public void testBallTraceOptimizedCorrect() throws Exception {
        long goals = 0;

        int iterations = 200;
        int tickDepth = 300;
        int mpt = 100;
        long start=System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            MyBall mb = randomBall();
            MyBall mb2 = mb.clone();
            MathUtils.r = new Random(0);
            BallTrace bt1 =  LookAhead.ballUntouchedTraceSimulator(rules, mb, tickDepth, mpt);
            MathUtils.r = new Random(0);
            BallTrace bt2 =  LookAhead.ballUntouchedTraceOptimized(rules, mb2, tickDepth, mpt / 10);

            if(bt1.goalScoredTick > 0 ) {
                goals ++;
            }

            assertEquals(String.valueOf(i), bt1.ballTrace.size(), bt2.ballTrace.size());
            for (int j = 0; j < bt2.ballTrace.size(); j++) {

                MyBall b1 = bt1.ballTrace.get(j);
                MyBall b2 = bt2.ballTrace.get(j);
//                assertTrue(i + "/" + j,b1.position.doubleEquals(b2.position));
            }

            System.out.println(bt1.ballTrace.get(bt1.ballTrace.size()-1).position);
            System.out.println(bt2.ballTrace.get(bt2.ballTrace.size()-1).position);

        }

        System.out.println("Goals" + goals);
        System.out.println(System.currentTimeMillis() - start + "ms");
        System.out.println((System.currentTimeMillis() - start) / iterations + "ms per tick");
    }

    public static MyBall randomBall() {
        MyBall ball = new MyBall();

        ball.velocity = of(random(-10.0, 10.0), random(-10.0, 10.0),random(-10.0, 10.0));
        ball.position = new Position(random(-25.0, 25.0), random(3.0, 5.0), random(-35.0, 35.0));

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
    public void testKickFirstLahStraight() throws Exception {
        MyRobot r1 = TestUtils.robotInTheAir(new Position(-20, 1.5, -35));
        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, 0));

        JumpCondition jc = (r, b) -> {
            if(r.position.minus(b.position).length() < 4) {
                return 15;
            } else {
                return 0;
            }
        };

        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            BestMoveDouble bmd = LookAhead.singleRobotKickGoalGround(rules, r1, myBall, jc, -Math.PI, Math.PI, 72,
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
            if(r.position.minus(b.position).length() < 5) {
                return 15;
            } else {
                return 0;
            }
        };

        long start = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            BestMoveDouble bmd = LookAhead.singleRobotKickGoalGround(rules, r1, myBall, jc, 0.95993, 1.1344, 30,
                    (Constants.ROBOT_MAX_RADIUS + Constants.BALL_RADIUS) + 4, true, 150, 300, 100);

            System.out.println(bmd);

        }
        System.out.println(Dan.DAN_COUNT + " " + (System.currentTimeMillis() - start) + "ms");

//        LookAhead


    }
}