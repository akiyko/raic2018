package ai;

import ai.model.MyBall;
import ai.model.MyRobot;
import ai.model.Position;
import model.Arena;
import model.Rules;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ai.TestUtils.*;

/**
 * By no one on 22.12.2018.
 */
public class PlayTest {
    public static final int GAME_TICKS_SHORT = 10_000;

    @Test
    public void testPlayAgame() throws Exception {
        Arena arena = TestUtils.standardArena();
        Rules rules = new Rules();
        rules.arena = arena;

        MyRobot r = robotInTheAir(new Position(0, arena.height / 2, 0));
        r.action = doNothingAction();
        MyBall ball = ballInTheAir(new Position(Constants.BALL_RADIUS * 4, arena.height / 2, Constants.BALL_RADIUS * 4));

        List<MyRobot> robots = Collections.singletonList(r);

        for (int i = 0; i < GAME_TICKS_SHORT; i++) {
            System.out.println("Tick # " + i + "Robot: " + r.position);
            Simulator.tick(rules, robots, ball);
            if(r.touch) {
                r.action.jump_speed = Constants.ROBOT_MAX_JUMP_SPEED;
            } else {
                r.action.jump_speed = 0;
            }
        }
    }
}
