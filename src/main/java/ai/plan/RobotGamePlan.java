package ai.plan;

import ai.model.MyRobot;

/**
 * @since 12/27/2018.
 */
public class RobotGamePlan {
    public MyRobot initialPosition;

    public TargetVelocityProvider targetVelocityProvider;
    public JumpCondition jumpCondition;
}
