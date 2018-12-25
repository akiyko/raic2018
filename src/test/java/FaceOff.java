import ai.Constants;
import ai.GoalScoredException;
import ai.Simulator;
import ai.TestUtils;
import ai.model.MyBall;
import ai.model.MyRobot;
import ai.model.Position;
import model.Arena;
import model.Game;
import model.Robot;
import model.Rules;

import java.util.*;

/**
 * By no one on 23.12.2018.
 */
public class FaceOff {
    public static final int GAME_TICKS = 18_000;

    private MyMyStrategy myStrategy;
    private MyMyStrategy opponentStrategy;

    private Arena arena = TestUtils.standardArena();

    public FaceOff(MyMyStrategy myStrategy, MyMyStrategy opponentStrategy) {
        this.myStrategy = myStrategy;
        this.opponentStrategy = opponentStrategy;
    }

    public void simulate() {
        int myGoals = 0;
        int oppGoals = 0;

        Rules rules = new Rules();
        rules.arena = arena;


        List<MyRobot> robots = new ArrayList<>();
        List<MyRobot> myrobots = myRobots();
        List<MyRobot> opprobots = oppRobots();
        robots.addAll(myrobots);
        robots.addAll(opprobots);


        MyBall myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, 0));

        boolean gamestart = true;

        for (int i = 0; i < GAME_TICKS; i++) {
            if(gamestart) {
                robots = new ArrayList<>();
                myrobots = myRobots();
                opprobots = oppRobots();

                robots.clear();
                robots.addAll(myrobots);
                robots.addAll(opprobots);

                myBall = TestUtils.ballInTheAir(new Position(0, Constants.BALL_RADIUS * 2, 0));

                gamestart = false;
            }

            Map<Integer, MyRobot> myRobotMap = toMapClone(myrobots);
            Map<Integer, MyRobot> oppRobMap = toMapClone(opprobots);

            Map<Integer, MyRobot> myRobotMapNegateZ = toMapCloneNegateZ(myrobots);
            Map<Integer, MyRobot> oppRobMapNegateZ = toMapCloneNegateZ(opprobots);

            myStrategy.act(myRobotMap, oppRobMap, myBall, arena);
            opponentStrategy.act(oppRobMapNegateZ, myRobotMapNegateZ, myBall.cloneNegateZ(), arena);

            for (Map.Entry<Integer, MyRobot> entry : myRobotMap.entrySet()) {
                myrobots.get(entry.getKey()).action = entry.getValue().action;
            }
            for (Map.Entry<Integer, MyRobot> entry : oppRobMapNegateZ.entrySet()) {
                opprobots.get(entry.getKey()).action = entry.getValue().action;
                opprobots.get(entry.getKey()).action.target_velocity_z = -opprobots.get(entry.getKey()).action.target_velocity_z;
                opprobots.get(entry.getKey()).action.target_velocity = opprobots.get(entry.getKey()).action.target_velocity.negateZ();
            }

            try {
                Simulator.tick(rules, robots, myBall);
            } catch(GoalScoredException e) {
                if( e.getZ() > 0) {
//                    System.out.println("Goal scored for me at tick " + i);
                    myGoals ++;
                } else {
//                    System.out.println("Goal scored for opponent at tick " + i);
                    oppGoals ++;
                }

                gamestart = true;
            }

//            myStrategy.act();
//
//            Simulator.tick();
        }

        System.out.println("Final score: " + myGoals + "/" + oppGoals);

    }

    Map<Integer, MyRobot> toMapClone(List<MyRobot> robots) {
        Map<Integer, MyRobot> result = new HashMap<>();
        for (int i = 0; i < robots.size(); i++) {
            result.put(i, robots.get(i).clone());
        }

        return result;
    }

    Map<Integer, MyRobot> toMapCloneNegateZ(List<MyRobot> robots) {
        Map<Integer, MyRobot> result = new HashMap<>();
        for (int i = 0; i < robots.size(); i++) {
            result.put(i, robots.get(i).cloneNegateZ());
        }

        return result;
    }

    public static List<MyRobot> myRobots() {
        MyRobot r1 = TestUtils.robotInTheAir(new Position(-10, 10, -10));
        MyRobot r2 = TestUtils.robotInTheAir(new Position(10, 10, -10));
        return Arrays.asList(r1,r2);
    }

    public static List<MyRobot> oppRobots() {
        MyRobot r1 = TestUtils.robotInTheAir(new Position(-10, 10, 10));
        MyRobot r2 = TestUtils.robotInTheAir(new Position(10, 10, 10));
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
