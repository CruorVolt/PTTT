package polar.test;

import polar.game.Game;
import polar.game.Move;

public class TestGame extends Game {
	TestMap map;
	public TestGame() {
		map = new TestMap();
		super.map = map;						// maintain consistency across child and parent
	}
	public void undoLastMove() {
		map.undoMove(map.lastMove());
		super.passPlay();
	}
	// skip printing
	public void printWin(Move[] winState) {
		
	}
}
