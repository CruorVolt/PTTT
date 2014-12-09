package logic;

import java.util.Random;

import polar.game.*;
import polar.game.exceptions.BadCoordinateException;

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
	public static SearchNode minimax(SearchNode root, int currentDepth, boolean maxPlayer, boolean pruning, Integer alpha, Integer beta) throws BadCoordinateException {
		
		SearchNode bestNode = null, alphaNode = null, betaNode = null;
		Integer bestValue, currentValue;
		
	    //if depth = 0 or node is a terminal node return root for heuristic evaluation
		if ((currentDepth == 0) || (root.getValue() > 800) || (root.getValue() < -800)) {
			return root;
		}
		
		//Make a random move if the map is empty
		if (root.getMap().getMoves().size() == 0) {
			Random random = new Random();
			int x = random.nextInt(4) + 1;
			int y = random.nextInt(12);
			PolarCoordinate location =  new PolarCoordinate(new UnTestedCoordinates(x,y));
			root.setMove(location);
			return root;
		}
		
		//give root correct children
		root.createChildren();
		
		if (maxPlayer) { //maximizing player node evaluation
			bestValue = Integer.MIN_VALUE;
			for (SearchNode child : root.getChildren()) {
				if (!pruning) { //This is standard minimax
					//Compare heuristic value to find the best
					currentValue = minimax(child, currentDepth - 1, false, false, null, null).getValue();
					if (currentValue > bestValue) { //bestValue = max(current, best)
						bestValue= currentValue;
						bestNode = child;
					}
				} else { //This is minimax with alpha-beta pruning
					//Compare heuristic value to find the best and prune if able
					currentValue = minimax(child, currentDepth - 1, false, true, alpha, beta).getValue();
					alpha = Math.max(alpha, currentValue);
					alphaNode = child;
					if (beta < alpha) {
						break;
					}
				}
			}
			if (pruning) { //return alpha and associated move
				return alphaNode;
			} else { //return best value so far and associated move
				return bestNode;
			}
		} else { //minimizing player node evaluation
			bestValue = Integer.MAX_VALUE;
			for (SearchNode child : root.getChildren()) {
				if (!pruning) { //This is standard minimax
					//Compare heuristic value to find the best
					currentValue = minimax(child, currentDepth - 1, true, false, null, null).getValue();
					if (currentValue < bestValue) { //bestValue = min(current, best)
						bestValue = currentValue;
						bestNode = child;
					}
				} else { //This is minimax with alpha-beta pruning
					//Compare heuristic value to find the best and prune if able
					currentValue = minimax(child, currentDepth - 1, true, true, alpha, beta).getValue();
					beta = Math.min(beta, currentValue);
					betaNode = child;
					if (beta < alpha) {
						break;
					}
				}
			}
			if (pruning) { //return beta and associated move
				return betaNode;
			} else { //return best value so far and associated move
				return bestNode;
			}
		}
	}
	
}
