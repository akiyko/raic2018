import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * immutable
 * <p>
 * By no one on 18.12.2018.
 */
public final class Vector3d {
    public final double dx;
    public final double dy;
    public final double dz;

    private Vector3d(double dx, double dy, double dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public Vector3d zeroY() {
        return of(dx, 0, dz);
    }

    public static Vector3d of(double dx, double dy, double dz) {
        return new Vector3d(dx, dy, dz);
    }

    public Vector3d multiply(double d) {
        return of(dx * d, dy * d, dz * d);
    }

    public Vector3d minus(Vector3d b) {
        return of(dx - b.dx, dy - b.dy, dz - b.dz);
    }

    public Vector3d plus(Vector3d b) {
        return of(dx + b.dx, dy + b.dy, dz + b.dz);
    }

    public Vector3d clamp(double max) {
        double len = length();
        if (len > max) {
            return this.multiply(max / len);
        } else {
            return this;
        }
    }

    public Vector3d normalize() {
        double len = length();
        if (Math.abs(len) < Constants.DOUBLE_ZERO) {
            return this;
        }
        double divlen = 1 / len;
        return new Vector3d(dx * divlen, dy * divlen, dz * divlen);
    }

    public Vector3d rotate(double thetha) {
        double x1 = dx * cos(thetha) - dz * sin(thetha);
        double z1 = dx * sin(thetha) + dz * cos(thetha);

        return of(x1, dy, z1);
    }

    /**
     * v1.rotate(res) = v2
     * @param v1
     * @param v2
     * @return
     */
    public static double angle2dBetween(Vector3d v1, Vector3d v2) {
        Vector3d v1N = v1.normalize();
        Vector3d v2N = v2.normalize();

        double theta = Math.acos(Vector3d.dot(v1N, v2N));

        Vector3d v2t = v1N.rotate(theta);
        Vector3d v2tm = v1N.rotate(-theta);
        Vector3d v2tp = v1N.rotate(Math.PI + theta);
        if(v2t.doubleEquals(v2N)) {
            return theta;
        } else if(v2tm.doubleEquals(v2N)) {
            return -theta;
        } else if(v2tp.doubleEquals(v2tp)) {
            return Math.PI+theta;
        } else {
            return Math.PI-theta;
        }
    }

    public static double dot(Vector3d a, Vector3d b) {
        return a.dx * b.dx + a.dy * b.dy + a.dz * b.dz;
    }

    public double length() {
        return Math.sqrt(lengthSquare());
    }

    public double lengthSquare() {
        return sq(dx) + sq(dy) + sq(dz);
    }

    public static double sq(double v) {
        return v * v;
    }

    public static double diffSquare(Vector3d a, Vector3d b) {
        return sq(a.dx - b.dx) + sq(a.dy - b.dy) + sq(a.dz - b.dz);
    }

    public boolean doubleEquals(Vector3d b) {
        return MathUtils.isZero(dx - b.dx)
                && MathUtils.isZero(dy - b.dy)
                && MathUtils.isZero(dz - b.dz);
    }

    public Vector3d negateZ() {
        return new Vector3d(dx, dy, -dz);
    }

    @Override
    public String toString() {
        return "{" +
                "dx=" + dx +
                ", dy=" + dy +
                ", dz=" + dz +
                '}';
    }
}
