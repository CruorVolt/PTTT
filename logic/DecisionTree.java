package logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polar.game.GameMap;

/*
 * Decision tree built from the training 
 * procedures in Classifier.java
 */
public class DecisionTree {
	
	
	public static int nodes = 0;
	
	public static final int TREE_DEPTH = 7;
    public static final String TRAINING_FILE = "./src/training_set.csv";
    ArrayList<Double> partitionVals;
    ArrayList<gainMap> gainMaps;
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
			
			ArrayList<gainMap> sorted = new ArrayList<gainMap>();
			if (gains != null && partitions != null) {
				for (int i = 0; i < gains.length; i++) {
					sorted.add(new gainMap(gains[i], partitions[i]));
				}
			}
			Collections.sort(sorted);
			this.gainMaps = sorted;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setupTree(String examplesFile) throws FileNotFoundException {

		ArrayList<ArrayList<Double>> examples = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> targets = new ArrayList<Double>(); 
		ArrayList<String> attributes = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(examplesFile));
		try {

			String[] headers = br.readLine().split(", ");

			for (int i = 1; i < headers.length; i++) {
				attributes.add(headers[i]);
			}

			if (headers.length -1 == gainMaps.size()) { //Make sure the featureset and file match
				String rawLine;
				String[] line;
				ArrayList<Double> values;
				while ((rawLine = br.readLine()) != null) {
					values = new ArrayList<Double>();
					line = rawLine.split(", ");
					targets.add(Double.valueOf(line[0])); //class label
					for (int i = 1; i < line.length; i++) {
						values.add(Double.valueOf(line[i]));
					}
					examples.add(values);
				}
			} else {
				System.out.println("DecisionTree.buildTree had a mismatch of features");
			}
			
			System.out.println("Done with setup, builidng tree");
			//attributes and partitions need to be sorted by gains here
			List<Double> partitions = new ArrayList<Double>();
			for (gainMap g : gainMaps) {
				partitions.add(g.partition);
			}
			Node tree = buildTree(examples, targets, 0, attributes, partitions);
			System.out.println("Tree has " + nodes + " nodes");
			this.root = tree;
		} catch (IOException e) {
			System.out.println("Reset or read issue?");
			e.printStackTrace();
		}
	}
	
	/*
	 * Build a classification tree 
	 */
//		ID3 (Examples, Target_Attribute, Attributes)
	public Node buildTree(ArrayList<ArrayList<Double>> examples, ArrayList<Double> labels, int currentFeature, List<String> attributes, List<Double> partitions) { 
//	    Create a root node for the tree
		Node root;
		if (attributes.size() > 0) {
			root = new Node(attributes.get(0), partitions.get(0));
		} else {
			root = new Node("", 0);
		}
		nodes++;

//	    If all examples are positive, Return the single-node tree Root, with label = +.
//	    If all examples are negative, Return the single-node tree Root, with label = -.
		boolean positiveExample = false;
		boolean negativeExample = false;
		int weight = 0;
		for (int i = 0; i < examples.size(); i++) {
			Double d = labels.get(i);
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
			System.out.println("Labeling true");
			return root;
		} else if (!positiveExample) {
			root.label(false);
			System.out.println("Labeling false");
			return root;
		}

//	    If number of predicting attributes is empty, then Return the single node tree Root,
//	    with label = most common value of the target attribute in the examples.
		boolean label = (weight >= 0) ? true : false;
		if (attributes.size() == 0 || attributes == null) {
			root.label(label);
			System.out.println("Weighted label " + label);
			return root;
		}
		
//	    Otherwise Begin
//	        A < The Attribute that best classifies examples.
			double partition = partitions.get(0);

//	        Decision Tree attribute for Root = A.
//	        For each possible value, v_i, of A,
//	            Add a new tree branch below Root, corresponding to the test A = v_i.
//	            Let Examples(v_i) be the subset of examples that have the value v_i for A
				ArrayList<ArrayList<Double>> smallChildExamples = new ArrayList<ArrayList<Double>>();
				ArrayList<Double> smallLabels = new ArrayList<Double>();
				ArrayList<ArrayList<Double>> largeChildExamples = new ArrayList<ArrayList<Double>>();
				ArrayList<Double> largeLabels = new ArrayList<Double>();
				for (int i = 0; i < examples.size(); i++) {
					ArrayList<Double> l = examples.get(i);
					if (l.get(currentFeature) <= partition) {
						smallChildExamples.add(l);
						smallLabels.add(labels.get(i));
					} else {
						largeChildExamples.add(l);
						largeLabels.add(labels.get(i));
					}
				}	
//	            If Examples(v_i) is empty
//	                Then below this new branch add a leaf node with label = most common target value in the examples
				Node child = new Node("",0);
				nodes++;
				child.label( (weight >= 0) ? true : false );
				if (smallChildExamples.size() == 0) {
					root.setLeftChild(child);
				} else {
					root.setLeftChild(buildTree(smallChildExamples, smallLabels, currentFeature + 1, attributes.subList(1, attributes.size()), partitions.subList(1, partitions.size())));
				}

				if (largeChildExamples.size() == 0) {
					root.setRightChild(child);
				} else {
					root.setRightChild(buildTree(largeChildExamples, largeLabels, currentFeature + 1, attributes.subList(1, attributes.size()), partitions.subList(1, partitions.size())));
				}
//	            Else below this new branch add the subtree ID3 (Examples(v_i), Target_Attribute, Attributes – {A})
//	    End
//	    Return Root	
		return root;
	}
	
	/*
	 * Classify game map as a win for player X (true) or win 
	 * for player O (false) 
	 */
	public boolean classify(Character player, GameMap map) {
		ArrayList<String> featuresNames;
		ArrayList<Double> featuresVals;
		Node currentNode = root;
		Double currentValue = null;
		Boolean label = null;
		if (this.root != null) {
			featuresNames = SupportFunctions.featureNames();
			featuresVals = SupportFunctions.mapFeatures(map, 'X');
			while (currentNode != null) {
				//get feature
				for(int i = 1; i < featuresNames.size(); i++) {
					if (featuresNames.get(i).compareTo(currentNode.featureName) == 0) {
						currentValue = featuresVals.get(i);
						//System.out.println("Testing " + featuresNames.get(i) + " partitioned at " + currentValue);
						break;
					}
				}
				//test feature
				if (currentValue == null) {
					System.out.println("No features matched!");
				} else {
					label = currentNode.getLabel();
					//System.out.println("Labeled " + label);
					currentNode = currentNode.child(currentValue);
				}
			}
			return label;
		} else {
			System.out.println("Can't clasify, no tree built yet!");
		}
		return false;
	}
	
	class Node {
		public String featureName;
		public double partition;
		private Node leftChild, rightChild;
		private Boolean label;
		
		public Node(String featureName, double partition) {
			this.featureName = featureName;
			this.partition = partition;
			label = null;
			leftChild = null;
			rightChild = null;
		}
		
		public Node child(double value) {
			int direction = test(value);
			if (leftChild == null && rightChild == null) {
				return null;
			} else if (direction < 0) {
				return leftChild;
			} else {
				return rightChild;
			}
		}
		
		public Boolean getLabel() {
			return (label == null) ? null : label;
		}
		
		public int test(double value) {
			if (value <= partition) {
				return -1;
			} else {
				return 1;
			}
		}
		
		public void setLeftChild(Node newChild) {
			leftChild = newChild;
		}

		public void setRightChild(Node newChild) {
			rightChild = newChild;
		}
		
		public void label(boolean win) {
			this.label = win;
		}

		@Override
		public String toString() {
			return this.featureName + "partitioned at " + this.partition;
		}
	}
	
	class gainMap implements Comparable<gainMap> {
		public double gain;
		public double partition;
		
		public gainMap(double gain, double partition) {
			this.gain = gain;
			this.partition = partition;
		}

		@Override
		//gainMaps sort in descending order
		public int compareTo(gainMap other) { 
			if (this.gain > other.gain) {
				return -1;
			} else if (this.gain < other.gain) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	public static void main(String[] args) {
		DecisionTree tree = new DecisionTree();
		try {
			tree.setupTree(TRAINING_FILE);
			ArrayList<ArrayList<Object>> mapMap = SupportFunctions.generateWinStates(1);
			Character actual = (Character) mapMap.get(0).get(0);
			GameMap map = (GameMap) mapMap.get(0).get(1);
			System.out.println("Actual winner is " + actual);
			boolean c = tree.classify('X', map);
			System.out.println("Predicted for X: " + c);
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
