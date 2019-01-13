/**
 * @author akiyko
 * @since 12/27/2018.
 */
@FunctionalInterface
public interface TargetVelocityProv {
    Vector3d targetVelocity(PV pv);
}
