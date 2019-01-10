/**
 * @author akiyko
 * @since 1/10/2019.
 */
public class OptimiseOptions {
    public static int SKIP_BALL_TRACE_TICKS = 20;

    public static double MIN_LEN_TO_BALL_REQUIRED = Constants.ROBOT_MIN_RADIUS + Constants.BALL_RADIUS
            + Constants.MAX_ENTITY_SPEED * SKIP_BALL_TRACE_TICKS / Constants.TICKS_PER_SECOND;

    public boolean noCollideWithArena;


    public static OptimiseOptions noCollideWithArena() {
        OptimiseOptions opt = new OptimiseOptions();
        opt.noCollideWithArena = true;

        return opt;
    }
}
