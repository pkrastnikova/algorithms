import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;

import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

/**
 * A Boggle solver that finds all valid words in a given Boggle board, using a given dictionary.
 * A valid word must be composed by following a sequence of adjacent dice
 * Two dice are adjacent if they are horizontal, vertical, or diagonal neighbors.
 * A valid word can use each die at most once.
 * A valid word must contain at least 3 letters.
 * A valid word must be in the dictionary (which typically does not contain proper nouns).
 * Assume that each word in the dictionary contains only the uppercase letters A-Z
 * 
 * Dependency: BoggleBoard.java, TrieST26P.java, Bag.java
 * 
 * @author pkrastnikova
 * 
 */

public class BoggleSolver {
	private int cols; // number of columns
	private int N; // number of letters in the board
	private AdjDice[] adj; // array with adjacent nodes of each node
	private char[] nodes; // array with all letters in the board
	private TrieST26P<Integer> st; // dictionary implemented as a 26-way trie
	private SET<String> results;  // words found

	/**
	 * Initializes the data structure using the given array of strings as a dictionary
	 * Use TriST26P class to store the dictionary:
	 * symbol table - key:value corresponds to word:value of the last letter/node, which equals the word;
	 * if word does not exist in the dictionary the value is null
	 * 
	 * @param dictionary
	 */
	public BoggleSolver(String[] dictionary) {
		st = new TrieST26P<Integer>();
		for (int i = 0; i < dictionary.length; i++) {
			st.put(dictionary[i]);
		}
	}

	// define extended class to be able to create array of bags
	private class AdjDice extends Bag<Integer> {
	}

	/**
	 * Pre-compute the board by populating adj[] and nodes[] arrays
	 * Each adj[i] is a bag that holds all adjacent nodes of the i-th node
	 * @param board
	 */
	private void computeBoard(BoggleBoard board) {
		int rows = board.rows();
		cols = board.cols();
		N = rows * cols;
		
		// adj = (Bag<Integer>[]) new Bag[N];
		adj = new AdjDice[N];
		nodes = new char[N];

		// populate nodes[], adj[]; use helper convert(i, j) to transform 2D to 1D array index
		int k = 0; // index in nodes[]
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				nodes[k] = board.getLetter(i, j);
				// adj[k] = new Bag<Integer>();
				adj[k] = new AdjDice();
				int left, right, top, down, topleft, topright, downleft, downright;

				if (i == 0) { // first row
					if (j == 0) { // (0, 0) element - add right, down, downright
						if (cols > 1) {
							right = convert(i, j + 1);
							adj[k].add(right);
							if (rows > 1) {
								downright = convert(i + 1, j + 1);
								adj[k].add(downright);
								down = convert(i + 1, j);
								adj[k].add(down);
							}
						} else {
							if (rows > 1) {
								down = convert(i + 1, j);
								adj[k].add(down);
							}
						}
					}

					else if (j == cols - 1) { // last element in first row
						left = convert(i, j - 1);
						adj[k].add(left);
						if (rows > 1) {
							down = convert(i + 1, j);
							downleft = convert(i + 1, j - 1);
							adj[k].add(down);
							adj[k].add(downleft);
						}
				}

					else { // middle elements in first row
						left = convert(i, j - 1);
						right = convert(i, j + 1);
						adj[k].add(left);
						adj[k].add(right);
						if (rows > 1) {
							down = convert(i + 1, j);
							downleft = convert(i + 1, j - 1);
							downright = convert(i + 1, j + 1);
							adj[k].add(down);
							adj[k].add(downleft);
							adj[k].add(downright);
						}
					}
				}

				else if (i == rows - 1) { // last row
					if (j == 0) { // first element in last row
						top = convert(i - 1, j);
						adj[k].add(top);
						if (cols > 1) {
							topright = convert(i - 1, j + 1);
							right = convert(i, j + 1);
							adj[k].add(topright);
							adj[k].add(right);
						}
					} else if (j == cols - 1) { // last element in last row
						top = convert(i - 1, j);
						topleft = convert(i - 1, j - 1);
						left = convert(i, j - 1);
						adj[k].add(top);
						adj[k].add(topleft);
						adj[k].add(left);
					} else { // middle elements in last row
						left = convert(i, j - 1);
						right = convert(i, j + 1);
						top = convert(i - 1, j);
						topleft = convert(i - 1, j - 1);
						topright = convert(i - 1, j + 1);
						adj[k].add(left);
						adj[k].add(right);
						adj[k].add(top);
						adj[k].add(topleft);
						adj[k].add(topright);
					}

				} else if (j == 0) { // first column, middle row
					top = convert(i - 1, j);
					down = convert(i + 1, j);
					adj[k].add(down);
					adj[k].add(top);
					if (cols > 1) {
						topright = convert(i - 1, j + 1);
						right = convert(i, j + 1);
						downright = convert(i + 1, j + 1);
						adj[k].add(topright);
						adj[k].add(right);
						adj[k].add(downright);
					}
				}

				else if (j == cols - 1) { // last column, middle row
					top = convert(i - 1, j);
					topleft = convert(i - 1, j - 1);
					left = convert(i, j - 1);
					downleft = convert(i + 1, j - 1);
					down = convert(i + 1, j);
					adj[k].add(top);
					adj[k].add(topleft);
					adj[k].add(left);
					adj[k].add(downleft);
					adj[k].add(down);

				} else { // middle row and column
					left = convert(i, j - 1);
					right = convert(i, j + 1);
					top = convert(i - 1, j);
					down = convert(i + 1, j);
					topleft = convert(i - 1, j - 1);
					topright = convert(i - 1, j + 1);
					downleft = convert(i + 1, j - 1);
					downright = convert(i + 1, j + 1);
					adj[k].add(left);
					adj[k].add(right);
					adj[k].add(top);
					adj[k].add(topleft);
					adj[k].add(topright);
					adj[k].add(down);
					adj[k].add(downleft);
					adj[k].add(downright);
				}
				k++;
			}
		}
	}

	//convert 2D to 1D array index
	private int convert(int i, int j) {
		return (i * cols + j);
	}

	/**
	 * Use DFS to search for valid words in the dictionary
	 * @param i index of letter on the board (use 1D array of N)
	 * @param x node in the Trie to start search from
	 * @param marked mark letter as visited
	 */
	private void searchInDict2(int i, TrieST26P.Node x, boolean marked[]) {
		if (marked[i] == true) return;
		char currentChar = nodes[i];
		TrieST26P.Node matched = st.getChar(x, currentChar); //check if there's a node with currentChar under node x
		if (matched == null) return;
		marked[i] = true;

		if (currentChar == 'Q') {
			matched = st.getChar(matched, 'U');
		}

		if (matched != null) {
			String s = matched.val;
			if (s != null && s.length() >= 3) { // check if matched node is a last letter of a valid word 
											   // and is at least 3 letters long
				results.add(s);
			}
			if (matched.next != null) { // has children/adjacent nodes
				for (int k : adj[i]) {
					searchInDict2(k, matched, marked);
				}
			}
		}
		marked[i] = false;
	}

	// Returns the set of all valid words in the given Boggle board as an Iterable.
	
	public Iterable<String> getAllValidWords(BoggleBoard board) {
		results = new SET<String>();
		computeBoard(board);
		boolean[] marked = new boolean[N]; // mark each letter that is already part of a found word
		for (int i = 0; i < N; i++) {
			searchInDict2(i, st.root, marked); // start a new search from each letter on the board
		}
		return results;
	}

	// Returns the score of the given word if it is in the dictionary, zero otherwise.

	public int scoreOf(String word) {

		if (st.contains(word)) {
			int l = word.length();
			if (l <= 2)
				return 0;
			if (l == 3 || l == 4)
				return 1;
			if (l == 5)
				return 2;
			if (l == 6)
				return 3;
			if (l == 7)
				return 5;
			if (l>=8) 
				return 11;
		}
		return 0;
	}

	public static void main(String[] args) {
		In in = new In(args[0]);
		String[] dictionary = in.readAllStrings();
		BoggleSolver solver = new BoggleSolver(dictionary);
		BoggleBoard board = new BoggleBoard(args[1]);
		System.out.println(board);
		solver.computeBoard(board);
		System.out.println(solver.N);
		/*
		 * for (int i=0; i< solver.N; i++) { System.out.print("Node: " + i);
		 * System.out.println(solver.nodes[i]); for (int k: solver.adj[i]){
		 * System.out.print(k); System.out.print(solver.nodes[k] + " "); }
		 * System.out.println(); }
		 */
		// solver.findWord();

		solver.getAllValidWords(board);
		System.out.println(solver.results.size());

		for (String word : solver.results) {
			System.out.println(word);
		}

		In in1 = new In(args[0]);
		String[] dictionary1 = in1.readAllStrings();
		BoggleSolver solver1 = new BoggleSolver(dictionary1);
		BoggleBoard board1 = new BoggleBoard(args[1]);
		BoggleBoard board2 = new BoggleBoard(args[2]);
		System.out.println(board1);
		int score = 0;
		for (String word : solver1.getAllValidWords(board1)) {
			StdOut.println(word);
			score += solver1.scoreOf(word);
		}

		StdOut.println("Score = " + score);

		System.out.println("Second board, same dictionary:");
		System.out.println(board2);
		int score2 = 0;
		for (String word : solver1.getAllValidWords(board2)) {
			StdOut.println(word);
			score2 += solver1.scoreOf(word);
		}
		StdOut.println("Score = " + score2);

	}

}
