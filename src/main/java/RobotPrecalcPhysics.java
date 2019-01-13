import model.Rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * By no one on 12.01.2019.
 */
public class RobotPrecalcPhysics {
    public static final int steps = (int) Constants.ROBOT_MAX_GROUND_SPEED;
    public static final int maxTickDepth = 100;

    public static TargetVelocityProv tang = pv -> pv.v;

    //0 = speed 0, 1 = speed 1, ... size 31;
    ArrayList<List<PV>> tangJumpFromSpeedWithNitro = new ArrayList<>();
    ArrayList<List<PV>> tangJumpFromSpeedWithoutNitro = new ArrayList<>();

    public static RobotPrecalcPhysics calculate(Rules rules) {
        RobotPrecalcPhysics ph = new RobotPrecalcPhysics();

        for (int i = 0; i <= steps ; i++) {
            ph.tangJumpFromSpeedWithNitro.add(
            RobotNitroJumps.robotTraceFromSpeediZeroPosJump(rules, (double) i, maxTickDepth, Constants.ROBOT_MAX_JUMP_SPEED, tang, true));
            ph.tangJumpFromSpeedWithoutNitro.add(
            RobotNitroJumps.robotTraceFromSpeediZeroPosJump(rules, (double) i, maxTickDepth, Constants.ROBOT_MAX_JUMP_SPEED, tang, false));
        }

        return ph;
    }


}
