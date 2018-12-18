package ai;

import ai.model.Entity;
import ai.model.Position;
import ai.model.Vector3d;

/**
 * By no one on 17.12.2018.
 */
public class Simulator {
//    public static void collide_entities(Entity a, Entity b) {
//        Vector3d delta_position = Position.minus(b.position, a.position);
//        double distance = delta_position.length();
//        double penetration = a.radius + b.radius - distance;
//        if (penetration > 0) {
//            double k_a = (1 / a.mass) / ((1 / a.mass) + (1 / b.mass));
//            double k_b = (1 / b.mass) / ((1 / a.mass) + (1 / b.mass));
//            Vector3d normal = delta_position.normalize();
//            a.position -= normal.multiply(penetration * k_a);
//            b.position += normal * penetration * k_b;
//            let delta_velocity = dot(b.velocity - a.velocity, normal);
//            +b.radius_change_speed - a.radius_change_speed;
//            if delta_velocity< 0:;
//            let impulse = (1 + random(MIN_HIT_E, MAX_HIT_E)) * delta_velocity * normal;
//            a.velocity += impulse * k_a;
//            b.velocity -= impulse * k_b;
//        }
//    }


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
