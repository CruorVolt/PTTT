package polar.game;
// Define the human behavior interaction for Game.
public class HumanPlayStyle implements PlayStyle {
	UnTestedCoordinates c;	// most recent coordinates attempted
	private Object wait;
	public HumanPlayStyle() {
		wait = new Object();
	}
	@Override
	public UnTestedCoordinates getMove() {
        synchronized(wait){
            try{
                wait.wait();
                return c;
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
		return null;
	}
	public void Update(UnTestedCoordinates c) {
		this.c = c;
		wait.notify();
	}
}
