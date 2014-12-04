package polar.test;

import polar.game.GameMap;
import polar.game.Move;

public class TestMap extends GameMap {
	protected void undoMove(Move m) {
		super.removeAll(m);
	}
}
