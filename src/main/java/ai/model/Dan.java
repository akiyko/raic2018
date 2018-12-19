package ai.model;

/**
 * By no one on 19.12.2018.
 */
public class Dan {
    public final double distance;
    public final Vector3d normal;

    public Dan(double distance, Vector3d normal) {
        this.distance = distance;
        this.normal = normal;
    }

    public static Dan of(double distance, Vector3d normal) {
        return new Dan(distance, normal);
    }
}
