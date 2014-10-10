package polar;
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
	}
}
