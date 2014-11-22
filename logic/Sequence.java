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
	
	// singleton sequence
	public Sequence(Move x) {
		sequence = new ArrayList<Move>();
		sequence.add(x);
		direction = -1;
		opposingDirection = -1;
		player = x.getPlayer();
	}
	// Pair sequence
	private Sequence(Sequence u, Sequence v) {
		sequence = u.sequence;
		for(Move m : v.getMoves()) {
			sequence.add(m);
		}
		direction = u.getDir();
		opposingDirection = u.getOpposingDir();
		player = u.getPlayer();
		uEnd = u.getMoves().get(0).getMoveOrBlock(opposingDirection);
		vEnd = v.getMoves().get(v.size()-1).getMoveOrBlock(direction);
	}

	// get the directionality of u->v
	public int getDir() {
		return direction;
	}
	// get the directionality of v->u
	public int getOpposingDir() {
		return opposingDirection;
	}
	// returns true if an end is updated by m
	public boolean updateEnd(Move m) {
		Move firstMove = sequence.get(0);
		Move lastMove = sequence.get(size()-1);
		if(direction==-1) {
			return false;
		}
		if(firstMove.getAdjMove(opposingDirection)!=null) {
			if(m.equals(firstMove.getAdjMove(opposingDirection))) {
				uEnd = m;				// uEnd updated with move m
				return true;
			}
		}
		if(lastMove.getAdjMove(direction)!=null) {
			if(m.equals(lastMove.getAdjMove(direction))) {
				vEnd = m;				// vEnd updated with move m
				return true;
			}
		}
		
		return false;
	}
	public Move vEnd() {
		return vEnd;
	}
	public Move uEnd() {
		return uEnd;
	}
	public boolean getPlayer() {
		return player;
	}
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
		if(vEnd==null) return false;
		if(uEnd==null) return false;
		// false if pair is actually a triple
		if(vEnd.getPlayer()==player) return false;
		if(uEnd.getPlayer()==player) return false;
		return true;
	}
	public boolean isWin() {
		return sequence.size()==4;						// win if this sequence has 4 moves
	}
	public int size() {
		return sequence.size();
	}
	public Sequence merge(Sequence q) {
		if(!this.player==q.getPlayer())					// sequences must be owned by the same player
			return null;
		// if both are single, compare moves and return sequence if they are adjacent
		if(size()==1) {
			 if(q.size()>1) {
				 q.merge(this);
			 }
			 Move myMove = sequence.get(0);
			 Move qMove = q.sequence.get(0);
			 int dir = myMove.compare(qMove);
			 if((dir>-1)) {
				 direction = dir;
				 return new Sequence(this, q);
			 }
		}
		if(q.size()==1) {
			updateEnd(q.getMoves().get(0));
		}

		Move QfirstMove = q.sequence.get(0);
		Move QlastMove = q.sequence.get(q.size()-1);
		Move first = sequence.get(0);
		Move last = sequence.get(size()-1);
		int dir = -1;
		
		dir = last.compare(QfirstMove);
		if(dir>-1) {
			direction = dir;
			opposingDirection = QfirstMove.compare(last);
			return new Sequence(this,q);
		}
		dir = first.compare(QfirstMove);
		
		if(dir>-1) {
			direction = dir;
			opposingDirection = QfirstMove.compare(first);
			reverse();
			return new Sequence(this,q);
		}
		dir = last.compare(QlastMove);

		if(dir>-1) {
			q.reverse();
			direction = dir;
			opposingDirection = QfirstMove.compare(first);
			return new Sequence(this,q);
		}

		dir = first.compare(QlastMove);
		if(dir>-1) {
			direction = dir;
			opposingDirection = QlastMove.compare(first);
			reverse();
			q.reverse();
			return new Sequence(this,q);
		}
		
		return null;
	}
	private void reverse() {
		ArrayList<Move> seq = new ArrayList<Move>();
		
		for(int i=size()-1;i>=0;i--) {
			seq.add(sequence.get(i));
		}
		sequence = seq;
		Move tEnd = uEnd;
		uEnd= vEnd;
		vEnd = tEnd;
		int tDir = direction;
		direction = opposingDirection;
		opposingDirection = tDir;
	}
	public boolean equals(Sequence other) {
		// if these have the same moves and the same # of moves, they are the same
		if((size()==other.size())&&(sequence.containsAll(other.getMoves()))){
			return true;
		}
		return false;
	}
}
