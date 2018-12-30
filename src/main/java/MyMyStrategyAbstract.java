import ai.model.MyBall;
import ai.model.MyRobot;
import model.Action;
import model.Arena;

import java.util.HashMap;
import java.util.Map;

/**
 * I'm so good in class naming today!
 */
public abstract class MyMyStrategyAbstract {

    public Map<Integer, Action> thisTickActions = new HashMap<>();
    public Map<Integer, Action> previousTickAction = new HashMap<>();
    public int lastTickProcessed = -1;

    public abstract void computeTickLogic(int tickNumber, Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena);

    public void computeTick(int tickNumber, Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena) {
        computeTickLogic(tickNumber, myRobots, opponentRobots, ball, arena);

        previousTickAction.putAll(thisTickActions);
    }

    public Action act(int myRobotId) {
        return thisTickActions.get(myRobotId);
    }

    public boolean isTickComputed(int tickNumber) {
        return tickNumber == lastTickProcessed;
    }
}
