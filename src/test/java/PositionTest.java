import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author akiyko
 * @since 1/11/2019.
 */
public class PositionTest {
    @Test
    public void testRotate() throws Exception {
        Position p1 = new Position(0,0,10);
        assertTrue(new Position(-10, 0, 0).doubleEquals(p1.rotateAroundZero(Math.PI * 0.5)));
        assertTrue(new Position(0, 0, -10).doubleEquals(p1.rotateAroundZero(Math.PI)));


    }
}