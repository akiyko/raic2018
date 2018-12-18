package ai.model;

/**
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
}
