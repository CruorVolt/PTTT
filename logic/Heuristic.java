package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import polar.game.*;

public class Heuristic {
	
	private static final int SCORE_MARKED= 1;
	private static final int SCORE_ADJACENT= 10;
	private static final int SCORE_THREE= 30;
	private static final int SCORE_WIN= 1000;
	
	/*
	 * Evaluates a game state and assigns it an 
	 * overall score assuming the given player is 
	 * the maximizing player and the other player is the minimizing
	 * player.
	 * 
	 * @param map    The current game map
	 * @param maxPlayer The token for the positive player
	 */
	public static int evaluateMinMax(Map map, Character maxPlayer) {
		Character minPlayer = (maxPlayer == 'X') ? 'O' : 'X';
		return evaluate(map, maxPlayer) - evaluate(map, minPlayer);
	}
	
	/*
	 * Evaluates a game state and assigns it a positive
	 * score with respect to the given player.
	 * 
	 * @param map    The current game map
	 * @param player The token for the positive player
	 */
	public static int evaluate(Map map, Character player) {
		int score = 0;
		ArrayList<Move> neighbors;
		ArrayList<Move> moves = new ArrayList<Move>();
		Boolean[] searched = new Boolean[map.getMoves().size()]; //Indexes of moves to skip while searching for threes, wins and adjacents
		ArrayList<ArrayList<PolarCoordinate>> scoredWins = new ArrayList<ArrayList<PolarCoordinate>>(); //Lines of four that cannot be scored again
		ArrayList<ArrayList<PolarCoordinate>> scoredThrees = new ArrayList<ArrayList<PolarCoordinate>>(); //Lines of three that cannot be scored again
		ArrayList<ArrayList<PolarCoordinate>> scoredPairs = new ArrayList<ArrayList<PolarCoordinate>>(); //Lines of two that cannot be scored again
		Move current;

		for (Move m : map.getMoves()) {
			moves.add(m);
		}
		
		for (int line_length = 4; line_length > 0; line_length--) {
			Arrays.fill(searched,  false);

			for (Integer i = 0; i < moves.size(); i++) {
				current = moves.get(i);

				// Check for scores if this move belongs to this player and has
				// not been included in any previous scorings
				if ( (current.getToken() == player) && !(searched[i]) ){

					neighbors = adjacentMoves(current, true);
					if (neighbors.size() > 0) {

						ArrayList<PolarCoordinate> toRemove = new ArrayList<PolarCoordinate>();
						ArrayList<ArrayList<PolarCoordinate>> alreadyScored = new ArrayList<ArrayList<PolarCoordinate>>();
						System.out.println("LOOK");
						switch (line_length) {
						case 4:
							toRemove = isWin(current, map);
							alreadyScored = scoredWins;
							break;
						case 3:
							toRemove = isThree(current, map);
							alreadyScored = scoredThrees;
							break;
						case 2:
							toRemove = isPair(current, neighbors);
							alreadyScored = scoredPairs;
							break;
						case 1:
							break;
						}

						if ((toRemove.size() > 0) && !(alreadyScored.contains(toRemove))) {
							System.out.println("AlreadyScored is " + alreadyScored);
							markScored(toRemove, scoredWins, scoredThrees, scoredPairs);
							System.out.println("SCORING LINE" + toRemove);
							switch (toRemove.size()) {
							case 2:
								score += SCORE_ADJACENT;
								break;
							case 3: 
								score += SCORE_THREE;
								break;
							case 4:
								score += SCORE_WIN;
								break;
							}
							for (PolarCoordinate remove : toRemove) {
								int index = moves.indexOf(remove);
								if (index >= 0) {
									searched[index] = true;
								}
							}
						}
					} else if (line_length == 1) {
						score += scoreMarked(current, map);
					}
				}
			}
		}
		System.out.println("Total score is " + score);
		return score;
	}
	
	/*
	 * Score an occurance of MARKED (no other adjacent nodes by this player, 
	 * score per open winning empty line) for this move if it exists.
	 */
	private static int scoreMarked(Move move, Map map) {
		Character token;
		int score = 0;
		try {
			HashMap<String, ArrayList<PolarCoordinate>> lines = getLines(move);
			for (String key : lines.keySet()) {
				int valid = 0;
				for (PolarCoordinate c : lines.get(key)) {
					token = map.isSet(c);
					if ((token == null) || (token == move.getToken())) { //empty node or this player's move
						valid++;
					} else { //opponent's move
						break;
					}
				}
				// Horizontal line is scored differently since it has more options
				if ((valid == 4 && (key !="hozironal" && key != "horizontal2") ) || (valid >= 5 && (key == "horizontal1" || key == "horizontal2"))) {
					score += SCORE_MARKED;
				}
			}
		} catch (BadCoordinateException e) {
			e.printStackTrace();
		}
		return score;
	}
	
	/*
	 * Score an occurance of WIN (four moves in a line) 
	 * for this move if one exists and get an array of 
	 * the winning moves back, or null.
	 */
	private static ArrayList<PolarCoordinate> isWin(Move move, Map map) {
		boolean win;
		Character token;
		int score = 0;
		try {
			HashMap<String, ArrayList<PolarCoordinate>> lines = getLines(move);
			for (String key : lines.keySet()) {
				win = true;
				for (PolarCoordinate c : lines.get(key)) {
					if (map.isSet(c) != move.getToken()) { //Move not marked by this player
						win = false;
						break;
					}
				}
				if (win) {
					return lines.get(key);
				}
			}
		} catch (BadCoordinateException e) {
			e.printStackTrace();
		}
		return new ArrayList<PolarCoordinate>();
	}

	/*
	 * Score three moves in a line for this move if 
	 * they exist. Get an array of the scoring moves
	 * back or null.
	 */
	private static ArrayList<PolarCoordinate> isThree(Move move, Map map) {
		ArrayList<PolarCoordinate> lineCopy;
		try {
			HashMap<String, ArrayList<PolarCoordinate>> lines = getLines(move);
			for (String key : lines.keySet()) {
				int invalid_count = 0; //number of nodes not marked by player
				boolean valid = true;
				lineCopy = (ArrayList<PolarCoordinate>) lines.get(key).clone();
				for (PolarCoordinate c : lines.get(key)) {
					Character set = map.isSet(c);
					if (move.getToken() != set) { //Move not marked by this player - check if empty
						lineCopy.remove(c);
						if (set != null) {
							valid = false;
						} else if (key != "horizontal1" && key != "horizontal2") {
							if (!( (c.getX() == 1) || (c.getX() == 4) )) { //Cannot be a valid line of three
								valid = false;
							}
						} else {
							int moveY = move.getLoc().getY();
							if (Math.abs(moveY - move.getLoc().getY()) < 3) { //Cannot be a valid line of three
								valid = false;
							}
						}
					}
				}
				if (valid) {
					return lineCopy;
				}
			}
		} catch (BadCoordinateException e) {
			e.printStackTrace();
		}
		return new ArrayList<PolarCoordinate>();
	}
	
	/*
	 * Score two adjacent moves if 
	 * they exist. Get an array of the scoring moves
	 * back or null.
	 */
	private static ArrayList<PolarCoordinate> isPair(Move move, ArrayList<Move> adjacents) {
		ArrayList<PolarCoordinate> pair = new ArrayList<PolarCoordinate>();
		//Select the first available pair to score,
		//if other options are available other calls will take care of them
		if (!adjacents.isEmpty()) {
			pair.add(move.getLoc());
			pair.add(adjacents.get(0).getLoc());
		}
		return pair;
	}
	
	/*
	 * Returns a HashMap of the five possible winning 
	 * combinations centered on the input Move. The horizontal
	 * lines start at the given move and extend three moves in
	 * both directions
	 * 
	 * Output key: "horizontal1", "horizontal2" "vertical", "diagonal1", "diagonal2"
	 */
	private static HashMap<String, ArrayList<PolarCoordinate>> getLines(Move move) throws BadCoordinateException {
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

		PolarCoordinate location;

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
		line.add(move.getLoc());
		location = move.getLoc().getAdjacent(1); //above+ahead
		while (location != null) {
			line.add(location);
			location = location.getAdjacent(1);
		}
		location = move.getLoc().getAdjacent(5); //below+behind
		while (location != null) {
			line.add(location);
			location = location.getAdjacent(5);
		}
		hash.put("diagonal1", line);
		
		//diagonal2: below+ahead / ahead+behind
		line = new ArrayList<PolarCoordinate>();
		line.add(move.getLoc());
		location = move.getLoc().getAdjacent(3); //below+ahead
		while (location != null) {
			line.add(location);
			location = location.getAdjacent(3);
		}
		location = move.getLoc().getAdjacent(7); //ahead+behind
		while (location != null) {
			line.add(location);
			location = location.getAdjacent(7);
		}
		hash.put("diagonal2", line);
		
		return hash;
	}
	
	private static void permuteFours(ArrayList<PolarCoordinate> line, ArrayList<ArrayList<PolarCoordinate>> fours) {
		//Add every ordering of the three-in-a-line to threes
		ArrayList<PolarCoordinate> vector;
		
		if (line.size() == 4) {
			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(0));
			vector.add(line.get(1));
			vector.add(line.get(2));
			vector.add(line.get(3));
			fours.add(vector);

			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(3));
			vector.add(line.get(2));
			vector.add(line.get(1));
			vector.add(line.get(0));
			fours.add(vector);
		}
	}

	private static void permuteThrees(ArrayList<PolarCoordinate> line, ArrayList<ArrayList<PolarCoordinate>> threes) {
		//Add every ordering of the three-in-a-line to threes
		ArrayList<PolarCoordinate> vector;

		if ( (line.size() == 4) || (line.size() == 3) ) {
			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(0));
			vector.add(line.get(1));
			vector.add(line.get(2));
			threes.add(vector);

			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(2));
			vector.add(line.get(1));
			vector.add(line.get(0));
			threes.add(vector);
		}
		
		if (line.size() == 4) {
			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(1));
			vector.add(line.get(2));
			vector.add(line.get(3));
			threes.add(vector);

			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(3));
			vector.add(line.get(2));
			vector.add(line.get(1));
			threes.add(vector);
		}
	}
	
	private static void permutePairs(ArrayList<PolarCoordinate> line, ArrayList<ArrayList<PolarCoordinate>> pairs) {
		//Add every ordering of the two-in-a-line to pairs
		ArrayList<PolarCoordinate> vector;
		if ( (line.size() == 2) || (line.size() == 3) || (line.size() == 4) ) {
			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(0));
			vector.add(line.get(1));
			pairs.add(vector);
			
			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(1));
			vector.add(line.get(0));
			pairs.add(vector);
		}

		if ((line.size() == 3) || (line.size() == 4) ) {
			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(1));
			vector.add(line.get(2));
			pairs.add(vector);
			
			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(2));
			vector.add(line.get(1));
			pairs.add(vector);
		}

		if (line.size() == 4) {
			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(2));
			vector.add(line.get(3));
			pairs.add(vector);
			
			vector = new ArrayList<PolarCoordinate>();
			vector.add(line.get(3));
			vector.add(line.get(2));
			pairs.add(vector);
		}
	}

	/*
	 * Mark a line that has already been scored to prevent 
	 * duplicate scoring: modifies the passed in array references
	 */
	private static void markScored(ArrayList<PolarCoordinate> line, ArrayList<ArrayList<PolarCoordinate>> fours, 
			ArrayList<ArrayList<PolarCoordinate>> threes, ArrayList<ArrayList<PolarCoordinate>> pairs) {

		switch (line.size()) {
		case 4:
			permuteFours(line, fours);
		case 3:
			permuteThrees(line, threes);
		case 2:
			permutePairs(line, pairs);
			break;
		default:
			System.out.println("PROBLEM: A scoring line was reported, but it was " + line.size() + " nodes long");
		}
	}

	/*
	 * Get a list of adjacent moves, use playerOnly = true to
	 * restrict output to moves made by the same player as given move.
	 */
	private static ArrayList<Move> adjacentMoves(Move move, boolean playerOnly) { 
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
