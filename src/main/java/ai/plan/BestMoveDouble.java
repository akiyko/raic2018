package ai.plan;

import ai.Constants;
import ai.MathUtils;
import ai.model.Vector3d;

import static ai.model.Vector3d.of;

/**
 * @author akiyko
 * @since 12/29/2018.
 */
public class BestMoveDouble {
    public double low;
    public double hi;
    public double optimal;

    public GamePlanResult lowPlanResult;
    public GamePlanResult optimalPlanResult;
    public GamePlanResult hiPlanResult;


    public Vector3d middleTargetVelocityAngleGround() {
        return MathUtils.robotGroundVelocity((low + hi) * 0.5);
    }

    @Override
    public String toString() {
        return "BestMoveDouble{" +
                "low=" + low +
                ", hi=" + hi +
                ", optimal=" + optimal +
                ", lowPlanResult=" + lowPlanResult +
                ", optimalPlanResult=" + optimalPlanResult +
                ", hiPlanResult=" + hiPlanResult +
                '}';
    }
}
