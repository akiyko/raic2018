package ai;

import ai.model.Dan;
import ai.model.Entity;
import ai.model.Position;
import ai.model.Vector3d;
import org.junit.Test;

import static ai.MathUtils.isZero;
import static ai.model.Vector3d.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * By no one on 19.12.2018.
 */
public class SimulatorTest {
    @Test
    public void move() throws Exception {
        Entity e = new Entity();
        e.velocity = of(100, 100, 100);
        e.position = new Position(0,0,0);

        Simulator.move(e, 0.1);

        assertZero(e.position.x - 5.773502691896258);
        assertZero(e.position.y - 5.623502691896258);
        assertZero(e.position.z - 5.773502691896258);

        assertZero(e.velocity.dx - 57.735026918962575);
        assertZero(e.velocity.dy - 54.735026918962575);
        assertZero(e.velocity.dz - 57.735026918962575);
    }

    @Test
    public void danToPlaneTest() throws Exception {
        Position p = new Position(0,10, 0);
        Vector3d normal = of(0,1,0);
        Position onPlane = new Position(100, -10, 200);

        assertZero(Simulator.dan_to_plane(p, onPlane, normal).distance - 20);
    }

    @Test
    public void danToSphereInnerTest() throws Exception {
        Position p = new Position(0,1, 0);
        Position c = new Position(0,10, 0);

        Dan danToSphere = Simulator.dan_to_sphere_inner(p, c, 100);
        assertZero(danToSphere.distance - 91);
    }

    @Test
    public void danToSphereOuterTest() throws Exception {
        Position p = new Position(0,-100, 0);
        Position c = new Position(0,10, 0);

        Dan danToSphere = Simulator.dan_to_sphere_inner(p, c, 100);
        assertZero(danToSphere.distance - 10);
    }

    public static void assertZero(double d) {
        assertTrue(isZero(d));
    }
}