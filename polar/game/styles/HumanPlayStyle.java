package polar.game.styles;


import polar.game.Player;

import polar.game.MoveReport;
import polar.game.UnTestedCoordinates;

// Define the human behavior interaction for Game.
public class HumanPlayStyle extends PlayStyle {

	private static MoveReport report;	// most recent coordinates attempted
	private static Object wait = new Object();
	private static HumanPlayStyle instance;

	// Do not allow instantiation
	protected HumanPlayStyle() {} 

	public static HumanPlayStyle getInstance() {
		if (instance == null) {
			instance = new HumanPlayStyle();
		} 
		return instance;
	}
	
	public static void unlock() {
		Update(new UnTestedCoordinates(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}

	@Override
	public MoveReport getMove() {
        synchronized(wait){
            try{
            	wait.wait();
                return report;
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
		return null;
	}

	public static void Update(UnTestedCoordinates newCoords) {
		synchronized(wait) {
			report = new MoveReport(newCoords.getX(), newCoords.getY());
			wait.notifyAll();
		}
	}

	@Override
	public String toString() {
		return Player.PlayerTypes.HUMAN.toString();
	}
}
