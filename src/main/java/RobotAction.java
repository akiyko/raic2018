/**
 * @author akiyko
 * @since 1/16/2019.
 */
public interface RobotAction {
    MyAction act(int currentTick, PV thisRobotCurrentPV);
    int validFromTick();
    int validToTick();
}
