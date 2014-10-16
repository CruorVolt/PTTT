package polar.game;

public class Game {
	private Player playerX;
	private Player playerO;
	private Play playX;
	private Play playO;
	private Map map;
	private boolean notWin;
	private boolean turn; // true when player X's turn, false when player O's turn.
	
	public Game(String player1, String player2, GameViewer viewer) {
		map = new Map(viewer);
		playerX = new Player('X', true, player1, this);
		playerX.setTurn(false);
		playerO = new Player('O', false, player2, this);
		playerO.setTurn(true);
		playX = new ManualPlay();
		playO = new ManualPlay();
		turn = true;
		notWin = true;
	}
	public void setPlayStyle(Play styleX, Play styleO) {
		playX = styleX;
		playO = styleO;
	}
	// start the game.
	public void begin() {
		while(notWin) {
			//nextTurn();
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
	public boolean nextTurn(UnTestedCoordinates loc) {
		boolean success = false;
		Move move;
		if(turn) {
			while(!success) {
				//loc = playX.getNextMove();
				move = playerX.move(loc);
				if(move!=null) {
					try {
						turn = false;
						map.updateAll(move);
						success = true;
					} catch (MoveDuplicateException e) {
						turn = true;
						return false; //Illegal move
					}
				}
			}
		}
		else {
			while(!success) {
				//loc = playO.getNextMove();
				move = playerO.move(loc);
				if(move!=null) {
					try {
						turn = true;
						map.updateAll(move);
						success = true;
					} catch (MoveDuplicateException e) {
						turn = false;
						return false; //Illegal move
					}
				}
			}
		}
		return true; //The turn was completed succesfully
	}
}
