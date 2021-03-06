import model.Action;
import model.Arena;
import model.Rules;

import java.util.*;

public final class SingleKickGoalLahStrategy extends MyMyStrategyAbstract implements MyMyStrategy {

    int tickDepth = 260;
    int mpt = 100;

    int planRecalculateFrequency = Integer.MAX_VALUE; //never
    int planCalculateGoalPlanFrequency = 5; //if failed to find a goal plan - don't try ~ next 5 ticks

    Rules rules;



    Map<Integer, JumpCommand> jumpTick = new HashMap<>();//when touch floor - unset this
    Map<Integer, RobotMoveJumpPlan> thisTickPlans = new HashMap<>();//when touch floor - unset this
    Map<Integer, RobotMoveJumpPlan> previousTickPlans = new HashMap<>();

    @Override
    public void computeTickLogic(int tickNumber, Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Rules rules) {

//        System.out.println("JujpTick: " + jumpTick);
        this.setRules(rules);

        act(myRobots, opponentRobots, ball, rules.arena, tickNumber);

        myRobots.forEach((id, mr) -> {
            thisTickActions.put(id, mr.action);
        });

    }

    @Override
    public void act(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena, int currentTick) {
        thisTickPlans.clear();

        setBackToStablesForAll(myRobots);

        setOrUnsetJump(myRobots, currentTick);
        BallTrace bt = LookAhead.ballUntouchedTraceOptimized(rules, ball.clone(), tickDepth, mpt);

        if(!previousTickPlans.isEmpty() || currentTick % planCalculateGoalPlanFrequency == 0) {//need to reset counter after opponent touchs the ball
            for (MyRobot myRobot : myRobots.values()) {
                if(currentTick % planCalculateGoalPlanFrequency != 0) {
                    //skip other robots if not calculate tick
                    if(!previousTickPlans.containsKey(myRobot.id)) {
                        continue;
                    }
                }

                if (myRobot.touch && Vector3d.dot(myRobot.touch_normal, Vector3d.of(0.0, 1.0, 0.0)) > 0.99) {
                    //check previous
                    Optional<RobotMoveJumpPlan> previousPlan = Optional.ofNullable(previousTickPlans.get(myRobot.id));
                    boolean recalculateAnyway = ((currentTick + 1) % planRecalculateFrequency == 0);
                    if (!recalculateAnyway && previousPlan.isPresent()) {
                        Optional<RobotMoveJumpPlan> recheckedPlan = LookAhead.robotMoveJumpGoalOptionsCheckPrevious(
                                previousPlan.get(), rules, myRobot.clone(), bt);

                        recheckedPlan.ifPresent(rmjplan -> thisTickPlans.put(myRobot.id, rmjplan));
//                    System.out.println("Recalculated plan used");
                    } else {
                        List<RobotMoveJumpPlan> rmjp = LookAhead.robotMoveJumpGoalOptions(rules, myRobot.clone(), bt);
                        if (!rmjp.isEmpty()) {
                            RobotMoveJumpPlan rmjplan = rmjp.get(0);
                            thisTickPlans.put(myRobot.id, rmjplan);
                        }
                    }
                }
            }
        }

        OptionalInt bestGoal = thisTickPlans.values().stream()
                .mapToInt(p -> p.gamePlanResult.goalScoredTick).min();


        if (bestGoal.isPresent()) {
            Map.Entry<Integer, RobotMoveJumpPlan> bestGoalPlan = thisTickPlans.entrySet().stream().filter(e -> e.getValue().gamePlanResult.goalScoredTick == bestGoal.getAsInt())
                    .findAny().orElse(null);

            thisTickPlans.entrySet().removeIf(e -> !e.getKey().equals(bestGoalPlan.getKey()));

            if (bestGoalPlan != null) {
//                System.out.println(currentTick + ": " + bestGoalPlan);

//                thisTickPlans.put(bestGoalPlan.getKey(), bestGoalPlan.getValue());
                jumpTick.clear();
                jumpTick.put(bestGoalPlan.getKey(), new JumpCommand(bestGoalPlan.getValue().jumpTick + currentTick,
                        bestGoalPlan.getValue().jumpSpeed));

                int id = bestGoalPlan.getKey();
                myRobots.get(id).action.target_velocity = thisTickPlans.get(id).targetVelocity;
            }
        }
//
//        for (Map.Entry<Integer, RobotMoveJumpPlan> planEntry : thisTickPlans.entrySet()) {
//            int id = planEntry.getKey();
//
//            myRobots.get(id).action.target_velocity = thisTickPlans.get(id).targetVelocity;
//        }

        previousTickPlans.clear();
        previousTickPlans.putAll(thisTickPlans);
    }

    @Override
    public void setRules(Rules rules) {
        this.rules = rules;
    }


    private void setOrUnsetJump(Map<Integer, MyRobot> myRobots, int currentTick) {
        myRobots.forEach((id, mr) -> {
            if ((mr.touch || isGoingToTouchGround(mr)) && jumpTick.containsKey(id) && jumpTick.get(id).jumpTick < currentTick) {
                jumpTick.remove(id);
            }
            if (jumpTick.containsKey(id) && jumpTick.get(id).jumpTick <= currentTick) {
                mr.action.jump_speed = jumpTick.get(id).jumpSpeed;
            }
        });
    }

    private boolean isGoingToTouchGround(MyRobot mr) {
        return mr.position.y < mr.radius * 1.5 && mr.velocity.dy < 0;

    }

    private void setBackToStablesForAll(Map<Integer, MyRobot> myRobots) {
        myRobots.forEach((id, mr) -> {
            mr.action = new MyAction();
            mr.action.target_velocity =
                    new Position(0, 1, -0.5 * rules.arena.depth).minus(mr.position).zeroY()
                            .normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);
        });
    }
}
