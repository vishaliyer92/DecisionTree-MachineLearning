import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DecisionTree {

	private int numberNonLeafNodes = 0;

	Node prunedTree;
	Node tempTree;

	/**
	 * Function to get input from the specified data set file in the arguement
	 */
	@SuppressWarnings("resource")
	public static ArrayList<ArrayList<String>> FileUtils(String fileName) {

		// String fileDirectory = System.getProperty("user.dir");
		// StringBuilder stringBuilder = new StringBuilder(fileDirectory);
		// stringBuilder.append(System.getProperty("file.separator"));
		// stringBuilder.append(fileName);
		// String file_Name = stringBuilder.toString();
		String file_Name = fileName;
		ArrayList<ArrayList<String>> input_data = new ArrayList<ArrayList<String>>();
		File file = new File(file_Name);
		Scanner input;
		try {
			input = new Scanner(file);
			while (input.hasNext()) {
				String[] inputForEachRow = input.next().split(",");
				input_data.add(new ArrayList<String>(Arrays
						.asList(inputForEachRow)));

			}
		} catch (IOException e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
		}

		return input_data;

	}

	// Helper functions

	public int getNumberNonLeafNodes() {
		int number = numberNonLeafNodes;
		setNumberNonLeafNodes(0);
		return number;
	}

	public void setNumberNonLeafNodes(int numberNonLeafNodes) {
		this.numberNonLeafNodes = numberNonLeafNodes;
	}

	public void calculateNumLeafNodes(Node parent) {
		if (!parent.isLeafNode()) {
			numberNonLeafNodes++;
			parent.setNodeNumber(numberNonLeafNodes);
			calculateNumLeafNodes(parent.getLeft());
			calculateNumLeafNodes(parent.getRight());
		}
	}

	public List<Node> getLeafList(Node parent) {
		List<Node> leafList = new ArrayList<>();
		if (parent.isLeafNode()) {
			leafList.add(parent);
		} else {
			if (!parent.getLeft().isLeafNode()) {
				getLeafList(parent.getLeft());
			}
			if (!parent.getRight().isLeafNode()) {
				getLeafList(parent.getRight());
			}
		}
		return leafList;
	}

	public String computeMajorityClass(Node parent) {
		int countZero = 0;
		int countOne = 0;
		String majority = "0";
		List<Node> leafNodes = getLeafList(parent);
		for (Node node : leafNodes) {
			if (node.getLeafValue().equalsIgnoreCase("1")) {
				countOne++;
			} else {
				countZero++;
			}
		}
		if (countOne > countZero) {
			majority = "1";
		}

		return majority;
	}

	public void replaceNode(Node parent, int p) {
		if (!parent.isLeafNode()) {
			if (parent.getNodeNumber() == p) {
				String leafValueToBeChanged = computeMajorityClass(parent);
				parent.setLeafNode(Boolean.TRUE);
				parent.setLeft(null);
				parent.setRight(null);
				parent.setLeafValue(leafValueToBeChanged);
			} else {
				replaceNode(parent.getLeft(), p);
				replaceNode(parent.getRight(), p);
			}

		}
	}

	/**
	 * Function to make a copy for the original tree and store in another using
	 * recursion - used in post pruning
	 */
	public void copyTree(Node original, Node copy) {
		copy.setLeafNode(original.isLeafNode());
		copy.setName(original.getName());
		copy.setLeafValue(original.getLeafValue());

		if (!original.isLeafNode()) {
			copy.setLeft(new Node());
			copy.setRight(new Node());

			copyTree(original.getLeft(), copy.getLeft());
			copyTree(original.getRight(), copy.getRight());
		}
	}

	/**
	 * Function to build the decision tree from given data set and list of
	 * attributes - ID3 Algorithm
	 */
	public Node buildTree(ArrayList<ArrayList<String>> dataSet,
			ArrayList<String> attributeList) throws FileNotFoundException {
		int countZero = 0;
		int countOne = 0;

		for (int i = 1; i < dataSet.size(); i++) {
			if (dataSet.get(i).get(dataSet.get(i).size() - 1)
					.equalsIgnoreCase("1")) {
				countOne++;
			} else {
				countZero++;
			}
		}
		if (attributeList.isEmpty() || countZero == dataSet.size() - 1) {
			return new Node("0");

		} else if (attributeList.isEmpty() || countOne == dataSet.size() - 1) {
			return new Node("1");
		} else {
			ComputeInfoGain gain = new ComputeInfoGain();
			String bestAttribute = gain.bestAttribute(dataSet, attributeList);
			ArrayList<String> attributes2 = new ArrayList<String>();
			HashMap<String, ArrayList<ArrayList<String>>> newMap = ComputeInfoGain
					.classifyOnBestAttribute(dataSet, bestAttribute);
			for (String attr : attributeList) {
				if (!attr.equalsIgnoreCase(bestAttribute)) {
					attributes2.add(attr);
				}
			}
			if (newMap.size() < 2) {
				String value = "0";
				if (countOne > countZero) {
					value = "1";
				}
				return new Node(value);
			}
			return new Node(bestAttribute, buildTree(newMap.get("0"),
					attributes2), buildTree(newMap.get("1"), attributes2));
		}

	}

	/**
	 * Function to check each row in the tree against a particular row of the
	 * data set
	 */
	public boolean verifyTreeRow(Node parent, ArrayList<String> row,
			ArrayList<String> attributeList) {
		Node current = parent;
		while (true) {
			if (current.isLeafNode()) {
				if (current.getLeafValue().equalsIgnoreCase(
						row.get(row.size() - 1))) {
					return true;
				} else
					return false;
			}

			int index = attributeList.indexOf(current.getName());
			String value = row.get(index);
			if (value.equalsIgnoreCase("0")) {
				current = current.getLeft();
			} else
				current = current.getRight();

		}
	}

	/**
	 * pruning using Algorithm 1 output is a post pruned decision tree
	 */
	public Node computePostPrunedTree(Node parent, int M,
			ArrayList<ArrayList<String>> validationData) {
		prunedTree = new Node();
		copyTree(parent, prunedTree);
		double accuracyPrunedTree = computeAccuracy(prunedTree, validationData);
		// System.out.println("Here the accuracy : " + accuracyPrunedTree);
		tempTree = new Node();
		// for (int i = 1; i <= l; i++) {
		copyTree(parent, tempTree);
		Random random = new Random();
		// int M = 1 + random.nextInt(k);
		for (int j = 1; j <= M; j++) {
			// System.out.println("\n***Loop " + j + "***");
			calculateNumLeafNodes(tempTree); // sets the number of leafnodes
												// in the class variable
			int N = getNumberNonLeafNodes();
			// System.out.print("N : " + N);
			if (N > 1) {
				int P = random.nextInt(N) + 1;
				// System.out.print(" ; P : " + P);
				replaceNode(tempTree, P);
			} else {
				break;
			}

			double accuracyTempTree = computeAccuracy(tempTree, validationData);
			// System.out.println("accuracyTempTree : " + accuracyTempTree
			// + " ; accuracyPrunedTree : " + accuracyPrunedTree);
			if (accuracyTempTree < accuracyPrunedTree) {
				accuracyPrunedTree = accuracyTempTree;
				copyTree(tempTree, prunedTree);
				// System.out.println("Copy Happened");
			}
		}
		return prunedTree;
	}

	/**
	 * Function to calculate the accuracy of the tree learned based on a certain
	 * test data set
	 */
	public double computeAccuracy(Node node,
			ArrayList<ArrayList<String>> dataSet) {
		double accuracy = 0;
		int positiveExamples = 0;
		ArrayList<String> attributes = dataSet.get(0);

		for (ArrayList<String> dataRow : dataSet.subList(1, dataSet.size())) {
			boolean verifyData = verifyTreeRow(node, dataRow, attributes);
			if (verifyData) {
				positiveExamples++;
			}
		}
		accuracy = (((double) positiveExamples / (double) (dataSet.size() - 1)) * 100.00);
		return accuracy;
	}

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length == 0) {
			System.out.println("Please pass some arguments.");
			System.exit(0);
		} else {
			// Take command line input for values of <M>, <training_set>,
			// <validation_set>, <test_set>, <to-print> - {yes,no}
			int M = Integer.parseInt(args[0]);
			// int M = 10;
			String training_set = args[1];
			String validation_set = args[2];
			String test_set = args[3];
			// "1" or "0"
			boolean bool = false;

			int to_print = Integer.parseInt(args[4]);
			if (to_print == 1) {
				bool = true;
			}
			ArrayList<ArrayList<String>> TrainingSet = FileUtils(training_set);
			ArrayList<ArrayList<String>> TestSet = FileUtils(test_set);
			ArrayList<ArrayList<String>> ValidationSet = FileUtils(validation_set);
			ArrayList<String> attributeList = TrainingSet.get(0);
			// System.out.println("Attribute list : " +
			// attributeList.toString());
			DecisionTree tree = new DecisionTree();

			Node learningRoot = tree.buildTree(TrainingSet, attributeList);
			if (bool == true) {
				System.out
						.println("\nDecision tree to standard output in required format: \n");
				learningRoot.display();
			}

			Node computePostPrunedTree = tree.computePostPrunedTree(
					learningRoot, M, ValidationSet);
			if (bool == true) {
				System.out
						.println("\nPost Pruned tree to standard output in required format: \n");
				computePostPrunedTree.display();
			}
			System.out.println("\nAccuracy on Test set for Decision tree: "
					+ tree.computeAccuracy(learningRoot, TestSet));
			System.out.println("\nAccuracy on Test set for Post pruned tree: "
					+ tree.computeAccuracy(computePostPrunedTree, TestSet));
		}
	}
}