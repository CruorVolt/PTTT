package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import polar.game.*;
import polar.game.exceptions.BadCoordinateException;

public class Heuristic extends SupportFunctions {
	
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
	public static int evaluateMinMax(GameMap map, boolean maxPlayer) {
		boolean minPlayer = !maxPlayer;
		return evaluate(map, maxPlayer) - evaluate(map, minPlayer);
	}
	
	/*
	 * Evaluates a game state and assigns it a positive
	 * score with respect to the given player.
	 * 
	 * @param map    The current game map
	 * @param player The token for the positive player
	 */
	public static int evaluate(GameMap map, boolean player) {
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
				if ( (current.getPlayer() == player) && !(searched[i]) ){

					neighbors = SupportFunctions.adjacentMoves(current, true, true);
					if (neighbors.size() > 0) {

						ArrayList<PolarCoordinate> toRemove = new ArrayList<PolarCoordinate>();
						ArrayList<ArrayList<PolarCoordinate>> alreadyScored = new ArrayList<ArrayList<PolarCoordinate>>();
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
							toRemove = isPair(current, map);
							alreadyScored = scoredPairs;
							break;
						case 1:
							break;
						}

						if ((toRemove.size() > 0) && !(containsMatch(alreadyScored,toRemove))) {
							markScored(toRemove, scoredWins, scoredThrees, scoredPairs);
							switch (toRemove.size()) {
							case 2:
								int firstX = toRemove.get(0).getX();
								int secondX = toRemove.get(1).getX();
								if (firstX == secondX) { //horizontal pair: special scoring
									int firstY = toRemove.get(0).getY();
									int secondY = toRemove.get(1).getY();
									//System.out.println("line of two: " + toRemove);
									//The horizontal directions for adjacency are 2 and 6
									PolarCoordinate firstOffset = (toRemove.get(0).getAdjacent(2) == toRemove.get(1)) ? toRemove.get(1).getAdjacent(2) : toRemove.get(0).getAdjacent(2);
									PolarCoordinate secondOffset = (toRemove.get(0).getAdjacent(6) == toRemove.get(1)) ? toRemove.get(1).getAdjacent(6) : toRemove.get(0).getAdjacent(6);
									Character currentToken = map.isSet(toRemove.get(0));
									//System.out.println("outliers: " + firstX + "," + firstOffset + " and " + firstX + "," + secondOffset);
									//try {
										Character lower = map.isSet(firstOffset);
										Character upper = map.isSet(secondOffset);
										if ( ((lower != null) && (lower != currentToken)) || ((upper != null) && (upper != currentToken))) {
											//System.out.println("Found a blocked end");
											score += SCORE_ADJACENT; //one pair scoring given since one end is blocked
										} else {
											//System.out.println("Found an open end: extra points!");
											score += (SCORE_ADJACENT * 3); //extra pair scoring since both ends are open
										}
									//} catch (BadCoordinateException b) {
										//System.out.println("Offset is off the board: Actual points: " + toRemove + ". Offsets: " + firstX + "," + firstOffset + " and " + firstX + "," + secondOffset);
									//}

								} else { //vertical or diagonal pair: normal scoring
									score += SCORE_ADJACENT;
								}
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
		return score;
	}
	
	/*
	 * Score an occurance of MARKED (no other adjacent nodes by this player, 
	 * score per open winning empty line) for this move if it exists.
	 */
	private static int scoreMarked(Move move, GameMap map) {
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
	private static ArrayList<PolarCoordinate> isWin(Move move, GameMap map) {
		boolean win;
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
	private static ArrayList<PolarCoordinate> isThree(Move move, GameMap map) {
		ArrayList<PolarCoordinate> lineCopy;
		try {
			HashMap<String, ArrayList<PolarCoordinate>> lines = getLines(move);
			for (String key : lines.keySet()) {
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
							if (Math.abs(moveY - c.getY()) < 3) { //Cannot be a valid line of three
								valid = false;
							}
						}
					}
				}
				if ( (valid) && (lineCopy.size() == 3) ){
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
	private static ArrayList<PolarCoordinate> isPair(Move move, GameMap map) {
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
						invalid_count++;
						if (invalid_count > 2) { //too many empty nodes in this line
							valid = false;
						} else if (set != null) { //opponent marked node
							valid = false;
						}
					}
				}
				if (valid && lineCopy.size() == 2) {
					return lineCopy;
				}
			}
		} catch (BadCoordinateException e) {
			e.printStackTrace();
		}
		return new ArrayList<PolarCoordinate>();
	}
	
	
	private static void permuteFours(ArrayList<PolarCoordinate> line, ArrayList<ArrayList<PolarCoordinate>> fours) {
		//Add every ordering of the four-in-a-line to fours
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
		}
	}

	/*
	 * Compare the candidate list to each element of collection input,
	 * return true if a match is found (matches ignore ordering)
	 */
	private static boolean containsMatch(ArrayList<ArrayList<PolarCoordinate>> collection, ArrayList<PolarCoordinate> candidate) {
		for (Collection c : collection) {
			if (c.containsAll(candidate) && candidate.containsAll(c)) {
				return true;
			}
		}
		return false;
	}

}
