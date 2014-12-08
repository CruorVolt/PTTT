 package polar.game;
import java.util.ArrayList;

import polar.game.exceptions.MoveDuplicateException;
import logic.Status;
import logic.state.GameState;

public class GameMap {

	private ArrayList<Move> moves;
	private Move[] winSequence;
	private ArrayList<GameViewer> viewers;
	private Move currentMove;
	private GameState state;	
	private boolean hasWin;
	
	public GameMap() {
		hasWin = false;
		currentMove = null;
		moves = new ArrayList<Move>();
		viewers = new ArrayList<GameViewer>();
		state = new GameState(this);
		viewers.add(state);
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
	public boolean updateAll(MoveReport n) throws MoveDuplicateException {
		boolean validMove = false;
		// allow move in any space, if no moves exist.
		if(moves.isEmpty())
			validMove = true;
		
		for (Move move : moves) {
			try {
				if(move.update(n.getMove())) {
					n.getMove().update(move);
					// allow move only in adjacent spaces, if moves exist.
					validMove = true;
				}
			}
		// don't allow duplicate moves
			catch(MoveDuplicateException e) {
				validMove = false;
				// undo any partial updates
				removeAll(n.getMove());
				// escalate exception and back out of update.
				throw(e);
			}
		}
		if(validMove)
			moves.add(n.getMove());
			currentMove = n.getMove();
		if ((!viewers.isEmpty())&&validMove) {
			for(GameViewer viewer : viewers) {
				viewer.notifyMove(n);
			}
		}
		// end the game if a win is found, or if the board is out of moves.
		if ( (win(n.getMove().getPlayer())||(moves.size() >= 48)) && (!viewers.isEmpty()) ){
			for(GameViewer viewer : viewers) {
				viewer.notifyWin(true, winSequence);
			}
		}
		return validMove;
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
	protected void removeAll(Move m) {
		for( Move move : moves) {
			move.remove(m);
		}
		moves.remove(m);
		if(m.equals(currentMove))
			currentMove = lastMove();
	}
	/*
	 * Copy the map and all its moves. Use for evaluating
	 * hypothetical game states in search trees without 
	 * modifying the map.
	 */
	public GameMap deepCopy() throws MoveDuplicateException {
		GameMap map = new GameMap(); // null viewer should not send signals to the gui
		Move moveCopy;
		PolarCoordinate location;
		boolean player;
		for (Move move : moves) {
			location = move.getLoc();
			player = move.getPlayer();
			moveCopy = new Move(player, location);
			map.updateAll(new MoveReport(moveCopy));
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
						boolean refuted = !(Status.resolve(u,v,x,y,d,p));
						// if a win state is found, save the state and 
						if(refuted) {
							Move[] temp = {u,v,x,y};
							winSequence = temp;
							hasWin = true;
							return true;
						}
					}
		return false;
	}
	public Move lastMove() {
		return moves.get(moves.size()-1);
	}
	public Move getCurrentMove() {
		return currentMove;
	}
	public GameState getState() {
		return state;
	}
	
	public boolean containsWin() {
		return hasWin;
	}
}
