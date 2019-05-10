import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GeneBankCreateBTree {

	public static final long BASE_A = 0b00L;
	public static final long BASE_T = 0b11L;
	public static final long BASE_C = 0b01L;
	public static final long BASE_G = 0b10L;

	public static final int SLENGTH_MAX = 31;
	public static final int DEBUG_MAX = 1;
	

	public static void main(String[] args) throws IOException {
		if (args.length != 4 || args.length !=3) {
			badUsage();
		}
		
		

		//degree
		int deg = Integer.parseInt(args[0]);
		deg = getDegree(deg);
		//sequence length
		int len = Integer.parseInt(args[2]);
		seqLeng(len);
		//debug level
		int dlevel = Integer.parseInt(args[3]);
		getDebLev(dlevel);
		
		System.out.println("Degree: " + deg +
						   "\nSequence Length: " + len +
						   "\nDebug Level: " + dlevel);
		// file stored in args[1]
		
		File genebnk = new File (args[1]);
		String BTreeFile = (genebnk + ".btree.data." + len + "." + deg);
		BTree tree = new BTree(deg, BTreeFile, dlevel);
		scannest seqScan = new scannest(genebnk, len);
		
		
		
		
		while(seqScan.nextBlock()) {
			while(!seqScan.isEnd()) {
				tree.insert(seqScan.nextSubstring());
			}
		}
		
		if(dlevel == 1) {
			File debug = new File("BTreeFile" + ".debug");
			FileWriter fw = new FileWriter(debug);
			BufferedWriter bw = new BufferedWriter(fw);
			tree.inOrderTraversalDump(tree.root, bw, len);
		}
	}
	
	
	
	
	public static int getDegree(int deg) {
		int degree = 0;
		try {
			if (deg < 0) badUsage();
			else if (deg == 0) {
				degree = getOptimalDegree();
				return degree;
			}
			else {
				degree = deg;
				return degree;
			}
		} catch (NumberFormatException e) {
			badUsage();
		}
		return degree;
	} // end get degree method
	
	
	public static int getOptimalDegree(){
		return 128;
	}
	
	
	public static int seqLeng(int len) {
		int sLeng = 0;
		try {
			if (len < 1 || len > DEBUG_MAX) {
				badUsage();
			}
			else {
				sLeng = len;
				return sLeng;
			}
		} catch (NumberFormatException e) {
			badUsage();
		}
		return sLeng;		
	} //end get seq leng method
	
	
	
	
	
	public static int getDebLev(int dlevel) {
		int debugLevel = 0;
		try {
			if (dlevel < 0 || dlevel > DEBUG_MAX) {
				badUsage();
				return debugLevel;
			}
			else {
				debugLevel = dlevel;
				return debugLevel;
			}
		} catch (NumberFormatException e) {
				badUsage();
		}
		return debugLevel;
	}//end debug level
	
	
	

                     
	
	public static void badUsage() {
		System.out.println("Please use the following format to run the program: ");
		System.out.println("java GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]\n");
		System.out.println("Using a degree of 0 will set the program to use the optimal degree");
		System.out.println("31 is the maximum sequence length");
		System.out.println("Debug level 0 will print normal command line responses, and level 1 will create a file with an in order traversal of the tree");
		System.exit(0);
	}
	
}
	
