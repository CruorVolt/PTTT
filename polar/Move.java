package polar;
public class Move {
	private Move[] adjMoves;
	private boolean turn;
	private PolarCoordinate loc;
	public Move(boolean turn, PolarCoordinate loc) {
		this.turn = turn;
		this.loc = loc;
	}
	// add Move s to position i
	public boolean update(Move s) throws MoveDuplicateException {
		int i = loc.compare(s.getLoc());
		if(i>0) {
			if (adjMoves[i] == null) {
				adjMoves[i] = s;
			}
			else {
				return false;
			}
		}
		return true;
		
	}
	public boolean getTurn() {
		return turn;
	}
	public PolarCoordinate getLoc() {
		return loc;
	}
}
