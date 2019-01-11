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
     * @param v1
     * @param v2
     * @return
     */
    public static double angle2dBetween(Vector3d v1, Vector3d v2) {
        double theta = Math.acos(Vector3d.dot(v1.normalize(), v2.normalize()));
        ...
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
