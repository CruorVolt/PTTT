package polar.game;

import java.util.Random;

public class RandomPlayStyle implements PlayStyle {

	@Override
	public UnTestedCoordinates getMove() {
		Random rand = new Random();
		int x = rand.nextInt(4) + 1;
		int y = rand.nextInt(12);
		System.out.println("Random player tries move (" + x + "," + y + ")");
		return new UnTestedCoordinates(x,y);
	}

}