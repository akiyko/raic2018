/**
 * @author akiyko
 * @since 12/27/2018.
 */
public class GamePlanResult {
   public int goalScoredTick = -1;
   public int potentialGoalScoredTick = -1;//if goal is only by width, not height
   public int oppGoalScored = -1;
   public int beforeBallTouchTick = -1;

   public Position ballFinalPosition;

//   public List<Position> robotPositions = new ArrayList<>();
//   public List<Position> ballPositions = new ArrayList<>();

   public Vector3d minToBall = MathUtils.MAX_VECTOR;
   public int minToBallTick = Integer.MAX_VALUE;

   public Vector3d minToBallGround = MathUtils.MAX_VECTOR;
   public int minToBallGroundTick = Integer.MAX_VALUE;
   public PV ballAfterColllision = null;


   public Vector3d minBallToOppGateCenter = MathUtils.MAX_VECTOR;

   @Override
   public String toString() {
      return "GamePlanResult{" +
              "goalScoredTick=" + goalScoredTick +
              ", pgoalScoredTick=" + potentialGoalScoredTick +
              ", oppGoalScored=" + oppGoalScored +
              ", minToBall=" + minToBall +
              ", minToBallTick=" + minToBallTick +
              ", minBallToOppGateCenter=" + minBallToOppGateCenter +
              ", ballFinalPosition=" + ballFinalPosition +
              ", beforeTouchTick=" + beforeBallTouchTick +
              ", minToBallGroundTick=" + minToBallGroundTick +
              '}';
   }
}
