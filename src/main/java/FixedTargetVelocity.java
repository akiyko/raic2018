/**
 * @author akiyko
 * @since 12/27/2018.
 */
public class FixedTargetVelocity implements TargetVelocityProvider {
    private final Vector3d targetVelocity;

    public FixedTargetVelocity(Vector3d targetVelocity) {
        this.targetVelocity = targetVelocity;
    }

    @Override
    public Vector3d targetVelocity(Vector3d originalVelocity, Position currentPosition, int tickNumber) {
        return targetVelocity;
    }
}
