import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GeneBankSearch {

	public static void main(String args[]) {
		
		if(args.length != 2) {
			System.out.println(printUsage());
		}
		
		File btree = new File(args[0]);
		File queryFile = new File(args[1]);
		int debug = 0;
		
		//Check to see if there is debug level
		if(args[2] != null) {
			debug = Integer.parseInt(args[2]);
		}
		
		int seqLength = Integer.parseInt(btree.getName(),btree.getName().length()-2);
		int degree = Integer.parseInt(btree.getName(),btree.getName().length());
		
		BTree tree = new BTree(degree, btree.getName());
		
		try {
			Scanner queryScan = new Scanner(queryFile);
			scannest converter = new scannest(btree, seqLength);
			
			while(queryScan.hasNextLine()) {
				String query = queryScan.nextLine();
				long keyToSearch = converter.convertBinary(query);
				TreeObject ret = tree.search(tree.getRoot(), keyToSearch);
				if(ret != null) {
					System.out.println("Sequence \"" + query + "\" appears " + ret.getDuplicateCount() + " times");
				}
				else {
					System.out.println("Sequence \"" + query + "\" appears 0 times");
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String printUsage() {
		return "java GeneBankSearch <btree file> <query file> [<debug level>]";
	}
}
