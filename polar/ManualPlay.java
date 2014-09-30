package polar;
import java.util.Scanner;


public class ManualPlay extends Play {
	private Scanner in;
	public ManualPlay(Player player) {
		super(player);
		in = new Scanner(System.in);
	}

	@Override
	public UnTestedCoordinates getNextMove() {
		// TODO get manual move
		UnTestedCoordinates coords;
		System.out.println("Enter an X (1-4) ");
		int x = in.nextInt();
		System.out.println("Enter a Y (0-11) ");
		int y = in.nextInt();
		//in.close();
		coords = new UnTestedCoordinates(x,y);
		return coords;
	}
}
