package ai.model;

import static ai.Constants.DOUBLE_ZERO;

/**
 * immutable
 * <p>
 * By no one on 18.12.2018.
 */
public final class Vector2d {
    private final Vector3d vector3d;

    public double x() {
        return vector3d.dx;
    }

    public double y() {
        return vector3d.dy;
    }

    private Vector2d(Vector3d vector3d) {
        this.vector3d = vector3d;
    }

    public static Vector2d of(double da, double db) {
        return new Vector2d(Vector3d.of(da, db, 0));
    }

    public Vector2d minus(Vector2d b) {
        return new Vector2d(vector3d.minus(b.vector3d));
    }

    public Vector2d plus(Vector2d b) {
        return new Vector2d(vector3d.plus(b.vector3d));
    }

    public Vector2d multiply(double mul) {
        return new Vector2d(vector3d.multiply(mul));
    }

    public Vector2d normalize() {
        return new Vector2d(vector3d.normalize());
    }

    public double length() {
        return vector3d.length();
    }

}
