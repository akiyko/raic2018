package ai;

import ai.model.MyBall;
import ai.model.MyRobot;
import ai.model.Position;
import ai.model.Vector3d;
import ai.plan.*;
import model.Action;
import model.Arena;
import model.Rules;

import java.util.Collections;
import java.util.Optional;

import static ai.model.Vector3d.of;

/**
 * @author akiyko
 * @since 12/27/2018.
 */
public class LookAhead {
    public static GamePlanResult predictRobotBallFuture(Rules rules, MyRobot myRobot,
                                                        MyBall myBall, RobotGamePlan robotGamePlan, int tickDepth, int mpt) {
        GamePlanResult result = new GamePlanResult();
        Vector3d originalVelocity = myRobot.velocity;

        for (int i = 0; i < tickDepth; i++) {
            myRobot.action = new Action();
            myRobot.action.target_velocity = robotGamePlan.targetVelocityProvider
                    .targetVelocity(originalVelocity, myRobot.position, i);
            myRobot.action.jump_speed = robotGamePlan.jumpCondition.jumpSpeed(myRobot, myBall);

            Vector3d toBall = myBall.position.minus(myRobot.position);
            if (toBall.lengthSquare() < result.minToBall.lengthSquare()) {
                result.minToBall = toBall;
            }

            Vector3d toGateCenterMin = oppGateCenter(rules.arena, myBall);
            if (toGateCenterMin.lengthSquare() < result.minBallToOppGateCenter.lengthSquare()) {
                result.minBallToOppGateCenter = toGateCenterMin;
            }

            try {
                Simulator.tick(rules, Collections.singletonList(myRobot), myBall, mpt);
            } catch (GoalScoredException e) {
                if (e.getZ() > 0) {
                    result.goalScoredTick = i;
                } else {
                    result.oppGoalScored = i;
                }
                break;
            }
        }

        return result;
    }

    public static BestMoveDouble singleKickGoalBase(Rules rules, MyRobot myRobot,
                                  MyBall myBall, JumpCondition jumpCondition) {
        BestMoveDouble bmdBall = LookAhead.singleRobotKickGoalGround(rules, myRobot.clone(), myBall.clone(), jumpCondition, -Math.PI, Math.PI, 72,
                (Constants.ROBOT_MAX_RADIUS + Constants.BALL_RADIUS) + 2, false, 150, 300, 100);

        System.out.println("BmdBall: " + bmdBall);

        BestMoveDouble bmdGoal = LookAhead.singleRobotKickGoalGround(rules, myRobot.clone(), myBall.clone(), jumpCondition, bmdBall.low, bmdBall.hi, 72,
                0.0, true, 150, 300, 100);

        return bmdGoal;
    }

    public static BestMoveDouble singleRobotKickGoalGround(Rules rules, MyRobot myRobot,
                                                           MyBall myBall, JumpCondition jumpCondition,
                                                           double minAngle, double maxAngle, long steps,
                                                           double minLenToBallRequired,
                                                           boolean goalRequired,
                                                           int toBallTickDepth, int toGateTickDepth,
                                                           int mpt) {

        double dangle = (maxAngle - minAngle) / steps;

        Optional<Double> minAngleConditionMatched = Optional.empty();
        Optional<Double> bestAngleConditionMatched = Optional.empty();
        Optional<Double> maxAngleConditionMatched = Optional.empty();
        GamePlanResult low = null;
        GamePlanResult high = null;


        MyRobot mr = myRobot.clone();
        MyBall mb = myBall.clone();

        for (int i = 0; i <= steps; i++) {
            double x = Math.cos(minAngle + dangle * i);
            double z = Math.sin(minAngle + dangle * i);

            RobotGamePlan plan = new RobotGamePlan();
            plan.targetVelocityProvider = new FixedTargetVelocity(of(x, 0, z).multiply(Constants.ROBOT_MAX_GROUND_SPEED));
            plan.jumpCondition = jumpCondition;

            if (!goalRequired) {
                GamePlanResult res = predictRobotBallFuture(rules, mr.clone(), mb.clone(), plan, toBallTickDepth, mpt);

//                System.out.println(i + ": " + res.minToBall.length());
                if (res.minToBall.length() < minLenToBallRequired) {
                    if (!minAngleConditionMatched.isPresent()) {
                        minAngleConditionMatched = Optional.of(minAngle + dangle * i);
                        low = res;
                    }
                    maxAngleConditionMatched = Optional.of(minAngle + dangle * i);
                    high = res;
                }
            } else {
                GamePlanResult res = predictRobotBallFuture(rules, mr.clone(), mb.clone(), plan, toGateTickDepth, mpt);

                if (res.goalScoredTick > 0) {
                    if (!minAngleConditionMatched.isPresent()) {
                        minAngleConditionMatched = Optional.of(minAngle + dangle * i);
                        low = res;
                    }
                    maxAngleConditionMatched = Optional.of(minAngle + dangle * i);
                    high = res;
                }

            }
        }

        BestMoveDouble bestMove = new BestMoveDouble();
        bestMove.lowPlanResult = low;
        bestMove.low = minAngleConditionMatched.orElse(0.0);

        bestMove.hiPlanResult = high;
        bestMove.hi = maxAngleConditionMatched.orElse(0.0);

        return bestMove;
//      System.out.println("x/z: " + x + "/"  + z);
    }


    private static Vector3d oppGateCenter(Arena arena, MyBall myBall) {
        return new Position(arena.depth * 0.5 + Constants.BALL_RADIUS, arena.goal_height * 0.5, 0)
                .minus(myBall.position);
    }
}
