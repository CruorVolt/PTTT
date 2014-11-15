package logic;

import java.util.ArrayList;
import java.util.HashMap;

import polar.game.BadCoordinateException;
import polar.game.Move;
import polar.game.PolarCoordinate;
import polar.game.UnTestedCoordinates;

/*
 * Shared functions for logic package classes
 */
public class GameLogic {

	/*
	 * Returns a HashMap of the five possible winning 
	 * combinations centered on the input Move. The horizontal
	 * lines start at the given move and extend three moves in
	 * both directions
	 * 
	 * Output key: "horizontal1", "horizontal2" "vertical", "diagonal1", "diagonal2"
	 */
	protected static HashMap<String, ArrayList<PolarCoordinate>> getLines(Move move) throws BadCoordinateException {
		ArrayList<PolarCoordinate> line;
		boolean edge = false;
		HashMap<String, ArrayList<PolarCoordinate>> hash = new HashMap<String, ArrayList<PolarCoordinate>>();
		int verticalLayer = move.getLoc().getX();
		int horizontalLayer = move.getLoc().getY();
		
		//vertical line
		line = new ArrayList<PolarCoordinate>();
		for (int x = 1; x <= 4; x++) {
			line.add(new PolarCoordinate(new UnTestedCoordinates(x, horizontalLayer)));
		}
		hash.put("vertical", line);

		PolarCoordinate location, tempLocation;

		//horizontal line 1: move + three nodes behind
		//horizontal line 2: move + three nodes ahead
		String h = "NONE";
		for (int i = -1; i <= 1; i=i+2) {
			location = move.getLoc();
			line = new ArrayList<PolarCoordinate>();
			line.add(location);
			for (int j = 1; j <= 3; j++) {
				if (i < 0) {
					h = "horizontal1";
					location = location.getAdjacent(2); //ahead
					line.add(location);
				} else {
					h = "horizontal2";
					location = location.getAdjacent(6); //behind
					line.add(location);
				}
			}
			hash.put(h, line);
		}

		//diagonal1: above+ahead / below+behind
		line = new ArrayList<PolarCoordinate>();
		location = move.getLoc();
		do { //traverse to highest point
			tempLocation = location.getAdjacent(1); //above+ahead
			if (tempLocation != null) {
				location = tempLocation;
			}
		} while (tempLocation != null);
		while (location != null) {
			line.add(location);
			location = location.getAdjacent(5); //below+behind
		}
		hash.put("diagonal1", line);
		
		//diagonal2: below+ahead / ahead+behind
		line = new ArrayList<PolarCoordinate>();
		location = move.getLoc();
		do { //traverse to highest point
			tempLocation = location.getAdjacent(3); //below+ahead
			if (tempLocation != null) {
				location = tempLocation;
			}
		} while (tempLocation != null);
		while (location != null) {
			line.add(location);
			location = location.getAdjacent(7); //ahead+behind
		}
		hash.put("diagonal2", line);
		
		return hash;
	}
	/*
	 * Get a list of adjacent moves, use playerOnly = true to
	 * restrict output to moves made by the same player as given move.
	 */
	protected static ArrayList<Move> adjacentMoves(Move move, boolean playerOnly) { 
		Move adj;
		ArrayList<Move> neighbors = new ArrayList<Move>();
		for (int i = 0; i < 8; i++) {
			adj = move.getAdjMove(i);
			if ((adj != null) && (adj.getToken() == move.getToken()) ) {
				if ( !playerOnly || (adj.getToken() == move.getToken()) ) {
					neighbors.add(adj);
				}
			}
		}
		return neighbors;
		
	}
}
