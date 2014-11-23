package logic;

import polar.game.GameMap;
import polar.game.GameViewer;
import polar.game.Move;
import polar.game.Player;
import polar.game.PolarCoordinate;

/* Represents the current state of the map, based on relative advantages.
 * Infers the various advantages of the Map by tracking and reporting Sequences owned by each player.
 * Holds and refers to PlayerStates to report the state of the game.
 */
public class GameState implements GameViewer {
	private PlayerState stateX;
	private PlayerState stateO;
	private GameMap map;
	
	public GameState(GameMap map) {
		this.map = map;
		stateX = new PlayerState(Player.PLAYER_X);
		stateO = new PlayerState(Player.PLAYER_O);
	}
	
	@Override
	public void notifyMove(PolarCoordinate coord, boolean turn) {
		stateX.update(map.getCurrentMove());
		stateO.update(map.getCurrentMove());
		
	}
	@Override
	public void notifyWin(boolean turn, Move[] winState) {
		// not needed by this class.
	}
	public int getNumNodes(boolean player) {
		if(player==Player.PLAYER_X)
			return stateX.numNodes();
		else
			return stateO.numNodes();
	}
	public int getNumOpenPairs(boolean player) {
		if(player==Player.PLAYER_X)
			return stateX.numOpenPairs();
		else
			return stateO.numOpenPairs();
	}
	public int getNumClosedPairs(boolean player) {
		if(player==Player.PLAYER_X)
			return stateX.numClosedPairs();
		else
			return stateO.numClosedPairs();
	}
	public int getNumOpenTriplets(boolean player) {
		if(player==Player.PLAYER_X)
			return stateX.numOpenTriples();
		else
			return stateO.numOpenTriples();
	}
	public int getNumClosedTriples(boolean player) {
		if(player==Player.PLAYER_X)
			return stateX.numClosedTriples();
		else
			return stateO.numClosedTriples();
	}
}