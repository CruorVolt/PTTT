package polar.game.styles;

import java.util.Random;

import polar.game.Player;
import polar.game.UnTestedCoordinates;
import polar.game.MoveReport;


public class RandomPlayStyle extends PlayStyle {
/*	
	private Game game;
	private boolean player;
	
	public RandomPlayStyle(boolean player, Game game) {
		this.player = player;
		this.game = game;
	}
*/
	@Override
	public MoveReport getMove() {
		Random rand = new Random();
		int x = rand.nextInt(4) + 1;
		int y = rand.nextInt(12);
		return new MoveReport(x,y);
	}

	@Override
	public String toString() {
		return Player.PlayerTypes.RANDOM.toString();
	}

}
