package logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polar.game.GameMap;
import polar.game.styles.PlayStyle;

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
	
	/*
	 * New DecisionTrees have sorted gains and associated partition values
	 */
	public DecisionTree() {
		
		//get features and gains
		try {
			ArrayList<Object> blocks = (Classifier.gain(TRAINING_FILE, false));
			Double[] gains = null;
			String[] names = null;
			if (blocks.get(0) instanceof Double[]) {
				gains = (Double[]) blocks.get(0); //gains in file-order
			}
			double[] partitions = null;
			if (blocks.get(1) instanceof double[]) {
				partitions = (double[]) blocks.get(1); //partitions in file-order
			}
			if (blocks.get(2) instanceof String[]) {
				names = (String[]) blocks.get(2); //Feature names in file-order
			}
			
			ArrayList<gainMap> sorted = new ArrayList<gainMap>();
			if (gains != null && partitions != null) {
				for (int i = 0; i < gains.length; i++) {
					sorted.add(new gainMap(gains[i], partitions[i], names[i]));
				}
			}
			Collections.sort(sorted);
			this.gainMaps = sorted;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Get everything the object needs to start building the actual decision tree
	 * 
	 * @param examplesFile The filename of the labeled training examples, expects CSV with headers and the label as the first feature
	 */
	public void setupTree(String examplesFile) throws FileNotFoundException {

		ArrayList<ArrayList<Double>> examples = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> targets = new ArrayList<Double>(); 

		BufferedReader br = new BufferedReader(new FileReader(examplesFile));
		try {

			String[] headers = br.readLine().split(", ");
			ArrayList<String> headersList = new ArrayList<String>();
			for (int i = 1; i < headers.length; i++) {
				headersList.add(headers[i]);
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
			
			//attributes and partitions need to be sorted by gains here
			List<Double> partitions = new ArrayList<Double>();
			List<String> attributes = new ArrayList<String>();
			for (gainMap g : gainMaps) {
				partitions.add(g.partition);
				attributes.add(g.name);
			}
			Node tree = buildTree(examples, targets, attributes, partitions, headersList);
			System.out.println("Tree has " + nodes + " nodes");
			this.root = tree;
		} catch (IOException e) {
			System.out.println("Reset or read issue?");
			e.printStackTrace();
		}
	}
	
	/*
	 * Build a classification tree 
	 * Basic implementation of the ID3 Decision Tree algorithm
	 * 
	 * @param examples   A two dimensional array of double values in the same order as the training-set file
	 * @param labels     A set of labels in {-1.0, 1.0} indicating a win or loss for player X with same indexes as examples
	 * @param partitions A list of binary partition values in descending order by gain
	 * @param attributes A list of attribute names in descending order by gain
	 * @param headers    A list of attribute names in same order as training-set file, used to find current feature
	 */
	public Node buildTree(ArrayList<ArrayList<Double>> examples, ArrayList<Double> labels, List<String> attributes, List<Double> partitions, List<String> headers) { 
		
		//Root node has current attribute and associated partition value
		Node root;
		if (attributes.size() > 0) {
			root = new Node(attributes.get(0), partitions.get(0));
		} else {
			root = new Node("", 0);
		}
		nodes++;
		
		//Find the current feature index in examples by matching the file headers with the next attribute
		String s;
		int currentFeature = -1;
		if (attributes.size() != 0) {
			for (int i = 0; i < headers.size(); i++) {
				s = headers.get(i);
				if (s.equals(attributes.get(0))) {
					currentFeature = i;
				}
			}
			if (currentFeature < 0 ) {
				System.out.println("Problem: no feature found");
				System.exit(1);
			}
		} 

		// If all examples are of one type we'll return a node with that type's label
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
			return root;
		} else if (!positiveExample) {
			root.label(false);
			return root;
		} else {
			root.label((weight >= 0) ? true : false);
		}

		//If we're out of attributes to predict then we'll return a node with the most common label
		boolean label = (weight >= 0) ? true : false;
		if (attributes.size() == 0 || attributes == null) {
			root.label(label);
			return root;
		}
		
		//This is the partition for the attribute with the highest gain
		double partition = partitions.get(0);

		//Make subsets of the examples corresponding to the left and right children
		ArrayList<ArrayList<Double>> smallChildExamples = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> smallLabels = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> largeChildExamples = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> largeLabels = new ArrayList<Double>();

		//Add each example to the correct subset
		for (int i = 0; i < examples.size(); i++) {
			ArrayList<Double> example = examples.get(i);
			if (example.get(currentFeature) <= partition) {
				smallChildExamples.add(example);
				smallLabels.add(labels.get(i));
			} else {
				largeChildExamples.add(example);
				largeLabels.add(labels.get(i));
			}
		}	

		Node child = new Node("",0);
		nodes++;
		child.label( (weight >= 0) ? true : false );

		//If there are no examples for the left branch label that node with the most common label
		if (smallChildExamples.size() == 0) {
			root.setLeftChild(child);
		} else {
			//Recurse with the remining attributes
			root.setLeftChild(buildTree(smallChildExamples, smallLabels, attributes.subList(1, attributes.size()), partitions.subList(1, partitions.size()), headers));
		}

		//If there are no examples for the right branch label that node with the most common label
		if (largeChildExamples.size() == 0) {
			root.setRightChild(child);
		} else {
			//Recurse with the remining attributes
			root.setRightChild(buildTree(largeChildExamples, largeLabels, attributes.subList(1, attributes.size()), partitions.subList(1, partitions.size()), headers));
		}
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
		public String name;
		
		public gainMap(double gain, double partition, String name) {
			this.gain = gain;
			this.partition = partition;
			this.name = name;
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

	/*
	 * Test the decision tree here by testing it against
	 * a single new win state
	 */
	public static void main(String[] args) {
		PlayStyle.autoplay = true;
		DecisionTree tree = new DecisionTree();
		try {
			tree.setupTree(TRAINING_FILE);
			int correct = 0;
			int incorrect = 0;
			for (int i = 0; i < 50; i++) {
				ArrayList<ArrayList<Object>> mapMap = SupportFunctions.generateWinStates(1);
				Character actual = (Character) mapMap.get(0).get(0);
				GameMap map = (GameMap) mapMap.get(0).get(1);
				System.out.println("Actual winner is " + actual);
				boolean c = tree.classify('X', map);
				System.out.println("Predicted for X: " + c);
				if (actual == 'X' && c == true) {
					correct++;
				} else if (actual == 'O' && c == false) {
					correct++;
				} else {
					incorrect++;
				}
			}
			System.out.println("Correct: " + correct + " / " + (correct + incorrect));
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
