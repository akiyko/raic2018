package ai;

import ai.model.Vector3d;

import java.util.Random;

/**
 * @author akiyko
 * @since 12/18/2018.
 */
public abstract class MathUtils {
    static Random r = new Random();

    public static Vector3d MAX_VECTOR = Vector3d.of(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);

    private MathUtils() {
    }

    public static double random(double min, double max) {
        return min + (r.nextDouble() * (max - min));
    }

    public static boolean isZero(double d) {
        return Math.abs(d) < Constants.DOUBLE_ZERO;
    }

    public static double clamp(double v, double lo, double hi) {//TODO: test
        if (v < lo) {
            return lo;
        }
        if (v > hi) {
            return hi;
        }

        return v;
    }

}
