import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class scannest{
	
	private Scanner scan;
	private int subLength;
	private String currentBlock; //The current line of the dna file without spaces or line numbers
	private int pointer; //The scanner's progress through the current line
	private boolean end; //Is true if the current block is exhausted

	public scannest(File f, int len) throws FileNotFoundException {
		scan = new Scanner(f);
		subLength = len;
	}
	
/*  Gets and returns the DNA long based on the designated substring length
 * 	Returns a -1 if the end of the current DNA block has been reached
 */
	public long nextSubstring() { 
		String temp = "";
		
		
		if(pointer + subLength == currentBlock.length()) {
			end = true;
		} else if(pointer + subLength > currentBlock.length()) {
			end = true;
			return -1;
		}
		temp = currentBlock.substring(pointer, pointer+subLength);
		while(temp.contains("n")) {
			pointer++;
			temp = currentBlock.substring(pointer, pointer+subLength);
		}
		pointer++;
		return this.convertBinary(temp);
	}
	
/*	Moves the scanner to the next block of DNA and updates currentLine
 * 	Returns false if there isn't another block remaining
 */
	public boolean nextBlock() {
		String start = "";
		while(!start.equals("ORIGIN")) {
			if(scan.hasNextLine()) {
				start = scan.nextLine().replaceAll("\\s+", "");
			} else {
				end = true;
				return false;
			}
		}
		
		pointer = 0;
		end = false;
		boolean endOfBlock = false;
		currentBlock = "";
		while(!endOfBlock) {
			String temp = scan.nextLine();
			if(temp.equals("//")) {
				endOfBlock = true;
			} else {
				currentBlock += temp.substring(10).replaceAll("\\s+", "");
			}
		}
		
		return true;
	}
	
	public long convertBinary(String s) {
		long ret = 0;
		for(int i = 0; i < s.length(); i++) {
			char temp = s.charAt(i);
			
			if(temp == 'a' || temp == 'A') 	   { //A is 00
				ret = ret << 2;
			} else if(temp == 't' || temp == 'T') { //T is 11
				ret = ret << 2;
				ret = ret|3;
			} else if(temp == 'c' || temp == 'C') { //C is 01
				ret = ret << 2;
				ret = ret|1;
			} else if(temp == 'g' || temp == 'G') { //G is 10
				ret = ret << 2;
				ret = ret|2;
			}
		}
		
		return ret;
	}
	

	public static String convertString(long l, int subLen) {
		if(l == -1) {
			return "END";
		}
		
		String ret = "";
		String binary = Long.toBinaryString(l);
		int offset = 2*subLen - binary.length();
		if(offset != 0) {
			for(int i = 0; i < offset; i++) {
				binary = "0" + binary;
			}
		}
		for(int i = 0; i < binary.length(); i+=2) {
			if(binary.substring(i,i+2).equals("00")) {
				ret+= "a";
			} else if(binary.substring(i,i+2).equals("11")) {
				ret+= "t";
			} else if(binary.substring(i,i+2).equals("01")) {
				ret+= "c";
			} else if(binary.substring(i,i+2).equals("10")) {
				ret+= "g";
			}
		}
		return ret;
	}
	
	public boolean isEnd() {
		return end;
	}
	
}