package logic;

import java.util.ArrayList;
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
	public static SearchNode minimax(SearchNode root, int currentDepth, boolean maxPlayer, boolean pruning, Integer alpha, Integer beta) throws BadCoordinateException {
		Integer bestValue, currentValue;
		SearchNode bestNode = null, alphaNode = null, betaNode = null;

		//If the maximum depth has been reached or the map contains a win then return a heuristic evaluation
		if ( (currentDepth == 0) || (Math.abs(root.getValue()) > 500) ) {
			root.setValue(Heuristic.evaluateMinMax(root.getMap(), true));
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
		
		ArrayList<UnTestedCoordinates> availableChildren = Status.getValidPositions(root.getMap().getMoves());
		
		if (maxPlayer) {
			if (!pruning) { //this is standard minimax
				bestValue = Integer.MIN_VALUE;
				for (UnTestedCoordinates coords : availableChildren) {
					SearchNode child = root.addChild(coords);
					currentValue = minimax(child, currentDepth - 1, false, false, null, null).getValue();
					if (currentValue > bestValue) {
						bestValue = currentValue;
						bestNode = child;
					}
				}
				bestNode.setValue(bestValue);
				return bestNode;
			} else { //this is minimax with alpha-beta pruning
				for (UnTestedCoordinates coords : availableChildren) {
					//Compare heuristic value to find the best and prune if able
					SearchNode child = root.addChild(coords);
					//minimax(root, currentDepth, maxPlayer, pruning, alpha, beta) 
					currentValue = minimax(child, currentDepth - 1, false, true, alpha, beta).getValue();
					if (currentValue > alpha){ //current is largest - update alpha
						alpha = currentValue;
						alphaNode = child;
						alphaNode.setValue(currentValue);
					}

					if (beta <= alpha) {
						break;
					}
				}
				return alphaNode;
			}
			
		} else {
			if (!pruning) { //this is standard minimax
				bestValue = Integer.MAX_VALUE;
				for (UnTestedCoordinates coords : availableChildren) {
					SearchNode child = root.addChild(coords);
					currentValue = minimax(child, currentDepth - 1, true, false, null, null).getValue();
					if (currentValue < bestValue) {
						bestValue = currentValue;
						bestNode = child;
					}
				}
				bestNode.setValue(bestValue);
				return bestNode;
			} else { //this is minimax with alpha-beta pruning
				for (UnTestedCoordinates coords : availableChildren) {
					SearchNode child = root.addChild(coords);
					//Compare heuristic value to find the best and prune if able
					currentValue = minimax(child, currentDepth - 1, true, true, alpha, beta).getValue();
					
					if (currentValue < beta) {
						beta = currentValue;
						betaNode = child;
						betaNode.setValue(currentValue);
					}
					
					if (beta <= alpha) {
						break;
					}
				}
				return betaNode;
			}
		}
	}
	
}
