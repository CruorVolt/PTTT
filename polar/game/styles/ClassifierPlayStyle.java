package polar.game.styles;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import polar.game.Game;
import polar.game.GameMap;
import polar.game.Move;
import polar.game.MoveReport;
import polar.game.PolarCoordinate;
import polar.game.UnTestedCoordinates;
import polar.game.exceptions.BadCoordinateException;
import polar.game.exceptions.MoveDuplicateException;
import logic.Search;
import logic.SearchNode;
import logic.Status;
import logic.DecisionTree;

public class ClassifierPlayStyle extends PlayStyle {
	
	private Game game;
	private boolean player;
	private DecisionTree tree;
	private Character token;
	private GreedyPlayStyle backup;
	
	public ClassifierPlayStyle(boolean player, Game game) {
		this.player = player;
		this.game = game;

		this.tree = new DecisionTree();
		try {
			tree.setupTree(DecisionTree.TRAINING_FILE);
		} catch (FileNotFoundException e) {
			System.out.println("Could not location training file");
			e.printStackTrace();
		}

		this.token = (player) ? 'X' : 'O';
		this.backup = new GreedyPlayStyle(player, game);
	}

	@Override
	public MoveReport getMove() {
		MoveReport report;
		startTimer();
		
		ArrayList<UnTestedCoordinates> availableMoves = Status.getValidPositions(game.getMap().getMoves());
		GameMap tempMap;
		
		//Return the first move found that classifies as a winning state
		for (UnTestedCoordinates coords : availableMoves) {
			try {
				tempMap = game.getMap().deepCopy(); //resetting tempMap
				tempMap.removeViewers(); //make sure this map doesn't update the gui
				report = new MoveReport(new Move(player, new PolarCoordinate(coords)));
				tempMap.updateAll(report);
				if (tree.classify(this.token, tempMap)) {
					stopTimer();
					report.reportTime(getElapsedTime());
					report.reportNodes(SearchNode.countNodes());
					report.reportValue(1);
					SearchNode.reset();
					endTurn();
					return report;
				}
			} catch (MoveDuplicateException m) {
				m.printStackTrace();
			} catch (BadCoordinateException b) {
				b.printStackTrace();
			}
		}
		report.reportValue(0);
		//No state has classified as a win: play with greedy heuristic evaluation instead
		return backup.getMove();
	}

}
