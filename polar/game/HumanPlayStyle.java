package polar.game;
// Define the human behavior interaction for Game.
public class HumanPlayStyle implements PlayStyle {

	private static UnTestedCoordinates c;	// most recent coordinates attempted
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
	public UnTestedCoordinates getMove() {
        synchronized(wait){
            try{
            	System.out.println("getMove is going to wait now");
            	wait.wait();
            	System.out.println("getMove is done waiting now");
                return c;
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
		return null;
	}

	public static void Update(UnTestedCoordinates newCoords) {
		synchronized(wait) {
			c = newCoords;
			wait.notifyAll();
		}
	}
}
