package logic.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ListIterator;

import polar.game.Move;

/* Infers the various advantages of the current game state
 *  by tracking and reporting Sequences owned by the player
 * 
*/
class PlayerState {
	private ArrayList<Move> nodes;		// all sequences of size 1
	private ArrayList<Sequence> pairs; 		// all sequences of size 2
	private ArrayList<Sequence> triples;	// all sequences of size 3
	
	private boolean winState;				// Indicates if player has any 4-move sequence, ie they have won.
	private boolean player; 				// indicates which player is represented by this player state. 
	private int numOpenPairs;				// number of fully open sequences of size 2
	private int numClosedPairs;				// number of partially blocked sequences of size 2
	private int numOpenTriples;				// number of fully open sequences of size 3
	private int numClosedTriples;			// number of partially blocked sequences of size 3
	private int numNodes;					// total number of moves owned by player
	private PlayState state;
	
	protected PlayerState(boolean player) {
		this.player = player;
		nodes = new ArrayList<Move>();
		pairs = new ArrayList<Sequence>();
		triples = new ArrayList<Sequence>();
		winState = false;
		numNodes = 0;
		numOpenPairs = 0;
		numClosedPairs = 0;
		numOpenTriples = 0;
		numClosedTriples = 0;
		state = PlayState.active;
	}
	// update sequences with the new move . first discover new sequences, then merges them together.
	protected void update(Move newMove) {
		if(newMove.getPlayer()==player) {
			updateLists(newMove);
		}
		else {
			updateEnds(newMove);
		}
	}
	// update ends of all sequences except for nodes
	private void updateEnds(Move newMove) {
		updateEnd(pairs, newMove);
		updateEnd(triples, newMove);
	}
	// update end nodes of sequences in the list with the new move
	private void updateEnd(ArrayList<Sequence> list, Move newMove) {
		for(Sequence seq : list) {
			if(seq.updateEnd(newMove)) {
				switch(seq.size()) {
				case 2:
					if(seq.isBlocked()) {
						numClosedPairs--;
					}
					if(seq.isClosed()) {
						numOpenPairs--;
						numClosedPairs++;
					}
				case 3:
					if(seq.isBlocked()) {
						numClosedTriples--;
					}
					if(seq.isClosed())
						numOpenTriples--;
						numClosedTriples++;
				}
			}
		}
	}
	// update all sequences with new move
	private void updateLists(Move newMove) {
		ArrayList<Sequence> newPairs = getPairs(newMove);					// calculate new pairs created with new node
		ArrayList<Sequence> newTriples = getTriples(newPairs);				// calculate new triples from pairs list
		addAll(pairs, newPairs);											// update list and count
		addAll(triples, newTriples);										// update list and count
		removeAll(pairs,newTriples);										// remove merged pairs from pairs list
		addNode(newMove);
	}
	private ArrayList<Sequence> getTriples(ArrayList<Sequence> newPairs) {
		ArrayList<Sequence> newtriples = new ArrayList<Sequence>();
		Sequence lastpair = null;
		Sequence ntriple = null;
		// try to create triple out of all possible pair combinations
		for(Sequence newpair : newPairs) {
			if(lastpair!=null) {
				ntriple = Sequence.makeTriple(lastpair, newpair);
				if(ntriple!=null)
					newtriples.add(ntriple);
			}
			lastpair = newpair;
			for(Sequence pair : pairs) {
				Sequence newtriple = Sequence.makeTriple(pair, newpair);
				if(newtriple!=null)
					newtriples.add(newtriple);								// add only valid triples
			}
		}
		return newtriples;
	}
	private ArrayList<Sequence> getPairs(Move newMove) {
		ArrayList<Sequence> newpairs = new ArrayList<Sequence>();
		for(Move node : nodes) {
			// attempt to create new pairs with the new move and existing moves
			Sequence newpair = Sequence.makePair(node, newMove);
			if(newpair!=null)
				newpairs.add(newpair);
		}
		return newpairs;
	}
	// removes a sequence from pairs or triples lists, and adjusts counters as needed
	private void remove(Sequence old) {
		switch(old.size()) {
		case 2:
			pairs.remove(old);
			if(old.isClosedPair()) {
				numClosedPairs--;
			}
			else if(old.isOpenPair()) {
				numOpenPairs--;
			}
				
		case 3:
			triples.remove(old);
			if(old.isClosedTriple()) {
				numClosedTriples--;
			}
			else if(old.isOpenTriple()) {
				numOpenTriples--;
			}
		}
		if(old.size()>3)
			System.out.println("Error removing, invalid sequence. Sequence is size "+old.size());
	}
	// add move to node list
	private void addNode(Move newMove) {
		if(nodes.contains(newMove)) {										// ignore duplicates
			return;
		}	
		nodes.add(newMove);
		numNodes++;
		if(numNodes>=48)
			state = PlayState.draw;
	}
	// add new sequence to list without duplicates
	private void add(ArrayList<Sequence> list, Sequence newSeq) {
		if(list.contains(newSeq)) {										// ignore duplicates
			return;
		}
		if(newSeq.isBlocked())
			return;														// do not count blocked sequences
		
		list.add(newSeq);
		if(newSeq.isClosedPair()) {
			numClosedPairs++;
		}
		else if(newSeq.isOpenPair()) {
			numOpenPairs++;
		}
		else if(newSeq.isOpenTriple()) {
			numOpenTriples++;
		}
		else if(newSeq.isClosedTriple()) {
			numClosedTriples++;
		}
		else {
			System.out.println("Error adding to list, sequence is not a proper list item");
		}
	}
	// add all sequences in new list to list without duplicates
	private void addAll(ArrayList<Sequence> list, ArrayList<Sequence> newlist) {
		for(Sequence seq : newlist) {
			add(list, seq);
		}
	}
	// cleanup list items that are sub-sequences of remove sequence
	private void remove(ArrayList<Sequence> list, Sequence removeseq) {
		ArrayList<Sequence> removals = new ArrayList<Sequence>();
		if(list.isEmpty()||removeseq==null)													// do not attempt pointless update
			return;
		for(Sequence seq : list) {
			if(removeseq.contains(seq)) {							// if the remove sequence contains all moves in the list sequence, it is redundant
				removals.add(seq);
			}
		}
		for(Sequence seq : removals) {
			remove(seq);
		}
	}
	// cleanup list items that are sub-sequences of all sequences in removal list
	private void removeAll(ArrayList<Sequence> list, ArrayList<Sequence> removalList) {
		if(list.isEmpty()||removalList.isEmpty())											// do not attempt pointless updates
			return;
		for(Sequence seq : removalList) {
			remove(list, seq);
		}
	}
	
	protected int numOpenPairs() {
		// TODO Auto-generated method stub
		return numOpenPairs;
	}

	protected int numClosedPairs() {
		// TODO Auto-generated method stub
		return numClosedPairs;
	}

	protected int numOpenTriples() {
		// TODO Auto-generated method stub
		return numOpenTriples;
	}

	protected int numClosedTriples() {
		// TODO Auto-generated method stub
		return numClosedTriples;
	}
	protected int numNodes() {
		return numNodes;
	}
	public String toString() {
		String p;
		if(player)
			p = "X";
		else
			p="O";
		return "Player "+p+" has "+numNodes+" nodes, "
					+numOpenPairs+" open pairs, "+numClosedPairs+" closed pairs, "+
					numOpenTriples+" open triples, and "+numClosedTriples+" closed triples.\n"+
					"Player "+p+" has a total of "+pairs.size()+" pairs, and "+triples.size()+" triples.";
	}
	protected void printOut() {
		System.out.println(toString());
	}
	protected void notifyWin() {
		state = PlayState.win;
	}
	protected void notifyLose() {
		state = PlayState.lose;
	}
	protected boolean hasWon() {
		return winState;
	}
	protected PlayState getState() {
		return state;
		
	}
	public ArrayList<Move> getMoves() {
		return nodes;
	}
}
