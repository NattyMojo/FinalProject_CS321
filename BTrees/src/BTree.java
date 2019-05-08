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
		public void addKeyIfLeaf(TreeObject key) {
			
			// Checks for key and duplicates if found
			if(keys.contains(key)) { 
				keys.get(keys.indexOf(key)).increaseDuplicateCount();
			}
			
			//Searches for an insert point from right to left, moving keys in the process;
			int i = numKeys - 1;

			while(i >= 0 && keys.get(i).compareTo(key) == 1) {
				keys.add(i+1, keys.get(i));
				i--;
			}

			keys.add(i+1, key);
			numKeys++;

		}
		
		public void addKeyToParent(TreeObject key) {
			int i = numKeys - 1;
			while(i >= 0 && keys.get(i).compareTo(key) == 1) {
				keys.add(i+1, keys.get(i));
				i--;
			}
			keys.add(i+1, key);
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
		tree = new BTreeNode[3];
		nodeSize = (32 * degree - 3);
		insertion = 5 + nodeSize;
		rootOffset = 5;
		maxKeys = (2*degree);
		this.degree = degree;
		this.fileName = fileName;
		BTreeNode root = new BTreeNode(degree, true, insertion);
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
	
	//TODO: I will finish this today
	public void insert(long key) {
		//adds initial key if empty
		if(root == null) {
			root = new BTreeNode(degree, true, insertion);
			TreeObject k = new TreeObject(key);
			root.addKeyIfLeaf(k);
			this.writeToFile(root);
			increaseInsertionPoint();
			
		} else {
			//If root is full a special split occurs
			splitRoot(root); //This should cover it lol
			
//			if (root.numKeys == 2*degree-1) {
//				BTreeNode newRoot = new BTreeNode(degree, false, insertion);
//				newRoot.children.add(0, root.getOffset());
//				newRoot.splitChild(0, root);
//				
//				
//				TreeObject k = new TreeObject(key);
//				BTreeNode currentNode = newRoot;
//				
//				this.findLeafAndInsert(currentNode, k);
//				
//				this.writeToFile(root);
//				this.writeToFile(newRoot);
//				
//				root = newRoot;
//				
//			} else {
//				//Normal insert
//				TreeObject k = new TreeObject(key);
//				this.findLeafAndInsert(root, k);
//			}
			
		}
		
	}
	
	//Traverses downwards until it finds a leaf and then inserts
	//Unfortunately this all must be done in BTree to maintain access to read and write
	public void findLeafAndInsert(BTreeNode currentNode, TreeObject k) {
		boolean done = false;
		boolean needSplit = false;
		
		while(!done) {
			//Chooses child for traversal
			int i = 0;
			while(currentNode.keys.get(i).compareTo(k) == -1) {
				i++;
			}
			int childOffset = currentNode.children.get(i);
			currentNode = this.readNode(childOffset);
			
			if(currentNode.isFull)
			if(currentNode.isLeaf()) {
				done = true;
			}
		}
		
	}

	/**
	 * Splits an inner node into 2 new nodes and leaves the old one
	 * in the file with no pointers to it
	 * @param Node to split
	 */
	public void splitNode(BTreeNode node) {
		int parentOffset = node.parent;
		BTreeNode parent = readNode(parentOffset);
		int middleIndex = node.getNumKeys()/2;
		parent.addKeyToParent(node.getKey(middleIndex));
		
		BTreeNode newLeft = new BTreeNode(degree, true, insertion);
		for(int i = 0; i < middleIndex; i++) {
			TreeObject key = node.getKey(i);
			newLeft.addKeyIfLeaf(key);
			if(node.getChildren().isEmpty() == false) {
				if(node.getLeftChild(key) != -1) {
					newLeft.addChildren(key, node.getLeftChild(key));
				}
				if(node.getRightChild(key) != -1) {
					newLeft.addChildren(key, node.getRightChild(key));
				}
				newLeft.setLeaf(false);
			}
		}
		increaseInsertionPoint();
		
		BTreeNode newRight = new BTreeNode(degree, true, insertion);
		for(int i = middleIndex+1; i < node.getNumKeys(); i++) {
			TreeObject key = node.getKey(i);
			newRight.addKeyIfLeaf(key);
			if(node.getChildren().isEmpty() == false) {
				if(node.getLeftChild(key) != -1) {
					newLeft.addChildren(key, node.getLeftChild(key));
				}
				if(node.getRightChild(key) != -1) {
					newRight.addChildren(key, node.getRightChild(key));
				}
				newRight.setLeaf(false);
			}
		}
		increaseInsertionPoint();
		
		parent.deleteChildPointer(node.getOffset());	//This is because I am not going to be overwriting the current node, it just wont have pointers to it
		parent.addChildren(parent.getKey(middleIndex), newLeft.getOffset());
		parent.addChildren(parent.getKey(middleIndex), newRight.getOffset());
		
		writeToFile(parent);
		writeToFile(newLeft);
		writeToFile(newRight);
	}
	
	/**
	 * Splits root and creates a new one. Throws away the pointer to the old root
	 * @param Root node
	 */
	public void splitRoot(BTreeNode root) {
		BTreeNode newRoot = new BTreeNode(0, false, insertion);
		increaseInsertionPoint();
		this.root = newRoot;
		int middleIndex = root.getNumKeys()/2;
		newRoot.addKeyToParent(root.getKey(middleIndex));
		newRoot.setNumKeys(1);
		
		BTreeNode newLeft = new BTreeNode(degree, true, insertion);
		for(int i = 0; i < middleIndex; i++) {
			TreeObject key = root.getKey(i);
			newLeft.addKeyIfLeaf(key);
			if(root.getChildren().isEmpty() == false) {
				if(root.getLeftChild(key) != -1) {
					newLeft.addChildren(key, root.getLeftChild(key));
				}
				if(root.getRightChild(key) != -1) {
					newLeft.addChildren(key, root.getRightChild(key));
				}
				newLeft.setLeaf(false);
			}
		}
		increaseInsertionPoint();
		
		BTreeNode newRight = new BTreeNode(degree, true, insertion);
		for(int i = middleIndex+1; i < root.getNumKeys(); i++) {
			TreeObject key = root.getKey(i);
			newRight.addKeyIfLeaf(key);
			if(root.getChildren().isEmpty() == false) {
				if(root.getLeftChild(key) != -1) {
					newLeft.addChildren(key, root.getLeftChild(key));
				}
				if(root.getRightChild(key) != -1) {
					newRight.addChildren(key, root.getRightChild(key));
				}
				newRight.setLeaf(false);
			}
		}
		increaseInsertionPoint();
		
		newRoot.addChildren(newRoot.getKey(middleIndex), newLeft.getOffset());
		newRoot.addChildren(newRoot.getKey(middleIndex), newRight.getOffset());
		
		writeToFile(newRoot);
		writeToFile(newLeft);
		writeToFile(newRight);
	}
	
	/**
	 * Just increases the insertion point to after the next node, use this after every node create
	 */
	public void increaseInsertionPoint() {
		insertion += nodeSize;
	}
	
	/**
	 * Writes the tree MetaData to the disk at the beginning of the BTree file
	 * TODO: Remove this? How is it useful? It also offsets the nodes too much, making them too big if using optimal degree
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
			writeNodeMetaData(node);			//writes 9 bytes
			for(int i = 0; i < (2 * degree) - 1; i++) {
				if(i < node.getNumKeys() + 1 && !node.isLeaf) {
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
					node.addKeyIfLeaf(key); 
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
