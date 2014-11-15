package polar.game;

import java.util.Random;
import logic.Heuristic;

public class RandomPlayStyle implements PlayStyle {
	
	private Game game;
	private Character player;
	
	public RandomPlayStyle(Character player, Game game) {
		this.player = player;
		this.game = game;
	}

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
