import model.Arena;

/**
 * @author akiyko
 * @since 1/11/2019.
 */
public class StrategyParams {
    public static final int BOUNCES = 2;
    public static final int MAX_GOAL_TICK = 300;

    public int mpt = 100;
    public int ballTickDepth = 300;
    public int planRecalculateFrequency = Integer.MAX_VALUE; //never
    public boolean usePotentialGoals = false;

    public int seekSteps = 80;
    public int goalSteps = 250;
    public int ticksOffsetMin = -10;
    public int ticksOffsetStart = 1;//

//    public int useNitroOnGroundAmount = 25;
//    public int useNitroOnFlyAmount = 50;
    public int useNitroOnGroundAmount = 101;
    public int useNitroOnFlyAmount = 50;
    public static int fewTickMore = 2;

    public static boolean fearCorners = true;
    public static Arena arena;

    public static boolean random_on = false;

}
