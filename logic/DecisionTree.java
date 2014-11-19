package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import polar.game.GameMap;

/*
 * Decision tree built from the training 
 * procedures in Classifier.java
 */
public class DecisionTree {
	
	public static final int TREE_DEPTH = 7;
    public static final String TRAINING_FILE = "./src/training_set.csv";
    Node[] features;
	Node root;
	
	public DecisionTree() {
		
		//get features and gains
		try {
			ArrayList<Object> blocks = (Classifier.gain(TRAINING_FILE, false));
			Double[] gains = null;
			if (blocks.get(0) instanceof Double[]) {
				gains = (Double[]) blocks.get(0);
			}
			ArrayList<ArrayList<Double>> partitions = null;
			if (blocks.get(1) instanceof ArrayList<?>) {
				partitions = (ArrayList<ArrayList<Double>>) blocks.get(1);
			}
			String[] features = SupportFunctions.getFeatures(TRAINING_FILE);
			
			if (gains != null && partitions != null) {
				//Sort gains and associated features
				Node[] sorted = new Node[gains.length];
				Node pair;
				for (int j = 0; j < sorted.length; j++) {
					pair = new Node(gains[j], features[j], partitions.get(j+1));
					sorted[j] = pair;
					System.out.println(pair);
				}
				Arrays.sort(sorted);
				
				Node node;
				//trace the tree
				for (int i = sorted.length-1; i >= 0; i--) {
				}
				this.features = sorted;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * Classify game map as a win (true) or loss (false)
	 * given a player token.
	 */
	public boolean classify(Character player, GameMap map) {
		return false;
	}
	
	class Node implements Comparable {
		public Double val;
		public String tag;
		ArrayList<Double> partition;
		Node parent;
		
		public Node(Double val, String tag, ArrayList<Double> partition) {
			this.val = val;
			this.tag = tag;
			this.partition = partition;
		}

		public Node(Double val, String tag, ArrayList<Double> partition, Node parent) {
			this.val = val;
			this.tag = tag;
			this.partition = partition;
			this.parent = parent;
		}
		
		public void addParent(Node parent) {
			this.parent = parent;
		}

		@Override
		public int compareTo(Object o) {
			Node x = (Node) o;
			if ( x.val < this.val) {
				return 1;
			} else if (x.val > this.val) {
				return -1;
			} else {
				return 0;
			}
		}
		
		@Override
		public String toString() {
			return this.tag + ": " + this.val;
		}
	}

	public static void main(String[] args) {
		DecisionTree tree = new DecisionTree();
	}

}
