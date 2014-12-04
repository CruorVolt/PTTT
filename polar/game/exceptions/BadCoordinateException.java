package polar.game.exceptions;
public class BadCoordinateException extends Exception {
	private static final long serialVersionUID = -2359621346623570314L;
	int x;
	int y;
	public BadCoordinateException(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public String msg() {
		String s= "Invalid parameters. expected x=1-4 and y=0-11. got x="+x+", y="+y;
		return s;
	}
}
