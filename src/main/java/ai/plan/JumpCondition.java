package ai.plan;

import ai.model.MyBall;
import ai.model.MyRobot;

/**
 * @author akiyko
 * @since 12/27/2018.
 */

@FunctionalInterface
public interface JumpCondition {
    double jumpSpeed(MyRobot myRobot, MyBall myBall);
}
