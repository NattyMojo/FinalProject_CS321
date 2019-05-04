import java.io.File;
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
	public int nodeSize;
	public int insertion;
	public int rootOffset;

	public class BTreeNode {
		public int parent;
		public LinkedList<TreeObject> keys;
		public LinkedList<Integer> children;
		public int degree;
		public boolean isLeaf;
		public boolean isFull;
		public int numKeys;
		public int offset;
		
		public BTreeNode(int t) {					//TODO: Should this be int as an input?
			keys = new LinkedList<TreeObject>();
			children = new LinkedList<Integer>();
			degree = t;
			numKeys = 0;
		}
		
		public TreeObject getKey(int index) {
			return keys.get(index);
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
		public int getParent() {
			return parent;
		}
		
		/**
		 * Sets the parent of the working node
		 * @param BTreeNode parent
		 */
		public void setParent(int parent) {
			this.parent = parent;
		}
		
		public int getOffset() {
			return offset;
		}
		
		public void setOffset(int offset) {
			this.offset = offset;
		}
		
		/**
		 * Returns all children of a node in a linked list
		 * @return LinkedList of node's children
		 */
		public LinkedList<Integer> getChildren() {
			return children;
		}
		
		public void addChildren(int offset) {
			int i = 0;
			while(children.get(i) < offset) {
				i++;
			}
			children.add(i, offset);
		}
		
		public int getLeftChild(TreeObject t) {
			if(!keys.contains(t)) {
				return -1;
			}
			int index = keys.indexOf(t);
			if(children.get(index) != null) {
				return children.get(index);
			}
			else
				return -1;
		}
		
		public int getRightChild(TreeObject t) {
			if(!keys.contains(t)) {
				return -1;
			}
			int index = keys.indexOf(t);
			if(children.get(index+1) != null) {
				return children.get(index+1);
			}
			else
				return -1;
		}
		
		public int getNumKeys() {
			return numKeys;
		}
		
		public void setNumKeys(int num) {
			numKeys = num;
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
				if(keys.get(i).compareTo(key) == 0) {
					keys.get(i).increaseDuplicateCount();
				}
			}
			else {
				keys.add(i, key);
				numKeys++;
			}
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
		nodeSize = (32 * degree - 3);
		insertion = 12 + nodeSize;
		rootOffset = 12;
		this.degree = degree;
		this.fileName = fileName;
		BTreeNode root = new BTreeNode(degree);
		this.root = root;
		root.setLeaf(true);
		root.setOffset(rootOffset);
		
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
	 * Just returns the root node for whenever we need it
	 * @return Root node
	 */
	public BTreeNode getRoot() {
		return root;
	}
	
//	public void insert(long key) {
//		BTreeNode current = root;
//		if(!current.isFull() && current.isLeaf()) {
//			
//		}
//	}

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
	 * Writes node metadata ahead of node information
	 */
	public void writeNodeMetaData(BTreeNode node) {
		try {
			raf.seek(node.getOffset());
			raf.writeBoolean(node.isLeaf());
			raf.writeInt(node.getNumKeys());
			raf.writeInt(node.getParent());
		} catch (IOException e) {
			System.err.println("Could not write Node MetaData");
			System.exit(-1);
		}
	}
	
	/**
	 * Writes a given node to the file at the node's specified offset
	 * @param Node to write
	 */
	public void writeToFile(BTreeNode node) {
		try {
			writeNodeMetaData(node);
			for(int i = 0; i < (2 * degree) - 1; i++) {
				if(i < node.getNumKeys() + 1) {
					raf.writeInt(node.getChildren().get(i));
				}
				else {
					raf.writeInt(0);
				}
				if(i < node.getNumKeys()) {
					long data = node.getKey(i).getData();
					raf.writeLong(data);
					int dup = node.getKey(i).getDuplicateCount();
					raf.writeInt(dup);
				}
				else {
					raf.writeLong(0);
				}
				if(i == node.getNumKeys() && !node.isLeaf()) {
					raf.writeInt(node.getChildren().get(i+1));
				}
			}
		} catch (IOException e) {
			System.err.println("Could not write to file");
			System.exit(-1);
		}
	}
	
	public BTreeNode readNode(int offset) {
		BTreeNode node = new BTreeNode(0);
		TreeObject key = null;
		node.setOffset(offset);
		try {
			raf.seek(offset);
			boolean leaf = raf.readBoolean();
			int numkeys = raf.readInt();
			int parent = raf.readInt();
			node.setLeaf(leaf);
			node.setNumKeys(numkeys);
			node.setParent(parent);
			for(int i = 0; i < (2 * degree) - 1; i++) {
				if(i < node.getNumKeys()) {
					int child = raf.readInt();
					node.addChildren(child);
					long data = raf.readLong();
					key = new TreeObject(data);
					int dup = raf.readInt();
					key.setDuplicateCount(dup);
					node.addKey(key);
				}
				if(i == node.getNumKeys() && !node.isLeaf()) {
					int child = raf.readInt();
					node.addChildren(child);
				}
			}
		} catch (IOException e) {
			System.err.println("Could not read from disk");
			System.exit(-1);
		}
		
		return node;
		
	}
	
	
}
