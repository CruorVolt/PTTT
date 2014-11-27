package logic;

import java.util.ArrayList;

import polar.game.Move;

public class Sequence {
	private ArrayList<Move> sequence;
	private int direction;
	private int opposingDirection;
	private Move uEnd;
	private Move vEnd;
	private boolean player;
	
	// pair sequence
	private Sequence(Move u, Move v) {
		sequence = new ArrayList<Move>();
		sequence.add(u);
		sequence.add(v);
		direction = u.compare(v);
		opposingDirection = v.compare(u);
		player = u.getPlayer();
		uEnd = u.getMoveOrBlock(opposingDirection);
		vEnd = v.getMoveOrBlock(direction);
	}
	private Sequence(Move u, Move v, Move x) {
		sequence = new ArrayList<Move>();
		sequence.add(u);
		sequence.add(v);
		sequence.add(x);
		direction = u.compare(v);
		opposingDirection = v.compare(u);
		player = u.getPlayer();
		uEnd = u.getMoveOrBlock(opposingDirection);
		vEnd = x.getMoveOrBlock(direction);
	}
	// get the directionality of u->v
	public int getDir() {
		return direction;
	}
	// get the directionality of v->u
	public int getOpposingDir() {
		return opposingDirection;
	}
	// checks if m is at the end of this sequence, updates the end and returns true if so.
	public boolean updateEnd(Move m) {
		// an end cannot be owned by this player
		if(player==m.getPlayer())
				return false;
		Move firstMove = sequence.get(0);
		Move lastMove = sequence.get(size()-1);
		Move firstEnd = firstMove.getAdjMove(opposingDirection);
		Move lastEnd = lastMove.getAdjMove(direction);
		if(direction==-1) {
			return false;
		}
		if(firstEnd!=null) {
			if(m.equals(firstEnd)) {
				uEnd = m;				// uEnd updated with move m
				return true;
			}
		}
		if(lastEnd!=null) {
			if(m.equals(lastEnd)) {
				vEnd = m;				// vEnd updated with move m
				return true;
			}
		}
		
		return false;
	}
	// endpoint connected to the last node in the sequence
	public Move vEnd() {
		return vEnd;
	}
	// endpoint connected to the first node in the sequence
	public Move uEnd() {
		return uEnd;
	}
	// player who owns the sequence
	public boolean getPlayer() {
		return player;
	}
	// list of nodes in the sequence
	public ArrayList<Move> getMoves() {
		return sequence;
	}
	public boolean isOpen() {
		return vEnd==null&&uEnd==null;	// only open if both ends are open
	}
	public boolean isClosed() {
		if(vEnd==null^uEnd==null) return true;	// one and only one end is open (using xor operation).
		return false;
	}
	public boolean isSingle() {
		if(size()==1) 
			return true;
		return false;
	}
	public boolean isOpenPair() {
		if((size()==2)&&(isOpen()))
			return true;
		return false;
	}
	public boolean isClosedPair() {
		if((size()==2)&&(isClosed()))
			return true;
		return false;		
	}
	public boolean isOpenTriple() {
		if((size()==3)&&(isOpen()))
			return true;
		return false;		
	}
	public boolean isClosedTriple() {
		if((size()==3)&&(isClosed()))
			return true;
		return false;		
	}
	// returns true if both ends of this pair are blocked by another player.
	public boolean isBlocked() {
		// false is either end is open
		if((vEnd==null)||(uEnd==null)) return false;

		return true;
	}
	// returns the size of this sequence
	public int size() {
		return sequence.size();
	}
	// build a pair from 2 moves.
	public static Sequence makePair(Move move1, Move move2) {
		// Invalid parameters: identical moves, moves not owned by the same person, blocking move
		if((move1.getPlayer()!=move2.getPlayer())||(move1.equals(move2))||move1.isBlock()||move2.isBlock())
			return null;
		int dir = move1.compare(move2);
		if(Status.valid(dir)) {
			return new Sequence(move1,move2);
		}
		return null;
	}
	public static Sequence makeTriple(Sequence pair1, Sequence pair2) {
		ArrayList<Move> moves = pair1.getMoves();
		ArrayList<Move> othermoves = pair2.getMoves();
		ArrayList<Move> newlist = new ArrayList<Move>();
		
		Move midMove = null;
		
		for(Move move : moves) {
			if(othermoves.contains(move)) {
				midMove = move;
			}
			else {
				newlist.add(move);							// add first move at beginning
			}
		}
		if(midMove==null) 
			return null;
		else
			newlist.add(midMove);							// add shared move in the middle
		for(Move move : othermoves) {
			if(!move.equals(midMove)) {
				newlist.add(move);							// add last move at the end
			}
		}
		
		if(newlist.size()!=3) 
			return null;									// do not make triple if not a 3-move sequence
		Move u = newlist.get(0);
		Move v = newlist.get(1);
		Move x = newlist.get(2);
		int dir = u.compare(v);
		// create the sequence only if u,v and v,x share a direction and the direction is valid.
		if(Status.valid(dir)&&Status.direction(v, x, dir)) {	
			return new Sequence(u,v,x);
		}	
		return null;
	}
	// returns first element in the list
	private Move getFirstMove() {
		return sequence.get(0);
	}
	// returns last element in the list
	private Move getLastMove() {
		return sequence.get(size()-1);
	}
	// returns true if these sequences have the same elements, in whatever order.
	public boolean equals(Sequence other) {
		// if these have the same moves and the same # of moves, they are the same
		if((size()==other.size())&&(sequence.containsAll(other.getMoves()))){
			return true;
		}
		return false;
	}
	public boolean contains(Sequence other) {
		if(sequence.containsAll(other.getMoves()))
			return true;
		return false;
	}
}
