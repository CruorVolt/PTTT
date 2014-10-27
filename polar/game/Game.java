package polar.game;

public class Game {
	private Player playerX;
	private Player playerO;
	private Map map;
	private boolean notWin;
	private boolean turn; // true when player X's turn, false when player O's turn.
	private Player currentPlayer;
	
	public Game(String player1, String player2, GameViewer viewer) {
		map = new Map(viewer);
		playerX = new Player(new ManualPlay(), 'X', true, player1, this);
		playerO = new Player(new ManualPlay(), 'O', false, player2, this);
		turn = true;
		notWin = true;
	}
	public void setPlayStyles(PlayStyle styleX, PlayStyle styleO) {
		playerX.setPlayStyle(styleX);
		playerO.setPlayStyle(styleO);
	}
	public boolean getTurn() {
		return turn;
	}
	
	// start the game.
	public void begin() {
		currentPlayer = playerX;
		boolean success;
		while(notWin) {
			success = nextTurn(currentPlayer.move());
			if(success)
				if(currentPlayer.equals(playerX))
					currentPlayer = playerO;
				else
					currentPlayer = playerX;
		}
	}
	public Map getMap() {
		return map;
	}
	public Player getPlayerX() {
		return playerX;
	}
	public Player getPlayerO() {
		return playerO;
	}
	public Player currentPlayer() {
		Player current;
		if (turn) {
			current = this.playerX;
		} else {
			current = this.playerO;
		}
		return current;
	}
	// this should not be accessed externally. will make private once GUI is restructured to work with PlayStyles.
	private boolean nextTurn(Move move) {
		boolean success;
		if(move!=null) {
			success = map.updateAll(move); // return true if valid move and update succeeds.
			if(success)
				turn = !turn;
			return success;
		}
		return false;
	}
}
