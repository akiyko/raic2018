/**
 * @author akiyko
 * @since 1/11/2019.
 */
public class StrategyParams {
    public static final int BOUNCES = 10;

    public int mpt = 100;
    public int ballTickDepth = 300;
    public int planRecalculateFrequency = Integer.MAX_VALUE; //never
    public boolean usePotentialGoals = false;

    public int seekSteps = 80;
    public int goalSteps = 80;
    public int ticksOffsetMin = -30;
    public int ticksOffsetStart = 5;//
}
