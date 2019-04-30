
/**
 * TreeObject is an object to be saved in the BTree
 * 
 * @author Zach Luciano, Brendon Yoshino, Mason Humpherys
 *
 */
public class TreeObject implements Comparable<TreeObject>{

	public Long data;
	
	public TreeObject(long num) {
		data = num;
	}
	
	public long getData() {
		return data;
	}

	@Override
	public int compareTo(TreeObject o) {
		if(data < o.data) {
			return -1;
		}
		if(data > o.data) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
}
