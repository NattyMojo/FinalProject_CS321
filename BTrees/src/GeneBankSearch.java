import java.io.File;

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
	}
	
	private static String printUsage() {
		return "java GeneBankSearch <btree file> <query file> [<debug level>]";
	}
}
