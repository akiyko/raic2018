package ai;

import ai.model.Entity;
import ai.model.Position;
import ai.model.Vector3d;

import static ai.Constants.MAX_ENTITY_SPEED;
import static ai.Constants.MAX_HIT_E;
import static ai.Constants.MIN_HIT_E;
import static ai.MathUtils.random;
import static ai.model.Vector3d.dot;

/**
 * By no one on 17.12.2018.
 */
public class Simulator {
    public static void collideEntities(Entity a, Entity b) {
        Vector3d delta_position = Position.minus(b.position, a.position);
        double distance = delta_position.length();
        double penetration = a.radius + b.radius - distance;
        if (penetration > 0) {
            double k_a = (1 / a.mass) / ((1 / a.mass) + (1 / b.mass));
            double k_b = (1 / b.mass) / ((1 / a.mass) + (1 / b.mass));
            Vector3d normal = delta_position.normalize();
            a.position = a.position.minus(normal.multiply(penetration * k_a));
            b.position = b.position.plus(normal.multiply(penetration * k_b));
            double delta_velocity = dot(b.velocity.minus(a.velocity), normal) + b.radiusChangeSpeed - a.radiusChangeSpeed;
            if (delta_velocity < 0) {
                Vector3d impulse = normal.multiply((1 + random(MIN_HIT_E, MAX_HIT_E)) * delta_velocity);
                a.velocity = a.velocity.plus(impulse.multiply(k_a));
                b.velocity = b.velocity.minus(impulse.multiply(k_b));
            }
        }
    }

    public static void move(Entity e) {

//        e.velocity = clamp(e.velocity, MAX_ENTITY_SPEED);
//        e.position += e.velocity * delta_time
//        e.position.y -= GRAVITY * delta_time * delta_time / 2
//        e.velocity.y -= GRAVITY * delta_time
    }

//    function move(e: Entity):
//    e.velocity = clamp(e.velocity, MAX_ENTITY_SPEED)
//    e.position += e.velocity * delta_time
//    e.position.y -= GRAVITY * delta_time * delta_time / 2
//    e.velocity.y -= GRAVITY * delta_time


//    public static void collide_entities(a: Entity, b: Entity) {
//        let delta_position = b.position - a.position;
//        let distance = length(delta_position);
//        let penetration = a.radius + b.radius - distance;
//        if penetration > 0:;
//        let k_a = (1 / a.mass) / ((1 / a.mass) + (1 / b.mass));
//        let k_b = (1 / b.mass) / ((1 / a.mass) + (1 / b.mass));
//        let normal = normalize(delta_position);
//        a.position -= normal * penetration * k_a;
//        b.position += normal * penetration * k_b;
//        let delta_velocity = dot(b.velocity - a.velocity, normal);
//                + b.radius_change_speed - a.radius_change_speed;
//        if delta_velocity< 0:;
//        let impulse = (1 + random(MIN_HIT_E, MAX_HIT_E)) * delta_velocity * normal;
//        a.velocity += impulse * k_a;
//        b.velocity -= impulse * k_b;
//    }

}
