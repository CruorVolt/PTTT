package polar.game.styles;

import polar.game.Game;
import polar.game.MoveReport;
import polar.game.PolarCoordinate;
import polar.game.exceptions.BadCoordinateException;
import logic.Search;
import logic.SearchNode;

public class SearchPlayStyle extends PlayStyle {
	
	private Game game;
	private boolean player, pruning;
	private int maxDepth;
	
	public SearchPlayStyle(boolean player, Game game, boolean pruning, int maxDepth) {
		this.player = player;
		this.game = game;
		this.pruning = pruning; //Should this player us alpha-beta pruning in their search?
		this.maxDepth = maxDepth;
	}

	@Override
	public MoveReport getMove() {
		MoveReport report;
		startTimer();
		SearchNode minimax = null;
		try {
			minimax = Search.minimax(new SearchNode(this.game.getMap(), this.player, null), maxDepth, this.player, this.pruning, Integer.MIN_VALUE, Integer.MAX_VALUE);
			//minimax = Search.minimaxNew(new SearchNode(this.game.getMap(), this.player, null), 2, this.player);
			PolarCoordinate location = (PolarCoordinate) minimax.getMove();
			stopTimer();
			report = new MoveReport(location.getX(), location.getY());
			report.reportTime(getElapsedTime());
			report.reportDepth(maxDepth);
			report.reportNodes(SearchNode.countNodes());
			SearchNode.reset();
			endTurn();
			return report;
		} catch (BadCoordinateException e) {
			//e.printStackTrace();
		}
		return null;
	}

}
