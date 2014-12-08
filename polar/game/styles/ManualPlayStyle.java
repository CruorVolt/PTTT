package polar.game.styles;
import java.util.Scanner;

import polar.game.MoveReport;
import polar.game.UnTestedCoordinates;


public class ManualPlayStyle extends PlayStyle {
	@Override
	public MoveReport getMove() {
		// TODO get manual move
		System.out.println("Enter an X (1-4) ");
		Scanner in = new Scanner(System.in);
		int x = in.nextInt();
		System.out.println("Enter a Y (0-11) ");
		int y = in.nextInt();
		in.close();
		return new MoveReport(x,y);
	}
}
