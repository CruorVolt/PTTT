package logic;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import polar.game.Move;

/* Infers the various advantages of the current game state
 *  by tracking and reporting Sequences owned by the player
 * 
*/
 
public class PlayerState {
	private ArrayList<Sequence> nodes;		// all sequences of size 1
	private ArrayList<Sequence> pairs; 		// all sequences of size 2
	private ArrayList<Sequence> triples;	// all sequences of size 3
	
	private boolean winState;				// Indicates if player has any 4-move sequence, ie they have won.
	private boolean player; 				// indicates which player is represented by this player state. 
	private int numOpenPairs;				// number of fully open sequences of size 2
	private int numClosedPairs;				// number of partially blocked sequences of size 2
	private int numOpenTriples;				// number of fully open sequences of size 3
	private int numClosedTriples;			// number of partially blocked sequences of size 3
	private int numNodes;					// total number of moves owned by player
	
	public PlayerState(boolean player) {
		this.player = player;
		nodes = new ArrayList<Sequence>();
		pairs = new ArrayList<Sequence>();
		triples = new ArrayList<Sequence>();
		winState = false;
		numNodes = 0;
		numOpenPairs = 0;
		numClosedPairs = 0;
		numOpenTriples = 0;
		numClosedTriples = 0;
	}
	// update sequences with the new move . first discover new sequences, then merges them together.
	public void update(Move newMove) {
		if(newMove.getPlayer()==player) {
			updateLists(newMove);
			printOut();
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
					}
				case 3:
					if(seq.isBlocked()) {
						numClosedTriples--;
					}
					if(seq.isClosed())
						numOpenTriples--;
				}
			}
		}
	}
	// update all sequences with new move
	private void updateLists(Move newMove) {
		Sequence newNode = new Sequence(newMove);
		ArrayList<Sequence> newPairs = updateList(nodes, newNode);
		addAll(pairs, newPairs);
		ArrayList<Sequence> newTriples = updateList(pairs, newNode);
		addAll(triples, newTriples);
		ArrayList<Sequence> wins = updateList(triples, newNode);
		ArrayList<Sequence> rarewins = updateList(pairs, newPairs);
		ArrayList<Sequence> reallyrarewins = updateList(triples, newPairs);
		ArrayList<Sequence> reallystupidwins = updateList(triples, newTriples);
		// if no merge produced a win, there is no win. ;;
		if(!(wins.isEmpty())||rarewins.isEmpty()||reallyrarewins.isEmpty()||reallystupidwins.isEmpty()) {
			winState = true;
		}
		add(nodes,newNode);
	}
	// update the list with list of new sequences.
	private ArrayList<Sequence> updateList(ArrayList<Sequence> list, ArrayList<Sequence> newList) {
		ArrayList<Sequence> newlist = new ArrayList<Sequence>();
		for(Sequence newSeq : newList) {
			ArrayList<Sequence> mergeList = updateList(list, newSeq);
			newlist.addAll(mergeList);
		}
		return newlist;
	}
	// returns a list of new sequences created with newSeq
	private ArrayList<Sequence> updateList(ArrayList<Sequence> list, Sequence newSeq) {
		ArrayList<Sequence> newlist = new ArrayList<Sequence>();
		for(Sequence seq : list) {
			Sequence merged = seq.merge(newSeq);
			if(merged!=null) {
				newlist.add(merged);
				removeSeq(seq);			// this sequence is being merged, remove and update
			}
		}
		return newlist;
	}
	// removes a sequence from pairs or triples lists, and adjusts counters as needed
	private void removeSeq(Sequence old) {
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
	}
	// add new sequence to list without duplicates
	private void add(ArrayList<Sequence> list, Sequence newSeq) {
		for(Sequence seq : list) {
			if(seq.equals(newSeq)) {
				return;						// do not add duplicates
			}
		}
		list.add(newSeq);
		if(newSeq.isSingle()) {
			numNodes++;
		}
		else if(newSeq.isClosedPair()) {
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
	
	public int numOpenPairs() {
		// TODO Auto-generated method stub
		return numOpenPairs;
	}

	public int numClosedPairs() {
		// TODO Auto-generated method stub
		return numClosedPairs;
	}

	public int numOpenTriples() {
		// TODO Auto-generated method stub
		return numOpenTriples;
	}

	public int numClosedTriples() {
		// TODO Auto-generated method stub
		return numClosedTriples;
	}
	public int numNodes() {
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
					numOpenTriples+" open triples, and "+numClosedTriples+" closed triples.";
	}
	public void printOut() {
		System.out.println(toString());
	}
}
