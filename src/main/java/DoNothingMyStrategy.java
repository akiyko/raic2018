import model.*;

import java.util.Map;

public final class DoNothingMyStrategy implements MyMyStrategy {

    public void act(MyRobot r, MyBall ball, Arena arena) {
        MyAction action = new MyAction();

        action.target_velocity = Vector3d.of(0,0,0);
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
