package logic.TD;

import java.util.ArrayList;

import polar.game.GameViewer;
import polar.game.Move;
import polar.game.MoveReport;
import polar.game.Player;
import polar.game.PolarCoordinate;
import polar.game.UnTestedCoordinates;
import polar.game.exceptions.BadCoordinateException;
import polar.game.exceptions.MoveDuplicateException;
import polar.test.TestMap;
import logic.state.TimeSlice;

public class playTD implements GameViewer {
	TD td;
	TestMap test;
	boolean Xwin;
	boolean Owin;
	double learnRate;
	double discount;

	
	public playTD(TD td) {
		this.td = td;
		test = new TestMap();
		Xwin = false;
		Owin = false;
		discount = .5;
		learnRate = .1;
		
	}

	@Override
	public void notifyMove(MoveReport report) {
		PolarCoordinate coord = report.getCoordinates();
		boolean player = report.getMove().getPlayer();
		//if(player==Player.PLAYER_X)
		//	System.out.print("X: ");
	//	else
	//		System.out.print("O: ");
	//	System.out.println(coord);
		try {
			test.updateAll(report);
		} catch (MoveDuplicateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void notifyWin(boolean player, Move[] winState) {
		if(player==Player.PLAYER_X) {
			Xwin = true;
		//	System.out.println("Player X wins.");
		}
		if(player==Player.PLAYER_O) {
		//	System.out.println("Player O wins.");
			Owin = true;
		}
		
	}
	// evaluates the effect of the given movement on the test map, then returns the map to the original state
	public double valueOf(boolean player, UnTestedCoordinates choice)  {
		PolarCoordinate ch = null;
		try {
			ch = new PolarCoordinate(choice);
		} catch (BadCoordinateException e) {
			System.out.println("Error evaluating plays in playTD, bad coordinate used");
			return 0;
		}
		
		Move current = new Move(player,ch);
		
		try {
			test.updateAll(new MoveReport(current));
		} catch (MoveDuplicateException e) {
			System.out.println("Error evaluating plays in PlayTD, duplicate attempt.");
			return 0;
		}
		double val =  td.valueOf(test.getState());
		test.getState().getState(player);
		test.undoMove(current);
		return val;
	}
	// if the choice will be a win, returns 1 otherwise returns 0
	public double reward(boolean player, UnTestedCoordinates choice) {
		PolarCoordinate ch = null;
		try {
			ch = new PolarCoordinate(choice);
		} catch (BadCoordinateException e) {
			System.out.println("Error evaluating plays in playTD, bad coordinate used");
			return 0;
		}
		
		Move current = new Move(player,ch);
		
		try {
			test.updateAll(new MoveReport(current));
		} catch (MoveDuplicateException e) {
			System.out.println("Error evaluating plays in PlayTD, duplicate attempt.");
			return 0;
		}
		double reward = 0;
		if(test.containsWin())
			reward = 1;
		test.undoMove(current);
		return reward;
	}
}
