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

import polar.game.*;

public class Classifier {
	
	private static final int TRAINING_SET_SIZE = 100;

	public static boolean classify(Map map, Character player) {
		return false;
	}
	
	// Build a decision tree from the given training data
	public static void train(File trainingSet, Character positivePlayer) {
		
	}
	
	/*
	 * Calculate the entropy of a set of features given the 
	 * a set of end-game states.
	 * 
	 * trainingSet file format expects CSV with headers indicating feature name, 
	 * and the label as the first feature.
	 */
	public static ArrayList<ArrayList<Double>> entropy(String trainingSet) throws IOException {
		HashMap<String, Double> entropyMap = new HashMap<String, Double>();

		BufferedReader br = new BufferedReader(new FileReader(trainingSet));
		String line = br.readLine(); //header line is first

		String[] headers = line.split(", ");
		int numberOfFeatures = headers.length;
		
		//partition each feature into three evenly spaced blocks
		HashMap<String, double[]> minMax = featureMinMax(trainingSet);
		double[] mins = minMax.get("minimums");
		double[] maxs = minMax.get("maximums");
		ArrayList<ArrayList<Double>> partitions = new ArrayList<ArrayList<Double>>();
		partitions.add(null); //Don't need partitions for first feature
		for (int i = 1; i < numberOfFeatures; i++) {
			ArrayList<Double> splits = new ArrayList<Double>();
			double split = (maxs[i] - mins[i]) / 3;
			splits.add(mins[i]);
			splits.add(mins[i] + split);
			splits.add(maxs[i] - split);
			splits.add(maxs[i]);
			partitions.add(splits);
		}

		//Arrays for positive and negative examples of each feature, initizlized to zeroes for each possible partition
		ArrayList<ArrayList<Integer>> positiveExamples = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> negativeExamples = new ArrayList<ArrayList<Integer>>();
		positiveExamples.add(null);
		negativeExamples.add(null);

		for (int y = 1; y < numberOfFeatures; y++) {
			//Initial partitions are all zeroes -- incremented by observation
			ArrayList<Integer> zeroesP = new ArrayList<Integer>();
			ArrayList<Integer> zeroesN = new ArrayList<Integer>();
			for (int j = 0; j < 3; j++) {
                	zeroesP.add(0);
                	zeroesN.add(0);
			}
			positiveExamples.add(zeroesP);
			negativeExamples.add(zeroesN);
		}

		//Build the sets of positive/negative examples for each partition of each feature in the file
		while ((line = br.readLine()) != null) {
			String[] features = line.split(", ");
			Double win = Double.valueOf(features[0]);
			String feature;

			for (int i = 1; i < features.length; i++) {
				
				//Increment examples
				Double featureVal = Double.valueOf(features[i]);
				ArrayList<Double> partition = partitions.get(i);
				if (win > 0) {
					if (featureVal >= partition.get(0) && featureVal < partition.get(1)) { 
						// This value belongs to the smallest partition
						positiveExamples.get(i).set(0, positiveExamples.get(i).get(0) + 1);
					} else if (featureVal >= partition.get(1) && featureVal < partition.get(2)) { 
						// This value belongs to the middle partiton
						positiveExamples.get(i).set(1, positiveExamples.get(i).get(1) + 1);
					} else if (featureVal >= partition.get(2) && featureVal <= partition.get(3)) { 
						// This value belongs to the largest partiton
						positiveExamples.get(i).set(2, positiveExamples.get(i).get(2) + 1);
					} else { //Something went wrong: unexpected feature value
						System.out.println("Unexpected feature value while counting p and n for entropy");
					}
				} else {
					if (featureVal >= partition.get(0) && featureVal < partition.get(1)) { 
						// This value belongs to the smallest partition
						negativeExamples.get(i).set(0, positiveExamples.get(i).get(0) + 1);
					} else if (featureVal >= partition.get(1) && featureVal < partition.get(2)) { 
						// This value belongs to the middle partiton
						negativeExamples.get(i).set(1, positiveExamples.get(i).get(1) + 1);
					} else if (featureVal >= partition.get(2) && featureVal <= partition.get(3)) { 
						// This value belongs to the largest partiton
						negativeExamples.get(i).set(2, positiveExamples.get(i).get(2) + 1);
					} else { //Something went wrong: unexpected feature value
						System.out.println("Unexpected feature value while counting p and n for entropy");
					}
				}
			}
		}
		br.close();
		
		ArrayList<ArrayList<Double>> entropy = new ArrayList<ArrayList<Double>>();
		double entropyXI;
		entropy.add(null); //don't need entropy for first feature
		for (int x = 1; x < headers.length; x++) {
			//calculate entropy of feature at x
			ArrayList<Double> defaults = new ArrayList<Double>();
			defaults.ensureCapacity(3);
			defaults.add(0.0);
			defaults.add(0.0);
			defaults.add(0.0);
			entropy.add(defaults);
			for (int i = 0; i < 3; i++) { //Per feature-partition
				try {
					Double positiveXI = (double) positiveExamples.get(x).get(i);
					Double negativeXI = (double) negativeExamples.get(x).get(i);
					System.out.println("This partition (" + i + ") has " + positiveXI + "+," + negativeXI + "-");
					entropyXI = -( positiveXI / (positiveXI+negativeXI) ) * logBaseK( positiveXI / (positiveXI+negativeXI), 2 ) - (negativeXI / (positiveXI+negativeXI) ) * logBaseK( negativeXI / (positiveXI+negativeXI), 2 );
					entropy.get(x).set(i, entropyXI);
				} catch (ArithmeticException a) {
					a.printStackTrace();
				}
			}
		}
		
		return entropy;
	}
	
	/*
	 * Find the minimum and maximum values of each feature 
	 * in order to find useful (?) partition points.
	 */
	public static HashMap<String, double[]> featureMinMax(String fileName) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		String features[];
		String line;
		try {
			line = br.readLine(); //cycle through header line
			String[] headers = line.split(", ");
	
			double[] minimums = new double[headers.length];
			double[] maximums = new double[headers.length];
			Arrays.fill(minimums, Double.MAX_VALUE);
			Arrays.fill(maximums, 0);
			
			int numberOfFeatures = headers.length;
	
			while ((line = br.readLine()) != null) {
				features = line.split(", ");
				for (int x = 0; x < features.length; x++) {
					if (Double.valueOf(features[x]) > maximums[x]) { //New max value
						maximums[x] = Double.valueOf(features[x]);
					} else if (Double.valueOf(features[x]) < minimums[x]) { //New min value
						minimums[x] = Double.valueOf(features[x]);
					}
				}
			}
			HashMap<String, double[]> minMax = new HashMap<String, double[]>();
			minMax.put("minimums", minimums);
			minMax.put("maximums", maximums);
			return minMax;
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println("IO Problem finding min/max values");
		return null;
	}
	
	public static void generateFeatures(File output, Character positivePlayer) {
		
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

		ArrayList<ArrayList<Object>> games = generateWinStates(TRAINING_SET_SIZE);

		for (ArrayList<Object> gameMap : games) {
			
			Character winner = (Character) gameMap.get(0);
			Map map = (Map) gameMap.get(1);

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
					
					ArrayList<Move> adjacent = GameLogic.adjacentMoves(move, false, false);
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
						HashMap<String, ArrayList<PolarCoordinate>> lines = GameLogic.getLines(move);
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
	
	public static double arrayAverage( ArrayList<Double> list) {
		double sum = 0;
		for (Double d : list) {
			sum += d;
		}
		return sum/ ( (double) list.size() );
	}
	
	public static void writeArray(ArrayList<Double> list, String fileName) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
			out.println(list.toString().substring(1,list.toString().length() - 1));
		}catch (IOException e) {
		}
	}
	
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
	
	public static double logBaseK(double val, double k) {
		return Math.log(val) / Math.log(k);	
	}
	
	public static void main(String[] args) {
		try {
			generateFeatures(null, 'X');
			ArrayList<ArrayList<Double>> entropy = (entropy("./src/training_set.csv"));
			System.out.println(entropy);
			System.out.println("Entropy has " + entropy.size() + " entries");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
