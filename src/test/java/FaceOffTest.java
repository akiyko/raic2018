import org.junit.Test;

/**
 * @author akiyko
 * @since 12/25/2018.
 */
public class FaceOffTest {

    @Test
    public void testThe() throws Exception {
        FaceOff faceOff = new FaceOff(new TheStrategy(), new DoNothingMyStrategy());

        faceOff.simulate();

    }
    @Test
    public void testLahKick() throws Exception {
        FaceOff faceOff = new FaceOff(new SingleKickGoalLahStrategy(false), new DoNothingMyStrategy());

        faceOff.simulate();

    }

    @Test
    public void testLahKickJustKick() throws Exception {
        FaceOff faceOff = new FaceOff( new JustKickStrategy(), new SingleKickGoalLahStrategy());

        faceOff.simulate();

    }
    @Test
    public void testTheLahKickLahKick() throws Exception {
        FaceOff faceOff = new FaceOff(new TheStrategy(), new SingleKickGoalLahStrategy(false));

        faceOff.simulate();

    }

    @Test
    public void testLahKickLahKick() throws Exception {
        FaceOff faceOff = new FaceOff( new SingleKickGoalLahStrategy(false), new SingleKickGoalLahStrategy(false));

        faceOff.simulate();

    }

    @Test
    public void testDonething() throws Exception {
        FaceOff faceOff = new FaceOff(new DoNothingMyStrategy(), new DoNothingMyStrategy());

        faceOff.simulate();

    }

    @Test
    public void testSingleKickJDon() throws Exception {
        FaceOff faceOff = new FaceOff(new SingleKickStrategy(), new DoNothingMyStrategy());
//        FaceOff faceOff = new FaceOff(new DoNothingMyStrategy(), new JustKickStrategy());

        faceOff.simulate();

    }

    @Test
    public void testJustKickJDon() throws Exception {
        FaceOff faceOff = new FaceOff(new JustKickStrategy(true), new DoNothingMyStrategy());
//        FaceOff faceOff = new FaceOff(new DoNothingMyStrategy(), new JustKickStrategy());

        faceOff.simulate();

    }

    @Test
    public void testJustKickDon() throws Exception {
        FaceOff faceOff = new FaceOff(new JustKickStrategy(false), new DoNothingMyStrategy());
//        FaceOff faceOff = new FaceOff(new DoNothingMyStrategy(), new JustKickStrategy());

        faceOff.simulate();

    }

    @Test
    public void testJustKickPvP() throws Exception {
        for (int i = 0; i < 100; i++) {
            FaceOff faceOff = new FaceOff(new JustKickStrategy(false), new JustKickStrategy(true));
//        FaceOff faceOff = new FaceOff(new DoNothingMyStrategy(), new JustKickStrategy());

            faceOff.simulate();

        }
    }
}
