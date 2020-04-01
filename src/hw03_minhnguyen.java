import java.io.*;
import java.util.Scanner;

public class hw03_minhnguyen {
	public static void readInput(String filename) {
		try {
			Scanner in = new Scanner(new FileReader(filename));
			
			while(in.hasNext()) {
				System.out.println(in.next());
			}
			
		} catch(FileNotFoundException e) {
			System.out.println("The file " + filename + " was not found.");
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("ERROR: Missing argument!");
			System.out.println("Usage: java Puzzle \"input.txt\"");
			System.exit(1);
		}
		
		readInput(args[0]);
	}
}