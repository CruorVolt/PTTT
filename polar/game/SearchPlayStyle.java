package polar.game;

import java.util.HashMap;

import logic.Search;
import logic.SearchNode;

public class SearchPlayStyle implements PlayStyle {
	
	private Game game;
	private boolean player, pruning;
	
	public SearchPlayStyle(boolean player, Game game, boolean pruning) {
		this.player = player;
		this.game = game;
		this.pruning = pruning; //Should this player us alpha-beta pruning in their search?
	}

	@Override
	public UnTestedCoordinates getMove() {
		HashMap<Integer, PolarCoordinate> minimax;
		int maxDepth = (this.pruning) ? 4 : 3;
		try {
			minimax = Search.minimax(new SearchNode(this.game.getMap(), this.player), maxDepth, this.player, this.pruning, Integer.MIN_VALUE, Integer.MAX_VALUE);
			PolarCoordinate location = (PolarCoordinate) minimax.values().toArray()[0];
			return new UnTestedCoordinates(location.getX(), location.getY());
		} catch (BadCoordinateException e) {
			e.printStackTrace();
		}
		return null;
	}

}
