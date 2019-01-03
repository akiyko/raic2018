package ai.plan;

import ai.model.Position;

/**
 * By no one on 01.01.2019.
 */
public class BallGoal {
    public double goalScoredTick = -1;
    public double oppGoalScoredTick = -1;

    public Position finalPosition;


    @Override
    public String toString() {
        return "BallGoal{" +
                "goalScoredTick=" + goalScoredTick +
                ", oppGoalScoredTick=" + oppGoalScoredTick +
                ", finalPosition=" + finalPosition +
                '}';
    }
}
