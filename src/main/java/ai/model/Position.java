package ai.model;

import ai.MathUtils;

/**
 * immutable
 *
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

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
