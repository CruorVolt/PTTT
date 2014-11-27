package logic;

import polar.game.Move;

public final class Status {

	// test all predicates in our ruleset
	public static boolean resolve(Move u, Move v, Move x, Move y, int d, boolean p) {
	return	(!valid(d)||!owns(u,p)||!owns(v,p)||!owns(x,p)||!owns(y,p)||!direction(u,v,d)||!direction(v,x,d)||!direction(x,y,d));
	}
	// predicate to check directionality
	public static boolean direction(Move a, Move b, int i) {
		return a.compare(b)==i;
	}
	// predicate to check ownership
	public static boolean owns(Move m, boolean player) {
		return m.getPlayer()==player;
	}
	// predicate to check direction validity.
	public static boolean valid(int i) {
		return (i>-1)&&(i<8);
	}
}

