package logic.state;

import java.util.ArrayList;

import polar.game.GameMap;
import polar.game.Move;
import polar.game.Player;
import polar.game.PolarCoordinate;
import polar.game.exceptions.MoveDuplicateException;

// time slice representation of a game state.
// track changes in game state over time by encapsulating features in a particular time
public class TimeSlice {
		public final PlayerSlice O;
		public final PlayerSlice X;
		private final ArrayList<Move> moves;
	
	public TimeSlice(GameState state, GameMap map) {
		O = new PlayerSlice(state, Player.PLAYER_O);
		X = new PlayerSlice(state, Player.PLAYER_X);
		GameMap copy = null;
		try {
			copy = map.deepCopy();
		} catch (MoveDuplicateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		moves = copy.getMoves();
	}
	public TimeSlice(GameState state) {
		O = new PlayerSlice(state, Player.PLAYER_O);
		X = new PlayerSlice(state, Player.PLAYER_X);
		moves = null;
	}
	public String toString() {
		String tostring = "X,"+X.toString();
				tostring += "\n";
				tostring +="O,"+O.toString();
				tostring += "\n";
		return tostring;
		
	}
	public ArrayList<Move> getMoves() {
		return moves;
	}
	/* provides features in numeric representation
	public Double[] evalAllFeatures(boolean maxplayer) {
		ArrayList<Double> list = new ArrayList<Double>();
		list.addAll(mapFeatures(maxplayer));
		list.addAll(baseFeatures(maxplayer));
		return list.toArray(new Double[list.size()]);
	}
	*/
	// get all mapFeatures in numeric representation
	//where positions are marked by 1, -1, or 0
	// for owned by maxplayer, owned by minplayer, or unowned respectively
	public ArrayList<Double> mapFeatures(boolean maxplayer) {
		ArrayList<Double> list = new ArrayList<Double>();
		Double[][] mapfeatures = new Double[4][12];
		for(Move move : moves) {
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
	/* retrieve all base features where first 6 features are positive maxplayer features
	* and last 6 features are negated minplayer features.
	* 6 features are: 	state: 1 for win, -1 for lose, otherwise
	*					# nodes
	*					# closedpairs
	*					# openpairs
	*					# closedtriples
	*					# opentriples
	*/
	public double[] baseFeatures(boolean maxplayer) {
		PlayerSlice maxSlice, minSlice;
		if(maxplayer==Player.PLAYER_X) {
			maxSlice = X;
			minSlice = O;
		}
		else {
			maxSlice = O;
			minSlice = X;
		}
		double[] vals = new double[9];
		vals[0] = maxSlice.closedpairs;
		vals[1] = maxSlice.openpairs;
		vals[2] = maxSlice.closedtriples;
		vals[3] = maxSlice.opentriples;
		
		vals[4] = minSlice.closedpairs;
		vals[5] = minSlice.openpairs;
		vals[6] = minSlice.closedtriples;
		vals[7] = minSlice.opentriples;
		vals[8] = evalState(maxSlice.state);
		return vals;
	}
	private Double evalState(PlayState state) {
		if(state==PlayState.lose)
			return -1.0;
		else if(state==PlayState.win)
			return 1.0;
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
