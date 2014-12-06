package polar.game.styles;

import java.util.Random;

import polar.game.Game;
import polar.game.GameMap;
import polar.game.Move;
import polar.game.MoveReport;
import polar.game.PolarCoordinate;
import polar.game.UnTestedCoordinates;
import polar.game.exceptions.BadCoordinateException;
import polar.game.exceptions.MoveDuplicateException;
import logic.Heuristic;

/*
 * Greedy-Search play style makes move
 * choices based on the heuristic function and 
 * only searches the immediately available moves,
 * making the choice that improves its heuristic evaluation
 * the most for that turn.
 */
public class GreedyPlayStyle extends PlayStyle {
	
	private Game game;
	private boolean player;
	
	public GreedyPlayStyle(boolean player, Game game) {
		this.player = player;
		this.game = game;
	}

	@Override
	public MoveReport getMove() {

		GameMap map = game.getMap();

		int score;
		int maxScore = Integer.MIN_VALUE;
		GameMap tempMap = null;
		PolarCoordinate maxCoords = null;

		for (int layer = 1; layer <= 4; layer++) { //check all layers
			for (int radian = 0; radian <= 11; radian++) { //check all spokes

				PolarCoordinate location;
				try {
					location = new PolarCoordinate(new UnTestedCoordinates(layer, radian));
					boolean valid = false;
					if (map.isSet(location) == null) { //this location may be available for play
						PolarCoordinate adjacent = null;
						for (int i = 0; i <= 7; i++) {
							adjacent = location.getAdjacent(i);
							if (adjacent != null && map.isSet(adjacent) != null) { //this location available for play
								valid = true;
								break;
							}
						}

						if (valid) { // this move is valid, give it a score
							try {
								tempMap = (GameMap) map.deepCopy(); //resetting tempMap
								tempMap.removeViewers(); //make sure this map doesn't update the gui
								tempMap.updateAll(new MoveReport(new Move(player, location)));
								score = Heuristic.evaluateMinMax(tempMap, player);
								if (score > maxScore) { //This is the best location seen so far
									maxScore = score;
									maxCoords = location;
								}
							} catch (MoveDuplicateException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (BadCoordinateException e) {
					e.printStackTrace();
				}
			}
			
		}

		if (maxCoords != null) {
			return new MoveReport(maxCoords.getX(), maxCoords.getY());
		} else { //First move, play randomly
			Random rand = new Random();
			int x = rand.nextInt(4) + 1;
			int y = rand.nextInt(12);
			return new MoveReport(x,y);
		}
	}

}
