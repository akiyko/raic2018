import model.Arena;

import java.util.List;

/**
 * By no one on 12.01.2019.
 */
public class PV {
    public final Position p;
    public final Vector3d v;

    private PV(Position p, Vector3d v) {
        this.p = p;
        this.v = v;
    }

    public boolean isOnPlate(Arena arena) {
//        if(StrategyParams.fearCorners) {
//            if (Math.abs(p.x) > arena.width * 0.5 - arena.bottom_radius) {
//                return Math.abs(p.x) < arena.width * 0.5 - arena.bottom_radius
//                        && Math.abs(p.z) < arena.depth * 0.5 - arena.bottom_radius;
//            }
//        }
        return true;

    }

    public boolean isInArena(Arena arena) {
//        if(StrategyParams.fearCorners) {
//                return Math.abs(p.x) < arena.width * 0.5
//                        && Math.abs(p.z) < arena.depth * 0.5;
//        }
        return true;

    }

    public static PV middlePv(PV pv1, PV pv2, double d) {
        return of(Position.middlePos(pv1.p, pv2.p, d), Vector3d.middleVector(pv1.v, pv2.v, d));
    }

    public static PV of(Position p, Vector3d v) {
        return new PV(p,v);
    }

    public static PV rotate(double thetha, Position jumpPosition, Position prePosition, Vector3d preVelocity) {
        return of(jumpPosition.zeroY().plus(prePosition.toVector().rotate(thetha)),
                preVelocity.rotate(thetha));
    }

    public static PV rotate(SinCos sc, Position jumpPosition, Position prePosition, Vector3d preVelocity) {
        return of(jumpPosition.zeroY().plus(prePosition.toVector().rotate(sc)),
                preVelocity.rotate(sc));
    }

    //
    public static PV pvPrecalcRotate(PV precalculated,
                                PV beforeJumpPV) {

        Vector3d precalcVelocity = Vector3d.of(0, 0, Constants.ROBOT_MAX_GROUND_SPEED);
        SinCos sc = Vector3d.sincos2dBetween(precalcVelocity, beforeJumpPV.v);

        return PV.rotate(sc, beforeJumpPV.p, precalculated.p, precalculated.v);
    }



    @Override
    public String toString() {
        return "PV{" +
                "p=" + p +
                ", v=" + v +
                '}';
    }
}
