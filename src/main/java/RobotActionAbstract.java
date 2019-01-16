/**
 * @author akiyko
 * @since 1/16/2019.
 */
public abstract class RobotActionAbstract implements RobotAction {
    public final int validFromTick;
    public final int validToTick;

    public RobotActionAbstract(int validFromTick, int validToTick) {
        this.validFromTick = validFromTick;
        this.validToTick = validToTick;
    }

    public abstract MyAction act(int currentTick, PV thisRobotCurrentPV);

    @Override
    public int validFromTick() {
        return validFromTick;
    }

    @Override
    public int validToTick() {
        return validToTick;
    }
}
