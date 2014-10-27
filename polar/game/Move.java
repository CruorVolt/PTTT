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
	protected void setAdjMove(int i, Move m) {
		adjMoves[i] = m;
	}
	// add Move s to position i
	public boolean update(Move s) throws MoveDuplicateException {
		int i = loc.compare(s.getLoc());
		if(i>0) {
			if (adjMoves[i] == null) {
				adjMoves[i] = s;
				int j = s.getLoc().compare(loc);
				s.setAdjMove(j, this);
			}
			else {
				return false;
			}
		}
		else
			return false;
		return true;
		
	}
	// clean up method to help insure the map has a consistent state even when a move is invalid or fails.
	// remove a given move from all reference points to this move
	public void remove(Move s) {
		for(int i=0;i<8;i++) {
			if(adjMoves[i]!=null)
				if(adjMoves[i].equals(s))
						adjMoves[i] = null;
		}
	}
	public Move getAdjMove(int i) {
		if((i>-1)&&(i<8))
		return adjMoves[i];
		else
			return null;
	}
	public boolean getTurn() {
		return turn;
	}
	public PolarCoordinate getLoc() {
		return loc;
	}
	public Character getToken() {
		return this.token;
	}
	public String toString() {
		return this.token + ": " + this.loc.toString();
	}
}
