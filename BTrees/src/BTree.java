
public class BTree {

	public class BTreeNode {
		public String[] keys;
		public BTreeNode[] children;
		public int degree;
		
		public BTreeNode(int t) {
			keys = new String[(2*t)-1];
			children = new BTreeNode[(2*t)];
			degree = t;
		}
		
		/*Finds key based on given substring and returns position in array or -1 if doesn't exist 
		 * @param sub - substring to find in keys array
		 */
		public int findKey(String sub) {
			if(keys.length < 1) {
				return -1;
			}
			int i = 0;
			String currentKey = keys[i];
			while(!(sub.equals(currentKey))) {
				currentKey = keys[i++];
			}
			if(currentKey == null) {
				return -1;
			}
			else
				return i;
		}
		
		/*Finds and returns the left child given a key
		 * @param  sub - substring to find in keys array
		 */
		public BTreeNode leftChild(String sub) {
			int index = findKey(sub);
			if(index == -1) {
				return null;
			}
			return children[index+1];					//TODO: I don't think I did this right please check	
		}
		
		/*Finds and returns the right child given a key
		 * @param  sub - substring to find in keys array
		 */
		public BTreeNode rightChild(String sub) {
			int index = findKey(sub);
			if(index == -1) {
				return null;
			}
			return children[index+2];					//TODO: I don't think I did this right please check	
		}
	}
	
}
