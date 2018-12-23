package ai;

import ai.model.*;
import model.Arena;
import model.Robot;
import model.Rules;

import java.util.Collections;
import java.util.List;

import static ai.Constants.*;
import static ai.MathUtils.clamp;
import static ai.MathUtils.random;
import static ai.model.Dan.min;
import static ai.model.Vector3d.dot;
import static ai.model.Vector3d.of;
import static ai.model.Vector2d.of;

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

    public static Vector3d collide_with_arena(Entity e, Arena arena) {
        Dan danToArena = dan_to_arena(e.position, arena);
        double distance = danToArena.distance;
        Vector3d normal = danToArena.normal;
        double penetration = e.radius - distance;
        if (penetration > 0) {
            e.position = e.position.plus(normal.multiply(penetration));
            double velocity = dot(e.velocity, normal) - e.radiusChangeSpeed;
            if (velocity < 0) {
                e.velocity = e.velocity.minus(normal.multiply((1 + e.arena_e) * velocity));
                return normal;
            }
        }
        return null;
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


    public static Dan dan_to_arena_quarter(Position point, Arena arena) {
        // Ground
        Dan dan = dan_to_plane(point, new Position(0, 0, 0), of(0, 1, 0));
        // Ceiling
        dan = min(dan, dan_to_plane(point, new Position(0, arena.height, 0), of(0, -1, 0)));
        // Side x
        dan = min(dan, dan_to_plane(point, new Position(arena.width / 2, 0, 0), of(-1, 0, 0)));
        // Side z (goal)
        dan = min(dan, dan_to_plane(point, new Position(0, 0, (arena.depth / 2) + arena.goal_depth), of(0, 0, -1)));
        // Side z
        Vector2d v = of(point.x, point.y).minus(of(
                (arena.goal_width / 2) - arena.goal_top_radius,
                arena.goal_height - arena.goal_top_radius));

        if ((point.x >= (arena.goal_width / 2) + arena.goal_side_radius)
                || (point.y >= arena.goal_height + arena.goal_side_radius)
                || (v.x() > 0
                && v.y() > 0
                && v.length() >= arena.goal_top_radius + arena.goal_side_radius)) {
            dan = min(dan, dan_to_plane(point, new Position(0, 0, arena.depth / 2), of(0, 0, -1)));
        }
        // Side x & ceiling (goal)
        if (point.z >= (arena.depth / 2) + arena.goal_side_radius) {
            // x
            dan = min(dan, dan_to_plane(
                    point,
                    new Position(arena.goal_width / 2, 0, 0),
                    of(-1, 0, 0)));
            // y
            dan = min(dan, dan_to_plane(point, new Position(0, arena.goal_height, 0), of(0, -1, 0)));
        }
        // Goal back corners
        //assert arena.bottom_radius == arena.goal_top_radius
        if (point.z > (arena.depth / 2) + arena.goal_depth - arena.bottom_radius) {
            dan = min(dan, dan_to_sphere_inner(
                    point,
                    new Position(
                            clamp(point.x,
                                    arena.bottom_radius - (arena.goal_width / 2),
                                    (arena.goal_width / 2) - arena.bottom_radius
                            ),
                            clamp(point.y,
                                    arena.bottom_radius,
                                    arena.goal_height - arena.goal_top_radius
                            ),
                            (arena.depth / 2) + arena.goal_depth - arena.bottom_radius),
                    arena.bottom_radius));
        }
        // Corner
        if (point.x > (arena.width / 2) - arena.corner_radius
                && point.z > (arena.depth / 2) - arena.corner_radius) {
            dan = min(dan, dan_to_sphere_inner(
                    point,
                    new Position(
                            (arena.width / 2) - arena.corner_radius,
                            point.y,
                            (arena.depth / 2) - arena.corner_radius
                    ),
                    arena.corner_radius));
        }
        // Goal outer corner
        if (point.z < (arena.depth / 2) + arena.goal_side_radius) {
            // Side x
            if (point.x < (arena.goal_width / 2) + arena.goal_side_radius) {
                dan = min(dan, dan_to_sphere_outer(
                        point,
                        new Position(
                                (arena.goal_width / 2) + arena.goal_side_radius,
                                point.y,
                                (arena.depth / 2) + arena.goal_side_radius
                        ),
                        arena.goal_side_radius));
            }
            // Ceiling
            if (point.y < arena.goal_height + arena.goal_side_radius) {
                dan = min(dan, dan_to_sphere_outer(
                        point,
                        new Position(
                                point.x,
                                arena.goal_height + arena.goal_side_radius,
                                (arena.depth / 2) + arena.goal_side_radius
                        ),
                        arena.goal_side_radius));
            }
            // Top corner
            Vector2d o = of(
                    (arena.goal_width / 2) - arena.goal_top_radius,
                    arena.goal_height - arena.goal_top_radius
            );
            v = of(point.x, point.y).minus(o); //TODO: v already defined!!!

            if (v.x() > 0 && v.y() > 0) {
                o = o.plus(v.normalize().multiply(arena.goal_top_radius + arena.goal_side_radius));//TODO: o already defined!!!
                dan = min(dan, dan_to_sphere_outer(
                        point,
                        new Position(o.x(), o.y(), (arena.depth / 2) + arena.goal_side_radius),
                        arena.goal_side_radius));
            }
        }
        // Goal inside top corners
        if (point.z > (arena.depth / 2) + arena.goal_side_radius
                && point.y > arena.goal_height - arena.goal_top_radius) {
            // Side x
            if (point.x > (arena.goal_width / 2) - arena.goal_top_radius) {
                dan = min(dan, dan_to_sphere_inner(
                        point,
                        new Position(
                                (arena.goal_width / 2) - arena.goal_top_radius,
                                arena.goal_height - arena.goal_top_radius,
                                point.z
                        ),
                        arena.goal_top_radius));
            }
            // Side z
            if (point.z > (arena.depth / 2) + arena.goal_depth - arena.goal_top_radius) {
                dan = min(dan, dan_to_sphere_inner(
                        point,
                        new Position(
                                point.x,
                                arena.goal_height - arena.goal_top_radius,
                                (arena.depth / 2) + arena.goal_depth - arena.goal_top_radius
                        ),
                        arena.goal_top_radius));
            }
        }
        // Bottom corners
        if (point.y < arena.bottom_radius) {
            // Side x
            if (point.x > (arena.width / 2) - arena.bottom_radius) {
                dan = min(dan, dan_to_sphere_inner(
                        point,
                        new Position(
                                (arena.width / 2) - arena.bottom_radius,
                                arena.bottom_radius,
                                point.z
                        ),
                        arena.bottom_radius));
            }
            // Side z
            if (point.z > (arena.depth / 2) - arena.bottom_radius
                    && point.x >= (arena.goal_width / 2) + arena.goal_side_radius) {
                dan = min(dan, dan_to_sphere_inner(
                        point,
                        new Position(
                                point.x,
                                arena.bottom_radius,
                                (arena.depth / 2) - arena.bottom_radius
                        ),
                        arena.bottom_radius));
            }
            // Side z (goal)
            if (point.z > (arena.depth / 2) + arena.goal_depth - arena.bottom_radius) {
                dan = min(dan, dan_to_sphere_inner(
                        point,
                        new Position(
                                point.x,
                                arena.bottom_radius,
                                (arena.depth / 2) + arena.goal_depth - arena.bottom_radius
                        ),
                        arena.bottom_radius));
            }
            // Goal outer corner
            Vector2d o = of(
                    (arena.goal_width / 2) + arena.goal_side_radius,
                    (arena.depth / 2) + arena.goal_side_radius);
            v = of(point.x, point.z).minus(o); //v REUSE
            if (v.x() < 0 && v.y() < 0
                    && v.length() < arena.goal_side_radius + arena.bottom_radius) {
                o = o.plus(v.normalize().multiply(arena.goal_side_radius + arena.bottom_radius));//o REUSE
                dan = min(dan, dan_to_sphere_inner(
                        point,
                        new Position(o.x(), arena.bottom_radius, o.y()),
                        arena.bottom_radius));
            }
            // Side x (goal)
            if (point.z >= (arena.depth / 2) + arena.goal_side_radius
                    && point.x > (arena.goal_width / 2) - arena.bottom_radius) {
                dan = min(dan, dan_to_sphere_inner(
                        point,
                        new Position(
                                (arena.goal_width / 2) - arena.bottom_radius,
                                arena.bottom_radius,
                                point.z
                        ),
                        arena.bottom_radius));
            }
            // Corner
            if (point.x > (arena.width / 2) - arena.corner_radius
                    && point.z > (arena.depth / 2) - arena.corner_radius) {
                Vector2d corner_o = of(
                        (arena.width / 2) - arena.corner_radius,
                        (arena.depth / 2) - arena.corner_radius);
                Vector2d n = of(point.x, point.z).minus(corner_o);
                double dist = n.length();
                if (dist > arena.corner_radius - arena.bottom_radius) {
                    n = n.multiply(1.0 / dist);
                    Vector2d o2 = corner_o.plus(n.multiply(arena.corner_radius - arena.bottom_radius));
                    dan = min(dan, dan_to_sphere_inner(
                            point,
                            new Position(o2.x(), arena.bottom_radius, o2.y()),
                            arena.bottom_radius));
                }
            }
        }
        // Ceiling corners
        if (point.y > arena.height - arena.top_radius) {
            // Side x
            if (point.x > (arena.width / 2) - arena.top_radius) {
                dan = min(dan, dan_to_sphere_inner(
                        point,
                        new Position(
                                (arena.width / 2) - arena.top_radius,
                                arena.height - arena.top_radius,
                                point.z
                        ),
                        arena.top_radius));
            }
            // Side z
            if (point.z > (arena.depth / 2) - arena.top_radius) {
                dan = min(dan, dan_to_sphere_inner(
                        point,
                        new Position(
                                point.x,
                                arena.height - arena.top_radius,
                                (arena.depth / 2) - arena.top_radius
                        ),
                        arena.top_radius));
            }
            // Corner
            if (point.x > (arena.width / 2) - arena.corner_radius
                    && point.z > (arena.depth / 2) - arena.corner_radius) {
                Vector2d corner_o = of(
                        (arena.width / 2) - arena.corner_radius,
                        (arena.depth / 2) - arena.corner_radius);
                Vector2d dv = of(point.x, point.z).minus(corner_o);
                if (dv.length() > arena.corner_radius - arena.top_radius) {
                    Vector2d n = dv.normalize();
                    Vector2d o2 = corner_o.plus(n.multiply(arena.corner_radius - arena.top_radius));
                    dan = min(dan, dan_to_sphere_inner(
                            point,
                            new Position(o2.x(), arena.height - arena.top_radius, o2.y()),
                            arena.top_radius));
                }
            }
        }

        return dan;
    }

    public static Dan dan_to_arena(Position point, Arena arena) {
        Position pointTo = point;
        boolean negate_x = point.x < 0;
        boolean negate_z = point.z < 0;
        if (negate_x) {
//            point.x = -point.x;
            pointTo = pointTo.negateX();
        }
        if (negate_z) {
//            point.z = -point.z
            pointTo = pointTo.negateZ();
        }
        Dan result = dan_to_arena_quarter(pointTo, arena);
        double resultnormalx = result.normal.dx;
        double resultnormalz = result.normal.dz;
        if (negate_x) {
//            result.normal.x = -result.normal.x
            resultnormalx = -resultnormalx;
        }
        if (negate_z) {
//            result.normal.z = -result.normal.z
            resultnormalz = -resultnormalz;
        }
        return Dan.of(result.distance, of(resultnormalx, result.normal.dy, resultnormalz));
    }

    public static void update(double delta_time, Rules rules, List<MyRobot> robots, MyBall ball) {
        Collections.shuffle(robots);
        for (MyRobot robot : robots) {
            if (robot.touch) {
                Vector3d target_velocity =
                        robot.action.target_velocity.clamp(ROBOT_MAX_GROUND_SPEED);
                target_velocity = target_velocity.minus(robot.touch_normal.multiply(dot(robot.touch_normal, target_velocity)));
                Vector3d target_velocity_change = target_velocity.minus(robot.velocity);
                if (target_velocity_change.length() > 0) {
                    double acceleration = ROBOT_ACCELERATION * Math.max(0, robot.touch_normal.dy);
                    robot.velocity = robot.velocity.plus(
                            target_velocity_change.normalize().multiply(acceleration * delta_time)
                                    .clamp(target_velocity_change.length()));
                }
            }

            if (robot.action.use_nitro) {
                Vector3d target_velocity_change =
                        robot.action.target_velocity.minus(robot.velocity).clamp(robot.nitro * NITRO_POINT_VELOCITY_CHANGE);
                if (target_velocity_change.length() > 0) {
                    Vector3d acceleration = target_velocity_change.normalize().multiply(ROBOT_NITRO_ACCELERATION);
                    Vector3d velocity_change = acceleration.multiply(delta_time).clamp(target_velocity_change.length());
                    robot.velocity = robot.velocity.plus(velocity_change);
                    robot.nitro -= velocity_change.length() / NITRO_POINT_VELOCITY_CHANGE;
                }
            }
            move(robot, delta_time);
            robot.radius = ROBOT_MIN_RADIUS
                    + (ROBOT_MAX_RADIUS - ROBOT_MIN_RADIUS) * robot.action.jump_speed / ROBOT_MAX_JUMP_SPEED;
            robot.radiusChangeSpeed = robot.action.jump_speed;
        }
        move(ball, delta_time);
        for (int i = 0; i < robots.size(); i++) {
            for (int j = 0; j < i; j++) {
                collideEntities(robots.get(i), robots.get(j));
            }
        }
        for (MyRobot robot : robots) {
            collideEntities(robot, ball);
            Vector3d collision_normal = collide_with_arena(robot, rules.arena);
            if (collision_normal == null) {
                robot.touch = false;
            } else {
                robot.touch = true;
                robot.touch_normal = collision_normal;
            }
        }

        collide_with_arena(ball, rules.arena);
        if (Math.abs(ball.position.z) > rules.arena.depth / 2 + ball.radius) {
            goal_scored();
        }

        //TODO: implement nitro packs

//      for (MyRobot robot : robots) {
//          if (robot.nitro == MAX_NITRO_AMOUNT) {
//              continue;
//          }
//          for pack in nitro_packs:
//          if not pack.alive:
//          continue
//          if length(robot.position - pack.position) <= robot.radius + pack.radius:
//          robot.nitro = MAX_NITRO_AMOUNT
//          pack.alive = false
//          pack.respawn_ticks = NITRO_PACK_RESPAWN_TICKS
//      }

    }

    public static void tick(Rules rules, List<MyRobot> robots, MyBall ball) {
        double delta_time = 1 / TICKS_PER_SECOND;
        for( int i = 0; i< MICROTICKS_PER_TICK; i++) {
            update(delta_time / MICROTICKS_PER_TICK, rules, robots, ball);
        }
//        for pack in nitro_packs:
//        if pack.alive:
//        continue
//                pack.respawn_ticks -= 1
//        if pack.respawn_ticks == 0:
//        pack.alive = true
    }


    public static void goal_scored() {
        //TODO: implement!!!
        System.out.println("Goal scored");
    }

}


