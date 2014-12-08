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

public class DifferencePlayStyle extends PlayStyle {
	protected playTD td;
	protected boolean player;
	protected GameMap map;
	
	public DifferencePlayStyle(Game game, TD td, boolean player) {
		this.map = game.getMap();
		this.td = new playTD(td);
		map.addViewer(this.td);
		this.player = player;
	}
	
	@Override
	public MoveReport getMove() {
		ArrayList<UnTestedCoordinates> candidates = Status.getValidPositions(map.getMoves());
		UnTestedCoordinates bestMove = null;
		double bestVal = 0;
		for(UnTestedCoordinates choice : candidates) {
			double eval = td.valueOf(player, choice);
			if(eval>bestVal) {
				bestVal = eval;
				bestMove = choice;
			}
		}
		MoveReport report;
		try {
			if(bestMove!=null)
				report =  new MoveReport(new Move(player, new PolarCoordinate(bestMove)));
			else {
				Random rand = new Random();
				int x = rand.nextInt(1) + 2;
				int y = rand.nextInt(12);
				report = new MoveReport(new Move(player, new PolarCoordinate(new UnTestedCoordinates(x,y))));
			}
		} catch (BadCoordinateException e) {
			e.printStackTrace();
			report = null;
		}
		return report;
	}
}
