package ai.model;

/**
 * By no one on 18.12.2018.
 */
public class Entity {
    public double mass;
    public double radius;
    public Position position;

    public Entity(double mass, double radius, Position position) {
        this.mass = mass;
        this.radius = radius;
        this.position = position;
    }
}
