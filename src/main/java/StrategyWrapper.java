import model.Action;
import model.Game;
import model.Robot;
import model.Rules;

/**
 * @author akiyko
 * @since 12/25/2018.
 */
public class StrategyWrapper implements Strategy {

    private final MyMyStrategy myMyStrategy;

    public StrategyWrapper(MyMyStrategy myMyStrategy) {
        this.myMyStrategy = myMyStrategy;
    }

    @Override
    public void act(Robot me, Rules rules, Game game, Action action) {

    }

    @Override
    public String customRendering() {
        return null;
    }
}
