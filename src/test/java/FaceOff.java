import model.*;

import java.util.*;

/**
 * By no one on 23.12.2018.
 */
public class FaceOff {
    //    public static final int GAME_TICKS = 18_000;
    public static final int GAME_TICKS = 5000;

    private MyMyStrategy myStrategy;
    private MyMyStrategy opponentStrategy;

    private Arena arena = TestUtils.standardArena();
    private Rules rules = TestUtils.standardRules();

    public boolean unlimitedNitro;

    public FaceOff(MyMyStrategy myStrategy, MyMyStrategy opponentStrategy) {
        this.myStrategy = myStrategy;
        this.opponentStrategy = opponentStrategy;

        myStrategy.setRules(rules);
        opponentStrategy.setRules(rules);
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
//            System.out.println("TICK " + i + " ============================================");
            if (gamestart) {
                robots = new ArrayList<>();
                myrobots = myRobots();
                opprobots = oppRobots();

                robots.clear();
                robots.addAll(myrobots);
                robots.addAll(opprobots);

                myBall = TestUtils.ballInTheAir(new Position(0, MathUtils.random(Constants.BALL_RADIUS * 1.1, Constants.BALL_RADIUS * 4), 0));

                gamestart = false;

            }

            Map<Integer, MyRobot> myRobotMap = toMapClone(myrobots);
            Map<Integer, MyRobot> oppRobMap = toMapClone(opprobots);

            Map<Integer, MyRobot> myRobotMapNegateZ = toMapCloneNegateZ(myrobots);
            Map<Integer, MyRobot> oppRobMapNegateZ = toMapCloneNegateZ(opprobots);





            myStrategy.act(myRobotMap, oppRobMap, myBall.clone(), arena, i);
            opponentStrategy.act(oppRobMapNegateZ, myRobotMapNegateZ, myBall.cloneNegateZ(), arena, i);


            if(myBall.position.y < -39) {
                System.out.println(myBall);
                for (MyRobot robot : robots) {
                    if(robot.id < 3) {
                        System.out.println(robot);
                    }
                }
            }



            for (Map.Entry<Integer, MyRobot> entry : myRobotMap.entrySet()) {
                myrobots.stream()
                        .filter(r -> r.id == entry.getKey())
                        .findAny()
                        .orElse(null)
                        .action = entry.getValue().action;
//                myrobots.get(entry.getKey()).action = entry.getValue().action;
            }
            for (Map.Entry<Integer, MyRobot> entry : oppRobMapNegateZ.entrySet()) {
                opprobots.stream()
                        .filter(r -> r.id == entry.getKey())
                        .findAny()
                        .ifPresent(r -> {
                            r.action = entry.getValue().action;
                            r.action.target_velocity = r.action.target_velocity.negateZ();
                        });

//                opprobots.get(entry.getKey()).action = entry.getValue().action;
//                opprobots.get(entry.getKey()).action.target_velocity_z = -opprobots.get(entry.getKey()).action.target_velocity_z;
//                opprobots.get(entry.getKey()).action.target_velocity = opprobots.get(entry.getKey()).action.target_velocity.negateZ();
            }

            try {
//                System.out.println(robots);
//                System.out.println(i + "=============");
                if(unlimitedNitro) {
                    for (MyRobot robot : robots) {//TODO: test nitro always available
                        robot.nitro = 100;
                    }
                }

                StrategyParams.random_on = true;

                Simulator.tick(rules, robots, myBall);
                StrategyParams.random_on = false;

//                System.out.println("mraction:" + myrobots.get(0).action);

                robots.forEach(r -> r.action = new MyAction());
//                System.out.println(i + "\tb: " + myBall.position + " / p0: " + myrobots.get(0).position/* + "/ p1" + myrobots.get(1).position*/);
//                System.out.println(i + "\tb: " + myBall.velocity + " :ground speed = " + myBall.velocity.zeroY().length());
            } catch (GoalScoredException e) {
//                System.out.println(myBall);
//                for (MyRobot robot : robots) {
//                    System.out.println(robot);
//                }

                if (e.getZ() > 0) {
//                    System.out.println("Goal scored for me at tick " + i);
                    myGoals++;
                } else {
//                    System.out.println("Goal scored for opponent at tick " + i);
                    oppGoals++;
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
            result.put(robots.get(i).id, robots.get(i).clone());
        }

        return result;
    }

    Map<Integer, MyRobot> toMapCloneNegateZ(List<MyRobot> robots) {
        Map<Integer, MyRobot> result = new HashMap<>();
        for (int i = 0; i < robots.size(); i++) {
            result.put(robots.get(i).id, robots.get(i).cloneNegateZ());
        }

        return result;
    }

    public static List<MyRobot> myRobots() {
        MyRobot r1 = TestUtils.robotOnTheGround(new Position(10, 1, -30));
        MyRobot r2 = TestUtils.robotOnTheGround(new Position(-10, 1, -30));
        r1.id = 1;
        r2.id = 2;
        return Arrays.asList(r1, r2);
    }

    public static List<MyRobot> oppRobots() {
        MyRobot r1 = TestUtils.robotOnTheGround(new Position(-10, 1, 30));//35? 0\3 ?
        MyRobot r2 = TestUtils.robotOnTheGround(new Position(10, 1, 30));
        r1.id = 3;
        r2.id = 4;

        return Arrays.asList(r1, r2);
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
