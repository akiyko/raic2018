import ai.Simulator;
import ai.TestUtils;
import ai.model.MyRobot;
import ai.model.Position;
import model.Arena;
import model.Game;
import model.Robot;
import model.Rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * By no one on 23.12.2018.
 */
public class FaceOff {
    public static final int GAME_TICKS = 18_000;

    private Strategy myStrategy;
    private Strategy opponentStrategy;

    private Arena arena = TestUtils.standardArena();

    public FaceOff(Strategy myStrategy, Strategy opponentStrategy) {
        this.myStrategy = myStrategy;
        this.opponentStrategy = opponentStrategy;
    }

    public void simulate() {
        List<MyRobot> robots = new ArrayList<>();
        robots.addAll(myRobots());
        robots.addAll(oppRobots());


        for (int i = 0; i < GAME_TICKS; i++) {


//            myStrategy.act();
//
//            Simulator.tick();


        }

    }

    public static List<MyRobot> myRobots() {
        MyRobot r1 = TestUtils.robotInTheAir(new Position(-10, 10, -10));
        MyRobot r2 = TestUtils.robotInTheAir(new Position(10, 10, -10));
        return Arrays.asList(r1,r2);
    }

    public static List<MyRobot> oppRobots() {
        MyRobot r1 = TestUtils.robotInTheAir(new Position(-10, 10, -10));
        MyRobot r2 = TestUtils.robotInTheAir(new Position(10, 10, -10));
        return Arrays.asList(r1,r2);
    }

    public static Rules rules(Arena arena) {
        Rules rules = new Rules();

        rules.arena = arena;

        //...
        rules.max_tick_count = GAME_TICKS;
//        rules.seed



        return rules;

    }

    public static Game game(Robot[] robots) {
        Game game = new Game();


        return game;
    }




}
