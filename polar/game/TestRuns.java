package polar.game;

public class TestRuns {

	public static void main(String[] args) {
		// Test win checker
		UnTestedCoordinates[] tests1 = new UnTestedCoordinates[4];
		UnTestedCoordinates[] dummy1 = new UnTestedCoordinates[4];
		
		UnTestedCoordinates[] tests2 = new UnTestedCoordinates[4];
		UnTestedCoordinates[] dummy2 = new UnTestedCoordinates[4];	
		
		UnTestedCoordinates[] tests3 = new UnTestedCoordinates[4];
		UnTestedCoordinates[] dummy3 = new UnTestedCoordinates[4];	
		
		UnTestedCoordinates[] tests4 = new UnTestedCoordinates[4];
		UnTestedCoordinates[] dummy4 = new UnTestedCoordinates[4];	
		
		UnTestedCoordinates[] tests5 = new UnTestedCoordinates[4];
		UnTestedCoordinates[] dummy5 = new UnTestedCoordinates[4];	
		
		UnTestedCoordinates[] tests6 = new UnTestedCoordinates[4];
		UnTestedCoordinates[] dummy6 = new UnTestedCoordinates[4];	
		
		UnTestedCoordinates[] tests7 = new UnTestedCoordinates[4];
		UnTestedCoordinates[] dummy7 = new UnTestedCoordinates[4];	
		
		UnTestedCoordinates[] tests8 = new UnTestedCoordinates[4];
		UnTestedCoordinates[] dummy8 = new UnTestedCoordinates[4];
		
		int y, y1,y2,y3,y4,y5,y6,y7,y8;	//default to top spoke
		int x, x1,x2,x3,x4,x5,x6,x7,x8;	//default to inner orbital
		x = 1;
		y = 0;
		for(int i=0;i<4;i++) {
			// verticals
			x1 = i+1;	
			x2 = 4-i;
			y1 = y2 = y;
			x7 = x8 = x;
			// diagonals
			x3 = x1;
			x4 = x2;
			x5 = x1;
			x6 = x2;
			y3 = x1;
			y4 = x2;
			y5 = x2;
			y6 = x1;
			// horizontals
			y7 = x1;
			y8 = x2;
			tests1[i] = new UnTestedCoordinates(x1,y1);
			dummy1[i] = new UnTestedCoordinates(x1,y1+1);
			tests2[i] = new UnTestedCoordinates(x2,y2);
			dummy2[i] = new UnTestedCoordinates(x2,y2+1);
			
			tests3[i] = new UnTestedCoordinates(x3,y3);
			dummy3[i] = new UnTestedCoordinates(x,y3+1);
			tests4[i] = new UnTestedCoordinates(x4,y4);
			dummy4[i] = new UnTestedCoordinates(x,y4+1);
			tests5[i] = new UnTestedCoordinates(x5,y5);
			dummy5[i] = new UnTestedCoordinates(x,y5+1);
			tests6[i] = new UnTestedCoordinates(x6,y6);
			dummy6[i] = new UnTestedCoordinates(x6,y6+1);
			
			tests7[i] = new UnTestedCoordinates(x7,y7);
			dummy7[i] = new UnTestedCoordinates(x7+1,y7);
			tests8[i] = new UnTestedCoordinates(x8,y8);
			dummy8[i] = new UnTestedCoordinates(x8+1,y8);
		}
		
		Test test1 = new Test(tests1,dummy1);
		Test test2 = new Test(tests2,dummy2);
		Test test3 = new Test(tests3,dummy3);
		Test test4 = new Test(tests4,dummy4);
		Test test5 = new Test(tests5,dummy5);
		Test test6 = new Test(tests6,dummy6);
		Test test7 = new Test(tests7,dummy7);
		Test test8 = new Test(tests8,dummy8);
		
		int numSuccess = 0;
		int numFailure = 0;
		try {
			test1.runTest();
			numSuccess++;
		}
		catch(Exception e) {
			numFailure++;
		}
		try {
			test2.runTest();
			numSuccess++;
		}
		catch(Exception e) {
			numFailure++;
		}
		try {
			test3.runTest();
			numSuccess++;
		}
		catch(Exception e) {
			numFailure++;
		}
		try {
			test4.runTest();
			numSuccess++;
		}
		catch(Exception e) {
			numFailure++;
		}
		try {
			test5.runTest();
			numSuccess++;
		}
		catch(Exception e) {
			numFailure++;
		}
		try {
			test6.runTest();
			numSuccess++;
		}
		catch(Exception e) {
			numFailure++;
		}
		try {
			test7.runTest();
			numSuccess++;
		}
		catch(Exception e) {
			numFailure++;
		}
		try {
			test8.runTest();
			numSuccess++;
		}
		catch(Exception e) {
			numFailure++;
		}
		System.out.println("8 tests: "+numFailure+" failed. "+numSuccess+" passed.");
	}

}
