import java.util.Random;

/**
 * @author akiyko
 * @since 12/18/2018.
 */
public abstract class MathUtils {
    public static Random r = new Random();

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


    public static double whenHitGround(double y0, double vy0) {
        double d = vy0 * vy0 - 2 * Constants.GRAVITY * (Constants.BALL_RADIUS - y0);
        if(d < 0) {
            return -1;
        }
        return (vy0 + Math.sqrt(d)) / Constants.GRAVITY;
    }


    public static Vector3d robotGroundVelocity(double angle) {
        double x = Math.cos(angle);
        double z = Math.sin(angle);

        return Vector3d.of(x, 0, z).multiply(Constants.ROBOT_MAX_GROUND_SPEED);
    }

}
