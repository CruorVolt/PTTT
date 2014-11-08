package polar.game;
/*
 * defines a polar coordinate: x units from center and y units from right horizontal axis, clockwise
 * 
 */
public class PolarCoordinate {
	private int x;
	private int y;
	
	public static void main(String[] args) {
		try {
			PolarCoordinate a = new PolarCoordinate(new UnTestedCoordinates(3,11));
			try {
				System.out.println("4,11: " + a.compare(new PolarCoordinate(new UnTestedCoordinates(4,11))));
				System.out.println("4,0: " + a.compare(new PolarCoordinate(new UnTestedCoordinates(4,0))));
				System.out.println("3,0: " + a.compare(new PolarCoordinate(new UnTestedCoordinates(3,0))));
				System.out.println("2,0: " + a.compare(new PolarCoordinate(new UnTestedCoordinates(2,0))));
				System.out.println("2,11: " + a.compare(new PolarCoordinate(new UnTestedCoordinates(2,11))));
				System.out.println("2,10: " + a.compare(new PolarCoordinate(new UnTestedCoordinates(2,10))));
				System.out.println("3,10: " + a.compare(new PolarCoordinate(new UnTestedCoordinates(3,10))));
				System.out.println("4,10: " + a.compare(new PolarCoordinate(new UnTestedCoordinates(4,10))));
			} catch (MoveDuplicateException e) {
				e.printStackTrace();
			}
		} catch (BadCoordinateException e) {
			e.printStackTrace();
		}
	}

	public PolarCoordinate(UnTestedCoordinates uc) throws BadCoordinateException {
		int ucx = uc.getX();
		int ucy = uc.getY();
		if((ucx>4)||(ucx<1)||(ucy<0)||(ucy>11)) {
			// raise exception
			throw new BadCoordinateException(ucx,ucy);
		}
		else {
			x = ucx;
			y = ucy;
		}
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	/* compare current coordinate with given coordinate. return 0 if they are not adjacent.
	 * return 0 if given coordinate is vertically adjacent and above
	 * return 1 if diagonally adjacent: above and ahead
	 * return 2 if horizontally adjacent and ahead
	 * return 3 if diagonally adjacent: below and ahead
	 * return 4 if vertically adjacent and below
	 * return 5 if diagonally adjacent: below and behind
	 * return 6 if horizontally adjacent and behind
	 * return 7 if diagonally adjacent: above and behind
	 * refer to diagram for further clarity
	 * return -1 if not adjacent
	 * 
	 */
	public int compare(PolarCoordinate comp) throws MoveDuplicateException {
		// return values
		int VAdjAndAbove = 0;
		int DAdjAboveAndAhead = 1;
		int HAdjAndAhead = 2;
		int DAdjBelowAndAhead = 3;
		int VAdjAndBelow = 4;
		int DAdjBelowAndBehind = 5;
		int HAdjAndBehind = 6;
		int DAdjAboveAndBehind = 7;
		// comparative values
		int compx = comp.getX();
		int compy = comp.getY();
		//boolean intermediate values
		boolean sameOrbital = false; // within the same orbital
		boolean nearOrbital = false; // vertically adjacent within 1 unit
		boolean above = false;	// 1 unit further from the center
		boolean ahead = false;	// positioned 1 unit clockwise from this coordinate
		boolean nextTo = false; // 1 unit clockwise or counterclockwise from this coordinate
		
		if(compx==x) {		// horizontally adjacent
			sameOrbital = true;
		} else if((Math.abs(compx-x))==1) {
			nearOrbital = true;
			if(compx>x) {
				above = true;
			}
		}

		if(compy==y) {		// vertically adjacent
			if(sameOrbital) {
				// error
				throw new MoveDuplicateException(comp);
			}
			if(nearOrbital) {
				if(above)
					return VAdjAndAbove;
				else
					return VAdjAndBelow;
			}
		} else if((Math.abs(compy-y))==1) {
			nextTo = true;
		} else if((compy==11)&&(y==0)) {
			nextTo = true;
		} else if((compy==0)&&(y==11)) {
			nextTo = true;
			ahead = true;
		}

		if(nextTo) {
			if ( (y == 0) && (compy == 11) ) {
				ahead = false;
			} else if(compy>y) {
				ahead = true;
			}
			if(sameOrbital) {
				if(ahead)
					return HAdjAndAhead;
				else
					return HAdjAndBehind;
			}
			if(nearOrbital) {
				if(ahead) {
					if(above)
						return DAdjAboveAndAhead;
					else 
						return DAdjBelowAndAhead;
				}
				else {
					if(above)
						return DAdjAboveAndBehind;
					else
						return DAdjBelowAndBehind;
				}
			}
		}
		return -1;
	}
	
	/* Get the PolorCoordinate that corresponds to the adjacent node 
	 * represented by the input direction code:
	 */
	public PolarCoordinate getAdjacent(int direction) {
		int x = 0;
		int y = 0;
		switch (direction) {
		case 0: //vertically adjacent and above
			x = above(this.x);
			y = this.y;
			break;
		case 1: //diagonally above and ahead
			x = above(this.x);
			y = ahead(this.y);
			break;
		case 2: //horizontally adjacent and ahead
			x = this.x;
			y = ahead(this.y);
			break;
		case 3: //diagonally below and ahead
			x = below(this.x);
			y = ahead(this.y);
			break;
		case 4: //vertically adjacent and below
			x = below(this.x);
			y = this.y;
			break;
		case 5: //diagonally below and behind
			x = below(this.x);
			y = behind(this.y);
			break;
		case 6: //horizontally adjacent and behind
			x = this.x;
			y = behind(this.y);
			break;
		case 7: //diagonally above and behind
			x = above(this.x);
			y = behind(this.y);
			break;
		}
		try {
			return new PolarCoordinate(new UnTestedCoordinates(x,y));
		} catch (BadCoordinateException e) {
			return null;
		}
	}
	
	public Integer above(int x) {
		if (x == 4) {
			return -1;
		} else {
			return x+1;
		}
	}
	
	public Integer below(int x) {
		if (x == 1) {
			return -1;
		} else {
			return x-1;
		}
	}
	
	public Integer ahead(int y) {
		if (y == 11) {
			return 0;
		} else {
			return y+1;
		}
	}
	
	public Integer behind(int y) {
		if (y == 0) {
			return 11;
		} else {
			return y-1;
		}
	}
	
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		PolarCoordinate compare;
		try {
			compare = (PolarCoordinate) o;
		} catch (ClassCastException e) {
			compare = ((Move) o).getLoc();
		}
		if (this.x == compare.x && this.y == compare.y) {
			return true;
		} else {
			return false;
		}
	}
}
