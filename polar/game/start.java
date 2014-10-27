package polar.game;

public class start {

	public static void main(String[] args) {
		Game game = new Game("Player 1", "Player 2", new ManualViewer());
		ManualPlay playX = new ManualPlay();
		ManualPlay playO = new ManualPlay();
		game.setPlayStyles(playX, playO);
		game.begin();
	}
}
