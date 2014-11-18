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
	
	/*
	 * Adds move n to the map if valid and 
	 * updates the adjacency of all previous
	 * moves to include n if necessary
	 */
	public boolean updateAll(Move n, boolean turn) throws MoveDuplicateException {
		for (Move move : moves) {
			move.update(n);
		}
		moves.add(n);
		if (root==null)
			root = n;
		if (viewer != null) {
			viewer.notifyMove(n.getLoc(), turn);
		}
		if ( (moves.size() >= 48) && (viewer != null) ){
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
	
	/*
	 * Copy the map and all its moves. Use for evaluating
	 * hypothetical game states in search trees without 
	 * modifying the map.
	 */
	public Map deepCopy() throws MoveDuplicateException {
		Map map = new Map(null); // null viewer should not send signals to the gui
		Move moveCopy;
		PolarCoordinate location;
		Character player;
		for (Move move : moves) {
			location = move.getLoc();
			player = move.getToken();
			moveCopy = new Move(true, player, location);
			map.updateAll(moveCopy, true);
		}
		return map;
		
	}

	
}