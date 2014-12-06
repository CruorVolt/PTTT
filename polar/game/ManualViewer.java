package polar.game;

public class ManualViewer implements GameViewer {

	@Override
	public void notifyMove(MoveReport report) {
		System.out.println(report.getMove().toString()); 
		
	}

	@Override
	public void notifyWin(boolean turn, Move[] winState) {
		// TODO Auto-generated method stub
		System.out.println("Game is finished.");
	}

}
