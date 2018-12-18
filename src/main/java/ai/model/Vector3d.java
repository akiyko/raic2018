package ai.model;

import static ai.Constants.DOUBLE_ZERO;

/**
 * By no one on 18.12.2018.
 */
public final class Vector3d {
    public final double dx;
    public final double dy;
    public final double dz;

    public Vector3d(double dx, double dy, double dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public Vector3d normalize() {
        double len = length();
        if(Math.abs(len) < DOUBLE_ZERO) {
            return this;
        }
        double divlen = 1 / len;
        return new Vector3d(dx * divlen, dy * divlen, dz * divlen);
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

}
