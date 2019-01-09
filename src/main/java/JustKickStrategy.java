import model.*;

import java.util.Map;

public final class JustKickStrategy implements MyMyStrategy {

    private final boolean jumping;



    public JustKickStrategy() {
        this.jumping = false;
    }
    public JustKickStrategy(boolean jumping) {
        this.jumping = jumping;
    }

//    public void act(Robot me, Rules rules, Game game, Action action) {
//        if(me.z <= game.ball.z) {
//            action.target_velocity_x = (game.ball.x - me.x) * 100;
//            action.target_velocity_z = (game.ball.z - me.z) * 100;
//        } else {
//            action.target_velocity_x = (game.ball.x - me.x) * 100;
//            action.target_velocity_z = -(rules.arena.depth / 2 + rules.ROBOT_MIN_RADIUS * 2) - me.z;
//        }
//
//    }


    public void act(MyRobot r, MyBall ball, Arena arena) {
        MyAction action = new MyAction();

        double target_velocity_x;
        double target_velocity_y = 0;
        double target_velocity_z;

        if(r.position.z <= ball.position.z) {
            target_velocity_x = (ball.position.x - r.position.x) * 100;
            target_velocity_z = (ball.position.z - r.position.z) * 100;
        } else {
            target_velocity_x = (ball.position.x - r.position.x) * 100;
            target_velocity_z = -(arena.depth * 0.5 + Constants.ROBOT_MIN_RADIUS * 2) - r.position.z;
        }
        action.target_velocity = Vector3d.of(target_velocity_x, target_velocity_y, target_velocity_z);

        if(jumping) {
            Vector3d toBall = ball.position.minus(r.position);
            Vector3d flatPos = Vector3d.of(toBall.dx, 0, toBall.dz);

            if(flatPos.length() < Constants.ROBOT_RADIUS + Constants.BALL_RADIUS + 1) {
                action.jump_speed = Constants.ROBOT_MAX_JUMP_SPEED;
            }
        }

        r.action = action;
    }


    @Override
    public void act(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena, int ct) {
        myRobots.values().forEach(r -> {
            act(r, ball, arena);
        });
    }

    @Override
    public void setRules(Rules rules) {

    }
}
