package polar.game;
public class Player {

	public enum PlayerTypes {
		HUMAN ("Human");
		public String string;
		private PlayerTypes(String s) {
			this.string = s;
		}
	};
	
	private Character token;   // token used in the game, ie. X or O.
	private boolean turn; // true if taking even turns, false if taking odd turns.
	private Game game;
	private String name;
	private PlayStyle style;
	
	public Player(PlayStyle style, Character token, boolean turn, String name, Game game) {
		this.token = token;
		this.turn = turn;
		this.game = game;
		this.name = name;
		this.style = style;
	}
	// sets the behavior for the player.
	public void setPlayStyle(PlayStyle p) {
		style = p;
	}
	// return true if move succeeds, false if it fails
	public Move move() {
		try {
			UnTestedCoordinates uc = style.getMove();
			PolarCoordinate c = new PolarCoordinate(uc);
			Move newMove = new Move(true, token, c);
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

	public boolean isHuman() {
		name = style.getClass().getName();
		return name.equals("polar.game.HumanPlayStyle");
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
