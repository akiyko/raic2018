import ai.Constants;
import ai.LookAhead;
import ai.model.MyBall;
import ai.model.MyRobot;
import ai.model.Vector3d;
import ai.plan.BestMoveDouble;
import ai.plan.JumpCondition;
import model.Action;
import model.Arena;
import model.Rules;

import java.util.Map;

import static ai.model.Vector3d.of;

public final class SingleKickGoalLahStrategy implements MyMyStrategy {



    @Override
    public void act(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena) {
//        MyRobot rob = myRobots.get(0);
//        Rules rules = new Rules();
//        rules.arena = arena;
//
//        JumpCondition jc = (r, b) -> {
//            if (r.position.minus(b.position).length() < 5) {
//                return 15;
//            } else {
//                return 0;
//            }
//        };
//
//
//        BestMoveDouble mbd = LookAhead.singleKickGoalBase(rules, rob.clone(), ball.clone(), jc);
//
//        rob.action = new Action();
//        rob.action.jump_speed = jc.jumpSpeed(rob, ball);
//        rob.action.target_velocity = mbd.middleTargetVelocityAngleGround();
//        System.out.println("Bmd:" + mbd);
//        System.out.println("JU: " + rob.action.jump_speed);;


    }
}
