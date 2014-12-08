package polar.test;

import polar.game.GameMap;
import polar.game.Move;

public class TestMap extends GameMap {
	public void undoMove(Move m) {
		super.removeAll(m);
	}
}
