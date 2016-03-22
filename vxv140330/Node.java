import java.util.Set;

public class Node {
	private Node left, right;
	private String name;
	private boolean isLeafNode;
	private String leafValue;
	private int nodeNumber;
	private static int depth = -1;
	private Set<String> attributes;
	
	
	public Node() {
		super();
	}
	
	public Node(String leafValue){
		this.leafValue = leafValue;
		this.setLeafNode(Boolean.TRUE);
	}
	
	public Node(String bestAttr, Node left, Node right){
		this.name = bestAttr;
		this.left = left;
		this.right = right;
		this.setLeafNode(Boolean.FALSE);
	}
	
	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}
	
	
	public boolean isLeafNode() {
		return isLeafNode;
	}

	public void setLeafNode(boolean isLeafNode) {
		this.isLeafNode = isLeafNode;
	}

	public String getLeafValue() {
		return leafValue;
	}

	public void setLeafValue(String leafValue) {
		this.leafValue = leafValue;
	}

	public Set<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<String> attributes) {
		this.attributes = attributes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * Function to display the tree based on the standard format as required
	 */
	public void display(){
		depth++;
		if(this.name == null){
			System.out.print(" : " + leafValue);
		}
		else{
			System.out.println();
			for(int i=0; i<depth;i++){
				System.out.print(" | ");
			}
			System.out.print(name + " = 0");
		}

		if(left != null){
			left.display();
			if(this.name == null){
				System.out.print(" : " + leafValue);
			}
			else{
				System.out.println();
				for(int i=0; i<depth;i++){
					System.out.print(" | ");
				}
				System.out.print(name + " = 1" );
			}
			right.display();
		}
		depth--;
	}

	public int getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}
	
	
}
