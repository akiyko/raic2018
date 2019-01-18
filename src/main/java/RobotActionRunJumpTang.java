/**
 * @author akiyko
 * @since 1/16/2019.
 */
public class RobotActionRunJumpTang extends RobotActionAbstract {
    public final Vector3d targetVelocityGround;
    public final int jumpTick;
    public final boolean useNitroOnGround;
    public final boolean useNitroOnFly;

    public RobotActionRunJumpTang(int validFromTick, int validToTick, Vector3d targetVelocityGround,
                                  int jumpTick, boolean useNitroOnGround, boolean useNitroOnFly) {
        super(validFromTick, validToTick);
        this.targetVelocityGround = targetVelocityGround;
        this.jumpTick = jumpTick;
        this.useNitroOnGround = useNitroOnGround;
        this.useNitroOnFly = useNitroOnFly;
    }

    @Override
    public MyAction act(int currentTick, PV thisRobotCurrentPV) {
        MyAction myAction = new MyAction();
        if(currentTick < jumpTick) {
            myAction.target_velocity = targetVelocityGround;
            if(useNitroOnGround && targetVelocityGround.minus(thisRobotCurrentPV.v).length() > Constants.DOUBLE_ZERO) {
                myAction.use_nitro = true;
            }
        } else {
            myAction.target_velocity = thisRobotCurrentPV.v;
            myAction.use_nitro = useNitroOnFly;
            myAction.jump_speed = Constants.ROBOT_MAX_JUMP_SPEED;
        }

        return myAction;
    }

    @Override
    public boolean isGoal() {
        return true;
    }
}
