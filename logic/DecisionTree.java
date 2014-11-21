package logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
			double[] partitions = null;
			if (blocks.get(1) instanceof double[]) {
				partitions = (double[]) blocks.get(1);
			}
			String[] features = SupportFunctions.getFeatures(TRAINING_FILE);
			
			if (gains != null && partitions != null) {
				//Sort gains and associated features
				Node[] sorted = new Node[gains.length];
				Node pair;
				for (int j = 0; j < sorted.length; j++) {
					pair = new Node(gains[j], features[j], partitions[j+1]);
					sorted[j] = pair;
					System.out.println(pair);
				}
				Arrays.sort(sorted);
				this.features = sorted;
				
				Node node;
				//trace the tree
				for (int i = sorted.length-1; i >= 0; i--) {

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setupTree(String examplesFile) throws FileNotFoundException {

		ArrayList<ArrayList<Double>> examples = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> target = new ArrayList<Double>(); 
		ArrayList<String> attributes = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(examplesFile));
		try {

			String[] headers = br.readLine().split(", ");

			for (int i = 1; i < headers.length; i++) {
				attributes.add(headers[i]);
			}

			if (headers.length == features.length + 1) { //Make sure the featureset and file match
				String rawLine;
				String[] line;
				ArrayList<Double> values;
				while ((rawLine = br.readLine()) != null) {
					values = new ArrayList<Double>();
					line = rawLine.split(", ");
					target.add(Double.valueOf(line[0])); //class label
					for (int i = 1; i < line.length; i++) {
						values.add(Double.valueOf(line[i]));
					}
					examples.add(values);
				}
			} else {
				System.out.println("DecisionTree.buildTree had a mismatch of features");
			}
			
			System.out.println("Done with setup, builidng tree");
			buildTree(examples, target, attributes);
		} catch (IOException e) {
			System.out.println("Reset or read issue?");
			e.printStackTrace();
		}
	}
	
	/*
	 * Build a classification tree 
	 */
//		ID3 (Examples, Target_Attribute, Attributes)
	public Node buildTree(ArrayList<ArrayList<Double>> examples, ArrayList<Double> target, ArrayList<String> attributes) { 
//	    Create a root node for the tree
		Node root = new Node(features[0]);

//	    If all examples are positive, Return the single-node tree Root, with label = +.
//	    If all examples are negative, Return the single-node tree Root, with label = -.
		boolean positiveExample = false;
		boolean negativeExample = false;
		int weight = 0;
		for (Double d : target) {
			if (d > 0.0) {
				weight++;
				positiveExample = true;
			} else if (d < 0.0) {
				weight--;
				negativeExample = true;
			}
		}
		
		if (!negativeExample) {
			root.label(true);
			return root;
		} else if (!positiveExample) {
			root.label(false);
			return root;
		}

//	    If number of predicting attributes is empty, then Return the single node tree Root,
//	    with label = most common value of the target attribute in the examples.
		if (attributes.size() == 0 || attributes == null) {
			root.label( (weight >= 0) ? true : false);
			return root;
		}
		
		
		
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
		public double partition;
		public Node parent;
		public ArrayList<Node> children;
		boolean label;
		
		public Node(Node original) {
			this.val = original.val;
			this.tag = original.tag;
			this.partition = original.partition;
		}
		
		public Node(Double val, String tag, double partition) {
			children = new ArrayList<Node>();
			this.val = val;
			this.tag = tag;
			this.partition = partition;
		}

		public Node(Double val, String tag, double partition, Node parent) {
			children = new ArrayList<Node>();
			this.val = val;
			this.tag = tag;
			this.partition = partition;
			this.parent = parent;
			parent.addChild(this);
		}
		
		public void addParent(Node parent) {
			this.parent = parent;
			parent.addChild(this);
		}
		
		public void addChild(Node newChild) {
			if (children != null) {
				children.add(newChild);
			}
		}
		
		public void addChild(Double val, String tag, double partition) {
			this.addChild(new Node(val, tag, partition));
		}
		
		public void label(boolean win) {
			this.label = win;
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
		try {
			tree.setupTree(TRAINING_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
