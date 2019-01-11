import model.Rules;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author akiyko
 * @since 1/11/2019.
 */
public class RobotNitroJumpsTest {
    Rules rules = TestUtils.standardRules();

    @Test
    public void testTang() throws Exception {
        List<MyRobot> robotTrace = RobotNitroJumps.robotTraceFromMaxSpeedZeroPosToZTangMaxJumpFirstTick(rules, 100);

        int i = 0;
        for (MyRobot myRobot : robotTrace) {
            i++;

            System.out.println(i + ": " + myRobot);
        }
//no nitro   61: MyRobot{position={x=0.0, y=1.062475963049172, z=29.99999728170954}, velocity={dx=0.0, dy=14.958655659006878, dz=29.996738053452543}, action=Action{jump_speed=15.0, target_velocity={dx=0.0, dy=-28.97671854748606, dz=-95.70971623727542}}, touch_normal={dx=0.0, dy=1.0, dz=0.0}}
//           31: MyRobot{position={x=0.0, y=4.791669027777782, z=15.000000000000945}, velocity={dx=0.0, dy=-0.011666666667610216, dz=30.0}, action=Action{jump_speed=15.0, target_velocity={dx=0.0, dy=-28.97671854748606, dz=-95.70971623727542}}, touch_normal={dx=0.0, dy=1.0, dz=0.0}}

//with nitro 39: MyRobot{position={x=0.0, y=6.20720266379205, z=24.416322255055643}, velocity={dx=0.0, dy=0.10659143075882106, dz=47.87616977849462}, action=Action{jump_speed=15.0, target_velocity={dx=0.0, dy=10.964306168292177, dz=-99.39710252440936}}, touch_normal={dx=0.0, dy=1.0, dz=0.0}}


    }
}