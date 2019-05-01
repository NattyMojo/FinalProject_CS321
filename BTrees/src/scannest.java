import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class scannest{
	
	private Scanner scan;
	private int subLength;
	private String currentLine; //The current line of the dna file without spaces or line numbers
	private int pointer; //The scanner's progress through the current line
	private boolean endOfBlock;

	public scannest(File f, int len) throws FileNotFoundException {
		scan = new Scanner(f);
		subLength = len;
		this.nextBlock();
	}
	
/*  Gets and returns the DNA long based on the designated substring length
 * 	Returns a 0 if the end of the current DNA block has been reached
 * 	Have not accounted for "n" yet
 */
	public long nextSubstring() { 
		String temp = "";
		
		if(endOfBlock) return 0;
		
		if(pointer + subLength <= currentLine.length()) {
			temp = currentLine.substring(pointer, pointer+subLength);
			pointer+=subLength;
		} else if(pointer == currentLine.length()) {
			currentLine = scan.nextLine();
			
			if(currentLine.equals("//")) {
				endOfBlock = true;
				return 0;
			}
			
			currentLine = currentLine.substring(10).replaceAll("\\s+", "");
			pointer = 0;
			temp = currentLine.substring(pointer, pointer+subLength);
			pointer+=subLength;
			
		} else {
			temp = currentLine.substring(pointer);
			currentLine = scan.nextLine();
			
			if(currentLine.equals("//")) {
				endOfBlock = true;
				return convertBinary(temp);
			}
			
			currentLine = currentLine.substring(10).replaceAll("\\s+", "");
			pointer = 0;
			
			int overflow = temp.length();
			temp += currentLine.substring(pointer, pointer + subLength - overflow);
			pointer+=(subLength-overflow);
		}
		
		
		return convertBinary(temp);
	}
	
/*	Moves the scanner to the next block of DNA and updates currentLine
 * 	Returns false if there isn't another block remaining
 */
	public boolean nextBlock() {
		String start = "";
		while(!start.equals("ORIGIN")) {
			if(scan.hasNextLine()) {
				start = scan.nextLine().substring(0, 6);
				currentLine = scan.nextLine().substring(10).replaceAll("\\s+", "");
				pointer = 0;
				endOfBlock = false;
			} else {
				return false;
			}
		}
		return true;
	}
	
	// Returns true if the current DNA BLOCK (not the file!) is over
	public boolean isEnd() {
		return endOfBlock;
	}
	
	public static long convertBinary(String s) {
		long ret = 0;
		for(int i = 0; i < s.length(); i++) {
			char temp = s.charAt(i);
			
			if(temp == 'a') 	   { //A is 00
				ret = ret << 2;
			} else if(temp == 't') { //T is 11
				ret = ret << 2;
				ret = ret|3;
			} else if(temp == 'c') { //C is 01
				ret = ret << 2;
				ret = ret|1;
			} else if(temp == 'g') { //G is 10
				ret = ret << 2;
				ret = ret|2;
			}
		}
		
		return ret;
	}
	

	public static String convertString(long l) {
		if(l == 0) {
			return "0";
		}
		
		String ret = "";
		String binary = Long.toBinaryString(l);
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
	
	
	//testing again
	public static void main(String[] args) {
		
		System.out.println(Long.toBinaryString(convertBinary("a")));
		
//		File f = new File("test1.gbk");
//		try {
//			scannest testing = new scannest(f,3);
//			
//			while(!testing.isEnd()) {
//				System.out.println(Long.toBinaryString(testing.nextSubstring()));
//			}
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		
	}
	
}