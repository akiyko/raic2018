import model.Action;

/**
 * By no one on 09.01.2019.
 */
public class MyAction {
    public double jump_speed;
    public boolean use_nitro;

    //
    public Vector3d target_velocity;

    public Action toAction() {
        Action action = new Action();
        if(target_velocity != null) { //TODO: where???!!!
            action.target_velocity_x = target_velocity.dx;
            action.target_velocity_y = target_velocity.dy;
            action.target_velocity_z = target_velocity.dz;

            action.jump_speed = jump_speed;
            action.use_nitro = use_nitro;
        }

        return action;
    }

    @Override
    public String toString() {
        return "Action{" +
                "jump_speed=" + jump_speed +
                ", target_velocity=" + target_velocity +
                '}';
    }
}
