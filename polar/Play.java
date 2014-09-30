package polar;
public abstract class Play {
	Player player;
	public Play(Player player) {
		this.player = player;
	}
	public abstract UnTestedCoordinates getNextMove();
}
