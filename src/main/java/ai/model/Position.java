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
        return new Vector3d(a.x - b.x, a.y - b.y, a.z - b.z);
    }


}
