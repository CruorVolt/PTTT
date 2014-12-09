package polar.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import logic.TD.TD;
import polar.game.*;
import polar.game.styles.*;

public class PerformanceEval {
	static PrintWriter writer;
	static TD td;
	
	public static void main(String[] args) {
		int tests = 2;
		try {
			writer = new PrintWriter(new File("performance.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		td = new TD(Player.PLAYER_O, "TDweights.txt");
		Game game = new Game();
		PlayStyle[] style = new PlayStyle[5];
		style[0] = new SearchPlayStyle(false, game, false);
		style[1] = new SearchPlayStyle(false, game, false);
		style[2] = new GreedyPlayStyle(false, game);
		//style[3] = new ClassifierPlayStyle();
		style[3] = new DifferencePlayStyle(game, td, false);
		style[4] = new RandomPlayStyle();
		saveTable(style);
		for(int i=0;i<5;i++) {
			writer.print(style[i]);
			if(i==4)
				td = new TD(Player.PLAYER_X, "TDweights.txt");
			for( int j=0;j<5;j++) {
				int size = style[j].toString().length();
				if(j>i) {
					run(tests, i, j, size);
				}
				else {
					save("X", size);
				}
			}
			writer.println();
			
		}	
		writer.close();
	}
	public static void run(int tests, int x, int o, int format) {
		int Xwins =0;
		for(int i=0;i<tests;i++) {
			Game game = new Game();
			PlayStyle styleX = getInstance(x, game, Player.PLAYER_X);
			PlayStyle styleO = getInstance(o, game, Player.PLAYER_O);
			game.setPlayStyles(styleX, styleO);
			game.begin();
			if(game.getState().hasWon(Player.PLAYER_X))
				Xwins++;
		}
		saveMetric(Xwins, tests, format);
	}
	public static void saveMetric(int wins, int tests, int size) {
		String write = "";
		write += ((double)wins)/tests;
		save(write, size);
	}
	public static void saveTable(PlayStyle[] styles) {
		writer.print("| Player Opponent |");
		for(PlayStyle style : styles)
		saveStyle(style);
		writer.println();
	}
	public static void saveStyle(PlayStyle style) {
		String write = "";
		write += style.toString();
		save(write, 30);
	}
	public static void save(String write, int length) {
		while(write.length()<length) {
			if(write.length() % 2==1)
				write += " ";
			else {
				write = " "+write+" ";
			}
		}
		writer.print(write+"|");
	}
	public static PlayStyle getInstance(int type, Game game, boolean player) {
		switch(type) {
		case 0:
			return new SearchPlayStyle(player, game, false);
		case 1:
			return new SearchPlayStyle(player, game, true);
		case 2:
			return new GreedyPlayStyle(player, game);
		case 3:
			return new DifferencePlayStyle(game, td, player);
		case 4:
			return new RandomPlayStyle();
		}
		return null;
	}
}