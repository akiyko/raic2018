package ai.model;

import ai.Constants;
import model.Action;
import model.Robot;

import static ai.model.Vector3d.of;

/**
 * By no one on 22.12.2018.
 */
public class MyRobot extends Entity {
    public int id;
//    public int player_id;
//    public boolean is_teammate;
    public boolean touch;

    public Action action;
    public Vector3d touch_normal;
    public double nitro;

    public Robot toRobot(int id, int player_id, boolean isTeammate) {
        Robot r = new Robot();
        r.id = id;
        r.player_id = player_id;
        r.is_teammate = isTeammate;

        r.nitro_amount = nitro;

        r.radius = radius;
        r.touch_normal_x = touch_normal.dx;
        r.touch_normal_y = touch_normal.dy;
        r.touch_normal_z = touch_normal.dz;

        r.x = position.x;
        r.y = position.y;
        r.z = position.z;

        r.velocity_x = velocity.dx;
        r.velocity_y = velocity.dy;
        r.velocity_z = velocity.dz;

        return r;
    }

    public static MyRobot fromRobot(Robot r) {
        MyRobot mr = new MyRobot();
        mr.velocity = of(r.velocity_x, r.velocity_y, r.velocity_z);
        mr.position = new Position(r.x, r.y, r.z);
        mr.touch = r.touch;
        mr.touch_normal = of(r.touch_normal_x, r.touch_normal_y, r.touch_normal_z);

        mr.radius = r.radius;

        mr.nitro = r.nitro_amount;
        mr.id = r.id;

        mr.mass = Constants.ROBOT_MASS;
        mr.arena_e = Constants.ROBOT_ARENA_E;

        return mr;
    }

    @Override
    public MyRobot clone() {
        try {
            return (MyRobot) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MyRobot cloneNegateZ() {
        MyRobot mr = (MyRobot) super.cloneNegateZ();
        if(mr.touch_normal != null) {
            mr.touch_normal = mr.touch_normal.negateZ();
        }

        return  mr;
    }

    @Override
    public String toString() {
        return "MyRobot{" +
                "position=" + position +
                ", velocity=" + velocity +
                ", action=" + action +
                ", touch_normal=" + touch_normal +
                '}';
    }
}
