import ai.Constants;
import ai.model.MyBall;
import ai.model.MyRobot;
import model.*;

import java.util.Map;

import static ai.model.Vector3d.of;

public final class DoNothingMyStrategy implements MyMyStrategy {

    public void act(MyRobot r, MyBall ball, Arena arena) {
        Action action = new Action();

        action.target_velocity = of(0,0,0);
        r.action = action;
    }

    @Override
    public void act(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena, int currentTick) {
        myRobots.values().forEach(r -> {
            act(r, ball, arena);
        });
    }

    @Override
    public void setRules(Rules rules) {

    }
}
