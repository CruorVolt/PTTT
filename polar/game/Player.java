package polar.game;

import polar.game.exceptions.BadCoordinateException;
import polar.game.styles.HumanPlayStyle;
import polar.game.styles.PlayStyle;

public class Player {

	public enum PlayerTypes {
		HUMAN ("Human"), 
			RANDOM ("Random"), 
			GREEDY ("Greedy Heuristic"), 
			MINIMAX ("Minimax Search"),
			ALPHABETA ("Minimax w/Alpha-Beta Pruning");
		public String string;
		private PlayerTypes(String s) {
			this.string = s;
		}
	};
	
	private Character token;   // token used in the game, ie. X or O.
	private boolean turn; // true if taking even turns, false if taking odd turns.
	private PlayStyle style;
	public static boolean PLAYER_X = true;
	public static boolean PLAYER_O = false;
	
	public Player(PlayStyle style, Character token, boolean turn) {
		this.token = token;
		this.turn = turn;
		this.style = style;
	}

	// sets the behavior for the player.
	public void setPlayStyle(PlayStyle p) {
		if(style==null)
			style = p;
		if (!style.equals(p)) {
			if(style.equals(HumanPlayStyle.getInstance())) {
				HumanPlayStyle.unlock();
			}
			style = p;
		} 
	}

	// return true if move succeeds, false if it fails
	public Move move() {
		try {
			MoveReport report = style.getMove();
			UnTestedCoordinates uc = report.getCoordinates();;
			PolarCoordinate c = new PolarCoordinate(uc);
			Move newMove = new Move(turn, c);
			return newMove;
			
		} catch (BadCoordinateException e) {
			return null;
		}
	}
	public String toString() {
		return token.toString();
	}
	
	public Character getToken() {
		return token;
	}

	public boolean getTurn() {
		return turn;
	}
	
	public PlayStyle getStyle() {
		return style;
	}

	public boolean isHuman() {
		return this.style.equals(HumanPlayStyle.getInstance());
	}

	public boolean Update(UnTestedCoordinates uc) {
		if(this.isHuman()) {
			HumanPlayStyle p = (HumanPlayStyle)style;
			p.Update(uc);
			return true;
		}
		return false;
	}
}
