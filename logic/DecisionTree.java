package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
	
	public static void buildTree(String examplesFile) { 
//		ID3 (Examples, Target_Attribute, Attributes)
//	    Create a root node for the tree
//	    If all examples are positive, Return the single-node tree Root, with label = +.
//	    If all examples are negative, Return the single-node tree Root, with label = -.
//	    If number of predicting attributes is empty, then Return the single node tree Root,
//	    with label = most common value of the target attribute in the examples.
//	    Otherwise Begin
//	        A ← The Attribute that best classifies examples.
//	        Decision Tree attribute for Root = A.
//	        For each possible value, v_i, of A,
//	            Add a new tree branch below Root, corresponding to the test A = v_i.
//	            Let Examples(v_i) be the subset of examples that have the value v_i for A
//	            If Examples(v_i) is empty
//	                Then below this new branch add a leaf node with label = most common target value in the examples
//	            Else below this new branch add the subtree ID3 (Examples(v_i), Target_Attribute, Attributes – {A})
//	    End
//	    Return Root	
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
