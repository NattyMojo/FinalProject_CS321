import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

public class BTree {
	
	public int degree;
	public String fileName;
	public BTreeNode root;
	public File file;
	public File metadataFile;
	public RandomAccessFile raf;
	RandomAccessFile rafm;
	public int nodeSize;
	public int insertion;
	public int rootOffset;
	public int maxKeys;

	public class BTreeNode {
		public int parent;
		public LinkedList<TreeObject> keys;
		public LinkedList<Integer> children;
		public int degree;
		public boolean isLeaf;
		public boolean isFull;
		public int numKeys; //ONLY counts unique keys
		public int offset;
		
		public BTreeNode(int t, boolean leaf, int offset) {					//TODO: Should this be int as an input? Should isLeaf be an input value as well?
			keys = new LinkedList<TreeObject>();
			children = new LinkedList<Integer>();
			degree = t;
			numKeys = 0;
			isLeaf = leaf;
			this.offset = offset;
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
		
		public void addChildren(TreeObject parentKey, int offset) {
			if(parentKey == null) {
				children.add(offset);
			}
			int index = keys.indexOf(parentKey);
			children.add(index, offset);
		}
		
		public int getLeftChild(TreeObject key) {
			if(!keys.contains(key)) {
				return -1;
			}
			int index = keys.indexOf(key);
			if(children.get(index) != null) {
				return children.get(index);
			}
			else
				return -1;
		}
		
		public int getRightChild(TreeObject key) {
			if(!keys.contains(key)) {
				return -1;
			}
			int index = keys.indexOf(key);
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
		 * Inserts a key into a node
		 * Assumes the node isn't full already
		 * Requires the node to be a leaf
		 * @param TreeObject to insert
		 */
		public void addKey(TreeObject key) {
			
			// Checks for key and duplicates if found
			if(keys.contains(key)) { 
				keys.get(keys.indexOf(key)).increaseDuplicateCount();
			}
			
			//Searches for an insert point from right to left, moving keys in the process;
			int i = numKeys - 1;

			while(i >= 0 && keys.get(i).compareTo(key) == 1) {
				i--;
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
		
		public void deleteChildPointer(int offset) {
			if(children.contains(offset)) {
				children.remove(offset);
			}
		}
		
	}
	
	public BTree(int degree, String fileName) {
		nodeSize = (32 * degree); // Actually this - 3, but for simplicity's sake there's a three byte gap
		insertion = 0;
		rootOffset = 0;
		maxKeys = (2*degree - 1);
		this.degree = degree;
		this.fileName = fileName;
		BTreeNode root = new BTreeNode(degree, true, insertion);
		this.root = root;
		root.setLeaf(true);
		root.setOffset(rootOffset);
		
		metadataFile = new File("metadata.data");
		file = new File(fileName);
		if(file.exists() && metadataFile.exists()) {
			file.delete();
			metadataFile.delete();
		}
		try {
			file.createNewFile();
			metadataFile.createNewFile();
			raf = new RandomAccessFile(file,"rw");
			rafm = new RandomAccessFile(metadataFile, "rw");
			
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
	
	//TODO: I will finish this today
	public void insert(long key) {
		if(root.getNumKeys() == maxKeys) {
			BTreeNode oldRoot = root;
			BTreeNode newRoot = new BTreeNode(degree, false, insertion);
			root = newRoot;
			this.increaseInsertionPoint();
			
			oldRoot.parent = root.getOffset();
			root.children.add(oldRoot.getOffset());
			
			this.alternateSplitChild(root, 0, oldRoot);
			
			TreeObject k = new TreeObject(key);
			this.findLeafAndInsert(root, k);
			this.writeMetaData();
		}
		TreeObject k = new TreeObject(key);
		this.findLeafAndInsert(root, k);
	}
	
	//Traverses downwards until it finds a leaf and then inserts
	//Unfortunately this all must be done in BTree to maintain access to read and write
	public void findLeafAndInsert(BTreeNode currentNode, TreeObject k) {
		if(currentNode.isLeaf) {
			currentNode.addKey(k);
		} else {
			int i = 0;
			while(currentNode.keys.get(i).compareTo(k) == -1) {
				i++;
			}
			int childOffset = currentNode.children.get(i);
			BTreeNode nextNode = this.readNode(childOffset);
			
			if(nextNode.isFull()) {
				this.alternateSplitChild(currentNode, i, nextNode);
				if (k.compareTo(currentNode.keys.get(i)) == 1) {
					int rightChildOffset = currentNode.children.get(i+1);
					BTreeNode rightNode = this.readNode(rightChildOffset);
					this.findLeafAndInsert(rightNode, k);
				} else {
					this.findLeafAndInsert(nextNode, k);
				}
			} else {
				this.findLeafAndInsert(nextNode, k);
			}
			
			
		}
		
		
	}
	
	public TreeObject search(BTreeNode node, long key) {
		TreeObject object = new TreeObject(key);
		for(int i = 0; i < node.getNumKeys(); i++) {
			if(object.compareTo(node.getKey(i)) == 0) {
				return node.getKey(i);
			}
		}
		if(!node.isLeaf()) {
			for(int i = 0; i < node.getNumKeys() + 1; i++) {
				int offset = node.getChildren().get(i);
				BTreeNode next = readNode(offset);
				return search(next, key);
			}
		}
		return null;
	}

	//assumes child is full and parent is not
	public void alternateSplitChild(BTreeNode parent, int childIndex, BTreeNode child) {
		
		boolean newLeaf = child.isLeaf();
		BTreeNode rightChild = new BTreeNode(degree, newLeaf, insertion);
		rightChild.parent = parent.getOffset();
		increaseInsertionPoint();
		
		//copies and removes children, except the one being sent up
		for(int i = 0; i < degree - 1; i++) {
			rightChild.keys.add(i, child.keys.remove(i+degree));
			child.numKeys--;
			rightChild.numKeys++;
		}
		
		//copies and removes keys, except the one being sent up
		if(!newLeaf) {
			for(int i = 0; i < degree; i++) {
				rightChild.children.add(i, child.children.remove(i+degree));
			}
		}
		
		parent.children.add(childIndex+1, rightChild.getOffset());
		parent.keys.add(childIndex, child.keys.removeLast());
		child.numKeys--;
		parent.numKeys++;
		
		this.writeToFile(parent);
		this.writeToFile(child);
		this.writeToFile(rightChild);
		
	}
	
	/**
	 * Just increases the insertion point to after the next node, use this after every node create
	 */
	public void increaseInsertionPoint() {
		insertion += nodeSize;
	}
	
	/**
	 * Writes the tree MetaData to the disk at the beginning of the BTree file
	 */
	public void writeMetaData() {
        try {
            rafm.seek(0); //Moves to beginning of File
            rafm.writeInt(degree); //Writes the degree of the tree
            rafm.writeInt(rootOffset);
        } catch (IOException ioe) {
            System.err.println("Could not write MetaData");
            System.exit(-1);
        }
    }
	
	/**
	 * Writes node metadata ahead of node information
	 * @param Node to write data of
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
			for(int i = 0; i < (2 * degree) - 1; i++) {
				if(i < node.getNumKeys() + 1 && !node.isLeaf()) {
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
					raf.writeInt(0);
				}
			}
		} catch (IOException e) {
			System.err.println("Could not write to file");
			System.exit(-1);
		}
	}
	
	//Needs to be fixed to accommodate different functionality for Node methods
	//Because addKeyIfLeaf and other insertion methods assume initial insertion and sorting,
	//the node's variables will have to be set directly and strictly in order
	public BTreeNode readNode(int offset) {
		BTreeNode node = new BTreeNode(0, false, 0);
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
					node.addChildren(null, child);
					long data = raf.readLong();
					key = new TreeObject(data);
					int dup = raf.readInt();
					key.setDuplicateCount(dup);
					node.addKey(key); 
				}
				if(i == node.getNumKeys() && !node.isLeaf()) {
					int child = raf.readInt();
					node.addChildren(null, child);
				}
			}
		} catch (IOException e) {
			System.err.println("Could not read from disk");
			System.exit(-1);
		}
		
		return node;
		
	}
	
	
//	public static void main(String[] args) {
//		BTree tester = new BTree(128, "test");
//		BTree
//	}
	
}