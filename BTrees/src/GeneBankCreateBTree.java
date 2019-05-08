import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class GeneBankCreateBTree {

	public static void main(String args[]) {
		
		if(args.length != 3) {
			System.out.println(printUsage());
			System.exit(-1);
		}
		
		int degree = Integer.parseInt(args[0]);
		if(degree < 0) {
			System.out.println("Degree must be a positive number");
			System.exit(-1);
		}
		else if(degree == 0) {
			degree = 4096;   			//This is wrong?
		}
		File gbkFile = new File(args[1]);
		int seqLength = Integer.parseInt(args[2]);
		int debug = 0;
		
		//See if there is a debug level
		if(args[3] != null) {
			debug = Integer.parseInt(args[3]);
		}
		
		//Check sequence length
		if(seqLength < 1 || seqLength > 31) {
			System.out.println("Error: Sequence length must be between 1 and 31 (inclusive)\n" + printUsage());
			System.exit(-1);
		}
		
		File gbk = new File(args[1]);
		String BtreeFile = gbk + ",btree.data." + seqLength + "." + degree;
		
		BufferedReader read = null;
		try {
			read = new BufferedReader(new FileReader(gbk));
		} catch (FileNotFoundException e) {
			System.err.println("File \"" + gbk + "\" not found");
		}
		
		
		
		
		
		
	}
	
	private static String printUsage() {
		return "java GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]";
	}
	
}
