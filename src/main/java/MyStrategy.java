import model.*;

public final class MyStrategy implements Strategy {
    @Override
    public void act(Robot me, Rules rules, Game game, Action action) {
        action.jump_speed = 15;
        action.target_velocity_x = 100;
    }
}
