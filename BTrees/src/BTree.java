
public class BTree {
	
	public BTreeNode[] tree;

	public class BTreeNode {
		public int parent;
		public TreeObject[] keys;
		public BTreeNode[] children;
		public int degree;
		
		public BTreeNode(int t) {
			keys = new TreeObject[(2*t)-1];			//TODO: I'm not sure is this is supposed to be a String or TreeObject
													// I think TreeObject because it says that those are the objects we will store?
			children = new BTreeNode[(2*t)];
			degree = t;
		}
		
		/**Finds key based on given substring and returns position in array or -1 if doesn't exist 
		 * @param sub - substring to find in keys array
		 */
		public int findKey(String sub) {
			if(keys.length < 1) {
				return -1;
			}
			int i = 0;
			TreeObject currentKey = keys[i];
			while(!(sub.equals(currentKey))) {
				currentKey = keys[i++];
			}
			if(currentKey == null) {
				return -1;
			}
			else
				return i;
		}
		
		/**Finds and returns the left child given a key
		 * @param  sub - substring to find in keys array
		 */
		public BTreeNode leftChild(String sub) {
			int index = findKey(sub);
			if(index == -1) {
				return null;
			}
			return children[index];
		}
		
		/**Finds and returns the right child given a key
		 * @param  sub - substring to find in keys array
		 */
		public BTreeNode rightChild(String sub) {
			int index = findKey(sub);
			if(index == -1) {
				return null;
			}
			return children[index+1];
		}
		
	}
	
	public BTree(int degree) {
		tree = new BTreeNode[3];
	}
	
}
