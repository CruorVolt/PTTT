package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import polar.game.*;

public class Classifier {
	
	private static final int TRAINING_SET_SIZE = 1000;
	
	/*
	 * Calculate the gain of a set of features given the 
	 * a set of end-game states.
	 * 
	 * trainingSet file format expects CSV with headers indicating feature name, 
	 * and the label as the first feature.
	 * 
	 * @param traininSet The file name of the set of training data
	 * @param regenerate Whether to use existing training data or create new test games
	 */
	public static ArrayList<Object> gain(String trainingSet, boolean regenerate) throws IOException {
		File f = new File(trainingSet);
		if ((regenerate) || (!f.exists())){ //make new features if none exist
			SupportFunctions.generateFeatures(new File(DecisionTree.TRAINING_FILE), 'X', TRAINING_SET_SIZE);
		}
		HashMap<String, Double> entropyMap = new HashMap<String, Double>();

		BufferedReader br = new BufferedReader(new FileReader(trainingSet));
		String line = br.readLine(); //header line is first

		String[] headers = line.split(", ");
		int numberOfFeatures = headers.length;
		
		//partition each feature into three evenly spaced blocks
		HashMap<String, double[]> minMax = featureMinMax(trainingSet);
		double[] mins = minMax.get("minimums");
		double[] maxs = minMax.get("maximums");
		double[] partitions = new double[mins.length]; //halfway partition points
		partitions[0] = 0; //Don't need partitions for first feature
		for (int i = 1; i < numberOfFeatures; i++) {
			double split = (maxs[i] + mins[i]) / 2.0;
			partitions[i] = split;
		}

		//Arrays for positive and negative examples of each feature, initizlized to zeroes for each possible partition
		ArrayList<ArrayList<Integer>> positiveExamples = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> negativeExamples = new ArrayList<ArrayList<Integer>>();
		positiveExamples.add(null);
		negativeExamples.add(null);

		for (int y = 1; y < numberOfFeatures; y++) {
			//Initial partitions are all zeroes -- incremented by observation
			ArrayList<Integer> zeroesP = new ArrayList<Integer>();
			ArrayList<Integer> zeroesN = new ArrayList<Integer>();
			for (int j = 0; j < 2; j++) {
                	zeroesP.add(0);
                	zeroesN.add(0);
			}
			positiveExamples.add(zeroesP);
			negativeExamples.add(zeroesN);
		}

		//Build the sets of positive/negative examples for each partition of each feature in the file
		int overallPositives = 0;
		int overallNegatives = 0;
		while ((line = br.readLine()) != null) {
			String[] features = line.split(", ");
			Double win = Double.valueOf(features[0]);
			String feature;
			
			if (win > 0) {
				overallPositives++;
			} else {
				overallNegatives++;
			}

			for (int i = 1; i < features.length; i++) {
				
				//Increment examples
				Double featureVal = Double.valueOf(features[i]);
				if (win > 0) {
					if (featureVal <= partitions[i]) { // This value belongs to the bottom partition
						positiveExamples.get(i).set(0, positiveExamples.get(i).get(0) + 1);
					} else if (featureVal > partitions[i]) { // This value belongs to the top partiton
						positiveExamples.get(i).set(1, positiveExamples.get(i).get(1) + 1);
					} else { //Something went wrong: unexpected feature value
						System.out.println("Unexpected feature value while counting p and n for entropy");
					}
				} else {
					if (featureVal <= partitions[i]) { // This value belongs to the bottom partition
						negativeExamples.get(i).set(0, positiveExamples.get(i).get(0) + 1);
					} else if (featureVal > partitions[i]) { // This value belongs to the top partiton
						negativeExamples.get(i).set(1, positiveExamples.get(i).get(1) + 1);
					} else { //Something went wrong: unexpected feature value
						System.out.println("Unexpected feature value while counting p and n for entropy");
					}
				}
			}
		}
		double op = (double) overallPositives;
		double on = (double) overallNegatives;
		double ipn = -( op / (op+on) ) * logBaseK( op / (op+on), 2.0 ) - (on / (op+on) ) * logBaseK( on / (op+on), 2.0 );
		br.close();
		
		ArrayList<ArrayList<Double>> entropy = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> expectedEntropy = new ArrayList<Double>();
		Double[] gains = new Double[headers.length];
		double entropyXI;
		entropy.add(null); //don't need entropy for first feature
		expectedEntropy.add(null);
		for (int x = 1; x < headers.length; x++) {
			//calculate entropy of feature at x
			ArrayList<Double> defaults = new ArrayList<Double>();
			defaults.ensureCapacity(3);
			defaults.add(0.0);
			defaults.add(0.0);
			entropy.add(defaults); //initial zeroes for this index
			double expectedSum = 0.0;
			for (int i = 0; i < 2; i++) { //Per feature-partition
				try {

					Double positiveXI = (double) positiveExamples.get(x).get(i);
					Double negativeXI = (double) negativeExamples.get(x).get(i);

					Double positiveTerm;
					Double negativeTerm;
					
					//build positive term for entropy
					if (equalsFloat(positiveXI, 0.0)) {
						positiveTerm = 0.0;
					} else {
						positiveTerm = ( positiveXI / (positiveXI+negativeXI) ) 
							* logBaseK( positiveXI / (positiveXI+negativeXI), 2.0 );
					}	

					//build negative term for entropy
					if (equalsFloat(negativeXI, 0.0)) {
						negativeTerm = 0.0;
					} else {
						negativeTerm = (negativeXI / (positiveXI+negativeXI) ) 
							* logBaseK( negativeXI / (positiveXI+negativeXI), 2.0 );
					}
					
					entropyXI = -(positiveTerm) - (negativeTerm);

					if (Double.isNaN(entropyXI)) {
						entropy.get(x).set(i, Double.MAX_VALUE); //?
						System.out.println("max value here");
						System.out.println("pos: " + positiveXI);
						System.out.println("neg: " + negativeXI);
					} else {
						entropy.get(x).set(i, entropyXI);
					} 
					
					//Get expected entropy for feature x, partition i
					expectedSum += ( positiveXI + negativeXI ) / (op + on) * entropyXI;
				} catch (ArithmeticException a) {
					a.printStackTrace();
				}
			}
			//Expected entropy across the partitions of this feature
			expectedEntropy.add(expectedSum);
			
			//gain calculation (entropy - expected_entopy)
			double gainX = ipn - expectedSum;
			gains[x] = gainX;
		}
		
		int max_gain_index = 1;
		for (int g = 1; g < gains.length; g++) { 
			if (gains[g] >= gains[max_gain_index]) {
				max_gain_index = g;
			}
		}
		
		ArrayList<Object> output = new ArrayList<Object>();
		output.add(Arrays.copyOfRange(gains, 1, gains.length));
		output.add(partitions);
		output.add(Arrays.copyOfRange(headers, 1, headers.length));
		return output;
	}
	
	/*
	 * Find the minimum and maximum values of each feature 
	 * in order to find useful (?) partition points.
	 */
	public static HashMap<String, double[]> featureMinMax(String fileName) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		String features[];
		String line;
		try {
			line = br.readLine(); //cycle through header line
			String[] headers = line.split(", ");
	
			double[] minimums = new double[headers.length];
			double[] maximums = new double[headers.length];
			Arrays.fill(minimums, Double.MAX_VALUE);
			Arrays.fill(maximums, 0);
			
			int numberOfFeatures = headers.length;
	
			while ((line = br.readLine()) != null) {
				features = line.split(", ");
				for (int x = 0; x < features.length; x++) {
					if (Double.valueOf(features[x]) > maximums[x]) { //New max value
						maximums[x] = Double.valueOf(features[x]);
					} else if (Double.valueOf(features[x]) < minimums[x]) { //New min value
						minimums[x] = Double.valueOf(features[x]);
					}
				}
			}
			HashMap<String, double[]> minMax = new HashMap<String, double[]>();
			minMax.put("minimums", minimums);
			minMax.put("maximums", maximums);
			return minMax;
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println("IO Problem finding min/max values");
		return null;
	}
	
	public static double logBaseK(double val, double k) {
		return Math.log(val) / Math.log(k);	
	}
	
	public static boolean equalsFloat(double i, double j) {
		double epsilon = 0.0001;
		if (Math.abs(i-j) < epsilon) {
			return true;
		} else {
			return false;
		}
	}
}
