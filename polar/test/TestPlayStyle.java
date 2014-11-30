package polar.test;

import polar.game.UnTestedCoordinates;
import polar.game.styles.PlayStyle;

public class TestPlayStyle implements PlayStyle {
	private UnTestedCoordinates[] moves;
	int iterator;
	public TestPlayStyle(UnTestedCoordinates[] moves) {
		this.moves = moves;
		iterator = 0;
	}
	@Override
	public UnTestedCoordinates getMove() {
		if(iterator<moves.length) {
			UnTestedCoordinates m = moves[iterator];
			iterator++;
			System.out.print(m.toString()+" ");
			if(iterator==moves.length) { System.out.println();}	// formatting
			return m;
		}
		else {
			
			System.out.println("No more moves.");
			return null;
		}
	}
}