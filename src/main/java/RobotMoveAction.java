/**
 * @author akiyko
 * @since 1/16/2019.
 */
public class RobotMoveAction extends RobotActionAbstract {

    public final Vector3d targetVelocity;

    public RobotMoveAction(int validFromTick, int validToTick, Vector3d targetVelocity) {
        super(validFromTick, validToTick);
        this.targetVelocity = targetVelocity;
    }

    @Override
    public MyAction act(int currentTick, PV thisRobotCurrentPV) {
        MyAction action = new MyAction();
        action.target_velocity = targetVelocity;

        return action;
    }
}
