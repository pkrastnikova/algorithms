/**
 * Builds the WordNet digraph: each vertex v is an integer that represents a synset,
 * and each directed edge v→w represents that w is a hypernym of v. One word can be part of
 * several synsets.
 * The WordNet digraph is a rooted DAG: it is acyclic and has one vertex—the root—
 * that is an ancestor of every other vertex.
 * 
 * Provides methods for searching the distance between two nouns and their common ancestor that
 * participates in the shortest ancestral path (SAP)
 * 
 * Dependencies: SAP.java, Digraph.java, Bag.java
 * 
 * @param synsets input synsets file
 * @param hypernyms input hypernyms file
 * @throws IllegalArgumentException when synsets or hypernyms file is null, or graph is not rooted
 * @author pkrastnikova
 */
import java.util.HashMap;
import java.util.Map;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class WordNet {
	private Map<String, Bag<Integer>> wordSynsets; // maps "word -> synsets IDs"; one word can be part of several synsets
	private Map<Integer, String> idSynset; // maps "ID -> synset"
	private Digraph G;
	private int graphsize;
	private SAP paths;

	// constructor takes the names of the two input files
	public WordNet(String synsets, String hypernyms) {
		if (synsets == null || hypernyms == null) {
			throw new java.lang.IllegalArgumentException();
		}
		wordSynsets = new HashMap<String, Bag<Integer>>();
		idSynset = new HashMap<Integer, String>();
		buildWordMap(synsets);
		G = new Digraph(graphsize);
		buildGraph(hypernyms);
		if (!rootedDigraph1()) {
			throw new java.lang.IllegalArgumentException("Graph not rooted");
		}
		paths = new SAP(G);
	}

	/**
	 * Checks outdegree(v) for each vertex;
	 * If there's only one vertex with 0 outdegree (the root) -> rooted
	 */
	private boolean rootedDigraph1() {
		int count = 0;
		for (int id: this.idSynset.keySet()){
			if (G.outdegree(id) == 0) {count++;}
		}
		return (count == 1);	
	}

	/**
	 * Maps word -> IDs of synsets the word is part of
	 * Maps ID -> synset for each record of synsets file
	 * Sets size of the map to be Digraph's size (number of vertices)
	 * @param filename synsets file
	 */
	private void buildWordMap(String filename) {
		In synsets = new In(filename);
		while (synsets.hasNextLine()) {
			String line = synsets.readLine();
			String[] fields = line.split("\\,");
			int id = Integer.parseInt(fields[0]);
			idSynset.put(id, fields[1]);
			String[] words = fields[1].split("\\ ");
			for (String word: words) {
				Bag<Integer> synsetsOfWord = wordSynsets.get(word);
				if (synsetsOfWord == null) {
					wordSynsets.put(word, synsetsOfWord = new Bag<Integer>());
				}
				synsetsOfWord.add(id);
			}
		}
		this.graphsize = wordSynsets.size();
		synsets.close();
	}

	/**
	 * Builds Digraph from hypernyms file (creates edges between synset and its hypernyms)
	 * @param filename hypernyms file
	 */
	private void buildGraph(String filename) {
		In hypernyms = new In(filename);
		while (hypernyms.hasNextLine()) {
			String line = hypernyms.readLine();
			String[] fields = line.split("\\,");
			int id = Integer.parseInt(fields[0]);
			for (int i = 1; i < fields.length; i++) {
				int w = Integer.parseInt(fields[i]);
				G.addEdge(id, w);
			}
		}
		hypernyms.close();
	}

	/**
	 * @return all WordNet nouns
	 */
	public Iterable<String> nouns() {
		return wordSynsets.keySet();
	}

	/**
	 * Checks if the word a WordNet noun
	 * @param word a word
	 * @return (@code true) if the word is a WordNet noun and (@code false) if it is not
	 */
	public boolean isNoun(String word) {
		if (word == null) {
			throw new java.lang.IllegalArgumentException();
		}
		return wordSynsets.containsKey(word);
	}

	/**
	 * Computes the distance between nounA and nounB (number of edges from nounA to common
	 * ancestor plus number of edges from nounB to common ancestor)
	 * @param nounA a word
	 * @param nounB a word
	 * @throws IllegalArgumentException for null arguments or for words that are not in the graph
	 * @return distance between nounA and nounB
	 */
	public int distance(String nounA, String nounB) {
		if (nounA == null || nounB == null) {
			throw new java.lang.IllegalArgumentException();
		}
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new java.lang.IllegalArgumentException("Not a WordNet noun");
		}
		Bag<Integer> idA = wordSynsets.get(nounA); // get synsets IDs of nounA
		Bag<Integer> idB = wordSynsets.get(nounB); // get synsets IDs of nounB
		return paths.length(idA, idB); // call length(Iterable<Integer> v, Interable<Integer> w)
	}

	/**
	 * Finds a synset that is the common ancestor of nounA and nounB and is 
	 * in the shortest ancestral path
	 * @param nounA a word
	 * @param nounB a word
	 * @throws IllegalArgumentException for null arguments or for non-WordNet words
	 * @return the synset, the common ancestor 
	 */

	public String sap(String nounA, String nounB) {
		if (nounA == null || nounB == null) {
			throw new java.lang.IllegalArgumentException();
		}
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new java.lang.IllegalArgumentException("Not a WordNet noun");
		}
		Bag<Integer> idA = wordSynsets.get(nounA);
		Bag<Integer> idB = wordSynsets.get(nounB);
		int commonAncestor = paths.ancestor(idA, idB);
		return idSynset.get(commonAncestor);
	}

	// do unit testing of this class
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 WordNet wordnet = new WordNet(args[0], args[1]);
		 //System.out.println("Is digraph rooted? " + wordnet.rootedDigraph1());
		 /*for (String noun: wordnet.nouns()) { System.out.println(noun);
		 
		 }*/
		System.out.println("CA: " + wordnet.sap("municipality", "region"));
		System.out.println("DIST: " + wordnet.distance("municipality",
		 "region"));
		System.out.println("CA: " + wordnet.sap("harm", "increase"));
		System.out.println("DIST: " + wordnet.distance("harm", "increase"));
		System.out.println("CA: " + wordnet.sap("descent", "resistance"));
		System.out.println("DIST: " + wordnet.distance("descent", "resistance"));
		
		// System.out.println("locomotion: " + wordnet.isNoun("locomotion"));
		/*
		 System.out.println("entity: " + wordnet.isNoun("entity") +
		 wordnet.wordmaps.get("entity")); Bag<Integer> id =
		 wordnet.wordmaps.get("entity"); for (Integer id1: id ) {
		 System.out.println(id1); } System.out.println("entity outdegree: " +
		 wordnet.G.outdegree(38003)); System.out.println("entity indegree: " +
		 wordnet.G.indegree(38003)); System.out.println("change outdegree: " +
		 wordnet.G.outdegree(79188)); System.out.println("change indegree: " +
		 wordnet.G.indegree(79188)); System.out.println("change outdegree: " +
		 wordnet.G.outdegree(28530)); System.out.println("change indegree: " +
		 wordnet.G.indegree(28530));
		  
		 System.out.println("harm outdegree: " + wordnet.G.outdegree(48251));
		 System.out.println("harm indegree: " + wordnet.G.indegree(48251));
		 System.out.println("harm outdegree: " + wordnet.G.outdegree(33756));
		 System.out.println("harm indegree: " + wordnet.G.indegree(33756));
		 System.out.println("harm outdegree: " + wordnet.G.outdegree(33755));
		 System.out.println("harme indegree: " + wordnet.G.indegree(33755));
		 System.out.println("CA: " + wordnet.sap("harm", "increase"));
		 System.out.println("DIST: " + wordnet.distance("harm", "increase"));*/
		 
	}
}