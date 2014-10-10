package polar;
public class Player {
	private String name;
	boolean turn;
	
	public Player(String name) {
		this.name = name;
	}
	public void setTurn(boolean turn) {
		this.turn = turn;
	}
	// return true if move succeeds, false if it fails
	public Move move(UnTestedCoordinates uc) {
		try {
			PolarCoordinate c = new PolarCoordinate(uc);
			Move newMove = new Move(turn,c);
			return newMove;
			
		} catch (BadCoordinateException e) {
			return null;
		}
	}
}
