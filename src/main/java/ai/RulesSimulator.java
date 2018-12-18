package ai;

/**
 * By no one on 17.12.2018.
 */
public class RulesSimulator {
    public static void collide_entities(a: Entity, b: Entity) {
        let delta_position = b.position - a.position;
        let distance = length(delta_position);
        let penetration = a.radius + b.radius - distance;
        if penetration > 0:;
        let k_a = (1 / a.mass) / ((1 / a.mass) + (1 / b.mass));
        let k_b = (1 / b.mass) / ((1 / a.mass) + (1 / b.mass));
        let normal = normalize(delta_position);
        a.position -= normal * penetration * k_a;
        b.position += normal * penetration * k_b;
        let delta_velocity = dot(b.velocity - a.velocity, normal);
                + b.radius_change_speed - a.radius_change_speed;
        if delta_velocity< 0:;
        let impulse = (1 + random(MIN_HIT_E, MAX_HIT_E)) * delta_velocity * normal;
        a.velocity += impulse * k_a;
        b.velocity -= impulse * k_b;
    }

}
