package ai.plan;

import ai.Constants;
import ai.model.Position;
import ai.model.Vector3d;

import java.util.ArrayList;
import java.util.List;

/**
 * @author akiyko
 * @since 12/27/2018.
 */
public class GamePlanResult {
   public int goalScoredTick = -1;
   public int oppGoalScored = -1;

   public List<Position> robotPositions = new ArrayList<>();
   public List<Position> ballPositions = new ArrayList<>();

   public Vector3d minToBall = Vector3d.of(10000, 10000, 10000);
}
