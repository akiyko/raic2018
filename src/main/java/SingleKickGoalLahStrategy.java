import ai.Constants;
import ai.LookAhead;
import ai.model.MyBall;
import ai.model.MyRobot;
import ai.model.Position;
import ai.model.Vector3d;
import ai.plan.*;
import model.Action;
import model.Arena;
import model.Rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import static ai.LookAhead.robotMoveJumpGoalOptions;
import static ai.model.Vector3d.of;

public final class SingleKickGoalLahStrategy extends MyMyStrategyAbstract implements MyMyStrategy {

    int tickDepth = 200;
    int mpt = 100;

    Rules rules;

    Map<Integer, JumpCommand> jumpTick = new HashMap<>();//when touch floor - unset this
    Map<Integer, RobotMoveJumpPlan> thisTickPlans = new HashMap<>();//when touch floor - unset this

    @Override
    public void computeTickLogic(int tickNumber, Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Rules rules) {

        System.out.println("JujpTick: " + jumpTick);
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

        for (MyRobot myRobot : myRobots.values()) {
            if (myRobot.touch && Vector3d.dot(myRobot.touch_normal, of(0.0, 1.0, 0.0)) > 0.99) {

                List<RobotMoveJumpPlan> rmjp = LookAhead.robotMoveJumpGoalOptions(rules, myRobot.clone(), bt);
                if (!rmjp.isEmpty()) {
                    RobotMoveJumpPlan rmjplan = rmjp.get(0);
                    thisTickPlans.put(myRobot.id, rmjplan);
                }
            }
        }

        OptionalInt bestGoal = thisTickPlans.values().stream()
                .mapToInt(p -> p.gamePlanResult.goalScoredTick).min();


        if (bestGoal.isPresent()) {
            Map.Entry<Integer, RobotMoveJumpPlan> bestGoalPlan = thisTickPlans.entrySet().stream().filter(e -> e.getValue().gamePlanResult.goalScoredTick == bestGoal.getAsInt())
                    .findAny().orElse(null);

            if (bestGoalPlan != null) {
                System.out.println(currentTick + ": " + bestGoalPlan);

                thisTickPlans.put(bestGoalPlan.getKey(), bestGoalPlan.getValue());
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
            mr.action = new Action();
            mr.action.target_velocity =
                    new Position(0, 1, -0.5 * rules.arena.depth).minus(mr.position).zeroY()
                            .normalize().multiply(Constants.ROBOT_MAX_GROUND_SPEED);
        });
    }
}
