package polar.test;

import logic.Status;
import polar.game.GameMap;
import polar.game.Move;

public class TestMap extends GameMap {
	public void undoMove(Move m) {
		super.removeAll(m);
	}
		public boolean win(boolean p) {
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
							boolean refuted = !(Status.printResolve(u,v,x,y,d,p));
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
}
