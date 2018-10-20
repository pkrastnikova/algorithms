import java.util.HashMap;
import java.util.Map;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class WordNet {
	private Map<String, Bag<Integer>> wordSynsetsMap;
	// maps "word -> synsets IDs", one word can be part of several synsets (IDs)
	private Map<Integer, String> idSynsetMap; // maps "ID -> synset"
	private Digraph G;
	private int graphsize;
	private SAP paths;

	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {
		if (synsets == null || hypernyms == null) {
			throw new java.lang.IllegalArgumentException();
		}
		this.wordSynsetsMap = new HashMap<String, Bag<Integer>>();
		this.idSynsetMap = new HashMap<Integer, String>();
		buildWordMap(synsets);
		this.G = new Digraph(graphsize);
		buildGraph(hypernyms);
		if (!rootedDigraph1()) {
			throw new java.lang.IllegalArgumentException("Graph not rooted");
		}
		this.paths = new SAP(G);
	}

	/*
	 * check G.outdegree(v) for each vertex if there's only one vertex with 0
	 * outdegree (the root) -> rooted
	 */
	private boolean rootedDigraph1() {
		int count = 0;
		for (int id: this.idSynsetMap.keySet()){
			if (G.outdegree(id) == 0) {count++;}
		}
		
		//System.out.println("Outdegree entity: " + G.outdegree(38003));
		//System.out.println("Rootcount: " + count);
		return (count == 1);
			
	}

	/*
	 * map word -> synset IDs (all synsets the word is part of)
	 * map ID -> synset for each record of synsets file
	 * set size of the map to be Digraph's size (number of vertices)
	 * @param filename synsets file
	 */
	private void buildWordMap(String filename) {
		In synsets = new In(filename);
		while (synsets.hasNextLine()) {
			String line = synsets.readLine();
			String[] fields = line.split("\\,");
			int id = Integer.parseInt(fields[0]);
			idSynsetMap.put(id, fields[1]);
			String[] words = fields[1].split("\\ ");
			for (String word : words) {
				Bag<Integer> synsetsOfWord = wordSynsetsMap.get(word);
				if (synsetsOfWord == null) {
					wordSynsetsMap.put(word, synsetsOfWord = new Bag<Integer>());
				}
				synsetsOfWord.add(id);
			}
		}
		this.graphsize = wordSynsetsMap.size();
		synsets.close();

	}

	// build Digraph from hypernyms file (create edges)
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

	// returns all WordNet nouns
	public Iterable<String> nouns() {
		return wordSynsetsMap.keySet();
	}

	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		if (word == null) {
			throw new java.lang.IllegalArgumentException();
		}
		return wordSynsetsMap.containsKey(word);
	}

	// distance between nounA and nounB (defined below)
	public int distance(String nounA, String nounB) {
		if (nounA == null || nounB == null) {
			throw new java.lang.IllegalArgumentException();
		}
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new java.lang.IllegalArgumentException("Not a WordNet noun");
		}
		Bag<Integer> idA = wordSynsetsMap.get(nounA); // get synsets IDs for nounA
		Bag<Integer> idB = wordSynsetsMap.get(nounB); // get synsets IDs for nounB
		return paths.length(idA, idB); // call length(Iterable<Integer> v,
										// Interable<Integer> w)
	}

	/*
	 * a synset (second field of synsets.txt) that is the common ancestor of
	 * nounA and nounB in a shortest ancestral path (defined below)
	 */

	public String sap(String nounA, String nounB) {
		if (nounA == null || nounB == null) {
			throw new java.lang.IllegalArgumentException();
		}
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new java.lang.IllegalArgumentException("Not a WordNet noun");
		}
		Bag<Integer> idA = wordSynsetsMap.get(nounA);
		Bag<Integer> idB = wordSynsetsMap.get(nounB);
		int commonAncestor = paths.ancestor(idA, idB);
		return idSynsetMap.get(commonAncestor);
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