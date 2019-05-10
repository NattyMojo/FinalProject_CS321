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
		int seqLength = 0;
		int degree = 0;
		
		//Check to see if there is debug level
		if(args.length == 3) {
			if(Integer.parseInt(args[2]) == 0) {				
				debug = Integer.parseInt(args[2]);
			}
			else {
				printUsage();
			}
		}
		
		if(btree.getName().subSequence(21, 23).charAt(1) == '.') {			
			seqLength = Integer.parseInt(btree.getName().substring(21, 22));
		}
		else {
			seqLength = Integer.parseInt(btree.getName().substring(21, 23));
		}
		
		if(seqLength < 10) {
			degree = Integer.parseInt(btree.getName().substring(23));
		}
		else {
			degree = Integer.parseInt(btree.getName().substring(24));
		}
		
		
		BTree tree = new BTree(degree, btree.getName(), debug, true);
		
		try {
			Scanner queryScan = new Scanner(queryFile);
			scannest converter = new scannest(btree, seqLength);
			
			while(queryScan.hasNextLine()) {
				String query = queryScan.nextLine();
				if(query.length() == seqLength) {
					long keyToSearch = converter.convertBinary(query);
					int ret = tree.search(keyToSearch);
					System.out.println("Sequence \"" + query + "\" appears " + ret + " times");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static String printUsage() {
		return "java GeneBankSearch <btree file> <query file> [<debug level>]";
	}
}
