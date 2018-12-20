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
        return Dan.of(dot(point.minus(point_on_plane), plane_normal),
                plane_normal);
    }

    public static Dan dan_to_sphere_inner(Position point, Position sphere_center, double sphere_radius) {
        return Dan.of(
                sphere_radius - point.minus(sphere_center).length(),
                sphere_center.minus(point).normalize());
    }

    public static Dan dan_to_sphere_outer(Position point, Position sphere_center, double sphere_radius) {
        return Dan.of(
                point.minus(sphere_center).length() - sphere_radius,
                point.minus(sphere_center).normalize());
    }




//    function dan_to_arena_quarter(point: Vec3D):
//    // Ground
//    let dan = dan_to_plane(point, (0, 0, 0), (0, 1, 0))
//// Ceiling
//    dan = min(dan, dan_to_plane(point, (0, arena.height, 0), (0, -1, 0)))
//// Side x
//    dan = min(dan, dan_to_plane(point, (arena.width / 2, 0, 0), (-1, 0, 0)))
//// Side z (goal)
//    dan = min(dan, dan_to_plane(
//            point,
//(0, 0, (arena.depth / 2) + arena.goal_depth),
//            (0, 0, -1)))
//    // Side z
//    let v = (point.x, point.y) - (
//            (arena.goal_width / 2) - arena.goal_top_radius,
//    arena.goal_height - arena.goal_top_radius)
//            11
//            if point.x >= (arena.goal_width / 2) + arena.goal_side_radius
//    or point.y >= arena.goal_height + arena.goal_side_radius
//    or (
//                    v.x > 0
//                    and v.y > 0
//                    and length(v) >= arena.goal_top_radius + arena.goal_side_radius):
//    dan = min(dan, dan_to_plane(point, (0, 0, arena.depth / 2), (0, 0, -1)))
//// Side x & ceiling (goal)
//            if point.z >= (arena.depth / 2) + arena.goal_side_radius:
//// x
//    dan = min(dan, dan_to_plane(
//            point,
//(arena.goal_width / 2, 0, 0),
//(-1, 0, 0)))
//// y
//    dan = min(dan, dan_to_plane(point, (0, arena.goal_height, 0), (0, -1, 0)))
//// Goal back corners
//            assert arena.bottom_radius == arena.goal_top_radius
//if point.z > (arena.depth / 2) + arena.goal_depth - arena.bottom_radius:
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(
//              clamp(
//                      point.x,
//              arena.bottom_radius - (arena.goal_width / 2),
//(arena.goal_width / 2) - arena.bottom_radius,
//            ),
//    clamp(
//            point.y,
//            arena.bottom_radius,
//            arena.goal_height - arena.goal_top_radius,
//            ),
//(arena.depth / 2) + arena.goal_depth - arena.bottom_radius),
//    arena.bottom_radius))
//// Corner
//            if point.x > (arena.width / 2) - arena.corner_radius
//    and point.z > (arena.depth / 2) - arena.corner_radius:
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(
//                      (arena.width / 2) - arena.corner_radius,
//    point.y,
//            (arena.depth / 2) - arena.corner_radius
//),
//    arena.corner_radius))
//// Goal outer corner
//            if point.z < (arena.depth / 2) + arena.goal_side_radius:
//// Side x
//            if point.x < (arena.goal_width / 2) + arena.goal_side_radius:
//    dan = min(dan, dan_to_sphere_outer(
//            point,
//(
//                      12
//                      (arena.goal_width / 2) + arena.goal_side_radius,
//    point.y,
//            (arena.depth / 2) + arena.goal_side_radius
//),
//    arena.goal_side_radius))
//// Ceiling
//            if point.y < arena.goal_height + arena.goal_side_radius:
//    dan = min(dan, dan_to_sphere_outer(
//            point,
//(
//              point.x,
//              arena.goal_height + arena.goal_side_radius,
//(arena.depth / 2) + arena.goal_side_radius
//),
//    arena.goal_side_radius))
//    // Top corner
//    let o = (
//            (arena.goal_width / 2) - arena.goal_top_radius,
//    arena.goal_height - arena.goal_top_radius
//)
//    let v = (point.x, point.y) - o
//if v.x > 0 and v.y > 0:
//    let o = o + normalize(v) * (arena.goal_top_radius + arena.goal_side_radius)
//    dan = min(dan, dan_to_sphere_outer(
//            point,
//(o.x, o.y, (arena.depth / 2) + arena.goal_side_radius),
//    arena.goal_side_radius))
//// Goal inside top corners
//            if point.z > (arena.depth / 2) + arena.goal_side_radius
//    and point.y > arena.goal_height - arena.goal_top_radius:
//// Side x
//            if point.x > (arena.goal_width / 2) - arena.goal_top_radius:
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(
//                      (arena.goal_width / 2) - arena.goal_top_radius,
//    arena.goal_height - arena.goal_top_radius,
//    point.z
//),
//    arena.goal_top_radius))
//// Side z
//            if point.z > (arena.depth / 2) + arena.goal_depth - arena.goal_top_radius:
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(
//              point.x,
//              arena.goal_height - arena.goal_top_radius,
//(arena.depth / 2) + arena.goal_depth - arena.goal_top_radius
//),
//    arena.goal_top_radius))
//// Bottom corners
//            if point.y < arena.bottom_radius:
//// Side x
//            if point.x > (arena.width / 2) - arena.bottom_radius:
//            13
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(
//                      (arena.width / 2) - arena.bottom_radius,
//    arena.bottom_radius,
//    point.z
//),
//    arena.bottom_radius))
//// Side z
//            if point.z > (arena.depth / 2) - arena.bottom_radius
//    and point.x >= (arena.goal_width / 2) + arena.goal_side_radius:
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(
//              point.x,
//              arena.bottom_radius,
//(arena.depth / 2) - arena.bottom_radius
//),
//    arena.bottom_radius))
//// Side z (goal)
//            if point.z > (arena.depth / 2) + arena.goal_depth - arena.bottom_radius:
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(
//              point.x,
//              arena.bottom_radius,
//(arena.depth / 2) + arena.goal_depth - arena.bottom_radius
//),
//    arena.bottom_radius))
//    // Goal outer corner
//    let o = (
//            (arena.goal_width / 2) + arena.goal_side_radius,
//(arena.depth / 2) + arena.goal_side_radius
//)
//    let v = (point.x, point.z) - o
//if v.x < 0 and v.y < 0
//    and length(v) < arena.goal_side_radius + arena.bottom_radius:
//    let o = o + normalize(v) * (arena.goal_side_radius + arena.bottom_radius)
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(o.x, arena.bottom_radius, o.y),
//    arena.bottom_radius))
//// Side x (goal)
//            if point.z >= (arena.depth / 2) + arena.goal_side_radius
//    and point.x > (arena.goal_width / 2) - arena.bottom_radius:
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(
//                      (arena.goal_width / 2) - arena.bottom_radius,
//    arena.bottom_radius,
//    point.z
//),
//    arena.bottom_radius))
//// Corner
//            if point.x > (arena.width / 2) - arena.corner_radius
//    and point.z > (arena.depth / 2) - arena.corner_radius:
//            14
//    let corner_o = (
//            (arena.width / 2) - arena.corner_radius,
//(arena.depth / 2) - arena.corner_radius
//)
//    let n = (point.x, point.z) - corner_o
//    let dist = n.len()
//if dist > arena.corner_radius - arena.bottom_radius:
//    let n = n / dist
//    let o2 = corner_o + n * (arena.corner_radius - arena.bottom_radius)
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(o2.x, arena.bottom_radius, o2.y),
//    arena.bottom_radius))
//// Ceiling corners
//            if point.y > arena.height - arena.top_radius:
//// Side x
//            if point.x > (arena.width / 2) - arena.top_radius:
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(
//                      (arena.width / 2) - arena.top_radius,
//    arena.height - arena.top_radius,
//    point.z,
//            ),
//    arena.top_radius))
//// Side z
//            if point.z > (arena.depth / 2) - arena.top_radius:
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(
//              point.x,
//              arena.height - arena.top_radius,
//(arena.depth / 2) - arena.top_radius,
//            )
//    arena.top_radius))
//// Corner
//            if point.x > (arena.width / 2) - arena.corner_radius
//    and point.z > (arena.depth / 2) - arena.corner_radius:
//    let corner_o = (
//            (arena.width / 2) - arena.corner_radius,
//(arena.depth / 2) - arena.corner_radius
//)
//    let dv = (point.x, point.z) - corner_o
//if length(dv) > arena.corner_radius - arena.top_radius:
//    let n = normalize(dv)
//    let o2 = corner_o + n * (arena.corner_radius - arena.top_radius)
//    dan = min(dan, dan_to_sphere_inner(
//            point,
//(o2.x, arena.height - arena.top_radius, o2.y),
//    arena.top_radius))
//            return dan
//}

//    function dan_to_sphere_inner(point: Vec3D, sphere_center: Vec3D, sphere_radius: Float):
//            return {
//        distance: sphere_radius - length(point - sphere_center)
//        normal: normalize(sphere_center - point)
//    }


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
