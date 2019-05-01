import java.io.File;

public class GeneBankCreateBTree {

	public static void main(String args[]) {
		
		if(args.length != 3) {
			System.out.println(printUsage());
			System.exit(-1);
		}
		
		int degree = Integer.parseInt(args[0]);
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
		else {
//			scannest scan = new scannest(gbkFile, seqLength);
		}
		
		
		
		
		
	}
	
	private static String printUsage() {
		return "java GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]";
	}
	
}
