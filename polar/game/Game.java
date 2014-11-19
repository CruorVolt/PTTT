package polar.game;

import logic.Heuristic;

public class Game {
	private Player playerX;
	private Player playerO;
	private GameMap map;
	private boolean notWin;
	private boolean turn; // true when player X's turn, false when player O's turn.
	private Player currentPlayer;
	private HumanPlayStyle humanStyle = HumanPlayStyle.getInstance();
	
	public Game(String player1, String player2, GameViewer viewer) {
		map = new GameMap(viewer);
		playerX = new Player(humanStyle, 'X', true, player1, this);
		playerO = new Player(humanStyle, 'O', false, player2, this);
		turn = true;
		notWin = true;
	}
	
	public void addViewer(GameViewer viewer) {
		synchronized(this) {
			map.addViewer(viewer);
			this.notifyAll();
		}
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
			if(success) {

				//Training stuff------------------------------------------------------
				int score = (Heuristic.evaluate(map, currentPlayer.getToken()));
				if ( (score >= 1000) || (map.getMoves().size() >= 48) ) { //someone won
					notWin = false;
					break;
				}
				//Training stuff------------------------------------------------------

				if(currentPlayer.equals(playerX)) {
					currentPlayer = playerO;
				} else {
					currentPlayer = playerX;
				}
			}
		}
	}
	public void end() {
		notWin = false;
	}
	public GameMap getMap() {
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
			try {
				success = map.updateAll(move, turn); // return true if valid move and update succeeds.
				if(success)
					turn = !turn;
			} catch(MoveDuplicateException m) {
				success = false;
			}
			return success;
		}
		return false;
	}
}
