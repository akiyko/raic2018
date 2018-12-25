import org.junit.Test;

/**
 * @author akiyko
 * @since 12/25/2018.
 */
public class FaceOffTest {
    @Test
    public void testDonething() throws Exception {
        FaceOff faceOff = new FaceOff(new DoNothingMyStrategy(), new DoNothingMyStrategy());

        faceOff.simulate();

    }

    @Test
    public void testJustKickDon() throws Exception {
        FaceOff faceOff = new FaceOff(new JustKickStrategy(), new DoNothingMyStrategy());

        faceOff.simulate();

    }
}
