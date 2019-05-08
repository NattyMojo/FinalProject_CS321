import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
		boolean checkCache = false;
		if (args.length < 4) {
			badUsage();
		}
		
		
		
		//cache
		int ca = Integer.parseInt(args[0]);
		checkCache = tryCache(ca);
		//degree
		int deg = Integer.parseInt(args[1]);
		int BTreeDegree = getDegree(deg);
		//sequence length
		int len = Integer.parseInt(args[3]);
		int sequenceLength = seqLeng(len);

		
		
		
		// cache and debug level
		int debugLevel = 0;

		if (!checkCache || args.length > 4) {
			try {
				int dlevel = Integer.parseInt(args[4]);
				if (dlevel < 0 || dlevel > DEBUG_MAX) badUsage();
				else debugLevel = dlevel;
			} catch (NumberFormatException e) {
					badUsage();
			}
		}
		
		
		
		
		// genebank file
		File gbk = new File(args[2]);

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(gbk));
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + gbk.getPath());
		}
		String BTreeFile = (gbk + ".btree.data." + sequenceLength + "." + BTreeDegree);
		BTree tree = new BTree(BTreeDegree, BTreeFile, checkCache, cacheSize);

		String line = null;
		line = in.readLine().toLowerCase().trim();
		boolean inSequence = false;
		int sequencePosition = 0;
		int charPosition = 0;
		long sequence = 0L;

		while (line != null) { 
			if (inSequence) {
				if (line.startsWith("//")) { 
					inSequence = false;
					sequence = 0;
					sequencePosition = 0;
				} else {
					while (charPosition < line.length()) {
						char c = line.charAt(charPosition++);
						switch (c) {
						case 'a':
							sequence = ((sequence<<2) | BASE_A);
							if (sequencePosition < sequenceLength) sequencePosition++;
							break;
						case 't':
							sequence = ((sequence<<2) | BASE_T);
							if (sequencePosition < sequenceLength) sequencePosition++;
							break;
						case 'c':
							sequence = ((sequence<<2) | BASE_C);
							if (sequencePosition < sequenceLength) sequencePosition++;
							break;
						case 'g':
							sequence = ((sequence<<2) | BASE_G);
							if (sequencePosition < sequenceLength) sequencePosition++;
							break;
						case 'n':
							sequencePosition = 0;
							sequence = 0;
							continue;
						default: //none of the above
							continue;
						}
						if (sequencePosition >= sequenceLength) {
							tree.BTreeInsert(sequence & (~(0xffffffffffffffffL<<(sequenceLength<<1))));
						}
					}
				}
			} else if (line.startsWith("ORIGIN")) {
				inSequence = true;
			}
			line = in.readLine();
			charPosition = 0;
		}

		if (debugLevel > 0) {
			File dumpFile = new File("dump");
			dumpFile.delete();
			dumpFile.createNewFile();
			PrintWriter writer = new PrintWriter(dumpFile);
			tree.inOrderPrintToWriter(tree.getRoot(),writer,sequenceLength);
			writer.close();
		}
		System.out.println("done");
		if (checkCache) tree.flushCache();
		in.close();
	}
	
	
	
	
	
	public static boolean tryCache(int t) {
		boolean checkCache = false;
		try {
			if (t == 0) {
				checkCache = false;
				return checkCache;
			}
				
			else if (t == 1) {
				checkCache = true;
				return checkCache;
			}
		} catch (NumberFormatException e) {
			badUsage();
		}
		return checkCache;			
	} // end try cache method
	
	
	
	
	
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
	
	
	

                      
	public static int getOptimalDegree(){
		double optimum;
		int sizeOfPointer = 4;
		int sizeOfObject = 12;
		int sizeOfMetadata = 5;
		optimum = 4096;
		optimum += sizeOfObject;
		optimum -= sizeOfPointer;
		optimum -= sizeOfMetadata;
		optimum /= (2 * (sizeOfObject + sizeOfPointer));
		return (int) Math.floor(optimum);
	}
	
	public static void badUsage() {}
	
}