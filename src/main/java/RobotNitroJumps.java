import model.Rules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author akiyko
 * @since 1/11/2019.
 */
public class RobotNitroJumps {
    //jump at 0 tick and use nitro on 1
    public static List<MyRobot> maxSpeedNitroJumpFromZero(MyRobot mr, int ticksDetch, int mpt) {
        return null;
    }

    public static List<MyRobot> robotJumpNitroTrace(Rules rules, MyRobot myRobotStart,
                                                    int ticksDepth, int mpt, double jumpSpeed,
                                                    int useNitroSinceTick,
                                                    Function<MyRobot, Vector3d> targetVelocityFun) {
        List<MyRobot> robotTrace = new ArrayList<>();
        MyRobot mr = myRobotStart.clone();
        robotTrace.add(mr.clone());
        for (int i = 0; i < ticksDepth; i++) {
            mr.action.target_velocity = targetVelocityFun.apply(mr);
            if (i == 0) {
                mr.action.jump_speed = jumpSpeed;
            }
            if (i >= useNitroSinceTick) {
                mr.action.use_nitro = true;
            }

            Simulator.tickRobotOnly(rules, mr, mpt);
            robotTrace.add(mr.clone());
        }

        return robotTrace;
    }

    public static List<MyRobot> robotTraceFromMaxSpeedZeroPosToZ(Rules rules, int ticksDepth, int mpt, double jumpSpeed,
                                                                 int useNitroSinceTick,
                                                                 Function<MyRobot, Vector3d> targetVelocityFun) {
        MyRobot mr = new MyRobot();
        mr.position = new Position(0, Constants.ROBOT_RADIUS, 0);
        mr.touch = true;
        mr.touch_normal = Vector3d.of(0, 1, 0);
        mr.velocity = Vector3d.of(0, 0, Constants.ROBOT_MAX_GROUND_SPEED);
        mr.action = new MyAction();
        mr.action.target_velocity = mr.velocity;
//        mr.nitro = ticksDepth;//has nitro for all ticks

        return robotJumpNitroTrace(rules, mr, ticksDepth, mpt, jumpSpeed, useNitroSinceTick, targetVelocityFun);
    }

    public static List<MyRobot> robotTraceFromMaxSpeedZeroPosToZTangMaxJumpFirstTick(Rules rules, int ticksDepth) {
        Function<MyRobot, Vector3d> tangVelocity = r -> r.velocity.normalize().multiply(Constants.MAX_ENTITY_SPEED);

        return robotTraceFromMaxSpeedZeroPosToZ(
                rules, ticksDepth, (int)Constants.MICROTICKS_PER_TICK, Constants.ROBOT_MAX_JUMP_SPEED, 1, tangVelocity);
    }

}
