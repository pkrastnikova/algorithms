
import edu.princeton.cs.algs4.In;

/**
 * 26-Way Trie to store a dictionary
 * Each node represents a letter A-Z
 * Link to next letter in the word is implicitly stored in next[26]
 * Last node of the word is assigned a value equal to the word (the key)
 * 
 * @author pkrastnikova
 *
 * @param <Value>
 */
public class TrieST26P<Value> {
    private static final int R = 26;       
    public Node root;      // root of the trie
    private int n;         // number of keys in the trie
    
    // 26-way trie node
    public static class Node {
        public String val; // assigned the word (key) to the last node (letter) in the word; null otherwise
    	public Node[] next = new Node[R]; // children nodes
    }
    
    // convert char into index 0-25 to be able to address each child node in next[] 
    private static int convertChar(char ch) {
    	return ch - 'A';
    }
    
    public TrieST26P() {
    }

    /**
     * Check if trie contains the word
     * @param key the word to search for
     * @return true if found; false otherwise
     */
    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }
    
	/**
	 * Get the word from the tree
	 * Use helper method to do the recursive search
	 * @param key the word
	 * @return the word if found; null otherwise
	 */
    public String get(String key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node x = get(root, key, 0); //start search from the root with first letter of the key
        if (x == null) return null;  //not found
        return x.val; //return the key
    }

    /**
     * Find the word by recursive traversal of the tree
     * @param x the node to start the search from
     * @param key the word to search 
     * @param d current letter in the word
     * @return if found, return the node with value = key; if not found, return null
     */
    public Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x; // reached end of the word; the word was found
        char c = key.charAt(d);
        int index = convertChar(c);
        return get(x.next[index], key, d+1); // go to the next letter in the word
    }
    
    // search for key under node x
    public Node getChar(Node x, char key) {
        if (x == null) return null;
        int index = convertChar(key);
        return x.next[index];
    }
    
    /**
     * Add a new word to the tree
     * Use helper method to recursively create node for each letter of the word
     * @param key the word
     */
    public void put(String key) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        root = put(root, key, 0); // start from the root with first letter of the word
    }

    /**
     * Add word to the tree through recursive traversal
     * @param x node to start from (initially the root)
     * @param key the word to put in the tree
     * @param d the letter to start from (initially first letter of the word)
     * @return the node with last letter of the word
     */
    private Node put(Node x, String key, int d) {
        if (x == null) {
        	x = new Node();	        	
        }
        
        // base case - reached end of the word
        if (d == key.length()) {
            if (x.val == null) n++;
            x.val = key;
            return x;
        }
        char c = key.charAt(d);
        int index = convertChar(c);
        x.next[index] = put(x.next[index], key, d+1);
        return x;
    }
    
    
    public static void main(String[] args) {

        // build symbol table from standard input
        TrieST26P<Integer> st = new TrieST26P<Integer>();
        In textfile = new In(args[0]);
        for (int i = 0; !textfile.isEmpty(); i++) {
            String key = textfile.readString();
            st.put(key);
        }

        
    }
}

