package polar.test;

import polar.game.Game;
import polar.game.Player;
import polar.game.UnTestedCoordinates;

public class Test {
	UnTestedCoordinates[] tests;
	UnTestedCoordinates[] dummy;
	
	public Test(UnTestedCoordinates[] tests, UnTestedCoordinates[] dummy) {
		this.tests = tests;
		this.dummy = dummy;
	}
	public void runTest() {
		TestPlayStyle styleX = new TestPlayStyle(tests);
		TestPlayStyle styleO = new TestPlayStyle(dummy);
		
		Player x = new Player(styleX, 'X', Player.PLAYER_X);
		Player o = new Player(styleO, 'O', Player.PLAYER_O);
		Game g = new Game();
		g.setPlayStyles(styleX, styleO);
		g.begin();	
	}
}