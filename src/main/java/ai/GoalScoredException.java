package ai;

/**
 * @author akiyko
 * @since 12/25/2018.
 */
public class GoalScoredException extends RuntimeException {
    private final double z;

    public GoalScoredException(double z) {
        this.z = z;
    }

    public double getZ() {
        return z;
    }
}
