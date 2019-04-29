
public class BTree {

	public class BTreeNode {
		public String[] keys;
		public BTreeNode[] children;
		public int degree;
		
		public BTreeNode(int t) {
			keys = new String[(2*t)-1];
			children = new BTreeNode[(2*t)]
			degree = t;
		}
		
	}
	
}
