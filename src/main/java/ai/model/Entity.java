package ai.model;

/**
 * mutable thing
 *
 * By no one on 18.12.2018.
 */
public class Entity implements Cloneable {
    public double mass;
    public double radius;
    public Position position;
    public Vector3d velocity;
    public double radiusChangeSpeed;

    public double arena_e;

    public Entity cloneNegateZ() {
        try {
            Entity cloned = (Entity)this.clone();
            cloned.position = cloned.position.negateZ();
            cloned.velocity = cloned.velocity.negateZ();

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
