package ai;

/**
 * @author akiyko
 * @since 12/18/2018.
 */
public abstract class MathUtils {
    private MathUtils() {
    }

    public static boolean isZero(double d) {
        return Math.abs(d) < Constants.DOUBLE_ZERO;
    }
}
