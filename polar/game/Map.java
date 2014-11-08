package polar.game;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Map {
	Move root;
	LinkedList<Move> moves;
	GameViewer viewer;
	public Map(GameViewer viewer) {
		moves = new LinkedList<Move>();
		this.viewer = viewer;
	}
	public void addViewer(GameViewer viewer) {
		this.viewer = viewer;
	}
	public boolean updateAll(Move n, boolean turn) throws MoveDuplicateException {
		for (Move move : moves) {
			move.update(n);
		}
		moves.add(n);
		if (root==null)
			root = n;
		viewer.notifyMove(n.getLoc(), turn);
		if (moves.size() >= 48) {
			viewer.notifyWin(true, null);
		}
		return true;
	}
	
	/*
	 * Checks whether the supplied coordinates are set
	 * and returns the associated player token, or null
	 * if the node is unset.
	 */
	public Character isSet(PolarCoordinate coord) {
		for (Move move : this.moves) {
			if ( (move.getLoc().getX() == coord.getX()) && (move.getLoc().getY() == coord.getY()) ){
				return move.getToken();
			}
		}
		return null;
	}
	
	public LinkedList<Move> getMoves() {
		return moves;
	}
	
}