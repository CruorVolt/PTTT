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
	public void updateAll(Move n) throws MoveDuplicateException {
		for (Move move : moves) {
			move.update(n);
		}
		moves.add(n);
		if (root==null)
			root = n;
		viewer.notifyMove(n.getLoc(), n.getTurn());
		if (hasWon(n.getToken())) {
			System.out.println("A PLAYER HAS WON");
		}
	}
	
	public boolean hasWon(Character token) {
		Move[][] moveGrid = new Move[4][12];
		PolarCoordinate currentLoc;
		for (Move m : moves) {
			currentLoc = m.getLoc();
			if (m.getToken() == token) {
				moveGrid[currentLoc.getX()-1][currentLoc.getY()] = m;
				for (Move[] what : moveGrid) {
					System.out.println(Arrays.deepToString(what));
				}
			}
		}
		return false;
	}
}