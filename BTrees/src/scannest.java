import java.util.Scanner;

public class scannest{
	
	private Scanner scan;
	private int subLength;

	public scannest(Scanner s, int len) {
		scan = s;
		subLength = len;
	}
	
	public static long convertBinary(String s) {
		long ret = 0;
		for(int i = 0; i < s.length(); i++) {
			char temp = s.charAt(i);
			
			if(temp == 'A') 	   { //A is 00
				ret = ret << 2;
			} else if(temp == 'T') { //T is 11
				ret = ret << 2;
				ret = ret|3;
			} else if(temp == 'C') { //C is 01
				ret = ret << 2;
				ret = ret|1;
			} else if(temp == 'G') { //G is 10
				ret = ret << 2;
				ret = ret|2;
			}
		}
		
		return ret;
	}
	
	//unfinished
	public static String convertBinaryString(long l) {
		String ret = "";
		String binary = Long.toBinaryString(l);
		for(int i = 0; i < binary.length(); i+=2) {
			if(binary.substring(i,i+2).equals("00")) {
				ret+= "A";
			} else if(binary.substring(i,i+2).equals("11")) {
				ret+= "T";
			} else if(binary.substring(i,i+2).equals("01")) {
				ret+= "C";
			} else if(binary.substring(i,i+2).equals("10")) {
				ret+= "G";
			}
		}
		return ret;
	}
	
}