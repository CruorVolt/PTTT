package logic.TD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TDtest {
	private static TD td;
	private static int success;
	public static void main(String[] args) {
		success = 0;
		td = new TD(true, "weights.txt");
		int numFeatures = 9;
		for(int batch = 0;batch<100;batch++) {
			double[][][] weights = runTests(1000);
			try {
				PrintWriter writer = new PrintWriter(new File("weights.txt"));
				for(int l=0;l<2;l++) {
					for(int j=0;j<numFeatures;j++) {
						for(int i=0;i<numFeatures;i++) {
							writer.print(weights[l][j][i]);
							if(i<8)
							writer.print(",");
						}
						writer.println();
					}
				}
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Results: 100,000 tests, "+ success/100000.+" accuracy rate.");
	}
	public static double[][][] runTests(int num) {
		double learnRate = .1;
		double[] features;
		features = new double[9];
		for(int j=0;j<9;j++) {
			features[j] = 0;
		}
		for(int i=0;i<num;i++) {
			double reward = 0;

			// feed in 2 values between 0 and 1
			features[0] = Math.random();
			features[1] = Math.random();
			if((Math.round(features[0])==0)^(Math.round(features[1])==0)) {
				reward = 1.;
			}
			double stateValue = td.feedForward(features);
			if(Math.round(stateValue)==reward) {
				success++;
			}
			double target = stateValue + learnRate*(reward  - stateValue);
			td.backprop(features, target);
		}
		return td.getWeights();
	}
}
