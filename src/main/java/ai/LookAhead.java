package ai;

import ai.model.*;
import ai.plan.*;
import model.Action;
import model.Arena;
import model.Rules;

import java.util.Collections;
import java.util.Optional;

import static ai.Constants.*;
import static ai.model.Vector3d.of;

/**
 * @author akiyko
 * @since 12/27/2018.
 */
public class LookAhead {
//    public static BestMoveDouble singleKickGoalBase(Rules rules, MyRobot myRobot,
//                                                    MyBall myBall, JumpCondition jumpCondition) {
//        BestMoveDouble bmdBall = LookAhead.singleRobotKickGoalGround(rules, myRobot.clone(), myBall.clone(), jumpCondition, -Math.PI, Math.PI, 72,
//                (Constants.ROBOT_MAX_RADIUS + Constants.BALL_RADIUS) + 2, false, 150, 300, 100);
//
//        System.out.println("BmdBall: " + bmdBall);
//
//        BestMoveDouble bmdGoal = LookAhead.singleRobotKickGoalGround(rules, myRobot.clone(), myBall.clone(), jumpCondition, bmdBall.low, bmdBall.hi, 72,
//                0.0, true, 150, 300, 100);
//
//        return bmdGoal;
//    }

//    public static BestMoveDouble singleRobotKickGoalGround(Rules rules, MyRobot myRobot,
//                                                           BallTrace ballTrace, int jumpTick,
//                                                           double minAngle, double maxAngle, long steps,
//                                                           double minLenToBallRequired,
//                                                           boolean goalRequired,
//                                                           int toBallTickDepth, int toGateTickDepth) {
//
//        double dangle = (maxAngle - minAngle) / steps;
//
//        Optional<Double> minAngleConditionMatched = Optional.empty();
//        Optional<Double> bestAngleConditionMatched = Optional.empty();
//        Optional<Double> maxAngleConditionMatched = Optional.empty();
//        GamePlanResult low = null;
//        GamePlanResult high = null;
//
//
//        MyRobot mr = myRobot.clone();
//
//        for (int i = 0; i <= steps; i++) {
//            double x = Math.cos(minAngle + dangle * i);
//            double z = Math.sin(minAngle + dangle * i);
//
//            RobotGamePlan plan = new RobotGamePlan();
//            plan.targetVelocityProvider = new FixedTargetVelocity(of(x, 0, z).multiply(Constants.ROBOT_MAX_GROUND_SPEED));
//            plan.jumpCondition = jumpCondition;
//
//            if (!goalRequired) {
//                GamePlanResult res = predictRobotBallFuture(rules, mr.clone(), mb.clone(), plan, toBallTickDepth, mpt);
//
////                System.out.println(i + ": " + res.minToBall.length());
//                if (res.minToBall.length() < minLenToBallRequired) {
//                    if (!minAngleConditionMatched.isPresent()) {
//                        minAngleConditionMatched = Optional.of(minAngle + dangle * i);
//                        low = res;
//                    }
//                    maxAngleConditionMatched = Optional.of(minAngle + dangle * i);
//                    high = res;
//                }
//            } else {
////                GamePlanResult res = predictRobotBallFuture(rules, mr.clone(), mb.clone(), plan, toGateTickDepth, mpt);
////
////                if (res.goalScoredTick > 0) {
////                    if (!minAngleConditionMatched.isPresent()) {
////                        minAngleConditionMatched = Optional.of(minAngle + dangle * i);
////                        low = res;
////                    }
////                    maxAngleConditionMatched = Optional.of(minAngle + dangle * i);
////                    high = res;
////                }
//
//            }
//        }
//
//        BestMoveDouble bestMove = new BestMoveDouble();
//        bestMove.lowPlanResult = low;
//        bestMove.low = minAngleConditionMatched.orElse(0.0);
//
//        bestMove.hiPlanResult = high;
//        bestMove.hi = maxAngleConditionMatched.orElse(0.0);
//
//        return bestMove;
////      System.out.println("x/z: " + x + "/"  + z);
//    }


    public static GamePlanResult predictRobotBallFutureMath(Rules rules, BallTrace ballTrace, MyRobot myRobot, Vector3d targetVelocity,
                                                            int jumpTick, double jumpSpeed, double mpt) {
        GamePlanResult result = new GamePlanResult();

        int beforeTouchTick = -1;

        for (int i = 0; i < ballTrace.ballTrace.size(); i++) {
            MyBall thisTickBall = ballTrace.ballTrace.get(i);

            MyRobot mr = robotGroundMoveAndJump(myRobot.clone(), targetVelocity, i + 1, jumpTick, jumpSpeed);

            Vector3d toBall = thisTickBall.position.minus(mr.position);
            if (toBall.lengthSquare() < result.minToBall.lengthSquare()) {
                result.minToBall = toBall;
                result.minToBallTick = i + 1;
            }

            if (toBall.lengthSquare() < myRobot.radius + thisTickBall.radius) {
                //touch
                beforeTouchTick = i;
                break;
            }
        }

        if(beforeTouchTick > 0) {
            MyBall b = ballTrace.ballTrace.get(beforeTouchTick - 1).clone();
            MyRobot mr =  robotGroundMoveAndJump(myRobot.clone(), targetVelocity, beforeTouchTick, jumpTick, jumpSpeed);
            mr.action = new Action();
            mr.action.jump_speed = jumpSpeed;
            mr.action.target_velocity = targetVelocity;

            Simulator.tick(rules, Collections.singletonList(mr), b, mpt);

            BallGoal bg = LookAhead.ballFlyUntouched(rules, b);
            result.ballFinalPosition = bg.finalPosition;
            if(bg.goalScoredTick > 0) {
                result.goalScoredTick = (int) bg.goalScoredTick + beforeTouchTick + 1;
            }
            if(bg.oppGoalScoredTick > 0) {
                result.oppGoalScored = (int) bg.oppGoalScoredTick + beforeTouchTick + 1;
            }
        }

        return result;
    }


    public static MyRobot robotGroundMoveAndJump(MyRobot mr, Vector3d targetVelocity, int ticks, int jumpTick, double jumpSpeed) {
        if (jumpTick > ticks) {
            return robotGroundMove(mr, targetVelocity, ticks);
        }

        MyRobot beforeJump = robotGroundMove(mr, targetVelocity, jumpTick - 1);

        beforeJump.velocity = of(beforeJump.velocity.dx, jumpSpeed, beforeJump.velocity.dz);
        double dr = (ROBOT_MAX_RADIUS - ROBOT_MIN_RADIUS) * jumpSpeed / ROBOT_MAX_JUMP_SPEED;

        beforeJump.position = beforeJump.position.plus(of(0, dr, 0));

        MyRobot afterJump = robotJump(beforeJump, (ticks + 1 - jumpTick) / Constants.TICKS_PER_SECOND);

        return afterJump;
    }

    public static MyRobot robotGroundMove(MyRobot mr, Vector3d targetVelocityRequested, int ticks) {
        if (ticks <= 0) {
            return mr.clone();
        }

        Vector3d targetVelocity = targetVelocityRequested.clamp(Constants.ROBOT_MAX_GROUND_SPEED);
        Vector3d vdiff = targetVelocity.minus(mr.velocity);
        Vector3d amax = vdiff.multiply(1000.0).clamp(Constants.ROBOT_ACCELERATION);

        double ttillspeed = vdiff.length() / (Constants.ROBOT_ACCELERATION / Constants.TICKS_PER_SECOND);


        if (ticks < ttillspeed) {
            double delta_time = ((double) ticks) / Constants.TICKS_PER_SECOND;

            Vector3d targetVelocityMid = mr.velocity.plus(amax.multiply(delta_time));
            Position positionMid = mr.position.plus(mr.velocity.multiply(delta_time)).plus(amax.multiply(0.5 * delta_time * delta_time));

            MyRobot mrMoved = mr.clone();
            mrMoved.velocity = targetVelocityMid;
            mrMoved.position = positionMid;

            return mrMoved;

        } else {
            double flatTimeInTicks = ticks - ttillspeed;
            double delta_time = ttillspeed / Constants.TICKS_PER_SECOND;

            Vector3d targetVelocityFin = mr.velocity.plus(amax.multiply(delta_time));
            Position positionMid = mr.position.plus(mr.velocity.multiply(delta_time)).plus(amax.multiply(0.5 * delta_time * delta_time));

            positionMid = positionMid.plus(targetVelocityFin.multiply(flatTimeInTicks / Constants.TICKS_PER_SECOND));

            MyRobot mrMoved = mr.clone();
            mrMoved.velocity = targetVelocityFin;
            mrMoved.position = positionMid;

            return mrMoved;
        }
    }


    public static BallGoal ballFlyUntouched(Rules rules, MyBall mb) {
        double goal_z = rules.arena.depth / 2 + mb.radius;

        if (MathUtils.isZero(mb.velocity.dz)) {
            return new BallGoal();//no goals
        }


        double t;

        if (mb.velocity.dz > 0) {
            t = Math.abs((goal_z - mb.position.z) / mb.velocity.dz);
        } else {
            t = Math.abs((goal_z + mb.position.z) / mb.velocity.dz);
        }


        Position ballFinalPosition = ballPositionFly(mb, t);

        if (ballFinalPosition.y < 0) {
            return new BallGoal();
        }


        Dan danToArena = Simulator.dan_to_arena(ballFinalPosition, rules.arena);

        BallGoal result = new BallGoal();
        result.finalPosition = ballFinalPosition;

        if (danToArena.distance > mb.radius) {
            if (ballFinalPosition.z > 0) {
                result.goalScoredTick = t * Constants.TICKS_PER_SECOND;
            } else {
                result.oppGoalScoredTick = t * Constants.TICKS_PER_SECOND;
            }
        }
        return result;
    }

    public static Position ballPositionFly(MyBall mb, double t) {
        double x = mb.position.x + t * mb.velocity.dx;
        double z = mb.position.z + t * mb.velocity.dz;
        double y = mb.position.y + t * mb.velocity.dy - 0.5 * Constants.GRAVITY * t * t;

        return new Position(x, y, z);
    }

    public static MyRobot robotJump(MyRobot mr, double dt) {
        double x = mr.position.x + dt * mr.velocity.dx;
        double z = mr.position.z + dt * mr.velocity.dz;
        double y = mr.position.y + dt * mr.velocity.dy - 0.5 * Constants.GRAVITY * dt * dt;

        double dy = mr.velocity.dy - Constants.GRAVITY * dt;

        MyRobot rmoved = mr.clone();
        rmoved.position = new Position(x, y, z);
        rmoved.velocity = of(mr.velocity.dx, dy, mr.velocity.dz);

        return rmoved;
    }

    public static BallTrace ballUntouchedTraceOptimized(Rules rules, MyBall myBall, int tickDepth, int mpt) {

        BallTrace bt = new BallTrace();

        int i = 0;
        try {
            for (; i < tickDepth; i++) {
                Simulator.updateBallOnlyTick(rules, myBall, mpt);
                bt.ballTrace.add(myBall.clone());
            }

        } catch (GoalScoredException e) {
            if (e.getZ() > 0) {
                bt.goalScoredTick = i;
            } else {
                bt.oppGoalScoredTick = i;
            }
        }

        return bt;
    }

    public static BallTrace ballUntouchedTraceSimulator(Rules rules, MyBall myBall, int tickDepth, int mpt) {

        BallTrace bt = new BallTrace();

        int i = 0;
        try {
            for (; i < tickDepth; i++) {
                Simulator.tick(rules, Collections.emptyList(), myBall, mpt);
                bt.ballTrace.add(myBall.clone());
            }

        } catch (GoalScoredException e) {
            if (e.getZ() > 0) {
                bt.goalScoredTick = i;
            } else {
                bt.oppGoalScoredTick = i;
            }
        }

        return bt;
    }

    public static GamePlanResult predictRobotBallFutureSimulate(Rules rules, MyRobot myRobot,
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

    public static BestMoveDouble singleKickGoalBaseOld(Rules rules, MyRobot myRobot,
                                                       MyBall myBall, JumpCondition jumpCondition) {
        BestMoveDouble bmdBall = LookAhead.singleRobotKickGoalGroundOld(rules, myRobot.clone(), myBall.clone(), jumpCondition, -Math.PI, Math.PI, 72,
                (Constants.ROBOT_MAX_RADIUS + Constants.BALL_RADIUS) + 2, false, 150, 300, 100);

        System.out.println("BmdBall: " + bmdBall);

        BestMoveDouble bmdGoal = LookAhead.singleRobotKickGoalGroundOld(rules, myRobot.clone(), myBall.clone(), jumpCondition, bmdBall.low, bmdBall.hi, 72,
                0.0, true, 150, 300, 100);

        return bmdGoal;
    }

    public static BestMoveDouble singleRobotKickGoalGroundOld(Rules rules, MyRobot myRobot,
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
                GamePlanResult res = predictRobotBallFutureSimulate(rules, mr.clone(), mb.clone(), plan, toBallTickDepth, mpt);

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
                GamePlanResult res = predictRobotBallFutureSimulate(rules, mr.clone(), mb.clone(), plan, toGateTickDepth, mpt);

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
