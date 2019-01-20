import model.Arena;
import model.Rules;

import java.util.*;
import java.util.stream.Collectors;

public final class FinalStrategy extends MyMyStrategyAbstract implements MyMyStrategy {

    Rules rules;

    final StrategyParams p = new StrategyParams();
    RobotPrecalcPhysics phys = null;
    public boolean improving = false;

    Map<Integer, RobotAction> activeActions = new HashMap<>();
    Map<Integer, RobotMoveJumpPlan> thisTickGoals = new HashMap<>();
    Map<Integer, RobotMoveJumpPlan> previousTickGoals = new HashMap<>();
    BallTrace bt; //this tick ball trace
    BallTrace oppKickBt; //this tick ball trace expecting opponent nearest to ball opponent run forwar, jump and kick


    public FinalStrategy(boolean improving) {
        this.improving = improving;
    }

    public FinalStrategy() {
    }

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

        Map<Integer, RobotMoveJumpPlan> goals = new HashMap<>();
        thisTickGoals.clear();

        for (MyRobot myRobot : myRobots.values()) {

            if (myRobot.touch && Vector3d.dot(myRobot.touch_normal, Vector3d.of(0.0, 1.0, 0.0)) > 0.99) {
                //check previous
                RobotMoveJumpPlan prevGoalPlan = previousTickGoals.get(myRobot.id);

                List<RobotMoveJumpPlan> rmjp = new ArrayList<>();

                if (prevGoalPlan != null) {
                    Optional<RobotMoveJumpPlan> prevChecked =
                            RobotLookAhead.robotMoveJumpGooalOptionsCheckPrevious(rules, phys, myRobot.pv(), bt, prevGoalPlan, p);
                    if (prevChecked.isPresent()) {
                        rmjp.add(prevChecked.get());
                    }
                } else {
                    rmjp = RobotLookAhead.robotMoveJumpGoalOptions(rules, phys, myRobot.clone(), bt, p);
                }
                if (!rmjp.isEmpty()) {
                    RobotMoveJumpPlan rmjplan = rmjp.get(0);
                    goals.put(myRobot.id, rmjplan);
                    thisTickGoals.put(myRobot.id, rmjplan);
                }
            }
        }

        OptionalInt bestGoal = goals.values().stream()
                .mapToInt(p -> p.gamePlanResult.goalScoredTick).min();


        if (bestGoal.isPresent()) {
            Map.Entry<Integer, RobotMoveJumpPlan> bestGoalPlan = goals.entrySet().stream().filter(e -> e.getValue().gamePlanResult.goalScoredTick == bestGoal.getAsInt())
                    .findAny().orElse(null);

//            System.out.println(currentTick + ", p: " + bestGoalPlan.getKey() + " " + bestGoalPlan.getValue());


            if (bestGoalPlan != null) {
                //cleanup other active 'goal' actions
                activeActions.entrySet()
                        .removeIf(e -> e.getValue().isGoal());

                activeActions.put(bestGoalPlan.getKey(),
                        bestGoalPlan.getValue().toRobotAction(currentTick));
            }
        } else {
//            if(improving) {
//                activeActions.entrySet()
//                        .removeIf(e -> e.getValue().isGoal());
//            }
        }

        previousTickGoals.clear();
        previousTickGoals.putAll(thisTickGoals);
//
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

    public void actBackDefendPosition(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots,
                                      MyBall ball, Arena arena, int currentTick) {
        Map<Integer, MyRobot> notActionedRobots = notActionedRobots(myRobots);

        Position dfb = new Position(0, 1, -0.5 * rules.arena.depth );

        MyRobot closest = notActionedRobots.values().stream().min(Comparator.comparing(r -> ((Double) dfb.minus(r.position).length())))
                .orElse(null);

        double defx = ball.position.x;
        double defz = -0.5 * rules.arena.depth + 1;
        if(StrategyParams.improving) {
            defz = -0.5 * rules.arena.depth - 1;
            if(ball.position.x > 0) {
                defx = Math.min(ball.position.x, rules.arena.width * 0.5 - arena.bottom_radius);
            } else {
                defx = Math.max(ball.position.x, -rules.arena.width * 0.5 + arena.bottom_radius);
            }
        }
        if(closest != null) {
            Vector3d target_velocity =
                    new Position(defx, 1, defz).minus(closest.position).zeroY() //TODO:!!! not to edge
                            .normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);
            activeActions.put(closest.id, new RobotMoveAction(currentTick, currentTick, target_velocity));
        }

    }

    public void actBackToGates(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots,
                               MyBall ball, Arena arena, int currentTick) {
        Map<Integer, MyRobot> notActionedRobots = notActionedRobots(myRobots);

        Position dfb;
        if(improving) {
            dfb = new Position(ball.position.x, 1, 0);
        } else {
            dfb = new Position(0, 1, -0.25 * rules.arena.depth);
        }

        MyRobot closest = notActionedRobots.values().stream().min(Comparator.comparing(r -> ((Double) dfb.minus(r.position).length())))
                .orElse(null);

        if(closest != null) {
            Vector3d target_velocity =
                    new Position(ball.position.x, 1, -0.5 * rules.arena.depth + 1).minus(closest.position).zeroY()
                            .normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);
            activeActions.put(closest.id, new RobotMoveAction(currentTick, currentTick, target_velocity));
        }
    }

    public void setActions(Map<Integer, MyRobot> myRobots, int currentTick) {
        myRobots.forEach((id, r) ->
                r.action = activeActions.getOrDefault(id, new RobotDoNothingAction(currentTick, currentTick))
                        .act(currentTick, r.pv()));
    }

    private Map<Integer, MyRobot> notActionedRobots(Map<Integer, MyRobot> myRobots) {
        return myRobots.entrySet().stream()
                .filter(e -> !activeActions.containsKey(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    @Override
    public void act(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena, int currentTick) {
        if (phys == null) {
            StrategyParams.arena = arena;
            phys = RobotPrecalcPhysics.calculate(rules);
        }
        StrategyParams.improving = improving;

        bt = LookAhead.ballUntouchedTraceOptimized(rules, ball.clone(), p.ballTickDepth, p.mpt);

        cleanupOutdatedActions(currentTick);

        actGoalOrDefence(myRobots, opponentRobots, ball, arena, currentTick);
        actNitroHunt(myRobots, opponentRobots, ball, arena, currentTick);
        actPositioning(myRobots, opponentRobots, ball, arena, currentTick);
        actAgainstEnemy(myRobots, opponentRobots, ball, arena, currentTick);

        actBackToGates(myRobots, opponentRobots, ball, arena, currentTick);
        actBackDefendPosition(myRobots, opponentRobots, ball, arena, currentTick);

        setActions(myRobots, currentTick);
    }

    @Override
    public void setRules(Rules rules) {
        this.rules = rules;
    }

}
