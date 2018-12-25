import ai.Constants;
import ai.model.MyBall;
import ai.model.MyRobot;
import model.*;

import java.util.Map;

import static ai.model.Vector3d.of;

public final class JustKickStrategy implements MyMyStrategy {

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
        Action action = new Action();

        if(r.position.z <= ball.position.z) {
            action.target_velocity_x = (ball.position.x - r.position.x) * 100;
            action.target_velocity_z = (ball.position.z - r.position.z) * 100;
        } else {
            action.target_velocity_x = (ball.position.x - r.position.x) * 100;
            action.target_velocity_z = -(arena.depth / 2 + Constants.ROBOT_MIN_RADIUS * 2) - r.position.z;
        }
        action.target_velocity = of(action.target_velocity_x, action.target_velocity_y, action.target_velocity_z);

        r.action = action;
    }


    @Override
    public void act(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena) {
        myRobots.values().forEach(r -> {
            act(r, ball, arena);
        });
    }
}
