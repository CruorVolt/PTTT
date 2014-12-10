package logic;

import java.util.ArrayList;
import java.util.Scanner;

import polar.game.Move;
import polar.game.UnTestedCoordinates;

public final class Status {

	// test all predicates in our ruleset
	public static boolean resolve(Move u, Move v, Move x, Move y, int d, boolean p) {
	return	(!valid(d)||!owns(u,p)||!owns(v,p)||!owns(x,p)||!owns(y,p)||!direction(u,v,d)||!direction(v,x,d)||!direction(x,y,d));
	}
	public static boolean printResolve(Move u, Move v, Move x, Move y, int d, boolean p) {
		
		System.out.println();
		System.out.print("Resolving: "+u.getLoc()+","+"v.getLoc()"+x.getLoc()+""+y.getLoc()+","+" for direction "+d);
		if(p)
			System.out.println(" and player X");
		else
			System.out.println(" and player O");
		if(!valid(d)) {
			System.out.println("Invalid direction.");
		}
		else if(!owns(u,p)||!owns(v,p)||!owns(x,p)||!owns(y,p)) {
			System.out.println("One or more moves not owned by this player.");
		}
		else if(!direction(u,v,d)||!direction(v,x,d)||!direction(x,y,d)) {
			System.out.println("One or more moves do not line up");
		}
		else {
			System.out.println("No contradictions found");
		}
		return resolve(u,v,x,y,d,p);
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
	// verify coordinates used / wrap around as needed
	public static UnTestedCoordinates getCoord(int x,int y) {
		if(y==12)
			y = 0;
		if(y==-1)
			y = 11;
		if((x<1)||(x>4))
			return null;
		return new UnTestedCoordinates(x,y);
	}
	// returns all valid coordinates available for play.
	public static ArrayList<UnTestedCoordinates> getValidPositions(ArrayList<Move> moves) {
		ArrayList<UnTestedCoordinates> valid = new ArrayList<UnTestedCoordinates>();
		for(Move move : moves) {
			int x = move.getLoc().getX();
			int y = move.getLoc().getY();
			for(int i=0;i<8;i++) {
				Move connected = move.getMoveOrBlock(i);
				if(connected==null) {
					UnTestedCoordinates coord;
					switch(i) {
					case 0: 
						coord = getCoord(x+1,y);
						if(!valid.contains(coord))
							valid.add(coord);
						break;
					
					case 1: 
						coord = getCoord(x+1,y+1);
						if(!valid.contains(coord))
							valid.add(coord);
						break;
					
					case 2: 
						coord = getCoord(x,y+1);
						if(!valid.contains(coord))
							valid.add(coord);
						break;
					
					case 3: 
						coord = getCoord(x-1,y+1);
						if(!valid.contains(coord))
							valid.add(coord);
						break;
					case 4: 
						coord = getCoord(x-1,y);
						if(!valid.contains(coord))
							valid.add(coord);
						break;
					case 5: 
						coord = getCoord(x-1,y-1);
						if(!valid.contains(coord))
							valid.add(coord);
						break;
					case 6: 
						coord = getCoord(x,y-1);
						if(!valid.contains(coord))
							valid.add(coord);
						break;
					case 7: 
						coord = getCoord(x+1,y-1);
						if(!valid.contains(coord))
							valid.add(coord);
						break;
					}
				}
			}
		}
		return valid;
	}
}

