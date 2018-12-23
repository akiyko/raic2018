import ai.Constants;
import model.Action;
import model.Game;
import model.Robot;
import model.Rules;

public final class JustKickStrategy implements Strategy {
    @Override
    public void act(Robot me, Rules rules, Game game, Action action) {
        if(me.z <= game.ball.z) {
            action.target_velocity_x = (game.ball.x - me.x) * 100;
            action.target_velocity_z = (game.ball.z - me.z) * 100;
        } else {
            action.target_velocity_x = (game.ball.x - me.x) * 100;
            action.target_velocity_z = -(rules.arena.depth / 2 + rules.ROBOT_MIN_RADIUS * 2) - me.z;

        }

    }

    @Override
    public String customRendering() {
        return "WhAT'S IT?";
    }
}
