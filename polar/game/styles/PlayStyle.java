package polar.game.styles;

import polar.game.MoveReport;

public abstract class PlayStyle {
	
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
	
	
}	