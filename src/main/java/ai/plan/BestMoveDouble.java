package ai.plan;

/**
 * @author akiyko
 * @since 12/29/2018.
 */
public class BestMoveDouble {
    public double low;
    public double hi;
    public double optimal;

    public GamePlanResult lowPlanResult;
    public GamePlanResult optimalPlanResult;
    public GamePlanResult hiPlanResult;


    @Override
    public String toString() {
        return "BestMoveDouble{" +
                "low=" + low +
                ", hi=" + hi +
                ", optimal=" + optimal +
                ", lowPlanResult=" + lowPlanResult +
                ", optimalPlanResult=" + optimalPlanResult +
                ", hiPlanResult=" + hiPlanResult +
                '}';
    }
}
