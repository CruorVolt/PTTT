package polar.game;

import polar.game.styles.HumanPlayStyle;
import polar.game.styles.PlayStyle;

public class Player {

	public enum PlayerTypes {
		HUMAN ("Human"), 
			RANDOM ("Random"), 
			GREEDY ("Greedy Heuristic"), 
			TEMPORALDIFF ("Temporal Difference"),
			CLASSIFIER ("Decision Tree Classification"),
			MINIMAX3 ("Minimax Search (3 plys)"),
			MINIMAX4 ("Minimax Search (4 plys)"),
			MINIMAX5 ("Minimax Search (5 plys)"),
			ALPHABETA3 ("Alpha-Beta Pruning (3 plys)"),
			ALPHABETA4 ("Alpha-Beta Pruning (4 plys)"),
			ALPHABETA5 ("Alpha-Beta Pruning (5 plys)");
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
	public MoveReport move() {
			MoveReport report = style.getMove();
			return report;
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
