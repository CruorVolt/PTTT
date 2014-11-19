package polar.game;
public class Move {
	private Move[] adjMoves = new Move[8];
	private boolean player;
	private PolarCoordinate loc;
	private Character token;

	public Move(boolean player, PolarCoordinate loc) {
		if(player==Player.PLAYER_X)
			token='X';
		else
			token='O';
		this.player = player;
		this.loc = loc;
	}
	protected void setAdjMove(int i, Move m) {
		adjMoves[i] = m;
	}
	// add Move s to position i
	public boolean update(Move s) throws MoveDuplicateException {
		int i = loc.compare(s.getLoc());
		if(i>=0) {
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
	/* compare this move to another move and return a direction between 0-7 if they are
		adjacent, or -1 if they are not.
	*/
	public int compare(Move m) {
		for(int i=0;i<8;i++) {
			if(m.equals(adjMoves[i])) {
				return i;
			}
		}
		return -1;
	}
	public Move getAdjMove(int i) {
		if((i>-1)&&(i<8))
		return adjMoves[i];
		else
			return null;
	}
	public Move[] adjacencyArray() {
		return adjMoves;
	}
	/*  Returns the player turn this Move belongs to.
	*   A true result means this belongs to player X. 
		false result means this belongs to player O.
		Not to be confused with the current turn: 
		the player who has control of the map.
	*/
	public boolean getPlayer() {
		return player;
	}
	public PolarCoordinate getLoc() {
		return loc;
	}
	/* In most situations, ie. game logic, getPlayer is recommended over this method.
	*/
	public Character getToken() {
		return this.token;
	}
	public String toString() {
		return this.token + ": " + this.loc.toString();
	}
}
