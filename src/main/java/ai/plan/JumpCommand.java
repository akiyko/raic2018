package ai.plan;

/**
 * By no one on 04.01.2019.
 */
public class JumpCommand {
    public int jumpTick;
    public double jumpSpeed;

    public JumpCommand(int jumpTick, double jumpSpeed) {
        this.jumpTick = jumpTick;
        this.jumpSpeed = jumpSpeed;
    }

    @Override
    public String toString() {
        return
                "{" + jumpTick +
                ", " + jumpSpeed +
                '}';
    }
}
