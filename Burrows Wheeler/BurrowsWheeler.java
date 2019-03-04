import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * The Burrows–Wheeler transform is the last column in the sorted suffixes array
 * t[] of the original string, preceded by the row number first (the first
 * suffix index) in which the original string ends up.
 * 
 * The inverse transform is to recover the original string using first and t[].
 *
 * Dependency: CircularSuffixArray.java, BinaryStdIn.java, BinaryStdOut.java
 *
 * @author pkrastnikova
 *
 */

public class BurrowsWheeler {

	/**
	 * Apply Burrows-Wheeler transform, reading from standard input and writing
	 * to standard output
	 * given a string s, generate first and t[i] (last
	 * letters of sorted suffixes)
	 */
	public static void transform() {
		String s = BinaryStdIn.readString();

		// Create an array of sorted suffixes of string s
		CircularSuffixArray sf = new CircularSuffixArray(s);

		char t[] = new char[sf.length()];
		int first = 0;

		// fill in t[i] with last letter of i-th sorted suffix
		for (int i = 0; i < sf.length(); i++) {
			if (sf.index(i) == 0) {
				first = i;
				t[i] = s.charAt(sf.length() - 1);
			}

			// if index[i] = 7, the last letter of that suffix will be the 7-th letter
			// of the original string, with string index 6
			else
				t[i] = s.charAt(sf.index(i) - 1);
		}

		// output first and t[i]
		BinaryStdOut.write(first);

		for (int i = 0; i < sf.length(); i++) {
			BinaryStdOut.write(t[i]);
		}

		BinaryStdOut.flush();

	}

	/**
	 * Apply Burrows-Wheeler inverse transform, reading from standard input and
	 * writing to standard output given first and t[i] decode - recover the
	 * original string s
	 * 
	 * 1. Sort t[i] using key-indexed counting algorithm
	 * 2. Construct next[i] using first and sorted t[i]
	 * 3. Recover the original string using first and next[i]
	 * The idea for finding next[i] - first letter in a sorted suffix appears last in t[i]
	 */
	public static void inverseTransform() {
		int R = 256; // use extended ACSII code
		int first = BinaryStdIn.readInt();
		String s = BinaryStdIn.readString();
		char[] t = s.toCharArray();

		// Sort t[i] array, using key-indexed algorithm in 3 steps:

		// 1. Count how many times each letter appears in the string
		int[] count = new int[R + 1];
		for (int i = 0; i < t.length; i++) {
			count[t[i] + 1]++;
		}

		// 2. Find cumulative index
		for (int i = 0; i < R; i++) {
			count[i + 1] += count[i];
		}

		char[] aux = new char[t.length];
		int[] next = new int[t.length];

		// 3. Place at the exact index position
		for (int i = 0; i < t.length; i++) {
			char ch = t[i]; // current char in the unsorted t[i]
			int index = count[t[i]]; // the new index in the sorted t[i]
			aux[index] = ch;
			next[index] = i; // next equals the position i of current char (ch)
								// in unsorted t[i] array (it is the last in
								// that suffix)
			count[t[i]]++;
		}

		// Recover and output the original string, using first and next[i]

		int k = first;
		for (int i = 0; i < t.length; i++) {
			BinaryStdOut.write(aux[k]);
			k = next[k];
		}

		BinaryStdOut.flush();
	}

	// if args[0] is '-', apply Burrows-Wheeler transform
	// if args[0] is '+', apply Burrows-Wheeler inverse transform
	public static void main(String[] args) {
		if (args[0].equals("-"))
			transform();
		else if (args[0].equals("+"))
			inverseTransform();
		else
			throw new java.lang.IllegalArgumentException();
	}
}
