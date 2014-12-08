package logic;
	
import java.util.ArrayList;

import polar.game.*;
import polar.game.exceptions.BadCoordinateException;
import polar.game.exceptions.MoveDuplicateException;

public class SearchNode {
	
	private static int numberOfNodes = 0;
	
	GameMap map; //The state of the game at this node
	int value;   //The heuristic evaluation of map
	boolean max; //Max player (X => true) or min player (O => false)
	ArrayList<SearchNode> children; //All possible child states
	PolarCoordinate newestMove; //The move that creates this node
	SearchNode argmax; //Decision branch
	
	public SearchNode(GameMap map, boolean player, PolarCoordinate move) {
		this.map = map;
		this.value = Heuristic.evaluateMinMax(this.map, true);
		this.max = player;
		this.newestMove = move;
		children = null;
		numberOfNodes++;
	}
	
	public int getValue() {
		return value;
	}
	
	public GameMap getMap() {
		return this.map;
	}
	
	//Set a move manually: for use during the first move of a game
	public void setMove(PolarCoordinate move) {
		this.newestMove = move;
	}
	
	public PolarCoordinate getMove() { //Get the move that resulted in this gamestate
		return this.newestMove;
	}
	
	public ArrayList<SearchNode> getChildren() {
		return this.children;
	}
	
	public void setArgMax(SearchNode node) {
		this.argmax = node;
	}
	
	public SearchNode getMax() {
		return this.argmax;
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
								tempMap.updateAll(new MoveReport(new Move(this.max, location)));
								child = new SearchNode(tempMap, !this.max, location);
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
	
	public static void reset() {
		numberOfNodes = 0;
	}
	
	public static int countNodes() {
		return numberOfNodes;
	}

}