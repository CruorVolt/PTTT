package polar.game;
public class Move {
	private Move[] adjMoves = new Move[8];
	private boolean turn;
	private PolarCoordinate loc;
	private Character token;

	public Move(boolean turn, Character token, PolarCoordinate loc) {
		this.turn = turn;
		this.loc = loc;
		this.token = token;
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
	public String toString() {
		return this.token + ": " + this.loc.toString();
	}
}
