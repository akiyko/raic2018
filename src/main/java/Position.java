import javafx.geometry.Pos;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * immutable
 * <p>
 * By no one on 18.12.2018.
 */
public final class Position {
    public final double x;
    public final double y;
    public final double z;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    //0 <= d <= 1
    public static Position middlePos(Position p1, Position p2, double d) {
        return p1.plus(p2.minus(p1).multiply(d));
    }

    public Position zeroY() {
        return new Position(x,0,z);
    }

    public Vector3d toVector() {
        return Vector3d.of(x,y,z);
    }

    public static Vector3d minus(Position a, Position b) {
        return Vector3d.of(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public Position plus(Vector3d v) {
        return new Position(x + v.dx, y + v.dy, z + v.dz);
    }

    public Position minus(Vector3d v) {
        return new Position(x - v.dx, y - v.dy, z - v.dz);
    }

    public Vector3d minus(Position p) {
        return Vector3d.of(x - p.x, y - p.y, z - p.z);
    }

    public Position negateX() {
        return new Position(-x, y, z);
    }

    public Position negateZ() {
        return new Position(x, y, -z);
    }

    public boolean doubleEquals(Position b) {
        return MathUtils.isZero(x - b.x)
                && MathUtils.isZero(y - b.y)
                && MathUtils.isZero(z - b.z);
    }

    public Position rotateAroundZero(double thetha) {
//        x1 = x cos t − y sin t
//        y1 = x sin t + y cos t
        double x1 = x * cos(thetha) - z * sin(thetha);
        double z1 = x * sin(thetha) + z * cos(thetha);

        return new Position(x1, y, z1);
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
