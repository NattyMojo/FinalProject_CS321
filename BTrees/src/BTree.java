import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

public class BTree {
	
	public BTreeNode[] tree;
	public int degree;
	public String fileName;
	public BTreeNode root;
	public File file;
	public RandomAccessFile raf;

	public class BTreeNode {
		public BTreeNode parent;
		public LinkedList<TreeObject> keys;
		public LinkedList<BTreeNode> children;
		public int degree;
		public boolean isLeaf;
		public boolean isFull;
		public int numKeys;
		
		public BTreeNode(int t) {					//TODO: Should this be int as an input?
			keys = new LinkedList<TreeObject>();
			children = new LinkedList<BTreeNode>();
			degree = t;
			numKeys = 0;
		}
		
		public boolean isLeaf() {
			return isLeaf;
		}
		
		public void setLeaf(boolean bool) {
			isLeaf = bool;
		}
		
		/**
		 * Checks to see if the node is full of keys
		 * @return boolean value based on if it is full
		 */
		public boolean isFull() {
			boolean ret = false;
			if(keys.size() >= (2*degree)-1) {
				ret = true;
			}
			return ret;
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
		
		/**
		 * Returns all children of a node in a linked list
		 * @return LinkedList of node's children
		 */
		public LinkedList<BTreeNode> getChildren() {
			return children;
		}
		
		public int getNumKeys() {
			return numKeys;
		}
		
		/**
		 * Inserts a key into a node assuming the node is not full ***Will fix later
		 * @param TreeObject to insert
		 */
		public void addKey(TreeObject key) {
			int i = 0;
			if(keys.get(i) != null) {
				while(keys.get(i).compareTo(key) == -1) {
					i++;
				}
			}
			keys.add(i, key);
			numKeys++;
		}
		
		/**
		 * Removes a given key from the list of keys
		 * @param TreeObject to remove
		 * @return will return TreeObject if it is in the node or null if not
		 */
		public TreeObject removeKey(TreeObject key) {
			TreeObject ret = null;
			if(keys.contains(key)) {
				ret = keys.remove(keys.indexOf(key));
			}
			numKeys--;
			return ret;
		}	
		
	}
	
	public BTree(int degree, String fileName) {
		tree = new BTreeNode[3];
		this.degree = degree;
		this.fileName = fileName;
		BTreeNode root = new BTreeNode(degree);
		this.root = root;
		root.setLeaf(true);
		
		file = new File(fileName);
		if(file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			raf = new RandomAccessFile(file,"rw");
		} catch (IOException ioe) {
			System.err.println("Could not create file \"" + fileName + "\"");
			System.exit(-1);
		}		
		writeMetaData();
	}
	
	/**
	 * Writes the tree MetaData to the disk at the beginning of the BTree file
	 */
	public void writeMetaData() {
        try {
            raf.seek(0); //Moves to beginning of File
            raf.writeInt(degree); //Writes the degree of the tree
            //TODO: Should probably add root offset here?
        } catch (IOException ioe) {
            System.err.println("Could not write MetaData");
            System.exit(-1);
        }
    }
	
	/**
	 * Just returns the root node for whenever we need it
	 * @return Root node
	 */
	public BTreeNode getRoot() {
		return root;
	}
	
}
