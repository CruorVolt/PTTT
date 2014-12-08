package polar.game.styles;

import java.util.ArrayList;
import java.util.Random;

import logic.Status;
import logic.TD.TD;
import logic.TD.playTD;
import polar.game.Game;
import polar.game.GameMap;
import polar.game.Player;
import polar.game.UnTestedCoordinates;

public class DifferencePlayStyle implements PlayStyle {
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
	public UnTestedCoordinates getMove() {
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
		if(bestMove!=null)
			return bestMove;
		else {
			Random rand = new Random();
			int x = rand.nextInt(1) + 2;
			int y = rand.nextInt(12);
			return new UnTestedCoordinates(x,y);
		}
			
	}

	@Override
	public String toString() {
		return Player.PlayerTypes.TEMPORALDIFF.toString();
	}
}
