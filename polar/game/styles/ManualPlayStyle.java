package polar.game.styles;
import java.util.Scanner;

import polar.game.UnTestedCoordinates;


public class ManualPlayStyle implements PlayStyle {
	@Override
	public UnTestedCoordinates getMove() {
		// TODO get manual move
		UnTestedCoordinates coords;
		System.out.println("Enter an X (1-4) ");
		Scanner in = new Scanner(System.in);
		int x = in.nextInt();
		System.out.println("Enter a Y (0-11) ");
		int y = in.nextInt();
		in.close();
		coords = new UnTestedCoordinates(x,y);
		return coords;
	}
}
