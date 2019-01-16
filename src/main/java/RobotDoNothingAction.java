/**
 * @author akiyko
 * @since 1/16/2019.
 */
public class RobotDoNothingAction extends RobotActionAbstract {

    public RobotDoNothingAction(int validFromTick, int validToTick) {
        super(validFromTick, validToTick);
    }

    @Override
    public MyAction act(int currentTick, PV thisRobotCurrentPV) {
        return new MyAction();
    }
}
