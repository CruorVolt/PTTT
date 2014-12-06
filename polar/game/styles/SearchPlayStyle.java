package polar.game.styles;

import java.util.HashMap;

import polar.game.Game;
import polar.game.MoveReport;
import polar.game.PolarCoordinate;
import polar.game.UnTestedCoordinates;
import polar.game.exceptions.BadCoordinateException;
import logic.Search;
import logic.SearchNode;

public class SearchPlayStyle extends PlayStyle {
	
	private Game game;
	private boolean player, pruning;
	
	public SearchPlayStyle(boolean player, Game game, boolean pruning) {
		this.player = player;
		this.game = game;
		this.pruning = pruning; //Should this player us alpha-beta pruning in their search?
	}

	@Override
	public MoveReport getMove() {
		MoveReport report;
		startTimer();
		HashMap<Integer, PolarCoordinate> minimax;
		int maxDepth = (this.pruning) ? 4 : 3;
		try {
			minimax = Search.minimax(new SearchNode(this.game.getMap(), this.player), maxDepth, this.player, this.pruning, Integer.MIN_VALUE, Integer.MAX_VALUE);
			PolarCoordinate location = (PolarCoordinate) minimax.values().toArray()[0];
			stopTimer();
			report = new MoveReport(location.getX(), location.getY());
			report.reportTime(getElapsedTime());
			report.reportDepth(maxDepth);
			return report;
		} catch (BadCoordinateException e) {
			e.printStackTrace();
		}
		return null;
	}

}
