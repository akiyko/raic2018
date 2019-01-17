/**
 * By no one on 03.01.2019.
 */
public class RobotMoveJumpPlan {
    public GamePlanResult gamePlanResult;
    public Vector3d targetVelocity;
    public double jumpSpeed;
    public int jumpTick; //1 to jump on next tick, 0 to jump this tick
    public boolean useNitroOnGround;
    public boolean useNitroOnFly;

    public RobotAction toRobotAction(int currentTick) {
        return new RobotActionRunJumpTang(currentTick, gamePlanResult.beforeBallTouchTick + StrategyParams.fewTickMore,
                targetVelocity, currentTick + jumpTick, useNitroOnGround, useNitroOnFly);
    }

    @Override
    public String toString() {
        return "RobotMoveJumpPlan{" +
                "gamePlanResult=" + gamePlanResult +
                ", targetVelocity=" + targetVelocity +
                ", jumpSpeed=" + jumpSpeed +
                ", jumpTick=" + jumpTick +
                '}';
    }
}
