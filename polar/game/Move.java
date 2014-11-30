package polar.game;

import polar.game.exceptions.MoveDuplicateException;

public class Move {
	private Move[] adjMoves = new Move[8];
	private boolean player;
	private PolarCoordinate loc;
	private Character token;
	public static Move block;
	private boolean blocker;	// tells whether this is a special blocking move

	public Move(boolean player, PolarCoordinate loc) {
		if(player==Player.PLAYER_X)
			token='X';
		else
			token='O';
		this.player = player;
		this.loc = loc;
		blocker = false;
		checkBlockingStatus();
	}
	// tests whether any adjacent locations are invalid.
	// adds blocking move to all invalid locations
	private void checkBlockingStatus() {
		getBlock();
		int x = loc.getX();
		// block all spaces which lead to the center of the circle!
		if(x==1) {
			adjMoves[PolarCoordinate.VAdjAndBelow] = block;
			adjMoves[PolarCoordinate.DAdjBelowAndBehind] = block;
			adjMoves[PolarCoordinate.DAdjBelowAndAhead] = block;
		}
		if(x==4) {
			adjMoves[PolarCoordinate.VAdjAndAbove] = block;
			adjMoves[PolarCoordinate.DAdjAboveAndBehind] = block;
			adjMoves[PolarCoordinate.DAdjAboveAndAhead] = block;			
		}
			
	}
	// Create a border move.
	private Move() {
		loc = null;
		token = null;
		adjMoves = null;
		blocker = true;
	}
	// returns the move blocking invalid locations
	public Move getBlock() {
		if(block==null) {
			block = new Move();
			block.blocker = true;
		}
		return block;
	}
	public boolean isBlock() {
		return blocker;
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
	// returns adjacent move in the given position if they are owned by a player
	// returns null if no adjacent move is present or if a position is blocked.
	public Move getAdjMove(int i) {
		Move t = getMoveOrBlock(i);
		if(t==null) return null;
		 if(!t.equals(block)) {
			 return null;
		 }
		 else return t;
	}
	// returns the adjacent move, even if it is the block move
	public Move getMoveOrBlock(int i) {
		if((i>-1)&&(i<8)) {
			return adjMoves[i];
		}
		else return null;
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
	public Boolean getPlayer() {
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
