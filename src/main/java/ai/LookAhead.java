package ai;

import ai.model.MyBall;
import ai.model.MyRobot;
import ai.model.Position;
import ai.model.Vector3d;
import ai.plan.GamePlanResult;
import ai.plan.RobotGamePlan;
import model.Action;
import model.Arena;
import model.Rules;

import java.util.Collections;

/**
 * @author akiyko
 * @since 12/27/2018.
 */
public class LookAhead {
    public static GamePlanResult predictRobotBallFuture(Rules rules, MyRobot myRobot,
                                                       MyBall myBall, RobotGamePlan robotGamePlan, int tickDepth) {
        GamePlanResult result = new GamePlanResult();
        Vector3d originalVelocity = myRobot.velocity;

        for (int i = 0; i < tickDepth; i++) {
            myRobot.action = new Action();
            myRobot.action.target_velocity = robotGamePlan.targetVelocityProvider
                    .targetVelocity(originalVelocity, myRobot.position, i);
            myRobot.action.jump_speed = robotGamePlan.jumpCondition.jumpSpeed(myRobot, myBall);

            Vector3d toBall = myBall.position.minus(myRobot.position);
            if(toBall.lengthSquare() < result.minToBall.lengthSquare()) {
                result.minToBall = toBall;
            }

            Vector3d toGateCenterMin = oppGateCenter(rules.arena, myBall);
            if(toGateCenterMin.lengthSquare() < result.minBallToOppGateCenter.lengthSquare()) {
                result.minBallToOppGateCenter = toGateCenterMin;
            }

            try {
                Simulator.tick(rules, Collections.singletonList(myRobot), myBall);
            } catch (GoalScoredException e) {
                if(e.getZ() > 0) {
                    result.goalScoredTick = i;
                } else {
                    result.oppGoalScored = i;
                }
                break;
            }
        }

        return result;
    }



    private static Vector3d oppGateCenter(Arena arena, MyBall myBall) {
        return new Position(arena.depth * 0.5 + Constants.BALL_RADIUS, arena.goal_height * 0.5, 0)
                .minus(myBall.position);
    }
}
