package polar.game;

import java.util.ArrayList;

public class ManualViewer implements GameViewer {

	@Override
	public void notifyMove(PolarCoordinate coord, boolean turn) {
		System.out.println(coord.toString()); 
		
	}

	@Override
	public void notifyWin(boolean turn, ArrayList<Move> winState) {
		// TODO Auto-generated method stub
		
	}

}
