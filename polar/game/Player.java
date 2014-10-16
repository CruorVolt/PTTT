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
	
	public Player(Character token, boolean turn, String name, Game game) {
		this.token = token;
		this.turn = turn;
		this.game = game;
		this.name = name;
	}

	public void setTurn(boolean turn) {
		this.turn = turn;
	}

	// return true if move succeeds, false if it fails
	public Move move(UnTestedCoordinates uc) {
		try {
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

}
