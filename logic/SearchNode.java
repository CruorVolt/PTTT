package logic;
	
import java.util.ArrayList;

import polar.game.*;
import polar.game.exceptions.BadCoordinateException;
import polar.game.exceptions.MoveDuplicateException;

public class SearchNode {
		
	GameMap map; //The state of the game at this node
	int alpha;   //The current estimated alpha value
	int beta;	 //The current estimated beta value
	boolean max; //Max player (X) or min player (O)
	ArrayList<SearchNode> children; //All possible child states
	
	public SearchNode(GameMap map, boolean player) {
		this.map = map;
		this.max = player;
		children = null;
	}
	
	public GameMap getMap() {
		return this.map;
	}
	
	public PolarCoordinate getMove() { //Get the move that resulted in this gamestate
		ArrayList<Move> moves = this.map.getMoves();
		return moves.get(moves.size() - 1).getLoc(); //newest move
	}
	
	public ArrayList<SearchNode> getChildren() {
		return this.children;
	}
	
	/*
	 * Build new child nodes for every possible valid move 
	 * that could be made from this gamestate
	 * TODO: This logic is similar to how GreedySearch checks for validity, roll that up into a function in GameMap
	 */
	public void createChildren() {

		if (this.children == null) {
			this.children = new ArrayList<SearchNode>();
		}

		for (int layer = 1; layer <= 4; layer++) { //check all layers
			for (int radian = 0; radian <= 11; radian++) { //check all spokes

				PolarCoordinate location;
				try {
					location = new PolarCoordinate(new UnTestedCoordinates(layer, radian));
					boolean valid = false;
					if (map.isSet(location) == null) { //this location may be available for play
						PolarCoordinate adjacent = null;
						for (int i = 0; i <= 7; i++) {
							adjacent = location.getAdjacent(i);
							if (adjacent != null && map.isSet(adjacent) != null) { //this location available for play
								valid = true;
								break;
							}
						}

						GameMap tempMap;
						SearchNode child;
						if (valid) { // this move is valid, make a child node
							try {
								tempMap = (GameMap) map.deepCopy(); //resetting tempMap
								tempMap.removeViewers(); //make sure this map doesn't update the gui
								tempMap.updateAll(new Move(this.max, location));
								child = new SearchNode(tempMap, !this.max);
								this.children.add(child);
							} catch (MoveDuplicateException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (BadCoordinateException e) {
					e.printStackTrace();
				}
			}
		}
	}
}