package ai;

import ai.model.Dan;
import ai.model.Entity;
import ai.model.Position;
import ai.model.Vector3d;

import static ai.Constants.*;
import static ai.MathUtils.random;
import static ai.model.Vector3d.dot;
import static ai.model.Vector3d.of;

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

    public static void move(Entity e, double delta_time) {
        e.velocity = e.velocity.clamp(MAX_ENTITY_SPEED);
        e.position = e.position.plus(e.velocity.multiply(delta_time));
        e.position = e.position.minus(of(0, GRAVITY * delta_time * delta_time / 2.0, 0));
        e.velocity = e.velocity.minus(of(0, GRAVITY * delta_time, 0));
    }

    public static Dan dan_to_plane(Position point, Position point_on_plane, Vector3d plane_normal) {
            return Dan.of(dot(point.minus(point_on_plane), plane_normal), plane_normal);
    }

//
//    function dan_to_plane(point: Vec3D, point_on_plane: Vec3D, plane_normal: Vec3D):
//            return {
//        distance: dot(point - point_on_plane, plane_normal)
//        normal: plane_normal
//    }
//    function dan_to_sphere_inner(point: Vec3D, sphere_center: Vec3D, sphere_radius: Float):
//            return {
//        distance: sphere_radius - length(point - sphere_center)
//        normal: normalize(sphere_center - point)
//    }
//    function dan_to_sphere_outer(point: Vec3D, sphere_center: Vec3D, sphere_radius: Float):
//            return {
//        distance: length(point - sphere_center) - sphere_radius
//        normal: normalize(point - sphere_center)
//    }


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
