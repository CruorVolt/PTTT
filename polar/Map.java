package polar;
import java.util.LinkedList;

public class Map {
	Move root;
	LinkedList<Move> moves;
	public Map() {
		moves = new LinkedList<Move>();
	}
	public void updateAll(Move n) throws MoveDuplicateException {
		for (Move move : moves) {
			move.update(n);
		}
		moves.add(n);
		if (root==null)
			root = n;
	}
}
