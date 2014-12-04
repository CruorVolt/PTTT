package polar.game.exceptions;

import polar.game.PolarCoordinate;

public class MoveDuplicateException extends Exception {
	private PolarCoordinate coord;
	private static final long serialVersionUID = -7630678621833038351L;
	public MoveDuplicateException(PolarCoordinate c) {
		this.coord = c;
	}
	public PolarCoordinate getCoordinate() {
		return coord;
	}
	public String msg() {
		return "Move update failed. Move already exists in this position.";
	}
}
