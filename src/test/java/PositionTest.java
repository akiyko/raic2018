import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author akiyko
 * @since 1/11/2019.
 */
public class PositionTest {
    @Test
    public void testRotate() throws Exception {
        Position p1 = new Position(0, 0, 10);
        assertTrue(new Position(-10, 0, 0).doubleEquals(p1.rotateAroundZero(Math.PI * 0.5)));
        assertTrue(new Position(0, 0, -10).doubleEquals(p1.rotateAroundZero(Math.PI)));


    }

    @Test
    public void angleTest() {
        Vector3d v1 = Vector3d.of(10, 0, 0);
        Vector3d v2 = Vector3d.of(0, 0, 10);
        Vector3d v3 = Vector3d.of(10, 0, 10).normalize();
        Vector3d v4 = Vector3d.of(10, 0, 0).normalize();


        assertTrue(MathUtils.isZero(Math.PI * 0.5 - Vector3d.angle2dBetween(v1, v2)));
        assertTrue(MathUtils.isZero(Math.PI * 1.5 - Vector3d.angle2dBetween(v2, v1)));
        assertTrue(MathUtils.isZero(Math.PI * 0.25 - Vector3d.angle2dBetween(v4, v3)));

    }

    @Test
    public void testRotateRandom() throws Exception {
        Random r = new Random(0);
        for (int i = 0; i < 10000; i++) {
            double dx1 = r.nextDouble();
            double dx2 = r.nextDouble();
            double dz1 = r.nextDouble();
            double dz2 = r.nextDouble();

            Vector3d v1 = Vector3d.of(dx1, 0, dz1).normalize();
            Vector3d v2 = Vector3d.of(dx2, 0, dz2).normalize();

            double thetha = Vector3d.angle2dBetween(v1, v2);

            Vector3d v2r = v1.rotate(thetha);

            System.out.println(thetha);
            System.out.println(v1);
            System.out.println(v2);
            System.out.println(v2r);

            assertTrue(v2r.doubleEquals(v2));


        }

    }
}