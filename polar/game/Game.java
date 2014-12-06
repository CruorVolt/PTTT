package polar.game;

import polar.game.exceptions.MoveDuplicateException;
import polar.game.styles.PlayStyle;

public class Game implements GameViewer {
	private Player playerX;
	private Player playerO;
	protected GameMap map;
	private boolean notWin;
	private boolean player; // true when player X's turn, false when player O's turn.
	//private Player currentPlayer;
	
	public Game() {
		map = new GameMap();
		playerX = new Player(null, 'X', Player.PLAYER_X);
		playerO = new Player(null, 'O', Player.PLAYER_O);
		player = Player.PLAYER_X;
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
	
	public boolean getCurrentPlayer() {
		return player;
	}
	
	// start the game.
	public void begin() {
		Player currentPlayer;
		// first hook into map updates so it knows when the game is over
		addViewer(this);
		currentPlayer = playerX;
		boolean success;
		MoveReport report;
		while(notWin) {
			report = currentPlayer.move();
			success = nextTurn(report);
			if(success) {
				if(player==Player.PLAYER_X) {
					currentPlayer = playerX;
				} else {
					currentPlayer = playerO;
				}
			}
		}
	}

	//Force the game to end
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
		if (player==Player.PLAYER_X) {
			current = this.playerX;
		} else {
			current = this.playerO;
		}
		return current;
	}

	// performs a new turn.
	private boolean nextTurn(MoveReport report) {
		boolean success;
		Move move = report.reportMove(player);
		if(move!=null) {
			try {
				success = map.updateAll(report); // return true if valid move and update succeeds.
				if(success) {
					passPlay();
				}
			} catch(MoveDuplicateException m) {
				success = false;
			}
			return success;
		}
		end();
		return false;
	}

	// pass play to the next player.
	protected void passPlay() {
		player = !player;
	}

	@Override
	public void notifyMove(MoveReport report) {
		// Game doesn't need to know this.
	}

	@Override
	public void notifyWin(boolean turn, Move[] winState) {
		notWin=false;
		if (winState != null) {
			for(Move m : winState) {
				System.out.println(m);
			}
		} else {
			System.out.println("DRAW");
		}
	}
}
