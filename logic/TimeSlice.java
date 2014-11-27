package logic;

import polar.game.Player;

// time slice representation of a game state.
// track changes in game state over time by encapsulating features in a particular time
public class TimeSlice {
		public final PlayerSlice O;
		public final PlayerSlice X;
	
	public TimeSlice(GameState state) {
		O = new PlayerSlice(state, Player.PLAYER_O);
		X = new PlayerSlice(state, Player.PLAYER_X);
	}
	public String toString() {
		String tostring = "X,"+X.toString();
				tostring += "\n";
				tostring +="O,"+O.toString();
				tostring += "\n";
		return tostring;
		
	}
	private class PlayerSlice {
		public final int closedpairs;
		public final int openpairs;
		public final int closedtriples;
		public final int opentriples;
		public final int nodes;
		public final PlayState state;
		
		private PlayerSlice(GameState state, boolean player) {
			closedpairs = state.getNumClosedPairs(player);
			openpairs = state.getNumOpenPairs(player);
			closedtriples = state.getNumClosedTriples(player);
			opentriples = state.getNumOpenTriplets(player);
			nodes = state.getNumNodes(player);
			this.state = state.getState(player);
		}
		public String toString() {
			String tostring = 	nodes+","+
								openpairs+","+
								closedpairs+","+
								opentriples+","+
								closedtriples;
			return tostring;
		}
	}
}
