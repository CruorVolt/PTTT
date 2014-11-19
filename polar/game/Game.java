package polar.game;

public class Game implements GameViewer {
	private Player playerX;
	private Player playerO;
	private GameMap map;
	private boolean notWin;
	private boolean turn; // true when player X's turn, false when player O's turn.
	private Player currentPlayer;
	
	public Game() {
		map = new Map();
		playerX = new Player(null, 'X', Player.PLAYER_X);
		playerO = new Player(null, 'O', Player.PLAYER_O);
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
		// first hook into map updates so it knows when the game is over
		addViewer(this);
		currentPlayer = playerX;
		boolean success;
		while(notWin) {
			success = nextTurn(currentPlayer.move());
			if(success) {
				if(currentPlayer.equals(playerX)) {
					currentPlayer = playerO;
				} else {
					currentPlayer = playerX;
				}
			}
		}
	}
	public void end() {
		System.out.println("Game ended early.");
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
	// performs a new turn.
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
		end();
		return false;
	}

	@Override
	public void notifyMove(PolarCoordinate coord, boolean turn) {
		// Game doesn't need to know this.
		
	}

	@Override
	public void notifyWin(boolean turn, Move[] winState) {
		notWin=false;
		for(Move m : winState) {
			System.out.println(m);
		}
	}
}
