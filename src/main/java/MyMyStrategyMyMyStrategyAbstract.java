import model.Rules;

import java.util.Map;

/**
 * By no one on 04.01.2019.
 */
public class MyMyStrategyMyMyStrategyAbstract extends MyMyStrategyAbstract {
    private final MyMyStrategy myMyStrategy;

    public MyMyStrategyMyMyStrategyAbstract(MyMyStrategy myMyStrategy) {
        this.myMyStrategy = myMyStrategy;
    }


    @Override
    public void computeTickLogic(int tickNumber, Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Rules rules) {
        myMyStrategy.setRules(rules);
        myMyStrategy.act(myRobots, opponentRobots, ball, rules.arena, tickNumber);
    }
}
