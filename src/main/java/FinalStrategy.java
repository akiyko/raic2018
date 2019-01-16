import model.Arena;
import model.Rules;

import java.util.*;

public final class FinalStrategy extends MyMyStrategyAbstract implements MyMyStrategy {

    Rules rules;

    final StrategyParams p = new StrategyParams();
    RobotPrecalcPhysics phys = null;

    Map<Integer, RobotAction> activeActions = new HashMap<>();

    @Override
    public void computeTickLogic(int tickNumber, Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Rules rules) {

        this.setRules(rules);

        act(myRobots, opponentRobots, ball, rules.arena, tickNumber);

        myRobots.forEach((id, mr) -> {
            thisTickActions.put(id, mr.action);
        });
    }


    public void cleanupOutdatedActions(int currentTick) {
        activeActions.entrySet()
                .removeIf(e -> e.getValue().validToTick() < currentTick);
    }

    public void actGoalOrDefence(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots,
                                 MyBall ball, Arena arena, int currentTick) {

    }

    public void actNitroHunt(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots,
                             MyBall ball, Arena arena, int currentTick) {

    }

    public void actPositioning(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots,
                               MyBall ball, Arena arena, int currentTick) {

    }

    public void actAgainstEnemy(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots,
                                MyBall ball, Arena arena, int currentTick) {

    }

    public void setActions(Map<Integer, MyRobot> myRobots, int currentTick) {
        myRobots.forEach((id, r) ->
                r.action = activeActions.getOrDefault(id, new RobotDoNothingAction(currentTick, currentTick))
                        .act(currentTick, r.pv()));
    }

    @Override
    public void act(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena, int currentTick) {
        if (phys == null) {
            phys = RobotPrecalcPhysics.calculate(rules);
        }

        cleanupOutdatedActions(currentTick);

        actGoalOrDefence(myRobots, opponentRobots, ball, arena, currentTick);
        actNitroHunt(myRobots, opponentRobots, ball, arena, currentTick);
        actPositioning(myRobots, opponentRobots, ball, arena, currentTick);
        actAgainstEnemy(myRobots, opponentRobots, ball, arena, currentTick);

        setActions(myRobots, currentTick);
    }

    @Override
    public void setRules(Rules rules) {
        this.rules = rules;
    }

}
