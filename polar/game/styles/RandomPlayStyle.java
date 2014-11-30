package polar.game.styles;

import java.util.Random;

import polar.game.UnTestedCoordinates;

public class RandomPlayStyle implements PlayStyle {
/*	
	private Game game;
	private boolean player;
	
	public RandomPlayStyle(boolean player, Game game) {
		this.player = player;
		this.game = game;
	}
*/
	@Override
	public UnTestedCoordinates getMove() {
		//try {
			//Thread.sleep(100);
		//} catch (InterruptedException e) {
			//e.printStackTrace();
		//}
		Random rand = new Random();
		int x = rand.nextInt(4) + 1;
		int y = rand.nextInt(12);
		return new UnTestedCoordinates(x,y);
	}

}
