package polar;
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
		playerX = new Player(player1);
		playerX.setTurn(true);
		playerO = new Player(player2);
		playerO.setTurn(false);
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
			nextTurn();
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
	public void nextTurn() {
		UnTestedCoordinates loc;
		boolean success = false;
		Move move;
		if(turn) {
			while(!success) {
				loc = playX.getNextMove();
				move = playerX.move(loc);
				if(move!=null) {
					try {
						map.updateAll(move);
						success = true;
						turn = false;
					} catch (MoveDuplicateException e) {
						// try again
					}
				}
			}
		}
		else {
			while(!success) {
				loc = playO.getNextMove();
				move = playerO.move(loc);
				if(move!=null) {
					try {
						map.updateAll(move);
						success = true;
						turn = true;
					} catch (MoveDuplicateException e) {
						// try again
					}
				}
			}
		}
		
	}
}
