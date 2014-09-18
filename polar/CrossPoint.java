package polar;

import java.awt.Point;

public class CrossPoint extends Point {
	
	private boolean marked;
	private String player;
	
	public CrossPoint(int x, int y) {
		super(x, y);
		marked = false;
		player = null;
	}
	
	public CrossPoint() {
		marked = false;
		player = null;
	}
	
	public void setMarked(boolean m) {
		marked = m;
	}
	
	public boolean isMarked() {
		return marked;
	}


}
