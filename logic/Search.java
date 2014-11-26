package logic;

import java.util.HashMap;
import java.util.Random;

import polar.game.*;

public class Search {
	
	/*
	 * Get the PolarCoordinate of the best move found by a Minimax search
	 * 
	 * @param root         The root SearchNode of the current tree
	 * @param currentDepth How many layers to continue searching before applying the heuristic function: counts down to zero at the leaves
	 * @param maxPlayer    Whether this call is for the maximimizing or minimizing player
	 * @param pruning      Whether or not to use alpha-beta pruning to limit the search
	 * @param alpha        The current alpha value or NULL in standard minimax
	 * @param beta         The current beta value or NULL in standard minimax
	 */
	
	//function minimax(node, depth, maximizingPlayer)
	public static HashMap<Integer, PolarCoordinate> minimax(SearchNode root, int currentDepth, boolean maxPlayer, boolean pruning, Integer alpha, Integer beta) throws BadCoordinateException {
		
		HashMap<Integer, PolarCoordinate> bestHash, alphaHash, betaHash;
		Integer bestValue, currentValue;
		PolarCoordinate bestMove = null, currentMove = null, alphaMove = null, betaMove = null;
		
	    //if depth = 0 or node is a terminal node
		if ((currentDepth == 0) || (root.getMap().containsWin())) {
	        //return the heuristic value of node
			bestHash = new HashMap<Integer, PolarCoordinate>();
			bestHash.put(Heuristic.evaluateMinMax(root.getMap(), true), root.getMove()); 
			return bestHash;
		}
		
		//Make a random move if the map is empty
		if (root.getMap().getMoves().size() == 0) {
			Random random = new Random();
			//0(inclusive) value(exclusive)
			int x = random.nextInt(4) + 1;
			int y = random.nextInt(12);
			PolarCoordinate location =  new PolarCoordinate(new UnTestedCoordinates(x,y));
			bestHash = new HashMap<Integer, PolarCoordinate>();
			bestHash.put(0, location); 
			return bestHash;
		}
		
		//give root correct children
		root.createChildren();
		
	    //if maximizingPlayer
		if (maxPlayer) {
	        //bestValue := -∞
			bestValue = Integer.MIN_VALUE;
	        //for each child of node
			for (SearchNode child : root.getChildren()) {
	        	//minimax:
				if (!pruning) { //This is standard minimax
	            	//val := minimax(child, depth - 1, FALSE)
					currentValue = (Integer) minimax(child, currentDepth - 1, false, false, null, null).keySet().toArray()[0];
					currentMove = child.getMove();
	            	//bestValue := max(bestValue, val)
					if (currentValue > bestValue) {
						bestValue= currentValue;
						bestMove = currentMove;
					}
	           	//ab:
				} else { //This is minimax with alpha-beta pruning
              		//α := max(α, alphabeta(child, depth - 1, α, β, FALSE))
					currentValue = (Integer) minimax(child, currentDepth - 1, false, true, alpha, beta).keySet().toArray()[0];
					alpha = Math.max(alpha, currentValue);
					//TODO: AS WITH STANDARD ABOVE
					alphaMove = child.getMove();
					if (beta < alpha) {
						break;
					}
				}
			}
	        //return bestValue or alpha
			if (pruning) {
				alphaHash = new HashMap<Integer, PolarCoordinate>();
				alphaHash.put(alpha, alphaMove);
				return alphaHash;
			} else {
				bestHash = new HashMap<Integer, PolarCoordinate>();
				bestHash.put(bestValue, bestMove);
				return bestHash;
			}
	    //else
		} else {
	        //bestValue := +∞
			bestValue = Integer.MAX_VALUE;
	        //for each child of node
			for (SearchNode child : root.getChildren()) {
	        	//minimax:
				if (!pruning) { //This is standard minimax
	            	//val := minimax(child, depth - 1, TRUE)
					currentValue = (Integer) minimax(child, currentDepth - 1, true, false, null, null).keySet().toArray()[0];
	            	//bestValue := min(bestValue, val)
					if (currentValue < bestValue) {
						bestValue = currentValue;
						bestMove = child.getMove();
					}
	            //ab:
				} else { //This is minimax with alpha-beta pruning
					//β := min(β, alphabeta(child, depth - 1, α, β, TRUE))
					currentValue = (Integer) minimax(child, currentDepth - 1, true, true, alpha, beta).keySet().toArray()[0];
					beta = Math.min(beta, currentValue);
					betaMove = child.getMove();
					if (beta < alpha) {
						break;
					}
				}
			}
	        //return bestValue or beta
			if (pruning) {
				betaHash = new HashMap<Integer, PolarCoordinate>();
				betaHash.put(beta, betaMove);
				return betaHash;
			} else {
				bestHash = new HashMap<Integer, PolarCoordinate>();
				bestHash.put(bestValue, bestMove);
				return bestHash;
			}
		}
	}
	
}
