import java.util.Collections;
import java.util.List;

/**
 * By no one on 12.01.2019.
 */
public class RobotLookAhead {

    public static PV findRobotPvTang(RobotPrecalcPhysics phys, PV startOnGround, Vector3d targetVeloGround, boolean useNitroOnGround,
                                     boolean useNitroOnJump, int tick, int jumpTick) {

        PV pvBeforeJump = startOnGround;

        if (tick == 0) {
            return startOnGround;
        }

        if (jumpTick < tick) {
            pvBeforeJump = LookAhead.robotGroundMove(startOnGround, targetVeloGround, jumpTick, useNitroOnGround);
        } else {
            return LookAhead.robotGroundMove(startOnGround, targetVeloGround, tick, useNitroOnGround);
        }

        if (MathUtils.isZero(pvBeforeJump.v.length() - Constants.ROBOT_MAX_GROUND_SPEED)) {
            int afterJumpTick = tick - jumpTick;
            List<PV> precalculatedForThisSpeed = Collections.emptyList();
            if (useNitroOnJump) {
                precalculatedForThisSpeed = phys.tangJumpFromSpeedWithNitro.get(Constants.ROBOT_MAX_GROUND_SPEED_I);
            } else {
                precalculatedForThisSpeed = phys.tangJumpFromSpeedWithoutNitro.get(Constants.ROBOT_MAX_GROUND_SPEED_I);
            }

            if (afterJumpTick > precalculatedForThisSpeed.size()) {
                return null;
            }

            PV refPv = precalculatedForThisSpeed.get(afterJumpTick);

            return PV.pvPrecalcRotate(refPv, pvBeforeJump);
        } else {
            double vlen = pvBeforeJump.v.length();
            double low = Math.floor(vlen);
            double hi = Math.ceil(vlen);

            double d = (vlen - low);

            if (low < 0 || low > Constants.ROBOT_MAX_GROUND_SPEED_I
                    || hi < 0 || hi > Constants.ROBOT_MAX_GROUND_SPEED_I) {
                return null;
            }

            int afterJumpTick = tick - jumpTick;
            List<PV> precalculatedForLowSpeed = Collections.emptyList();
            List<PV> precalculatedForHiSpeed = Collections.emptyList();
            if (useNitroOnJump) {
                precalculatedForLowSpeed = phys.tangJumpFromSpeedWithNitro.get((int) low);
                precalculatedForHiSpeed = phys.tangJumpFromSpeedWithNitro.get((int) hi);
            } else {
                precalculatedForLowSpeed = phys.tangJumpFromSpeedWithoutNitro.get((int) low);
                precalculatedForHiSpeed = phys.tangJumpFromSpeedWithoutNitro.get((int) hi);
            }

            if (afterJumpTick > precalculatedForLowSpeed.size() || afterJumpTick > precalculatedForHiSpeed.size()) {
                return null;
            }

            PV refPvLow = precalculatedForLowSpeed.get(afterJumpTick);
            PV refPvHi = precalculatedForHiSpeed.get(afterJumpTick);

            PV middlePV = PV.middlePv(refPvLow, refPvHi, d);

            return PV.pvPrecalcRotate(middlePV, pvBeforeJump);
        }
    }
}
