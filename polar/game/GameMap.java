package polar.game;
import java.util.ArrayList;

public class GameMap {

	private ArrayList<Move> moves;
	private Move[] winSequence;
	private ArrayList<GameViewer> viewers;
	public GameMap() {
		moves = new ArrayList<Move>();
		viewers = new ArrayList<GameViewer>();
	}

	public void addViewer(GameViewer viewer) {
		viewers.add(viewer);
	}
	public void removeViewers() {
		viewers.clear();
	}
	/*
	 * Adds move n to the map if valid and 
	 * updates the adjacency of all previous
	 * moves to include n if necessary
	 */
	public boolean updateAll(Move n, boolean turn) throws MoveDuplicateException {
		boolean validMove = false;
		// allow move in any space, if no moves exist.
		if(moves.isEmpty())
			validMove = true;
		
		for (Move move : moves) {
			try {
				if(move.update(n)) {
					n.update(move);
					// allow move only in adjacent spaces, if moves exist.
					validMove = true;
				}
			}
		// don't allow duplicate moves
			catch(MoveDuplicateException e) {
				validMove = false;
				// undo any partial updates
				removeAll(n);
				// escalate exception and back out of update.
				throw(e);
			}
		}
		if(validMove)
			moves.add(n);
		if ((!viewers.isEmpty())&&validMove) {
			for(GameViewer viewer : viewers) {
				viewer.notifyMove(n.getLoc(), turn);
			}
		}
		// end the game if a win is found, or if the board is out of moves.
		if ( (win(n.getPlayer())||(moves.size() >= 48)) && (!viewers.isEmpty()) ){
			for(GameViewer viewer : viewers) {
				viewer.notifyWin(true, winSequence);
			}
		}
		return true;
	}
	
	/*
	 * Checks whether the supplied coordinates are set
	 * and returns the associated player token, or null
	 * if the node is unset.
	 */
	public Character isSet(PolarCoordinate coord) {
		for (Move move : this.moves) {
			if ( (move.getLoc().getX() == coord.getX()) && (move.getLoc().getY() == coord.getY()) ){
				return move.getToken();
			}
		}
		return null;
	}
	
	public ArrayList<Move> getMoves() {
		return moves;
	}

	/* Removes a move from the map.
	*  Used if an issue is found during update.
	*  DO NOT USE THIS to modify the current map.
	*  Feel free to use this to revert changes of
	*  Hypothetical maps for AI search
	*/
	public void removeAll(Move m) {
		for( Move move : moves) {
			move.remove(m);
		}
	}
	/*
	 * Copy the map and all its moves. Use for evaluating
	 * hypothetical game states in search trees without 
	 * modifying the map.
	 */
	public GameMap deepCopy() throws MoveDuplicateException {
		GameMap map = new GameMap(null); // null viewer should not send signals to the gui
		Move moveCopy;
		PolarCoordinate location;
		boolean player;
		for (Move move : moves) {
			location = move.getLoc();
			player = move.getPlayer();
			moveCopy = new Move(player, location);
			map.updateAll(moveCopy, true);
		}
		return map;
		
	}
	// returns true if player has a set of winning moves as defined by resolve.
	// Currently it is pretty dumb, and checks all move combinations!
	private boolean win(boolean p) {
		// player X has turn true, player Y has turn false.
		// all terms: 
		// win or not valid(d) or not owns(u,p) or not owns(v,p) or not owns(x,p) or not owns(y,p)
		// or not direction(u,v,d) or not direction(v,x,d) or not direction(x,y,d)
		// where u,v,x,y are moves (owned by p indicated by playerTurn)
		// d is a direction
		// and u,v,x,y are all connected in direction d.
		// steps: assume not win.
		// unify terms for known constants.
		
		// skip test if insufficient number of moves to test.
		if(moves.size()<4)
			return false;
		// select all move combinations for u,v,x,y
		for(int i=0;i<moves.size();i++)
			for (int j=0;j<moves.size();j++)
				for (int k=0;k<moves.size();k++)
					for( int l=0;l<moves.size();l++)
						if(!(i==j||j==k||k==l)) {	// confirm moves are unique.
						Move u = moves.get(i);
						Move v = moves.get(j);
						Move x = moves.get(k);
						Move y = moves.get(l);
						int d = u.compare(v);
						// refute !win on current unification string if resolution rejects all predicates
						boolean refuted = !(resolve(u,v,x,y,d,p));
						// if a win state is found, save the state and 
						if(refuted) {
							Move[] temp = {u,v,x,y};
							winSequence = temp;
							return true;
						}
					}
		return false;
	}
	// test all predicates in our ruleset
	private boolean resolve(Move u, Move v, Move x, Move y, int d, boolean p) {
	return	(!valid(d)||!owns(u,p)||!owns(v,p)||!owns(x,p)||!owns(y,p)||!direction(u,v,d)||!direction(v,x,d)||!direction(x,y,d));
	}
	// predicate to check directionality
	private boolean direction(Move a, Move b, int i) {
		return a.compare(b)==i;
	}
	// predicate to check ownership
	private boolean owns(Move m, boolean player) {
		return m.getPlayer()==player;
	}
	// predicate to check direction validity.
	private boolean valid(int i) {
		return (i>-1)&&(i<8);
	}
	
}
