import model.Arena;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * By no one on 19.12.2018.
 */
public class SimulatorTest {

    static Arena arena = TestUtils.standardArena();

    @Test
    public void testDanToArena() throws Exception {
        Dan dan = Simulator.dan_to_arena(new Position(0, 9, 0), arena);
        assertZero(dan.distance - 9);
        assertSame(Vector3d.of(0, 1, 0), dan.normal);
    }
    @Test
    public void testDanToArena2() throws Exception {
        Dan dan = Simulator.dan_to_arena(new Position(0, 11, 0), arena);
        assertZero(dan.distance - 9);
        assertSame(Vector3d.of(0, -1, 0), dan.normal);
    }
    @Test
    public void testDanToArena3() throws Exception {
        Dan dan = Simulator.dan_to_arena(new Position(0, 10, 0), arena);
        assertZero(dan.distance - 10);
        assertSame(Vector3d.of(0, -1, 0), dan.normal);
    }

    @Test
    public void testDanToArena4() throws Exception {
        Dan dan = Simulator.dan_to_arena(new Position(25, 10, 10), arena);
        assertZero(dan.distance - 5);
        assertSame(Vector3d.of(-1, 0, 0), dan.normal);
    }

    @Test
    public void testDanToArena4m() throws Exception {
        Dan dan = Simulator.dan_to_arena(new Position(-25, 10, 10), arena);
        assertZero(dan.distance - 5);
        assertSame(Vector3d.of(1, 0, 0), dan.normal);
    }

    @Test
    public void testDanToArena5() throws Exception {
        Dan dan = Simulator.dan_to_arena(new Position(0, 5, 46), arena);
        assertZero(dan.distance - 4);
        assertSame(Vector3d.of(0, 0, -1), dan.normal);
    }

    @Test
    public void testDanToArena6() throws Exception {
        Dan dan = Simulator.dan_to_arena(new Position(0, 3, 46), arena);
        assertZero(dan.distance - 3);
        assertSame(Vector3d.of(0, 1, 0), dan.normal);
    }

    @Test
    public void testDanToArena7() throws Exception {
        Dan dan = Simulator.dan_to_arena(new Position(24, 10, 34), arena);
        assertZero(dan.distance - 3.1005050633883346);
        assertSame(Vector3d.of(-0.7071067811865475, 0.0, -0.7071067811865475), dan.normal);
    }

    @Test
    public void testDanToArena7b() throws Exception {
        Dan dan = Simulator.dan_to_arena(new Position(26, 2.5, 36), arena);
        assertZero(dan.distance - 0.22663403553680128);
        assertSame(Vector3d.of(-0.695520249707788, 0.18028634028353988, -0.695520249707788), dan.normal);
    }

    @Test
    public void testDanToArena7t() throws Exception {
        Dan dan = Simulator.dan_to_arena(new Position(25, 17, 35), arena);
        assertZero(dan.distance - 0.349022777652678);
        assertSame(Vector3d.of(-0.5649334206492194, -0.6014153809698787, -0.5649334206492194), dan.normal);
    }



    @Test
    public void move() throws Exception {
        Entity e = new Entity();
        e.velocity = Vector3d.of(100, 100, 100);
        e.position = new Position(0, 0, 0);

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
        Position p = new Position(0, 10, 0);
        Vector3d normal = Vector3d.of(0, 1, 0);
        Position onPlane = new Position(100, -10, 200);

        assertZero(Simulator.dan_to_plane(p, onPlane, normal).distance - 20);
    }

    @Test
    public void danToSphereInnerTest() throws Exception {
        Position p = new Position(0, 1, 0);
        Position c = new Position(0, 10, 0);

        Dan danToSphere = Simulator.dan_to_sphere_inner(p, c, 100);
        assertZero(danToSphere.distance - 91);
    }

    @Test
    public void danToSphereOuterTest() throws Exception {
        Position p = new Position(0, -100, 0);
        Position c = new Position(0, 10, 0);

        Dan danToSphere = Simulator.dan_to_sphere_outer(p, c, 100);
        assertZero(danToSphere.distance - 10);
    }

    public static void assertZero(double d) {
        assertTrue(MathUtils.isZero(d));
    }

    public static void assertSame(Vector3d a, Vector3d b) {
        assertTrue(MathUtils.isZero(a.dx - b.dx));
        assertTrue(MathUtils.isZero(a.dy - b.dy));
        assertTrue(MathUtils.isZero(a.dz - b.dz));
    }
}