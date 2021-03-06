package polar.game;

import polar.game.exceptions.BadCoordinateException;

public class MoveReport {
	
	UnTestedCoordinates coordinates;
	Move move;
	Integer searchDepth;
	Integer nodesSearched;
	Double seconds;
	Double value;
	
	public MoveReport(int x, int y) {
		this.coordinates = new UnTestedCoordinates(x, y);
		this.move = null;
		initialize();
	}
	
	public MoveReport(Move move) {
		this.move = move;
		PolarCoordinate c = move.getLoc();
		this.coordinates = new UnTestedCoordinates(c.getX(), c.getY());
		initialize();
	}
	
	public void initialize() {
		this.searchDepth = 0;
		this.nodesSearched = 0;
		this.seconds = 0.0;
		this.value = 0.0;
	}
	
	public PolarCoordinate getCoordinates() {
		try {
			return new PolarCoordinate(coordinates);
		} catch (BadCoordinateException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Move reportMove(Boolean player) {
		if (this.move == null) {
			this.move = new Move(player, getCoordinates());
		}
		return this.move;
	}
	
	public Move getMove() {
		return this.move;
	}
	
	public void reportDepth(int depth) {
		this.searchDepth = depth;
	}
	
	public int getDepth() {
		return this.searchDepth;
	}
	
	public void reportNodes(int nodes) {
		this.nodesSearched = nodes;
	}
	
	public int getNodes() {
		return this.nodesSearched;
	}

	public void reportTime(double time) {
		this.seconds = time;
	}
	public void reportValue(double val) {
		value = val;
	}
	public double getValue() {
		return value;
	}
	
	public double getTime() {
		return this.seconds;
	}
}
