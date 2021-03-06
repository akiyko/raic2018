import model.Arena;
import model.Rules;

import java.util.Map;

/**
 * @author akiyko
 * @since 12/25/2018.
 */
public interface MyMyStrategy {
    //sets action to every myRobot
    //id -> robot
    void act(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena, int currentTick);

    void setRules(Rules rules);
}
