package ai.plan;

import ai.model.Position;
import ai.model.Vector3d;

/**
 * @author akiyko
 * @since 12/27/2018.
 */
@FunctionalInterface
public interface TargetVelocityProvider {
    Vector3d targetVelocity(Vector3d originalVelocity, Position currentPosition, int tickNumber);
}
