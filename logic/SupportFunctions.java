package logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import polar.game.BadCoordinateException;
import polar.game.Game;
import polar.game.GreedyPlayStyle;
import polar.game.GameMap;
import polar.game.Move;
import polar.game.PlayStyle;
import polar.game.PolarCoordinate;
import polar.game.UnTestedCoordinates;

/*
 * Shared functions for logic package classes
 */
public class SupportFunctions {

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
		HashMap<String, ArrayList<PolarCoordinate>> hash = new HashMap<String, ArrayList<PolarCoordinate>>();
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
	protected static ArrayList<Move> adjacentMoves(Move move, boolean playerOnly, boolean markedOnly) { 
		Move adj;
		ArrayList<Move> neighbors = new ArrayList<Move>();
		for (int i = 0; i < 8; i++) {
			adj = move.getAdjMove(i);
			if (adj != null) {
				if ( !playerOnly || (adj.getToken() == move.getToken()) ) {
					neighbors.add(adj);
				}
			} else if (!markedOnly) {
				neighbors.add(adj);
			}
		}
		return neighbors;
	}

	/*
	 * Generate a set of game win-states and their associated features,
	 * stored in output file csv.
	 */
	public static void generateFeatures(File output, Character positivePlayer, int training_set_size) {
		
		//Truncate old training data
		FileChannel outChan;
		try {
			outChan = new FileOutputStream(new File("./src/training_set.csv"), true).getChannel();
			outChan.truncate(0);
			outChan.close();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./src/training_set.csv", true))); 
		    out.println(
		    		"win, " + 
		    		"player_avg_adjacent_player, " + 
			   		"player_avg_adjacent_opponent, " + 
			   		"player_avg_adjacent_empty, " + 
			   		"opponent_avg_adjacent_player, " + 
			   		"opponent_avg_adjacent_opponent, " + 
			   		"opponent_avg_adjacent_empty, " + 
			   		"player_avg_vert_player_marked, " + 
			   		"player_avg_vert_opponent_marked, " + 
			   		"player_avg_horiz_player_marked, " + 
			   		"player_avg_horiz_opponent_marked, " + 
			   		"player_avg_diag_player_marked, " + 
			   		"player_avg_diag_opponent_marked, " + 
			   		"opponent_avg_vert_player_marked, " + 
			   		"opponent_avg_vert_opponent_marked, " + 
			   		"opponent_avg_horiz_player_marked, " + 
			   		"opponent_avg_horiz_opponent_marked, " + 
			   		"opponent_avg_diag_player_marked, " + 
			   		"opponent_avg_diag_opponent_marked" 
			   	);
		    out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<ArrayList<Object>> games = generateWinStates(training_set_size);

		for (ArrayList<Object> gameMap : games) {
			
			Character winner = (Character) gameMap.get(0);
			GameMap map = (GameMap) gameMap.get(1);

			ArrayList<Double> player_adjacent_player_array = new ArrayList<Double>();
			ArrayList<Double> player_adjacent_opponent_array = new ArrayList<Double>();
			ArrayList<Double> player_adjacent_empty_array = new ArrayList<Double>();
			ArrayList<Double> opponent_adjacent_player_array = new ArrayList<Double>();
			ArrayList<Double> opponent_adjacent_opponent_array = new ArrayList<Double>();
			ArrayList<Double> opponent_adjacent_empty_array = new ArrayList<Double>();

			//Line features arrays
			ArrayList<Double> player_vert_player_marked_array = new ArrayList<Double>();
			ArrayList<Double> player_vert_opponent_marked_array = new ArrayList<Double>();
			ArrayList<Double> player_horiz_player_marked_array = new ArrayList<Double>();
			ArrayList<Double> player_horiz_opponent_marked_array = new ArrayList<Double>();
			ArrayList<Double> player_diag_player_marked_array = new ArrayList<Double>();
			ArrayList<Double> player_diag_opponent_marked_array = new ArrayList<Double>();
			ArrayList<Double> opponent_vert_player_marked_array = new ArrayList<Double>();
			ArrayList<Double> opponent_vert_opponent_marked_array = new ArrayList<Double>();
			ArrayList<Double> opponent_horiz_player_marked_array = new ArrayList<Double>();
			ArrayList<Double> opponent_horiz_opponent_marked_array = new ArrayList<Double>();
			ArrayList<Double> opponent_diag_player_marked_array = new ArrayList<Double>();
			ArrayList<Double> opponent_diag_opponent_marked_array = new ArrayList<Double>();

			for (Move move : map.getMoves()) {
				
				//Adjacent features for this node
				double player_adjacent_player = 0;
				double player_adjacent_opponent = 0;
				double player_adjacent_empty = 0;
				double opponent_adjacent_player = 0;
				double opponent_adjacent_opponent = 0;
				double opponent_adjacent_empty = 0;

				//Line features for this node
				double player_vert_player_marked = 0;
				double player_vert_opponent_marked = 0;
				double player_horiz_player_marked = 0;
				double player_horiz_opponent_marked = 0;
				double player_diag_player_marked = 0;
				double player_diag_opponent_marked = 0;
				double opponent_vert_player_marked = 0;
				double opponent_vert_opponent_marked = 0;
				double opponent_horiz_player_marked = 0;
				double opponent_horiz_opponent_marked = 0;
				double opponent_diag_player_marked = 0;
				double opponent_diag_opponent_marked = 0;

				if (map.isSet(move.getLoc()) != null) { //marked move
					
					ArrayList<Move> adjacent = SupportFunctions.adjacentMoves(move, false, false);
					double moves = 0;
					if (move.getToken() == positivePlayer) {
						for (Move m : adjacent) { // count for player-nodes
							moves+=1;
							if (m != null) {
								if (move.getToken() == m.getToken()) {
									player_adjacent_player+=1;
								} else {
									player_adjacent_opponent+=1;
								}
							} else {
								player_adjacent_empty+=1;
							}
						}
						player_adjacent_player = player_adjacent_player / moves;
						player_adjacent_opponent = player_adjacent_opponent / moves;
						player_adjacent_empty = player_adjacent_empty / moves;

					} else { // count for opponent nodes
						for (Move m : adjacent) {
							moves+=1;
							if (m != null) {
								if (move.getToken() == m.getToken()) {
									opponent_adjacent_player+=1;
								} else {
									opponent_adjacent_opponent+=1;
								}
							} else {
								opponent_adjacent_empty+=1;
							}
						}
						opponent_adjacent_player = opponent_adjacent_player / moves;
						opponent_adjacent_opponent = opponent_adjacent_opponent / moves;
						opponent_adjacent_empty = opponent_adjacent_empty / moves;
					}


					
					//score lines
					try {
						HashMap<String, ArrayList<PolarCoordinate>> lines = SupportFunctions.getLines(move);
						Character moveToken = move.getToken();
						for (String key : lines.keySet()) {
							ArrayList<PolarCoordinate> line = lines.get(key);
							if (key == "diagonal1" || key == "diagonal2") {
								for (PolarCoordinate c : line) {
									Character cToken = map.isSet(c);
									if (cToken == null) { //empty move
										
									} else if (cToken == moveToken) { //player move
										if(moveToken == positivePlayer) {
											player_diag_player_marked += 1;
										} else {
											opponent_diag_player_marked += 1;
										}
									} else { //opponent move
										if(moveToken == positivePlayer) {
											player_diag_opponent_marked += 1;
										} else {
											opponent_diag_opponent_marked += 1;
										}

									}
								}
							} else if (key == "vertical") {
								for (PolarCoordinate c : line) {
									Character cToken = map.isSet(c);
									if (cToken == null) { //empty move
										
									} else if (cToken == moveToken) { //player move
										if(moveToken == positivePlayer) {
											player_vert_player_marked += 1;
										} else {
											opponent_vert_player_marked += 1;
										}

									} else { //opponent move
										if(moveToken == positivePlayer) {
											player_vert_opponent_marked += 1;
										} else {
											opponent_vert_opponent_marked += 1;
										}

									}
								}
							} else if (key == "horizontal1" || key =="horizontal2") {
								for (PolarCoordinate c : line) {
									Character cToken = map.isSet(c);
									if (cToken == null) { //empty move
										
									} else if (cToken == moveToken) { //player move
										if(moveToken == positivePlayer) {
											player_horiz_player_marked += 1;
										} else {
											opponent_horiz_player_marked += 1;
										}
									} else { //opponent move
										if(moveToken == positivePlayer) {
											player_horiz_opponent_marked += 1;
										} else {
											opponent_horiz_opponent_marked += 1;
										}
									}
								}
							}
						}
					} catch (BadCoordinateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

				//This move is done here: add features to array
                player_adjacent_player_array.add(player_adjacent_player);
                player_adjacent_opponent_array.add(player_adjacent_opponent);
                player_adjacent_empty_array.add(player_adjacent_empty);
                opponent_adjacent_player_array.add(opponent_adjacent_player);
                opponent_adjacent_opponent_array.add(opponent_adjacent_opponent);
                opponent_adjacent_empty_array.add(opponent_adjacent_empty);

                //Line features arrays
                player_vert_player_marked_array.add(player_vert_player_marked);
                player_vert_opponent_marked_array.add(player_vert_opponent_marked);
                player_horiz_player_marked_array.add(player_horiz_player_marked);
                player_horiz_opponent_marked_array.add(player_horiz_opponent_marked);
                player_diag_player_marked_array.add(player_diag_player_marked);
                player_diag_opponent_marked_array.add(player_diag_opponent_marked);
                opponent_vert_player_marked_array.add(opponent_vert_player_marked);
                opponent_vert_opponent_marked_array.add(opponent_vert_opponent_marked);
                opponent_horiz_player_marked_array.add(opponent_horiz_player_marked);
                opponent_horiz_opponent_marked_array.add(opponent_horiz_opponent_marked);
                opponent_diag_player_marked_array.add(opponent_diag_player_marked);
                opponent_diag_opponent_marked_array.add(opponent_diag_opponent_marked);
			}
			//all moves for the map are done here: calculate average features for the map and output
			
			//Marked Node Averages
			double player_avg_adjacent_player = arrayAverage(player_adjacent_player_array);
			double player_avg_adjacent_opponent = arrayAverage(player_adjacent_opponent_array);
			double player_avg_adjacent_empty = arrayAverage(player_adjacent_empty_array);
			double opponent_avg_adjacent_player = arrayAverage(opponent_adjacent_player_array);
			double opponent_avg_adjacent_opponent = arrayAverage(opponent_adjacent_opponent_array);
			double opponent_avg_adjacent_empty = arrayAverage(opponent_adjacent_empty_array);
			
			// Node-line Averages
			double player_avg_vert_player_marked = arrayAverage(player_vert_player_marked_array);
			double player_avg_vert_opponent_marked = arrayAverage(player_vert_opponent_marked_array);
			double player_avg_horiz_player_marked = arrayAverage(player_horiz_player_marked_array);
			double player_avg_horiz_opponent_marked = arrayAverage(player_horiz_opponent_marked_array);
			double player_avg_diag_player_marked = arrayAverage(player_diag_player_marked_array);
			double player_avg_diag_opponent_marked = arrayAverage(player_diag_opponent_marked_array);
			double opponent_avg_vert_player_marked = arrayAverage(opponent_vert_player_marked_array);
			double opponent_avg_vert_opponent_marked = arrayAverage(opponent_vert_opponent_marked_array);
			double opponent_avg_horiz_player_marked = arrayAverage(opponent_horiz_player_marked_array);
			double opponent_avg_horiz_opponent_marked = arrayAverage(opponent_horiz_opponent_marked_array);
			double opponent_avg_diag_player_marked = arrayAverage(opponent_diag_player_marked_array);
			double opponent_avg_diag_opponent_marked = arrayAverage(opponent_diag_opponent_marked_array);
			
			ArrayList<Double> features = new ArrayList<Double>();
			if (winner == positivePlayer) {
				features.add(1.0);
			} else {
				features.add(-1.0);
			}
			features.add(player_avg_adjacent_player);
			features.add(player_avg_adjacent_opponent);
			features.add(player_avg_adjacent_empty);
			features.add(opponent_avg_adjacent_player);
			features.add(opponent_avg_adjacent_opponent);
			features.add(opponent_avg_adjacent_empty);
			
			// Node-line Averages
			features.add(player_avg_vert_player_marked);
			features.add(player_avg_vert_opponent_marked);
			features.add(player_avg_horiz_player_marked);
			features.add(player_avg_horiz_opponent_marked);
			features.add(player_avg_diag_player_marked);
			features.add(player_avg_diag_opponent_marked);
			features.add(opponent_avg_vert_player_marked);
			features.add(opponent_avg_vert_opponent_marked);
			features.add(opponent_avg_horiz_player_marked);
			features.add(opponent_avg_horiz_opponent_marked);
			features.add(opponent_avg_diag_player_marked);
			features.add(opponent_avg_diag_opponent_marked);
			
			writeArray(features, "./src/training_set.csv");
		}
	}
	
	/*
	 * Player -setSize- games and get a collection of the 
	 * the maps back in the format [winning_player_char, map]
	 */
	public static ArrayList<ArrayList<Object>> generateWinStates(int setSize) {
		ArrayList<ArrayList<Object>> trainingMaps = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i< setSize; i++) {
			Game game = new Game("Player1", "Player2", null);
			PlayStyle style1 = new GreedyPlayStyle('X', game);
			PlayStyle style2 = new GreedyPlayStyle('O', game);
			game.setPlayStyles(style1, style2);
			game.begin();

			ArrayList<Object> thisMap = new ArrayList<Object>();
			thisMap.add(game.currentPlayer().getToken());
			thisMap.add(game.getMap());
			
			trainingMaps.add(thisMap);
			System.out.println("Generated map " + i);
		}
		return trainingMaps;
		
	}
	
	/*
	 * Output an array as a row in the specified csv file
	 */
	public static void writeArray(ArrayList<Double> list, String fileName) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
			out.println(list.toString().substring(1,list.toString().length() - 1));
		}catch (IOException e) {
		}
	}
	
	public static String[] getFeatures(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = br.readLine(); //header line is first
		br.close();
		return Arrays.copyOfRange(line.split(", "),1, line.length()); // strip first feature
	}
	
	/*
	 * get the average value from an array of doubles
	 */
	public static double arrayAverage( ArrayList<Double> list) {
		double sum = 0;
		for (Double d : list) {
			sum += d;
		}
		return sum/ ( (double) list.size() );
	}
}
