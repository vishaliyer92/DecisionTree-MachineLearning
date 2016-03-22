import java.util.Set;

public class TreeNode {

	private String leafVal;
	private int nodeNumber;
	private static int depthOfTree = -1;
	private Set<String> attributes;
	private TreeNode leftNode, rightNode;
	private String nameOfNode;
	private boolean isLeafNode;

	public TreeNode() {
		super();
	}

	public TreeNode(String leafValue) {
		this.leafVal = leafValue;
		this.setLeafNode(Boolean.TRUE);
	}

	public TreeNode getLeft() {
		return leftNode;
	}

	public void setLeft(TreeNode left) {
		this.leftNode = left;
	}

	public TreeNode getRight() {
		return rightNode;
	}

	public void setRight(TreeNode right) {
		this.rightNode = right;
	}

	public Set<String> getAttributes() {
		return attributes;
	}

	public boolean isLeafNode() {
		return isLeafNode;
	}

	public TreeNode(String bestAttr, TreeNode left, TreeNode right) {
		this.nameOfNode = bestAttr;
		this.leftNode = left;
		this.rightNode = right;
		this.setLeafNode(Boolean.FALSE);
	}

	public void setLeafNode(boolean isLeafNode) {
		this.isLeafNode = isLeafNode;
	}

	public String getLeafValue() {
		return leafVal;
	}

	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public void setLeafValue(String leafValue) {
		this.leafVal = leafValue;
	}

	public void setAttributes(Set<String> attributes) {
		this.attributes = attributes;
	}

	public String getName() {
		return nameOfNode;
	}

	public void setName(String name) {
		this.nameOfNode = name;
	}

	/**
	 * Function to display the tree based on the standard format as required
	 */
	public void displayTree() {
		depthOfTree++;
		if (this.nameOfNode == null) {
			System.out.print(" : " + leafVal);
		} else {
			System.out.println();
			for (int i = 0; i < depthOfTree; i++) {
				System.out.print(" | ");
			}
			System.out.print(nameOfNode + " = 0");
		}

		if (leftNode != null) {
			leftNode.displayTree();
			if (this.nameOfNode == null) {
				System.out.print(" : " + leafVal);
			} else {
				System.out.println();
				for (int i = 0; i < depthOfTree; i++) {
					System.out.print(" | ");
				}
				System.out.print(nameOfNode + " = 1");
			}
			rightNode.displayTree();
		}
		depthOfTree--;
	}

	public int getNodeNumber() {
		return nodeNumber;
	}

}
