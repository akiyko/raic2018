import model.Rules;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * By no one on 12.01.2019.
 */
public class RobotLookAhead {
    public static final int touchTickWider = 15;


    public static GamePlanResult predictRobotBallFutureJump(Rules rules, RobotPrecalcPhysics ph,
                                                            BallTrace ballTrace, PV myRobotPv, Vector3d targetVelocity, boolean useNitroOnGround,
                                                            boolean useNitroOnJump,
                                                            int jumpTick,
                                                            int baseBeforeTouchTick) {
        GamePlanResult result = new GamePlanResult();

        int beforeTouchTick = -1;

        PV mrAtPrevTick = null;
        for (int i = -touchTickWider + baseBeforeTouchTick; i < touchTickWider + baseBeforeTouchTick; i++) {
            MyBall thisTickBall = ballTrace.ballTrace.get(i);

            PV mrAtTick = findRobotPvTang(ph, myRobotPv, targetVelocity, useNitroOnGround, useNitroOnJump, i, jumpTick);
            if (mrAtTick == null) {
                break;
            }

            Vector3d toBall = thisTickBall.position.minus(mrAtTick.p);

            if (toBall.lengthSquare() < result.minToBall.lengthSquare()) {
                result.minToBall = toBall;
                result.minToBallTick = i;
            }
            Vector3d toBallGround = toBall.zeroY();
            if (toBallGround.lengthSquare() < result.minToBallGround.lengthSquare()) {
                result.minToBallGround = toBallGround;
                result.minToBallGroundTick = i;
            }

            double dr = (Constants.ROBOT_MAX_RADIUS - Constants.ROBOT_MIN_RADIUS) * Constants.ROBOT_MAX_JUMP_SPEED / Constants.ROBOT_MAX_JUMP_SPEED;

            if (toBall.length() < Constants.ROBOT_MIN_RADIUS + Constants.BALL_RADIUS + dr) {
                //touch
                beforeTouchTick = i - 1;
                result.beforeBallTouchTick = beforeTouchTick;

                if (jumpTick > beforeTouchTick) {
                    break;
                }

                if (i > 0) {
                    handleCollisionMath(result, ballTrace.ballTrace.get(i - 1), ballTrace.ballTrace.get(i), mrAtPrevTick, mrAtTick);
                }

                break;
            }
            mrAtPrevTick = mrAtTick;
        }


        return result;
    }

    public static void handleCollisionMath(GamePlanResult gpr, MyBall beforeTouch, MyBall afterTouch, PV rBeforeTouch, PV rAfterTouch) {
        double lenBefore = beforeTouch.position.minus(rBeforeTouch.p).length();
        double lenAfter = afterTouch.position.minus(rAfterTouch.p).length();
        double d = Constants.COLLIDE_JUMP_RADIUS - lenBefore / (lenAfter - lenBefore);

        PV ballPvBefore = PV.of(beforeTouch.position, beforeTouch.velocity);
        PV ballPvAfter = PV.of(afterTouch.position, afterTouch.velocity);

        PV ballTouch = PV.middlePv(ballPvBefore, ballPvAfter, d);
        PV rtouch = PV.middlePv(rBeforeTouch, rAfterTouch, d);

        PV ballAfterCollision = collideBallAndRobot(rtouch, ballTouch);

        System.out.println("beforeTouch: " + beforeTouch);
        System.out.println("afterTouch: " + afterTouch);
        System.out.println("rbeforeTouch: " + rBeforeTouch);
        System.out.println("rafterTouch: " + rAfterTouch);

        System.out.println("Ball after col:" + ballAfterCollision);
    }

    //returns ball PV right after touch
    public static PV collideBallAndRobot(PV arobot, PV bball) {
        Vector3d delta_position = Position.minus(bball.p, arobot.p);

        double amass = Constants.ROBOT_MASS;
        double bmass = Constants.BALL_MASS;

        double distance = delta_position.length();
        double k_a = (1 / amass) / ((1 / amass) + (1 / bmass));
        double k_b = (1 / bmass) / ((1 / amass) + (1 / bmass));
        Vector3d normal = delta_position.normalize();
//        double delta_velocity = Vector3d.dot(b.velocity.minus(a.velocity), normal) - b.radiusChangeSpeed - a.radiusChangeSpeed;
        double delta_velocity = Vector3d.dot(bball.v.minus(arobot.v), normal) - 0 - Constants.ROBOT_MAX_JUMP_SPEED;
        if (delta_velocity < 0) {
//                Vector3d impulse = normal.multiply((1 + random(MIN_HIT_E, MAX_HIT_E)) * delta_velocity); //TODO: testing
            Vector3d impulse = normal.multiply((1 + 0.5 * (Constants.MIN_HIT_E + Constants.MAX_HIT_E)) * delta_velocity);
//            Vector3d avelocity = a.velocity.plus(impulse.multiply(k_a));
            Vector3d bvelocity = bball.v.minus(impulse.multiply(k_b));
            return PV.of(bball.p, bvelocity);
        }
        return bball;
    }


    public static BestMoveDouble robotSeekForBallOnGround(Rules rules, RobotPrecalcPhysics ph, PV myRobotPvAtStart,
                                                          BallTrace ballTrace,
                                                          double minAngle, double maxAngle, long steps,
                                                          double minLenToBallRequired,
                                                          boolean useNitroOnGround) {

        double dangle = (maxAngle - minAngle) / steps;

        Optional<Double> minAngleConditionMatched = Optional.empty();
        Optional<Double> maxAngleConditionMatched = Optional.empty();
        GamePlanResult low = null;
        GamePlanResult high = null;


        for (int i = 0; i <= steps; i++) {
            double angle = minAngle + dangle * i;

            Vector3d targetVelocity = MathUtils.robotGroundVelocity(angle);

            GamePlanResult res = predictRobotBallFutureGround(rules, ph, ballTrace, myRobotPvAtStart, targetVelocity,
                    useNitroOnGround);

            if (res.minToBallGround.length() < minLenToBallRequired) {
                if (!minAngleConditionMatched.isPresent()) {
                    minAngleConditionMatched = Optional.of(minAngle + dangle * i);
                    low = res;
                }
                maxAngleConditionMatched = Optional.of(minAngle + dangle * i);
                high = res;
            }
        }

        BestMoveDouble bestMove = new BestMoveDouble();
        bestMove.lowPlanResult = low;
        bestMove.low = minAngleConditionMatched.orElse(0.0);

        bestMove.hiPlanResult = high;
        bestMove.hi = maxAngleConditionMatched.orElse(0.0);

        return bestMove;
    }

    public static GamePlanResult predictRobotBallFutureGround(Rules rules, RobotPrecalcPhysics ph,
                                                              BallTrace ballTrace, PV myRobotPv, Vector3d targetVelocity, boolean useNitroOnGround) {
        GamePlanResult result = new GamePlanResult();

        int beforeTouchTick = -1;

        for (int i = 1; i < ballTrace.ballTrace.size(); i++) {
            MyBall thisTickBall = ballTrace.ballTrace.get(i);

            PV mrAtTick = findRobotPvTang(ph, myRobotPv, targetVelocity, useNitroOnGround, false, i, i + 1);
            if (mrAtTick == null) {
                break;
            }

            Vector3d toBall = thisTickBall.position.minus(mrAtTick.p);

            if (toBall.lengthSquare() < result.minToBall.lengthSquare()) {
                result.minToBall = toBall;
                result.minToBallTick = i;
            }
            Vector3d toBallGround = toBall.zeroY();
            if (toBallGround.lengthSquare() < result.minToBallGround.lengthSquare()) {
                result.minToBallGround = toBallGround;
                result.minToBallGroundTick = i;
            }

            double lenToBall = toBallGround.length();
            if (lenToBall > OptimiseOptions.MIN_LEN_TO_BALL_REQUIRED) {
                //skip some ticks if too far from ball
                i += OptimiseOptions.SKIP_BALL_TRACE_TICKS;
                continue;
            }
            if (lenToBall > OptimiseOptions.MIN_LEN_TO_BALL_REQUIRED * 0.5) {
                //skip some ticks if too far from ball
                i += OptimiseOptions.SKIP_BALL_TRACE_TICKS / 2;
                continue;
            }

            double dr = (Constants.ROBOT_MAX_RADIUS - Constants.ROBOT_MIN_RADIUS) * Constants.ROBOT_MAX_JUMP_SPEED / Constants.ROBOT_MAX_JUMP_SPEED;

            if (toBall.length() < Constants.ROBOT_MIN_RADIUS + Constants.BALL_RADIUS + dr) {
                //touch
                beforeTouchTick = i - 1;
                result.beforeBallTouchTick = beforeTouchTick;
                break;
            }
        }

        return result;
    }


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
