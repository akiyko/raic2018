package ai;

import ai.model.Dan;
import ai.model.MyBall;
import ai.model.MyRobot;
import ai.model.Position;
import ai.plan.BestMoveDouble;
import ai.plan.JumpCondition;
import model.Arena;
import model.Rules;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author akiyko
 * @since 12/29/2018.
 */
public class LookAheadTest {
    private Rules rules = TestUtils.standardRules();



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