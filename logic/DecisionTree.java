package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import polar.game.Map;

/*
 * Decision tree built from the training 
 * procedures in Classifier.java
 */
public class DecisionTree {
	
    public static final String TRAINING_FILE = "./src/training_set.csv";
	DecisionTree.Node root;
	
	public DecisionTree() {
		
		//get features and gains
		try {
			Double[] gains = (Classifier.gain(TRAINING_FILE, false));
			String[] features = SupportFunctions.getFeatures(TRAINING_FILE);
			for (int i = 0; i< gains.length; i++) {
				System.out.print(features[i]);
				System.out.println(": " + gains[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//sort gains
		// make layers 
		
		
		
	}
	
	/*
	 * Classify game map as a win (true) or loss (false)
	 * given a player token.
	 */
	public boolean classify(Character player, Map map) {
		return false;
	}
	
	class Node {
		
		String feature;
		ArrayList<Double> partitions;
		Node parent;
		ArrayList<Node> children;
		
		public Node(String feature, ArrayList<Double> partitions, Node parent) {
			this.feature = feature;
			this.partitions = partitions;
			this.parent = parent;
		}
		
	}

}
