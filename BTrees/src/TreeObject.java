
/**
 * TreeObject is an object to be saved in the BTree
 * 
 * @author Zach Luciano, Brendon Yoshino, Mason Humpherys
 *
 */
public class TreeObject implements Comparable<TreeObject>{

	public Long data;
	public int duplicateCount;
	
	public TreeObject(long num) {
		data = num;
		duplicateCount = 0;
	}
	
	public long getData() {
		return data;
	}
	
	public int getDuplicateCount() {
		return duplicateCount;
	}
	
	public void setDuplicateCount(int count) {
		duplicateCount = count;
	}
	
	public void increaseDuplicateCount() {
		duplicateCount++;
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
