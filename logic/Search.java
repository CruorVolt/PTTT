package logic;

import polar.game.GameMap;
import polar.game.PolarCoordinate;

public class Search {
	
	/*
	 * Get the PolarCoordinate of the best move found by a Minimax search
	 * 
	 * @param root     The root SearchNode of the current tree
	 * @param maxDepth How many layers to continue searching before applying the heuristic function: counts down to zero at the leaves
	 * @param pruning  Whether or not to use alpha-beta pruning to limit the search
	 * @param alpha    The current alpha value or NULL in standard minimax
	 * @param beta     The current beta value or NULL in standard minimax
	 */
	public static PolarCoordinate minimax(SearchNode root, int maxDepth, boolean pruning, Integer alpha, Integer beta) {
		
		/*
		function minimax(node, depth, maximizingPlayer)
	    if depth = 0 or node is a terminal node
	        return the heuristic value of node
	    if maximizingPlayer
	        bestValue := -∞
	        for each child of node
	        	minimax:
	            	val := minimax(child, depth - 1, FALSE)
	            	bestValue := max(bestValue, val)
	           	ab:
              		α := max(α, alphabeta(child, depth - 1, α, β, FALSE))
              		if β ≤ α
                  		break (* β cut-off *)
	        return bestValue or alpha
	    else
	        bestValue := +∞
	        for each child of node
	        	minimax:
	            	val := minimax(child, depth - 1, TRUE)
	            	bestValue := min(bestValue, val)
	            ab:
					β := min(β, alphabeta(child, depth - 1, α, β, TRUE))
					if β ≤ α
						break (* α cut-off *)
	        return bestValue or beta
	        */
		
	}
	
	class SearchNode {
		
		GameMap map; //The state of the game at this node
		int value;   //The heuristic evaluation of the this node's map
		int alpha;   //The current estimated alpha value
		int beta;	 //The current estimated beta value
		boolean max; //Max player (X) or min player (O)
	}

}
