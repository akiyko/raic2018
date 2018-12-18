package ai.model;

import static ai.Constants.DOUBLE_ZERO;

/**
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

    public static Vector3d of(double dx, double dy, double dz) {
        return new Vector3d(dx, dy, dz);
    }

    public Vector3d multiply(double d) {
        return of(dx * d, dy * d, dz * d);
    }

//    public Vector3d minus(Vector3d b) {
//        return of(dx - b.dx, dy - b.dy, dz - b.dz);
//    }
//
//    public Vector3d plus(Vector3d b) {
//        return of(dx + b.dx, dy + b.dy, dz + b.dz);
//    }

    public Vector3d normalize() {
        double len = length();
        if (Math.abs(len) < DOUBLE_ZERO) {
            return this;
        }
        double divlen = 1 / len;
        return new Vector3d(dx * divlen, dy * divlen, dz * divlen);
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
}
