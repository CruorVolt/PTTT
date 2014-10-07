package polar;
public class Game {
	private Player playerX;
	private Player playerO;
	private Play play1;
	private Play play2;
	private Map map;
	private boolean notWin;
	private boolean turn2; // false when player1's turn, true when player2's turn
	
	public Game(String player1, String player2) {
		map = new Map();
		playerX = new Player('X', false, player1, this);
		playerO = new Player('O', true, player2, this);
		play1 = new ManualPlay(playerX);
		play2 = new ManualPlay(playerO);
		turn2 = false;
		notWin = true;
	}
	public void setPlayStyle(Play style1, Play style2) {
		play1 = style1;
		play2 = style2;
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
	public Player getP1() {
		return playerX;
	}
	public Player getP2() {
		return playerO;
	}
	public Player currentPlayer() {
		Player current;
		if (turn2) {
			current = this.playerX;
		} else {
			current = this.playerO;
		}
		return current;
	}
	public boolean nextTurn(UnTestedCoordinates loc) {
		boolean success = false;
		Move move;
		if(turn2) {
			while(!success) {
				//loc = play2.getNextMove();
				move = playerX.move(loc);
				if(move!=null) {
					try {
						map.updateAll(move);
						success = true;
						turn2 = false;
					} catch (MoveDuplicateException e) {
						return false;
						// try again
					}
				}
			}
		}
		else {
			while(!success) {
				//loc = play1.getNextMove();
				move = playerO.move(loc);
				if(move!=null) {
					try {
						map.updateAll(move);
						success = true;
						turn2 = true;
					} catch (MoveDuplicateException e) {
						return false;
						// try again
					}
				}
			}
		}
		return true; //The turn was completed succesfully
	}
}
