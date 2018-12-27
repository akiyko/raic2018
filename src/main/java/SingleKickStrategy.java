import ai.Constants;
import ai.LookAhead;
import ai.model.MyBall;
import ai.model.MyRobot;
import ai.model.Vector3d;
import ai.plan.*;
import model.Action;
import model.Arena;
import model.Rules;

import java.util.Map;

import static ai.model.Vector3d.of;

public final class SingleKickStrategy implements MyMyStrategy {

    public static final int TICK_DEPTH = 400;

    public void act(MyRobot r, MyBall ball, Arena arena) {
        Rules rules = new Rules();
        rules.arena = arena;

        int steps = 100;

        for (int i = 0; i < 100; i++) {
             double x = Math.cos(2 * Math.PI * i / steps);
             double z = Math.sin(2 * Math.PI * i / steps);

             MyRobot mr = r.clone();
             MyBall mb = ball.clone();

            RobotGamePlan plan = new RobotGamePlan();
            TargetVelocityProvider velocityProvider = new FixedTargetVelocity(of(x, 0, z).multiply(Constants.ROBOT_MAX_GROUND_SPEED));
            plan.initialPosition = mr.clone();
            plan.jumpCondition = (myRobot, myBall) -> 0;
            plan.targetVelocityProvider = velocityProvider;

            GamePlanResult gpr = LookAhead.predictRobotBallFuture(rules, r, mb, plan, TICK_DEPTH);
            System.out.println(i + " min len: " + gpr.minToBall.length());
        }

    }


    @Override
    public void act(Map<Integer, MyRobot> myRobots, Map<Integer, MyRobot> opponentRobots, MyBall ball, Arena arena) {
//        myRobots.values().forEach(r -> {
//            act(r, ball, arena);
//        });

        act(myRobots.get(0), ball, arena);

        Action nop = new Action();
        nop.target_velocity = of(0,0,0);
        myRobots.get(1).action = nop;
        myRobots.get(0).action = nop;
    }
}
