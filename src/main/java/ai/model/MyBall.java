package ai.model;

/**
 * By no one on 22.12.2018.
 */
public class MyBall extends Entity implements Cloneable{

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
