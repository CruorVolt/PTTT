package polar.game;

public class MoveReport {
	
	UnTestedCoordinates coordinates;
	
	public MoveReport(int x, int y) {
		this.coordinates = new UnTestedCoordinates(x, y);
	}
	
	public UnTestedCoordinates getCoordinates() {
		return coordinates;
	}

}
