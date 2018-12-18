package ai.model;

import org.junit.Test;

import static ai.MathUtils.isZero;
import static ai.model.Vector3d.diffSquare;
import static ai.model.Vector3d.dot;
import static ai.model.Vector3d.of;
import static org.junit.Assert.*;

/**
 * By no one on 18.12.2018.
 */
public class Vector3dTest {

    @Test
    public void normalize() throws Exception {
        assertVectorEquals(of(0,0,0).normalize(), of(0,0,0));
        assertVectorEquals(of(10,0,0).normalize(), of(1,0,0));
        assertVectorEquals(of(-10,0,0).normalize(), of(-1,0,0));
        assertVectorEquals(of(0,10,0).normalize(), of(0,1,0));
        assertVectorEquals(of(0,-10,0).normalize(), of(0,-1,0));
        assertVectorEquals(of(0,0,10).normalize(), of(0,0,1));
        assertVectorEquals(of(0,0,-10).normalize(), of(0,0,-1));

        double t = 1/Math.sqrt(3);
        assertVectorEquals(of(10,10,10).normalize(), of(t,t,t));
    }

    @Test
    public void testDot() throws Exception {
        assertTrue(isZero(dot(of(1,0,0), of(0,1,0))));
        assertTrue(isZero(dot(of(-1,0,0), of(0,1,1))));
        assertTrue(isZero(dot(of(0,0,0), of(1,1,1))));
        assertTrue(isZero(dot(of(0,0,1), of(-1,1,0))));


        assertTrue(isZero(dot(of(1,0,0), of(1,0,0)) - 1.0));
        assertTrue(isZero(dot(of(0,2,0), of(1,2,0)) - 4.0));
        assertTrue(isZero(dot(of(0,0,3), of(1,2,3)) - 9.0));
    }

    @Test
    public void testMultiply() throws Exception {
        assertVectorEquals(of(1,2,3).multiply(2), of(2,4,6));
    }

    @Test
    public void testPlusMinus() throws Exception {
        assertPositionEquals(new Position(10,20,30).minus(of(1,2,3)), new Position(9,18,27));
        assertPositionEquals(new Position(10,20,30).plus(of(1,2,3)), new Position(11,22,33));
    }

    private static void assertVectorEquals(Vector3d a, Vector3d b) {
        assertTrue(isZero(diffSquare(a, b)));
    }

    private static void assertPositionEquals(Position a, Position b) {
        assertTrue(isZero(a.x - b.x));
        assertTrue(isZero(a.y - b.y));
        assertTrue(isZero(a.z - b.z));
    }
}