package polar.game.styles;
import java.util.ArrayList;
import java.util.Random;

import logic.Status;
import logic.TD.TD;
import logic.TD.playTD;
import polar.game.Game;
import polar.game.GameMap;
import polar.game.Move;
import polar.game.MoveReport;
import polar.game.PolarCoordinate;
import polar.game.UnTestedCoordinates;
import polar.game.exceptions.BadCoordinateException;
// a min player which allows TD to play against itself without instantiating two TD networks.
public class TDMinPlayStyle extends DifferencePlayStyle {
	
	public TDMinPlayStyle(Game game, TD td, boolean player) {
		super(game, td, player);
	}

	@Override
	public MoveReport getMove() {
		MoveReport report;
		startTimer();
		ArrayList<UnTestedCoordinates> candidates = Status.getValidPositions(map.getMoves());
		UnTestedCoordinates bestMove = null;
		double bestVal = 100;
		for(UnTestedCoordinates choice : candidates) {
			double eval = td.valueOf(player, choice);
			// when playing against max-player, select min-value of TD-net configured for max-player.
			if(eval<bestVal) {
				bestVal = eval;
				bestMove = choice;
			}
		}
		try {
			if(bestMove!=null) {
				report =  new MoveReport(new Move(player, new PolarCoordinate(bestMove)));
				report.reportValue(bestVal);
				report.reportNodes(candidates.size());
			}
			else {
				// select a semi-random position if 1st move.
				Random rand = new Random();
				int x = rand.nextInt(1) + 2;
				int y = rand.nextInt(12);
			
			
				report = new MoveReport(new Move(player, new PolarCoordinate(new UnTestedCoordinates(x,y))));
				report.reportValue(0);
				report.reportNodes(0);
			}
		} catch (BadCoordinateException e) {
			e.printStackTrace();
			report = null;
		}
		return report;
	}
}
