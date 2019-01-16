import model.Action;
import model.Game;
import model.Robot;
import model.Rules;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author akiyko
 * @since 12/25/2018.
 */
public class StrategyWrapper implements Strategy {

    private final MyMyStrategyAbstract myMyStrategy;

    public StrategyWrapper(MyMyStrategyAbstract myMyStrategy) {
        this.myMyStrategy = myMyStrategy;
    }

    @Override
    public void act(Robot me, Rules rules, Game game, Action action) {
        if(!myMyStrategy.isTickComputed(game.current_tick)) {
            Map<Integer, MyRobot> myRobots = Arrays.stream(game.robots).filter(r -> r.is_teammate)
                    .map(MyRobot::fromRobot)
                    .collect(Collectors.toMap(r -> r.id, Function.identity()));

            Map<Integer, MyRobot> oppRobots = Arrays.stream(game.robots).filter(r -> !r.is_teammate)
                    .map(MyRobot::fromRobot)
                    .collect(Collectors.toMap(r -> r.id, Function.identity()));

            myMyStrategy.computeTick(game.current_tick, myRobots, oppRobots, MyBall.fromBall(game.ball), rules);
        }

        Action act = myMyStrategy.act(me.id);
        action.jump_speed = act.jump_speed;
        action.target_velocity_x = act.target_velocity_x;
        action.target_velocity_y = act.target_velocity_y;
        action.target_velocity_z = act.target_velocity_z;
        action.use_nitro = act.use_nitro;
    }

    @Override
    public String customRendering() {
        return "";
    }
}
