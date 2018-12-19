package ai;

import org.junit.Test;

import static ai.MathUtils.random;
import static org.junit.Assert.*;

/**
 * By no one on 18.12.2018.
 */
public class MathUtilsTest {
    @Test
    public void randomTest() throws Exception {
        double min = -100;
        double max = 100;
        for (int i = 0; i < 100; i++) {
             assertIn(random(min, max), min, max);
        }
    }

    void assertIn(double a, double min, double max) {
        assertTrue(a >= min);
        assertTrue(a <= max);
    }

}