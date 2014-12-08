package polar.test;

import polar.game.Game;
import polar.game.Player;
import polar.game.UnTestedCoordinates;

public class Test {
	TestPlayStyle styleO;
	TestPlayStyle styleX;
	
	public Test(UnTestedCoordinates[] tests, UnTestedCoordinates[] dummy) {
		TestPlayStyle styleX = new TestPlayStyle(tests);
		TestPlayStyle styleO = new TestPlayStyle(dummy);
	}
	public void runTest() {
		Player x = new Player(styleX, 'X', Player.PLAYER_X);
		Player o = new Player(styleO, 'O', Player.PLAYER_O);
		Game g = new Game();
		g.setPlayStyles(styleX, styleO);
		g.begin();	
	}
}