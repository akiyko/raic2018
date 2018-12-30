package ai.plan;

import ai.MathUtils;
import ai.model.Vector3d;

/**
 * @author akiyko
 * @since 12/27/2018.
 */
public class GamePlanResult {
   public int goalScoredTick = -1;
   public int oppGoalScored = -1;

//   public List<Position> robotPositions = new ArrayList<>();
//   public List<Position> ballPositions = new ArrayList<>();

   public Vector3d minToBall = MathUtils.MAX_VECTOR;

   public Vector3d minBallToOppGateCenter = MathUtils.MAX_VECTOR;

   @Override
   public String toString() {
      return "GamePlanResult{" +
              "goalScoredTick=" + goalScoredTick +
              ", oppGoalScored=" + oppGoalScored +
              ", minToBall=" + minToBall.length() +
              ", minToOppGateCenter=" + minBallToOppGateCenter.length() +
              '}';
   }
}
