package polar.game;

public class ManualViewer implements GameViewer {

	@Override
	public void notifyMove(PolarCoordinate coord, boolean turn) {
		System.out.println(coord.toString()); 
		
	}

	@Override
	public void notifyWin(boolean turn, Move[] winState) {
		// TODO Auto-generated method stub
		System.out.println("Game is finished.");
	}

}
