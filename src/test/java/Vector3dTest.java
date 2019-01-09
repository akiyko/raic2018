import org.junit.Test;

import static org.junit.Assert.*;

/**
 * By no one on 18.12.2018.
 */
public class Vector3dTest {

    @Test
    public void normalize() throws Exception {
        assertVectorEquals(Vector3d.of(0,0,0).normalize(),   Vector3d.of(0,0,0));
        assertVectorEquals(Vector3d.of(10,0,0).normalize(),  Vector3d.of(1,0,0));
        assertVectorEquals(Vector3d.of(-10,0,0).normalize(), Vector3d.of(-1,0,0));
        assertVectorEquals(Vector3d.of(0,10,0).normalize(),  Vector3d.of(0,1,0));
        assertVectorEquals(Vector3d.of(0,-10,0).normalize(), Vector3d.of(0,-1,0));
        assertVectorEquals(Vector3d.of(0,0,10).normalize(),  Vector3d.of(0,0,1));
        assertVectorEquals(Vector3d.of(0,0,-10).normalize(), Vector3d.of(0,0,-1));

        double t = 1/Math.sqrt(3);
        assertVectorEquals(Vector3d.of(10,10,10).normalize(), Vector3d.of(t,t,t));
    }

    @Test
    public void testDot() throws Exception {
        assertTrue(MathUtils.isZero(Vector3d.dot(Vector3d.of(1,0,0), Vector3d. of(0,1,0))));
        assertTrue(MathUtils.isZero(Vector3d.dot(Vector3d.of(-1,0,0),Vector3d. of(0,1,1))));
        assertTrue(MathUtils.isZero(Vector3d.dot(Vector3d.of(0,0,0), Vector3d.of(1,1,1))));
        assertTrue(MathUtils.isZero(Vector3d.dot(Vector3d.of(0,0,1), Vector3d.of(-1,1,0))));
        assertTrue(MathUtils.isZero(Vector3d.dot(Vector3d.of(1,0,0), Vector3d.of(1,0,0)) - 1.0));
        assertTrue(MathUtils.isZero(Vector3d.dot(Vector3d.of(0,2,0), Vector3d.of(1,2,0)) - 4.0));
        assertTrue(MathUtils.isZero(Vector3d.dot(Vector3d.of(0,0,3), Vector3d.of(1,2,3)) - 9.0));
    }

    @Test
    public void testMultiply() throws Exception {
        assertVectorEquals(Vector3d.of(1,2,3).multiply(2), Vector3d.of(2,4,6));
    }

    @Test
    public void testPlusMinus() throws Exception {
        assertPositionEquals(new Position(10,20,30).minus(Vector3d.of(1,2,3)), new Position(9,18,27));
        assertPositionEquals(new Position(10,20,30).plus(Vector3d.of(1,2,3)), new Position(11,22,33));
    }

    @Test
    public void testClamp() throws Exception {
        assertVectorEquals(Vector3d.of(9,0,0).clamp(3), Vector3d.of(3,0,0));
        assertVectorEquals(Vector3d.of(0,-9,0).clamp(3),Vector3d. of(0,-3,0));
        assertVectorEquals(Vector3d.of(0,0,9).clamp(3), Vector3d.of(0,0,3));
        assertVectorEquals(Vector3d.of(1,1,1).clamp(10),Vector3d. of(1,1,1));
    }

    private static void assertVectorEquals(Vector3d a, Vector3d b) {
        assertTrue(MathUtils.isZero(Vector3d.diffSquare(a, b)));
    }

    private static void assertPositionEquals(Position a, Position b) {
        assertTrue(MathUtils.isZero(a.x - b.x));
        assertTrue(MathUtils.isZero(a.y - b.y));
        assertTrue(MathUtils.isZero(a.z - b.z));
    }
}