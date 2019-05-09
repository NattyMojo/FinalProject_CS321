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
		if (args.length < 4) {
			badUsage();
		}
		
		BufferedReader in = null; 
		//cache
		int ca = Integer.parseInt(args[0]);
		tryCache(ca);
		//degree
		int deg = Integer.parseInt(args[1]);
		getDegree(deg);
		//sequence length
		int len = Integer.parseInt(args[3]);
		seqLeng(len);
		//debug level
		int dlevel = Integer.parseInt(args[4]);
		getDebLev(dlevel);
		
		
		
		// file stored in args[2]
		
		File genebnk = new File (args[2]);
		String BTreeFile = (genebnk + ".btree.data." + seqLeng + "." + deg);
		
		
			
		
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
	
	
	public static int getOptimalDegree(){
		double opt;
		opt = 4096;
		opt = opt+12;
		opt = opt-4;
		opt = opt-5;
		opt /= (2 * (16));
		return (int) Math.floor(opt);
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
	
	
	

                     
	
	public static void badUsage() {}
	
}
	
