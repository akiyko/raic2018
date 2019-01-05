import ai.model.MyBall;
import ai.model.MyRobot;
import model.Action;
import model.Arena;
import model.Rules;

import java.util.HashMap;
import java.util.Map;

/**
 * I'm so good in class naming today!
 */
public abstract class MyMyStrategyAbstract {

    public Map<Integer, Action> thisTickActions = new HashMap<>();
    public Map<Integer, Action> previousTickAction = new HashMap<>();
    public int lastTickProcessed = -1;

    public abstract void computeTickLogic(int tickNumber, Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Rules rules);

    public final void computeTick(int tickNumber, Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Rules rules) {
        long start = System.currentTimeMillis();
        computeTickLogic(tickNumber, myRobots, opponentRobots, ball, rules);

        System.out.println("Tick took:" + (System.currentTimeMillis() - start) + "ms");

        previousTickAction.putAll(thisTickActions);
    }

    public Action act(int myRobotId) {
        return thisTickActions.get(myRobotId);
    }

    public boolean isTickComputed(int tickNumber) {
        return tickNumber == lastTickProcessed;
    }
}
