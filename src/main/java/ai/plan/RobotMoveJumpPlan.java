package ai.plan;

import ai.model.Vector3d;

/**
 * By no one on 03.01.2019.
 */
public class RobotMoveJumpPlan {
    public GamePlanResult gamePlanResult;
    public Vector3d targetVelocity;
    public double jumpSpeed;
    public int jumpTick; //1 to jump on next tick, 0 to jump this tick

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
