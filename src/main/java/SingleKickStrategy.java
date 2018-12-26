import ai.Constants;
import ai.model.MyBall;
import ai.model.MyRobot;
import ai.model.Vector3d;
import model.Action;
import model.Arena;

import java.util.Map;

import static ai.model.Vector3d.of;

public final class SingleKickStrategy implements MyMyStrategy {

    private final boolean jumping;



    public SingleKickStrategy() {
        this.jumping = false;
    }
    public SingleKickStrategy(boolean jumping) {
        this.jumping = jumping;
    }

    public void act(MyRobot r, MyBall ball, Arena arena) {
        Action action = new Action();

        if(r.position.z > 5) {
            action.target_velocity = of(0,0,0);
        } else {

            if (r.position.z <= ball.position.z) {
                action.target_velocity_x = (ball.position.x - r.position.x) * 100;
                action.target_velocity_z = (ball.position.z - r.position.z) * 100;
            } else {
                action.target_velocity_x = (ball.position.x - r.position.x) * 100;
                action.target_velocity_z = -(arena.depth / 2 + Constants.ROBOT_MIN_RADIUS * 2) - r.position.z;
            }
            action.target_velocity = of(action.target_velocity_x, action.target_velocity_y, action.target_velocity_z);

            if (jumping) {
                Vector3d toBall = ball.position.minus(r.position);
                Vector3d flatPos = of(toBall.dx, 0, toBall.dz);

                if (flatPos.length() < Constants.ROBOT_RADIUS + Constants.BALL_RADIUS - 0.2) {
                    action.jump_speed = Constants.ROBOT_MAX_JUMP_SPEED;
                }
            }

        }
        r.action = action;
    }


    @Override
    public void act(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena) {
//        myRobots.values().forEach(r -> {
//            act(r, ball, arena);
//        });

        act(myRobots.get(0), ball, arena);

        Action nop = new Action();
        nop.target_velocity = of(0,0,0);
        myRobots.get(1).action = nop;
    }
}
