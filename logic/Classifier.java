package logic;

import java.io.File;
import java.util.ArrayList;

import polar.game.*;

public class Classifier {
	
	private static final int TRAINING_SET_SIZE = 1000;

	public static boolean classify(Map map, Character player) {
		return false;
	}
	
	public static void train(File output) {
		
		ArrayList<Map> games = generateWinStates(TRAINING_SET_SIZE);
		
	}
	
	public static ArrayList<Map> generateWinStates(int setSize) {
		ArrayList<Map> trainingMaps = new ArrayList<Map>();
		for (int i = 0; i<= setSize; i++) {
			Game game = new Game("Player1", "Player2", null);
			PlayStyle style1 = new RandomPlayStyle('X', game);
			PlayStyle style2 = new RandomPlayStyle('O', game);
			game.setPlayStyles(style1, style2);
			game.begin();

			System.out.println("Game:" + i + " moves:" + game.getMap().getMoves().size() + " winnner:" + game.currentPlayer().getToken());
			
			trainingMaps.add(game.getMap());
		}
		return trainingMaps;
		
	}
	
	public static void main(String[] args) {
	}
}
