package ai.model;

import ai.Constants;
import model.Ball;

import static ai.model.Vector3d.of;

/**
 * By no one on 22.12.2018.
 */
public class MyBall extends Entity implements Cloneable {

    public static MyBall fromBall(Ball ball) {
        MyBall mb = new MyBall();

        mb.position = new Position(ball.x, ball.y, ball.z);
        mb.radius = ball.radius;
        mb.velocity = of(ball.velocity_x, ball.velocity_y, ball.velocity_z);

        mb.mass = Constants.BALL_MASS;
        mb.arena_e = Constants.BALL_ARENA_E;

        return mb;
    }


    @Override
    public MyBall clone() {
        try {
            return (MyBall) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MyBall cloneNegateZ() {
        return (MyBall) super.cloneNegateZ();
    }
}
