import model.Arena;
import model.Rules;

import java.util.Collections;
import java.util.List;

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
            double delta_velocity = Vector3d.dot(b.velocity.minus(a.velocity), normal) + b.radiusChangeSpeed - a.radiusChangeSpeed;
            if (delta_velocity < 0) {
//                Vector3d impulse = normal.multiply((1 + random(MIN_HIT_E, MAX_HIT_E)) * delta_velocity); //TODO: testing
                Vector3d impulse = normal.multiply((1 + 0.5 * (Constants.MIN_HIT_E + Constants.MAX_HIT_E)) * delta_velocity);
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
            double velocity = Vector3d.dot(e.velocity, normal) - e.radiusChangeSpeed;
            if (velocity < 0) {
                e.velocity = e.velocity.minus(normal.multiply((1 + e.arena_e) * velocity));
                return normal;
            }
        }
        return null;
    }

    public static Vector3d collide_with_arena_bot(Entity e, Arena arena) {
        Position pointTo = e.position;
        boolean negate_x = pointTo.x < 0;
        boolean negate_z = pointTo.z < 0;
        if (negate_x) {
            pointTo = pointTo.negateX();
        }
        if (negate_z) {
            pointTo = pointTo.negateZ();
        }
        Dan result = dan_to_plane(pointTo, new Position(0, 0, 0), Vector3d.of(0, 1, 0));
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
        Dan danToArena = Dan.of(result.distance, Vector3d.of(resultnormalx, result.normal.dy, resultnormalz));

        double distance = danToArena.distance;
        Vector3d normal = danToArena.normal;
        double penetration = e.radius - distance;
        if (penetration > 0) {
            e.position = e.position.plus(normal.multiply(penetration));
            double velocity = Vector3d.dot(e.velocity, normal) - e.radiusChangeSpeed;
            if (velocity < 0) {
                e.velocity = e.velocity.minus(normal.multiply((1 + e.arena_e) * velocity));
                return normal;
            }
        }
        return null;
    }

    public static void move(Entity e, double delta_time) {
        e.velocity = e.velocity.clamp(Constants.MAX_ENTITY_SPEED);
        e.position = e.position.plus(e.velocity.multiply(delta_time));
        e.position = e.position.minus(Vector3d.of(0, Constants.GRAVITY * delta_time * delta_time / 2.0, 0));
        e.velocity = e.velocity.minus(Vector3d.of(0, Constants.GRAVITY * delta_time, 0));
    }

    public static Dan dan_to_plane(Position point, Position point_on_plane, Vector3d plane_normal) {
        return Dan.of(Vector3d.dot(point.minus(point_on_plane), plane_normal),
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
        Dan dan = dan_to_plane(point, new Position(0, 0, 0), Vector3d.of(0, 1, 0));
        // Ceiling
        dan = Dan.min(dan, dan_to_plane(point, new Position(0, arena.height, 0), Vector3d.of(0, -1, 0)));
        // Side x
        dan = Dan.min(dan, dan_to_plane(point, new Position(arena.width / 2, 0, 0), Vector3d.of(-1, 0, 0)));
        // Side z (goal)
        dan = Dan.min(dan, dan_to_plane(point, new Position(0, 0, (arena.depth / 2) + arena.goal_depth), Vector3d.of(0, 0, -1)));
        // Side z
        Vector2d v = Vector2d.of(point.x, point.y).minus(Vector2d.of(
                (arena.goal_width / 2) - arena.goal_top_radius,
                arena.goal_height - arena.goal_top_radius));

        if ((point.x >= (arena.goal_width / 2) + arena.goal_side_radius)
                || (point.y >= arena.goal_height + arena.goal_side_radius)
                || (v.x() > 0
                && v.y() > 0
                && v.length() >= arena.goal_top_radius + arena.goal_side_radius)) {
            dan = Dan.min(dan, dan_to_plane(point, new Position(0, 0, arena.depth / 2), Vector3d.of(0, 0, -1)));
        }
        // Side x & ceiling (goal)
        if (point.z >= (arena.depth / 2) + arena.goal_side_radius) {
            // x
            dan = Dan.min(dan, dan_to_plane(
                    point,
                    new Position(arena.goal_width / 2, 0, 0),
                    Vector3d.of(-1, 0, 0)));
            // y
            dan = Dan.min(dan, dan_to_plane(point, new Position(0, arena.goal_height, 0), Vector3d.of(0, -1, 0)));
        }
        // Goal back corners
        //assert arena.bottom_radius == arena.goal_top_radius
        if (point.z > (arena.depth / 2) + arena.goal_depth - arena.bottom_radius) {
            dan = Dan.min(dan, dan_to_sphere_inner(
                    point,
                    new Position(
                            MathUtils.clamp(point.x,
                                    arena.bottom_radius - (arena.goal_width / 2),
                                    (arena.goal_width / 2) - arena.bottom_radius
                            ),
                            MathUtils.clamp(point.y,
                                    arena.bottom_radius,
                                    arena.goal_height - arena.goal_top_radius
                            ),
                            (arena.depth / 2) + arena.goal_depth - arena.bottom_radius),
                    arena.bottom_radius));
        }
        // Corner
        if (point.x > (arena.width / 2) - arena.corner_radius
                && point.z > (arena.depth / 2) - arena.corner_radius) {
            dan = Dan.min(dan, dan_to_sphere_inner(
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
                dan = Dan.min(dan, dan_to_sphere_outer(
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
                dan = Dan.min(dan, dan_to_sphere_outer(
                        point,
                        new Position(
                                point.x,
                                arena.goal_height + arena.goal_side_radius,
                                (arena.depth / 2) + arena.goal_side_radius
                        ),
                        arena.goal_side_radius));
            }
            // Top corner
            Vector2d o = Vector2d.of(
                    (arena.goal_width / 2) - arena.goal_top_radius,
                    arena.goal_height - arena.goal_top_radius
            );
            v = Vector2d.of(point.x, point.y).minus(o);

            if (v.x() > 0 && v.y() > 0) {
                o = o.plus(v.normalize().multiply(arena.goal_top_radius + arena.goal_side_radius));
                dan = Dan.min(dan, dan_to_sphere_outer(
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
                dan = Dan.min(dan, dan_to_sphere_inner(
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
                dan = Dan.min(dan, dan_to_sphere_inner(
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
                dan = Dan.min(dan, dan_to_sphere_inner(
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
                dan = Dan.min(dan, dan_to_sphere_inner(
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
                dan = Dan.min(dan, dan_to_sphere_inner(
                        point,
                        new Position(
                                point.x,
                                arena.bottom_radius,
                                (arena.depth / 2) + arena.goal_depth - arena.bottom_radius
                        ),
                        arena.bottom_radius));
            }
            // Goal outer corner
            Vector2d o = Vector2d.of(
                    (arena.goal_width / 2) + arena.goal_side_radius,
                    (arena.depth / 2) + arena.goal_side_radius);
            v = Vector2d.of(point.x, point.z).minus(o); //v REUSE
            if (v.x() < 0 && v.y() < 0
                    && v.length() < arena.goal_side_radius + arena.bottom_radius) {
                o = o.plus(v.normalize().multiply(arena.goal_side_radius + arena.bottom_radius));//o REUSE
                dan = Dan.min(dan, dan_to_sphere_inner(
                        point,
                        new Position(o.x(), arena.bottom_radius, o.y()),
                        arena.bottom_radius));
            }
            // Side x (goal)
            if (point.z >= (arena.depth / 2) + arena.goal_side_radius
                    && point.x > (arena.goal_width / 2) - arena.bottom_radius) {
                dan = Dan.min(dan, dan_to_sphere_inner(
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
                Vector2d corner_o = Vector2d.of(
                        (arena.width / 2) - arena.corner_radius,
                        (arena.depth / 2) - arena.corner_radius);
                Vector2d n = Vector2d.of(point.x, point.z).minus(corner_o);
                double dist = n.length();
                if (dist > arena.corner_radius - arena.bottom_radius) {
                    n = n.multiply(1.0 / dist);
                    Vector2d o2 = corner_o.plus(n.multiply(arena.corner_radius - arena.bottom_radius));
                    dan = Dan.min(dan, dan_to_sphere_inner(
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
                dan = Dan.min(dan, dan_to_sphere_inner(
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
                dan = Dan.min(dan, dan_to_sphere_inner(
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
                Vector2d corner_o = Vector2d.of(
                        (arena.width / 2) - arena.corner_radius,
                        (arena.depth / 2) - arena.corner_radius);
                Vector2d dv = Vector2d.of(point.x, point.z).minus(corner_o);
                if (dv.length() > arena.corner_radius - arena.top_radius) {
                    Vector2d n = dv.normalize();
                    Vector2d o2 = corner_o.plus(n.multiply(arena.corner_radius - arena.top_radius));
                    dan = Dan.min(dan, dan_to_sphere_inner(
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
        return Dan.of(result.distance, Vector3d.of(resultnormalx, result.normal.dy, resultnormalz));
    }

    public static void updateBallOnlyTick(Rules rules, MyBall ball, double mpt) throws GoalScoredException {

//        let delta_time = 1 / TICKS_PER_SECOND
//        for _ in 0 .. MICROTICKS_PER_TICK - 1:
//        update(delta_time / MICROTICKS_PER_TICK)
        double delta_time = 1.0 / (Constants.TICKS_PER_SECOND * mpt);

        Dan danToArena = dan_to_arena(ball.position, rules.arena);

        boolean isBottom = Math.abs(ball.position.x) < rules.arena.width * 0.5 - rules.arena.bottom_radius - 3
                && Math.abs(ball.position.z) < rules.arena.depth * 0.5 - rules.arena.bottom_radius - 3
                && MathUtils.isZero(danToArena.normal.dx) && MathUtils.isZero(danToArena.normal.dz) && danToArena.normal.dy > 0;

        //if no collision in two ticks - don't do collide
        if (danToArena.distance > ball.radius + 1.2 * ball.velocity.length() * mpt * delta_time) {
            for (int i = 0; i < mpt; i++) {
                move(ball, delta_time);
            }
        } else {
            if (isBottom) {
                for (int i = 0; i < mpt; i++) {
                    move(ball, delta_time);
                    collide_with_arena_bot(ball, rules.arena);
                }
            } else {
                for (int i = 0; i < mpt; i++) {
                    move(ball, delta_time);
                    collide_with_arena(ball, rules.arena);
                }
            }
        }
        if (Math.abs(ball.position.z) > rules.arena.depth / 2 + ball.radius) {
            goal_scored(ball.position.z);
        }
    }

    public static void updateRobotOnly(double delta_time, Rules rules, MyRobot robot) {
        if (robot.touch) {
            Vector3d target_velocity =
                    robot.action.target_velocity.clamp(Constants.ROBOT_MAX_GROUND_SPEED);
            target_velocity = target_velocity.minus(robot.touch_normal.multiply(Vector3d.dot(robot.touch_normal, target_velocity)));
            Vector3d target_velocity_change = target_velocity.minus(robot.velocity);
            if (target_velocity_change.length() > 0) {
                double acceleration = Constants.ROBOT_ACCELERATION * Math.max(0, robot.touch_normal.dy);
                robot.velocity = robot.velocity.plus(
                        target_velocity_change.normalize().multiply(acceleration * delta_time)
                                .clamp(target_velocity_change.length()));
            }
        }

        if (robot.action.use_nitro) {
            Vector3d target_velocity_change =
                    robot.action.target_velocity.minus(robot.velocity).clamp(robot.nitro * Constants.NITRO_POINT_VELOCITY_CHANGE);
            if (target_velocity_change.length() > Constants.DOUBLE_ZERO) {
                Vector3d acceleration = target_velocity_change.normalize().multiply(Constants.ROBOT_NITRO_ACCELERATION);
                Vector3d velocity_change = acceleration.multiply(delta_time).clamp(target_velocity_change.length());
                robot.velocity = robot.velocity.plus(velocity_change);
                robot.nitro -= velocity_change.length() / Constants.NITRO_POINT_VELOCITY_CHANGE;
            }
        }
        move(robot, delta_time);
        robot.radius = Constants.ROBOT_MIN_RADIUS
                + (Constants.ROBOT_MAX_RADIUS - Constants.ROBOT_MIN_RADIUS) * robot.action.jump_speed / Constants.ROBOT_MAX_JUMP_SPEED;
        robot.radiusChangeSpeed = robot.action.jump_speed;

        Vector3d collision_normal = collide_with_arena(robot, rules.arena);
        if (collision_normal == null) {
            robot.touch = false;
        } else {
            robot.touch = true;
            robot.touch_normal = collision_normal;
        }
    }

    public static void update(double delta_time, Rules rules, List<MyRobot> robots, MyBall ball, List<NitroPack> nitroPacks) {
        Collections.shuffle(robots);
        for (MyRobot robot : robots) {
            if (robot.touch) {
                Vector3d target_velocity =
                        robot.action.target_velocity.clamp(Constants.ROBOT_MAX_GROUND_SPEED);
                target_velocity = target_velocity.minus(robot.touch_normal.multiply(Vector3d.dot(robot.touch_normal, target_velocity)));
                Vector3d target_velocity_change = target_velocity.minus(robot.velocity);
                if (target_velocity_change.length() > 0) {
                    double acceleration = Constants.ROBOT_ACCELERATION * Math.max(0, robot.touch_normal.dy);
                    robot.velocity = robot.velocity.plus(
                            target_velocity_change.normalize().multiply(acceleration * delta_time)
                                    .clamp(target_velocity_change.length()));
                }
            }

            if (robot.action.use_nitro) {
                Vector3d target_velocity_change =
                        robot.action.target_velocity.minus(robot.velocity).clamp(robot.nitro * Constants.NITRO_POINT_VELOCITY_CHANGE);
                if (target_velocity_change.length() > 0) {
                    Vector3d acceleration = target_velocity_change.normalize().multiply(Constants.ROBOT_NITRO_ACCELERATION);
                    Vector3d velocity_change = acceleration.multiply(delta_time).clamp(target_velocity_change.length());
                    robot.velocity = robot.velocity.plus(velocity_change);
                    robot.nitro -= velocity_change.length() / Constants.NITRO_POINT_VELOCITY_CHANGE;
                }
            }
            move(robot, delta_time);
            robot.radius = Constants.ROBOT_MIN_RADIUS
                    + (Constants.ROBOT_MAX_RADIUS - Constants.ROBOT_MIN_RADIUS) * robot.action.jump_speed / Constants.ROBOT_MAX_JUMP_SPEED;
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
            goal_scored(ball.position.z);
        }

        //TODO: implement nitro packs

        for (MyRobot robot : robots) {
            if (robot.nitro == Constants.MAX_NITRO_AMOUNT) {
                continue;
            }

            for (NitroPack nitroPack : nitroPacks) {
                if (!nitroPack.alive) {
                    continue;
                }
                if (robot.position.minus(nitroPack.position).length() < robot.radius + nitroPack.radius) {
                    robot.nitro = Constants.MAX_NITRO_AMOUNT;
                    nitroPack.alive = false;
                    nitroPack.respawn_ticks = Constants.NITRO_RESPAWN_TICKS;
                }
            }
        }
    }

    public static void tickRobotOnly(Rules rules, MyRobot robot, double microticksPerTick) {
        double delta_time = 1.0 / Constants.TICKS_PER_SECOND;
        for (int i = 0; i < microticksPerTick; i++) {
            updateRobotOnly(delta_time / microticksPerTick, rules, robot);
        }
    }

    public static void tick(Rules rules, List<MyRobot> robots, MyBall ball) {
        tick(rules, robots, ball, Constants.MICROTICKS_PER_TICK, Collections.emptyList());
    }

    public static void tick(Rules rules, List<MyRobot> robots, MyBall ball, List<NitroPack> nitroPacks) {
        tick(rules, robots, ball, Constants.MICROTICKS_PER_TICK, nitroPacks);
    }

    public static void tick(Rules rules, List<MyRobot> robots, MyBall ball, double microticksPerTick, List<NitroPack> nitroPacks) {
        double delta_time = 1.0 / Constants.TICKS_PER_SECOND;
        for (int i = 0; i < microticksPerTick; i++) {
            update(delta_time / microticksPerTick, rules, robots, ball, nitroPacks);
        }
        for (NitroPack nitroPack : nitroPacks) {
            if (nitroPack.alive) {
                continue;
            }
            nitroPack.respawn_ticks--;
            if (nitroPack.respawn_ticks == 0) {
                nitroPack.alive = true;
            }
        }
//        for pack in nitro_packs:
//        if pack.alive:
//        continue
//                pack.respawn_ticks -= 1
//        if pack.respawn_ticks == 0:
//        pack.alive = true
    }


    public static void goal_scored(double z) throws RuntimeException {
        //TODO: implement!!!
//        System.out.println("Goal scored");
        throw new GoalScoredException(z);
    }

}


