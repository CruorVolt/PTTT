package polar;


public interface GameViewer {
	// notifyMove is called whenever a move is successfully completed by any player. 
	// coord = getX(), getY() to get coordinates. turn=true for player X and false for player O.
	public void notifyMove(PolarCoordinate coord, boolean turn);
	// notifyWin is called whenever a Win state is found. winState describes a sequence of moves that determine the win, and
	// the turn of the player who has won. turn=true if player X, false if player O.
	public void notifyWin(boolean turn, PolarCoordinate[] winState);
}
