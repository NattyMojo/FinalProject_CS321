import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.LinkedList;

public class BTree {
	
	public int degree;
	public String fileName;
	public BTreeNode root;
	public File file;
	public File metadataFile;
	public RandomAccessFile raf;
	public RandomAccessFile rafm;
	public int nodeSize;
	public int insertion;
	public int rootOffset;
	public int maxKeys;
	public int debug;

	public class BTreeNode {
		public int parent;
		public LinkedList<TreeObject> keys;
		public LinkedList<Integer> children;
		public int degree;
		public boolean isLeaf;
		public boolean isFull;
		public int numKeys; //ONLY counts unique keys
		public int offset;
		
		public BTreeNode(int t, boolean leaf, int offset) {	
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
			if(keys.size() >= maxKeys) {
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
		 * Used by BTree insert() and it's helper methods
		 * @param TreeObject to insert
		 */
		public void addKey(TreeObject key) {
			
			//Searches for an insert point from right to left, moving keys in the process;
			int i = 0;
			if(numKeys > 0) {
				while(i < numKeys && keys.get(i).compareTo(key) == -1) {
					i++;
				}
			}

			if(i < numKeys && keys.get(i).compareTo(key) == 0) {
				keys.get(i).increaseDuplicateCount();
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
				numKeys--;
			}
			return ret;
		}	
		
		public void deleteChildPointer(int offset) {
			if(children.contains(offset)) {
				children.remove(offset);
			}
		}
		
	}
	
	public BTree(int degree, String fileName, int debug, boolean searching) {
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
		this.increaseInsertionPoint();
		
		metadataFile = new File(fileName + ".m");
		file = new File(fileName);
		if(searching == false) {
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
		else {
			try {
				raf = new RandomAccessFile(file,"rw");
				rafm = new RandomAccessFile(metadataFile, "rw");
				this.root.setOffset(readRootOffset());
				this.rootOffset = this.root.offset;
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			this.root = readNode(this.rootOffset);
		}
	}
	
	/**
	 * Just returns the root node for whenever we need it
	 * @return Root node
	 */
	public BTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Inserts a key using helping methods findLeafAndInsert and SplitChild
	 * Splits upwards if full nodes are found while attempting insert
	 * @param Key that is being entered
	 */
	
	public void insert(long key) {
		if(root.getNumKeys() == maxKeys) {
			BTreeNode oldRoot = root;
			BTreeNode newRoot = new BTreeNode(degree, false, insertion);
			root = newRoot;
			this.increaseInsertionPoint();
			
			oldRoot.parent = root.getOffset();
			root.children.add(oldRoot.getOffset());
			
			this.splitChild(root, 0, oldRoot);
			
			TreeObject k = new TreeObject(key);
			this.findLeafAndInsert(root, k);
			this.rootOffset = newRoot.offset;
			this.writeMetaData();
		}
		TreeObject k = new TreeObject(key);
		this.findLeafAndInsert(root, k);
	}
	
	//Traverses downwards until it finds a leaf and then inserts
	//This all must be done in BTree to maintain access to read and write
	//Assumes that the initially given node isn't full
	public void findLeafAndInsert(BTreeNode currentNode, TreeObject k) {
		if(currentNode.isLeaf) {
			currentNode.addKey(k);
			this.writeToFile(currentNode);
		} else {
			
			//Finds the child to traverse to
			int i = 0;
			while(currentNode.getNumKeys() > i && currentNode.keys.get(i).compareTo(k) == -1 ) {
				i++;
			}
			if(i < currentNode.numKeys && currentNode.keys.get(i).compareTo(k) == 0) {
				currentNode.keys.get(i).increaseDuplicateCount();
			}
			int childOffset = currentNode.children.get(i);
			BTreeNode nextNode = this.readNode(childOffset);
			
			if(nextNode.isFull()) {
				BTreeNode rightNode = this.splitChild(currentNode, i, nextNode);
				
				//splitChild returns rightNode just in case it needs to be traversed immediately afterwards here
				if (k.compareTo(currentNode.keys.get(i)) == 1) {
					this.findLeafAndInsert(rightNode, k);
				} else {
					this.findLeafAndInsert(nextNode, k);
				}
			} else {
				this.findLeafAndInsert(nextNode, k);
			}
			
			
		}
		
		
	}
	
	//Takes a parent node, a child node, and the index of the child pointer on the parent node
	//Splits the node in half, and sends the middle key upwards
	//Operates under the assumption that the parent isn't already full
	//Creates a new node for the right side of the split at the current insertion point and pushes the point forward
	//Returns the rightChild for temporary use by the findLeafAndInsert class to prevent an unnecessary disk access
	public BTreeNode splitChild(BTreeNode parent, int childIndex, BTreeNode child) {

		boolean newLeaf = child.isLeaf();
		BTreeNode rightChild = new BTreeNode(degree, newLeaf, insertion);
		rightChild.parent = parent.getOffset();
		increaseInsertionPoint();

		//copies and removes children, except the one being sent up
			for(int i = 0; i < degree - 1; i++) {
				rightChild.keys.add(i, child.keys.remove(degree));
				child.numKeys--;
				rightChild.numKeys++;
			}
		
		//copies and removes keys, except the one being sent up
		if(!newLeaf) {
			for(int i = 0; i < degree; i++) {
				rightChild.children.add(i, child.children.remove(degree));
			}
		}

		//Adds the key and child pointers to the parent
		parent.children.add(childIndex+1, rightChild.getOffset());
		parent.keys.add(childIndex, child.keys.removeLast());
		child.numKeys--;
		parent.numKeys++;

		//Writes to file
		this.writeToFile(parent);
		this.writeToFile(child);
		this.writeToFile(rightChild);

		return rightChild;

	}

	//Starts a search without the need for a node
	//Returns the number of occurrences of the key in the file
	public int search(long key) {
		TreeObject object = new TreeObject(key);
		TreeObject foundKey = searchHelper(root, object);
		
		if(foundKey == null) return 0;
		else return foundKey.duplicateCount + 1;
	}
	
	//Recursive method to look for a key
	public TreeObject searchHelper(BTreeNode node, TreeObject key) {
		
		//Base case at leaf if key hasn't been found yet
		if(node.isLeaf) {
			for(int i = 0; i < node.numKeys; i++) {
				if(node.keys.get(i).compareTo(key) == 0) {
					return node.keys.get(i);
				}
			}
			
//			return null;
		}
		
		int i = 0;
		while(i < node.numKeys && node.keys.get(i).compareTo(key) == -1) {
			i++;
		}
		
		//Checks to see if it's on the last child first to avoid null pointers looking at empty list spots
		if(!node.children.isEmpty()) {
			if(i == node.numKeys){
				BTreeNode child = this.readNode(node.children.get(i));
				return this.searchHelper(child, key);
			}
			else if(node.keys.get(i).compareTo(key) == 0){
				return node.keys.get(i);
			}
			else{
				BTreeNode child = this.readNode(node.children.get(i));
				return this.searchHelper(child, key);
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Takes a node and traverses down the tree from there, in order
	 * Prints all of the node data into a separate file determined by the BufferedWriter
	 * Sublength is used to help convert the longs back into strings
	 * @param node
	 * @param bw
	 * @param subLen 
	 */
	public void inOrderTraversalDump(BTreeNode node, PrintWriter pw, int subLen)  {
		if(node.isLeaf) {
			for(int i = 0; i < node.numKeys; i++) {
				pw.print(node.getKey(i).getDuplicateCount() + " ");
				pw.println(scannest.convertString(node.getKey(i).getData(), subLen));
			}
		} else {
			for(int i = 0; i < node.numKeys; i++) {
				
				int offset = node.children.get(i);
				BTreeNode leftChild = this.readNode(offset);
				inOrderTraversalDump(leftChild, pw, subLen);
				pw.write(node.getKey(i).duplicateCount + " " + scannest.convertString(node.getKey(i).getData(), subLen)  + "\n");
			}
			
			//Traverses final right child
			int rOffset = node.children.getLast();
			BTreeNode rightChild = this.readNode(rOffset);
			inOrderTraversalDump(rightChild, pw, subLen);
		}
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
	 * Writes a given node to the file at the node's specified offset
	 * Overwrites empty portions with -1 to "erase" old data
	 * @param Node to write
	 */
	public void writeToFile(BTreeNode node) {
		try {
			raf.seek(node.getOffset());
			raf.writeBoolean(node.isLeaf());
			raf.writeInt(node.getNumKeys());
			raf.writeInt(node.getParent());
			
			for(int i = 0; i < maxKeys; i++) {
				if(i < node.getNumKeys() + 1 && !node.isLeaf()) {
					raf.writeInt(node.getChildren().get(i));
				}
				else {
					raf.writeInt(-1);
				}
				if(i < node.numKeys) {
					long data = node.getKey(i).getData();
					raf.writeLong(data);
					int dup = node.getKey(i).getDuplicateCount();
					raf.writeInt(dup);
				}
				else {
					raf.writeLong(-1);
					raf.writeInt(-1);
				}
			}
			
			if(node.isFull() && !node.isLeaf()) {
				raf.writeInt(node.getChildren().getLast());
			}
			else {
				raf.writeInt(-1);
			}
		} catch (IOException e) {
			System.err.println("Could not write to file");
			System.exit(-1);
		}
	}
	
	//Tries it's hardest to recreate the node based on what's on disk at the given offset
	public BTreeNode readNode(int offset) {
		BTreeNode node = new BTreeNode(degree, true, offset);
		
		try {
			raf.seek(offset);
			
			boolean leaf = raf.readBoolean();
			int numkeys = raf.readInt();
			int parent = raf.readInt();
			
			node.setLeaf(leaf);
			node.setNumKeys(numkeys);
			node.setParent(parent);
			
			//Loop that recreates the node, ignores -1 as they indicate no data
			for(int i = 0; i < (2 * degree) - 1; i++) {
				int child = raf.readInt();
				long key = raf.readLong();
				int dupCount = raf.readInt();
				
				if(child != -1) {
					node.children.add(child);
				}
				if(key != -1) {
					TreeObject k = new TreeObject(key);
					node.keys.add(k);
				}
				if(dupCount != -1) {
					node.keys.get(i).setDuplicateCount(dupCount);
				}
			}
			
			int lastChild = raf.readInt();
			if(lastChild != -1) {
				node.children.add(lastChild);
			}
			
		} catch (IOException e) {
//			System.err.println("Could not read from disk");
			e.printStackTrace();
			System.exit(-1);
		}
		
		return node;
		
	}
	
	public int readRootOffset() {
		try {
			rafm.seek(4);
			return rafm.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
}