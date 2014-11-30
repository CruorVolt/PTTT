package logic.state;

import java.util.ArrayList;
import java.util.Iterator;

import polar.game.GameMap;
import polar.game.Move;
import polar.game.Player;
import polar.game.PolarCoordinate;

// time slice representation of a game state.
// track changes in game state over time by encapsulating features in a particular time
public class TimeSlice {
		public final PlayerSlice O;
		public final PlayerSlice X;
		private int iterator;
		private GameMap map;
	
	public TimeSlice(GameState state, GameMap map) {
		O = new PlayerSlice(state, Player.PLAYER_O);
		X = new PlayerSlice(state, Player.PLAYER_X);
		iterator = 0;
	}
	public String toString() {
		String tostring = "X,"+X.toString();
				tostring += "\n";
				tostring +="O,"+O.toString();
				tostring += "\n";
		return tostring;
		
	}
	// provides features in numeric representation
	public Double[] evalAllFeatures(boolean maxplayer) {
		ArrayList<Double> list = new ArrayList<Double>();
		list.addAll(baseFeatures(X));
		list.addAll(baseFeatures(O));
		return list.toArray(new Double[list.size()]);
	}
	public ArrayList<Double> evalMapFeatures(boolean maxplayer) {
		return null;
	}
	public ArrayList<Double> evalbaseFeatures(boolean maxplayer) {
		if(maxplayer==Player.PLAYER_X) 
			return baseFeatures(X);
		else
			return baseFeatures(O);
	}

	private ArrayList<Double> mapFeatures(PlayerSlice slice, boolean maxplayer) {
		ArrayList<Double> list = new ArrayList<Double>();
		Double[][] mapfeatures = new Double[4][12];
		for(Move move : map.getMoves()) {
			PolarCoordinate loc = move.getLoc();
			if(move.getPlayer()==maxplayer)
					mapfeatures[loc.getX()][loc.getY()] = 1.0;
			else
				mapfeatures[loc.getX()][loc.getY()] = -1.0;
		}
		// add map features in consistent order, each playable position as a feature
		for(int x=1;x<5;x++) {
			for(int y=0;y<12;y++) {
				// if a map feature is not set, set feature to 0.
				if(mapfeatures==null)
					list.add(new Double(0.0));
				else
					list.add(mapfeatures[x][y]);
			}
		}	
		return list;
	}
	private ArrayList<Double> baseFeatures(PlayerSlice slice) {
		ArrayList<Double> list = new ArrayList<Double>();
		list.add(new Double(evalState(slice.state)));
		list.add(new Double((double)slice.nodes));
		list.add(new Double((double)slice.closedpairs));
		list.add(new Double((double)slice.openpairs));
		list.add(new Double((double)slice.closedtriples));
		list.add(new Double((double)slice.opentriples));
		return list;
	}
	private Double evalState(PlayState state) {
		if(state==state.lose)
			return -100.0;
		else if(state==state.win)
			return 100.0;
		else
			return 0.0;
	}

	private class PlayerSlice {
		public final int closedpairs;
		public final int openpairs;
		public final int closedtriples;
		public final int opentriples;
		public final int nodes;
		public final PlayState state;
		private int iterator;
		
		private PlayerSlice(GameState state, boolean player) {
			closedpairs = state.getNumClosedPairs(player);
			openpairs = state.getNumOpenPairs(player);
			closedtriples = state.getNumClosedTriples(player);
			opentriples = state.getNumOpenTriplets(player);
			nodes = state.getNumNodes(player);
			this.state = state.getState(player);
			iterator = 0;

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
