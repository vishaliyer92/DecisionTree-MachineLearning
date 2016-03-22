import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DecisionTreeML {

	private int numberOfNonLeafNodes = 0;

	TreeNode treePruned;
	TreeNode treeTemporary;

	public void setNonLeaf(int numberNonLeafNodes) {
		this.numberOfNonLeafNodes = numberNonLeafNodes;
	}

	public int numberOfNonLeafNodes() {
		int number = numberOfNonLeafNodes;
		setNonLeaf(0);
		return number;
	}

	public void calcNumNonLeafNodes(TreeNode parent) {
		if (!parent.isLeafNode()) {
			numberOfNonLeafNodes++;
			parent.setNodeNumber(numberOfNonLeafNodes);
			calcNumNonLeafNodes(parent.getLeft());
			calcNumNonLeafNodes(parent.getRight());
		}
	}

	public List<TreeNode> getLeafList(TreeNode parent) {
		List<TreeNode> leafList = new ArrayList<>();
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

	/**
	 * Function to get input from the specified data set file in the arguement
	 */
	@SuppressWarnings("resource")
	public static ArrayList<ArrayList<String>> fileUtility(String fName) {
		String file_Name = fName;
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

	public String majClassCompute(TreeNode parent) {
		int countZero = 0;
		int countOne = 0;
		String majority = "0";
		List<TreeNode> leafNodes = getLeafList(parent);
		for (TreeNode node : leafNodes) {
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

	public void replaceNode(TreeNode parent, int p) {
		if (!parent.isLeafNode()) {
			if (parent.getNodeNumber() == p) {
				String leftValueChanged = majClassCompute(parent);
				parent.setLeafNode(Boolean.TRUE);
				parent.setLeft(null);
				parent.setRight(null);
				parent.setLeafValue(leftValueChanged);
			} else {
				replaceNode(parent.getLeft(), p);
				replaceNode(parent.getRight(), p);
			}

		}
	}

	/**
	 * Function to check each row in the tree against a particular row of the
	 * data set
	 */
	public boolean verifyTreeRow(TreeNode parent, ArrayList<String> row,
			ArrayList<String> attributeList) {
		TreeNode current = parent;
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
	 * Function to make a copy for the original tree and store in another using
	 * recursion - used in post pruning
	 */
	public void treeCopy(TreeNode original, TreeNode copy) {
		copy.setLeafNode(original.isLeafNode());
		copy.setName(original.getName());
		copy.setLeafValue(original.getLeafValue());

		if (!original.isLeafNode()) {
			copy.setLeft(new TreeNode());
			copy.setRight(new TreeNode());

			treeCopy(original.getLeft(), copy.getLeft());
			treeCopy(original.getRight(), copy.getRight());
		}
	}

	/**
	 * Function to build the decision tree from given data set and list of
	 * attributes - ID3 Algorithm
	 */
	public TreeNode buildTreeML(ArrayList<ArrayList<String>> dataSet,
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
			return new TreeNode("0");

		} else if (attributeList.isEmpty() || countOne == dataSet.size() - 1) {
			return new TreeNode("1");
		} else {
			ComputeInformationGain gain = new ComputeInformationGain();
			String bestAttribute = gain.computeBestAttr(dataSet, attributeList);
			ArrayList<String> attributes2 = new ArrayList<String>();
			HashMap<String, ArrayList<ArrayList<String>>> newMap = ComputeInformationGain
					.bestAttributeClassification(dataSet, bestAttribute);
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
				return new TreeNode(value);
			}
			return new TreeNode(bestAttribute, buildTreeML(newMap.get("0"),
					attributes2), buildTreeML(newMap.get("1"), attributes2));
		}

	}

	public TreeNode computePostPrunedTree(TreeNode parent, int M,
			ArrayList<ArrayList<String>> validationData) {
		treePruned = new TreeNode();
		treeCopy(parent, treePruned);
		double accuracyPrunedTree = compAccuracy(treePruned, validationData);
		treeTemporary = new TreeNode();
		treeCopy(parent, treeTemporary);
		Random randomNumber = new Random();
		for (int j = 1; j <= M; j++) {
			calcNumNonLeafNodes(treeTemporary); // sets the number of leafnodes
			// in the class variable
			int N = numberOfNonLeafNodes();
			if (N > 1) {
				int P = randomNumber.nextInt(N) + 1;
				replaceNode(treeTemporary, P);
			} else {
				break;
			}

			double accuracyTempTree = compAccuracy(treeTemporary,
					validationData);
			if (accuracyTempTree < accuracyPrunedTree) {
				accuracyPrunedTree = accuracyTempTree;
				treeCopy(treeTemporary, treePruned);
			}
		}
		return treePruned;
	}

	public double compAccuracy(TreeNode node, ArrayList<ArrayList<String>> dataSet) {
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
			System.out.println("Arguments Required : \n" + "<Value of M>, "
					+ "<Path of Training Set>, " + "<Path of Validation Set>, "
					+ "<Path of Testing Set>, "
					+ "<1 (Print the tree) or 0 (Don't Print the tree)>");
			System.exit(0);
		} else {
			int M = Integer.parseInt(args[0]);
			String training_set = args[1];
			String validation_set = args[2];
			String test_set = args[3];
			// "1" or "0"
			boolean bool = false;

			int print = Integer.parseInt(args[4]);
			if (print == 1) {
				bool = true;
			}
			ArrayList<ArrayList<String>> TrainingSet = fileUtility(training_set);
			ArrayList<ArrayList<String>> TestSet = fileUtility(test_set);
			ArrayList<ArrayList<String>> ValidationSet = fileUtility(validation_set);
			ArrayList<String> attributeList = TrainingSet.get(0);
			DecisionTreeML tree = new DecisionTreeML();

			TreeNode learnRoot = tree.buildTreeML(TrainingSet, attributeList);
			if (bool == true) {
				System.out.println("\nDecision tree : \n");
				learnRoot.displayTree();
			}

			TreeNode computePostPrunedTree = tree.computePostPrunedTree(learnRoot,
					M, ValidationSet);
			if (bool == true) {
				System.out.println("\nPost Pruned tree : \n");
				computePostPrunedTree.displayTree();
			}
			System.out
					.println("\nAccuracy on Test set for original Decision tree: "
							+ tree.compAccuracy(learnRoot, TestSet));
			System.out.println("\nAccuracy on Test set for Post pruned tree : "
					+ tree.compAccuracy(computePostPrunedTree, TestSet));
		}
	}
}