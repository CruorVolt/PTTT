package logic;
	
import java.util.ArrayList;

import polar.game.*;
import polar.game.exceptions.BadCoordinateException;
import polar.game.exceptions.MoveDuplicateException;

/*
 * SearchNode instances are nodes in the trees built by minimax and 
 * alpha-beta pruning. They know the current state of the game and 
 * its associated estimated value.
 */
public class SearchNode {
	
	//Track the number of instances to report how many nodes a search examines
	private static int numberOfNodes = 0;
	
	GameMap map; //The state of the game at this node
	int value; //The heuristic evaluations observed deeper in the tree
	boolean max; //Max player (X => true) or min player (O => false)
	ArrayList<SearchNode> children; //All possible child states
	PolarCoordinate newestMove; //The move that creates this node
	
	public SearchNode(GameMap map, boolean player, PolarCoordinate move) {
		this.map = map;
		this.max = player;
		this.newestMove = move;
		children = new ArrayList<SearchNode>();
		numberOfNodes++;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int val) {
		this.value = val;
	}
	
	public GameMap getMap() {
		return this.map;
	}
	
	//Set a move manually: for use during the first move of a game
	public void setMove(PolarCoordinate move) {
		this.newestMove = move;
	}

	//Get the move that resulted in this gamestate
	public PolarCoordinate getMove() { 
		return this.newestMove;
	}
	
	public ArrayList<SearchNode> getChildren() {
		return this.children;
	}
	
	public SearchNode addChild(UnTestedCoordinates location) {
		try {
			SearchNode child;
			GameMap tempMap = map.deepCopy(); 
			PolarCoordinate coords = new PolarCoordinate(location);
			tempMap.removeViewers(); 
			tempMap.updateAll(new MoveReport(new Move(this.max, coords)));
			child = new SearchNode(tempMap, !this.max, coords);
			this.children.add(child);
			return child;
		} catch (MoveDuplicateException e) {
			e.printStackTrace();
			return null;
		} catch (BadCoordinateException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void reset() {
		numberOfNodes = 0;
	}
	
	public static int countNodes() {
		return numberOfNodes;
	}

}