import java.util.LinkedList;

public class BTree {
	
	public BTreeNode[] tree;

	public class BTreeNode {
		public BTreeNode parent;
		public LinkedList<TreeObject> keys;
		public LinkedList<BTreeNode> children;
		public int degree;
		public boolean isLeaf;
		
		public BTreeNode(int t) {
			keys = new LinkedList<TreeObject>();
			children = new LinkedList<BTreeNode>();
			degree = t;
		}
		
		/**
		 * Returns the parent Node of the working node
		 * @return parent node
		 */
		public BTreeNode getParent() {
			return parent;
		}
		
		/**
		 * Sets the parent of the working node
		 * @param BTreeNode parent
		 */
		public void setParent(BTreeNode parent) {
			this.parent = parent;
		}
		
		
		
		
	}
	
	public BTree(int degree) {
		tree = new BTreeNode[3];
	}
	
}
