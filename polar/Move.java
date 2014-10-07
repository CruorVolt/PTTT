package polar;
public class Move {
	Move[] adjMoves = new Move[8];
	char player;
	PolarCoordinate loc;
	public Move(char player, PolarCoordinate loc) {
		this.player = player;
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
	public PolarCoordinate getLoc() {
		return loc;
	}
}
