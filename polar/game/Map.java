package polar.game;
import java.util.LinkedList;

public class Map {
	Move root;
	LinkedList<Move> moves;
	GameViewer viewer;
	public Map(GameViewer viewer) {
		moves = new LinkedList<Move>();
		this.viewer = viewer;
	}
	public void updateAll(Move n) throws MoveDuplicateException {
		for (Move move : moves) {
			move.update(n);
		}
		moves.add(n);
		if (root==null)
			root = n;
		viewer.notifyMove(n.getLoc(), n.getTurn());
		
		System.out.println("vvvvvvvv MOVES SO FAR: vvvvvvvv");
		for (Move m : moves) {
			System.out.println(m);
		}
		System.out.println("^^^^^^^^ MOVES SO FAR: ^^^^^^^^");
	}
	
	public boolean hasWon(Character token) {
		System.out.println("Moves so far:");
		for (Move move : moves) {
			System.out.println(move);
		}
		System.out.println("");
		return false;
	}
}
