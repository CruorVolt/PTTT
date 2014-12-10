package polar.game.styles;

import polar.game.MoveReport;

public abstract class PlayStyle {
	
	//lock object for use if the game waits for prompting between moves
	protected static Object wait = new Object();
	public static boolean autoplay = false;

	protected double elapsedTime, timestamp;
	protected int depth;
	protected int nodes;

	public abstract MoveReport getMove();
	
	public void startTimer() {
		this.timestamp = System.currentTimeMillis();
	}
	
	public void stopTimer() {
		this.elapsedTime = System.currentTimeMillis() - this.timestamp;
	}
	
	public double getElapsedTime() {
		return elapsedTime;
	}
	
	public void addNode() {
		this.nodes++;
	}
	
	public int getNodes() {
		return this.nodes;
	}
	
	public void addLayer() {
		this.depth++;
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public void endTurn() {
		this.elapsedTime = 0;
		this.timestamp = 0;
		this.depth = 0;
		this.nodes = 0;
	}

	//lock the style until an update is sent from the gui
	public void lock() {
		if (!autoplay) {
        	synchronized(wait){
            	try{
            		wait.wait();
            	}catch(InterruptedException e){
                	e.printStackTrace();
            	}
        	}
		}
	}

	//Send update to all locked styles if the game is using locks between turns
	public static void update() {
		synchronized(wait) {
			wait.notifyAll();
		}
	}
	
	
}	