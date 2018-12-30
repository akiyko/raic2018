package model;

import ai.model.Vector3d;

public final class Action {
    public double target_velocity_x;
    public double target_velocity_y;
    public double target_velocity_z;
    public double jump_speed;
    public boolean use_nitro;

    //
    public Vector3d target_velocity;

    @Override
    public String toString() {
        return "Action{" +
                "jump_speed=" + jump_speed +
                ", target_velocity=" + target_velocity +
                '}';
    }
}
