import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * This class implements the Move-to-front encoding and decoding
 * The encoded text can be compressed using Huffmann algorithm 
 * The main idea of move-to-front encoding is to maintain an ordered sequence
 * of the characters in the alphabet, and repeatedly read a character from
 * the input message, print out the position in which that character appears, 
 * and move that character to the front of the sequence. This way the most frequent characters would
 * appear at a smaller integer positions, which is important for Huffman compression.
 * 
 * The implementation uses following algorithm: for each s[i] char from string s, start from the beginning of
 * abc array with all 256 ASCII codes and swap each abc[k] with current abc[0]
 * when s[i] is reached it will remain at the front
 * then output the index (ASCII code) of that element and stop the iteration
 */

/**
 * Apply move-to-front encoding, reading from standard input and writing to standard output
 * Dependency: BinaryStdIn.java, BinaryStdOut.java
 * 
 * @author pkrastnikova
 *
 */
public class MoveToFront {
    
    public static void encode() {
    	
    	/*char [] s = {'C', 'A', 'A', 'A', 'B', 'C', 'C', 'C', 'A', 'C', 'C', 'F'};
    	char [] s = {'A', 'B', 'R', 'A', 'C', 'A', 'D', 'A', 'B', 'R', 'A', '!'};
    	char [] abc = {'A', 'B', 'C', 'D', 'E', 'F'};
    	*/
    	
    	// read string from input stream
    	char [] s = BinaryStdIn.readString().toCharArray();
    	
    	// initialize the alphabet
    	char [] abc = new char[256]; // 
    	for (char i = 0; i < 256; i++) {
    		abc[i] = i;
    	}
    
    	// read each char from the string and look for it in the alphabet
    	for (int i = 0; i < s.length; i++) {
    		char temp;
    		for (char k = 0; k < 256; k++ ) {
    			temp = abc[k];
    			abc[k] = abc[0];
    			abc[0] = temp;
    			if (abc[0]==s[i]){
    				BinaryStdOut.write(k); // output the ascii code
    				break; // stop scanning when char is found
    			}
    		} 	
    	}
		BinaryStdOut.flush();
		BinaryStdIn.close();
    }

    /**
     * Apply move-to-front decoding, reading from standard input and writing to standard output
     * Use the same algorithm as in encode() but with input stream of integers (ASCII codes)
     */
    
    public static void decode() {
    	char [] s = BinaryStdIn.readString().toCharArray();	
    	char [] abc = new char[256];
    	for (char i = 0; i < 256; i++) {
    		abc[i] = i;
    	}
    	for (int i = 0; i < s.length; i++) {
    		char temp;		
    		for (char k = 0; k < 256; k++ ) { 			
    			temp = abc[k];
    			abc[k] = abc[0];
    			abc[0] = temp;
    			if (k == s[i]){  // compare ASCII codes
    				BinaryStdOut.write(abc[0]); //output the char
    				break;
    			}
    		}
    	}
		BinaryStdOut.flush();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
    	if (args[0].equals("-")) encode();
    	else if (args[0].equals("+")) decode();
    	else throw new java.lang.IllegalArgumentException();
    }
}