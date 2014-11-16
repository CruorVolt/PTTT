package logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import polar.game.*;

public class Classifier {
	
	private static final int TRAINING_SET_SIZE = 1000;

	public static boolean classify(Map map, Character player) {
		return false;
	}
	
	public static void train(File output, Character positivePlayer) {
		
		//Truncate old training data
		FileChannel outChan;
		try {
			outChan = new FileOutputStream(new File("./src/training_set.csv"), true).getChannel();
			outChan.truncate(0);
			outChan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<ArrayList<Object>> games = generateWinStates(TRAINING_SET_SIZE);

		System.out.println("HERE ");

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
			System.out.println(features);
		}
		
	}
	
	public static double arrayAverage( ArrayList<Double> list) {
		for (Double d : list) {
		}
		double sum = 0;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i);
		}
		return sum/ ( (double) list.size() );
	}
	
	public static void writeArray(ArrayList<Double> list, String fileName) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
		    out.println(list.toString());
		}catch (IOException e) {
		}
	}
	
	public static ArrayList<ArrayList<Object>> generateWinStates(int setSize) {
		ArrayList<ArrayList<Object>> trainingMaps = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i< setSize; i++) {
			Game game = new Game("Player1", "Player2", null);
			PlayStyle style1 = new RandomPlayStyle('X', game);
			PlayStyle style2 = new RandomPlayStyle('O', game);
			game.setPlayStyles(style1, style2);
			game.begin();

			System.out.println("Game:" + i + " moves:" + game.getMap().getMoves().size() + " winnner:" + game.currentPlayer().getToken());
			
			ArrayList<Object> thisMap = new ArrayList<Object>();
			thisMap.add(game.currentPlayer().getToken());
			thisMap.add(game.getMap());
			
			trainingMaps.add(thisMap);
		}
		return trainingMaps;
		
	}
	
	public static void main(String[] args) {
		train(null, 'X');
	}
}
