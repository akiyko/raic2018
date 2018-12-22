package ai;

import ai.model.MyBall;
import ai.model.MyRobot;
import ai.model.Position;
import ai.model.Vector3d;
import model.Action;
import model.Arena;
import org.junit.BeforeClass;

import static ai.model.Vector3d.of;

/**
 * By no one on 22.12.2018.
 */
public class TestUtils {
    public static Arena standardArena() {
        Arena arena = new Arena();

        //Arena{width=60.0, height=20.0, depth=80.0, bottom_radius=3.0,
        // top_radius=7.0, corner_radius=13.0, goal_top_radius=3.0,
        // goal_width=30.0, goal_height=10.0, goal_depth=10.0, goal_side_radius=1.0}
        arena.width = 60;
        arena.height = 20;
        arena.depth = 80;
        arena.bottom_radius = 3;
        arena.top_radius = 7;
        arena.corner_radius = 13;
        arena.goal_top_radius = 3;
        arena.goal_width = 30;
        arena.goal_height = 10;
        arena.goal_depth = 10;
        arena.goal_side_radius = 1;

        return arena;
    }

    public static MyRobot robotInTheAir(Position startLocation) {
        MyRobot robot = new MyRobot();

        robot.velocity = of(0,0,0);
        robot.position = startLocation;

        robot.arena_e = Constants.ROBOT_ARENA_E;

        robot.touch = false;
        robot.radiusChangeSpeed = 0;

        robot.mass = Constants.ROBOT_MASS;
        robot.radius = Constants.ROBOT_RADIUS;

        return robot;
    }



    public static MyBall ballInTheAir(Position startLocation) {
        MyBall ball = new MyBall();

        ball.velocity = of(0,0,0);
        ball.position = startLocation;

        ball.arena_e = Constants.BALL_ARENA_E;

        ball.radiusChangeSpeed = 0;

        ball.mass = Constants.BALL_MASS;
        ball.radius = Constants.BALL_RADIUS;

        return ball;
    }

    public static Action doNothingAction() {
        Action action = new Action();
        action.use_nitro = false;
        action.jump_speed = 0;
        action.target_velocity_x = 0;
        action.target_velocity_y = 0;
        action.target_velocity_z = 0;
        action.target_velocity = of(0,0,0);

        return action;
    }

    public static Action alwaysJumpAction() {
        Action action = new Action();
        action.use_nitro = false;
        action.jump_speed = Constants.ROBOT_MAX_JUMP_SPEED;
        action.target_velocity_x = 0;
        action.target_velocity_y = 0;
        action.target_velocity_z = 0;
        action.target_velocity = of(0,0,0);

        return action;
    }
}
