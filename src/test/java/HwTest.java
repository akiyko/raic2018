import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * By no one on 16.12.2018.
 */
public class HwTest {
    @Test
    public void name() throws Exception {
        assertEquals(30, (int) Stream.of(1,30).max(Integer::compareTo).orElse(0));
    }
}
