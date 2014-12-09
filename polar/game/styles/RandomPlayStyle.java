package polar.game.styles;

import java.util.ArrayList;
import java.util.Random;

import logic.Status;
import polar.game.Game;
import polar.game.Player;
import polar.game.PolarCoordinate;
import polar.game.UnTestedCoordinates;
import polar.game.MoveReport;


public class RandomPlayStyle extends PlayStyle {
	
	private Game game;
	private boolean player;
	
	public RandomPlayStyle(boolean player, Game game) {
		this.player = player;
		this.game = game;
	}

	@Override
	public MoveReport getMove() {
		System.out.println("Locking");
		lock();
		System.out.println("unlocked");
		boolean valid = false;
		ArrayList<UnTestedCoordinates> available = Status.getValidPositions(game.getMap().getMoves());
		System.out.println("found available");
		int x;
		int y;
		UnTestedCoordinates possible;
		if (available.size() == 0) {
			System.out.println("available empty");
			Random rand = new Random();
			x = rand.nextInt(4) + 1;
			y = rand.nextInt(12);
			possible = new UnTestedCoordinates(x, y);
		} else {
			System.out.println("available has stuff");
			x = available.get(0).getX();
			y = available.get(0).getY();
			possible = available.get(0);
			while (!valid) {
				System.out.println("Waiting for valid");
				Random rand = new Random();
				x = rand.nextInt(4) + 1;
				y = rand.nextInt(12);
				possible = new UnTestedCoordinates(x, y);
				for (UnTestedCoordinates test : available) {
					if ((test.getX() == x) && (test.getY() == y)) {
						valid = true;
						System.out.println("found valid");
						break;
					}
				}
			}
		}
		MoveReport report = new MoveReport(x,y);
		report.reportValue(0);
		report.reportTime(getElapsedTime());
		report.reportNodes(getNodes());
		endTurn();
		System.out.println("Returning " + x + "," + y);
		return report;
	}

	@Override
	public String toString() {
		return Player.PlayerTypes.RANDOM.toString();
	}

}
