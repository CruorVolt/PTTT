package logic.TD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

import logic.state.GameState;
import logic.state.TimeSlice;
import polar.game.GameMap;
import polar.game.GameViewer;
import polar.game.Move;
import polar.game.MoveReport;
import polar.game.Player;
import polar.game.PolarCoordinate;
import polar.game.styles.DifferencePlayStyle;
import polar.game.styles.PlayStyle;
import polar.game.styles.TDMinPlayStyle;
import polar.test.TestGame;
import polar.test.TestMap;

public class TrainTD implements GameViewer{
	TD td;
	GameState state;
	Stack<TimeSlice> states;
	GameMap map;
	
	public TrainTD() {
		td = new TD(Player.PLAYER_X, "./src/TDweights.txt");
		state = null;
		states = new Stack<TimeSlice>();

	}
	// train on the given # of games
	public void train(int numGames) {
		int games = 0;
		while(games<numGames) {
			TestGame game = new TestGame();
			map = game.getMap();
			PlayStyle styleO = new DifferencePlayStyle(game, td, Player.PLAYER_X);
			PlayStyle styleX = new TDMinPlayStyle(game, td, Player.PLAYER_O);
			game.setPlayStyles(styleX, styleO);
			game.addViewer(this);
			state = game.getState();
			game.begin();
			games++;
		}
		saveTD(td.getWeights());
	}
	public void saveTD(double weights[][][]) {
		int numFeatures = 9;
		try {
			PrintWriter writer = new PrintWriter(new File("./src/TDweights.txt"));
			for(int l=0;l<2;l++) {
				for(int j=0;j<numFeatures;j++) {
					for(int i=0;i<numFeatures;i++) {
						writer.print(weights[l][j][i]);
						if(i<8)
						writer.print(",");
					}
					writer.println();
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void notifyMove(MoveReport report) {
		TimeSlice slice = new TimeSlice(map.getState());
		states.push(slice);
	}
	@Override
	public void notifyWin(boolean player, Move[] winState) {
		int exp = 0;
		double reward = 0;
		if(player==Player.PLAYER_X) {
			reward = 1;
		}
		while(!states.empty()) {
			td.feedback(states.pop(), Math.pow(2, exp));
			exp--;				// discount reward to incentive shorter games
		}
	}
}
