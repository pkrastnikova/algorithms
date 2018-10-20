
/**
 * Finds the shortest ancestral path in a digraph between two vertices or
 * between two sets of vertices  
 * Dependencies: Digraph.java, DeluxeBFS.java
 * Help classes: Query, QuerySets, Ancestor
 * @param G digraph
 * @throws NullPointerException when constructor is called with a null argument
 * @author pkrastnikova
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class SAP {
	private Digraph G;
	private DeluxeBFS myBFS;
	private Map<Query, Ancestor> results; // stores search results for (int v, int w) queries in a HashTable
	private Map<QuerySets, Ancestor> resultsIter; // stores search results for sets queries in a HashTable

	// SAP constructor - creates DeluxeBFS object and initializes HashTables with results
	public SAP(Digraph G) {
		if (G == null) {
			throw new java.lang.NullPointerException();
		}
		this.G = new Digraph(G); // make a deep copy of the graph (immutable)
		results = new HashMap<Query, Ancestor>();
		resultsIter = new HashMap<QuerySets, Ancestor>();
		myBFS = new DeluxeBFS(G);
	}

	/**
	 * Help class for query (int v, int w)
	 * @overrides default equals() and HashCode() methods of Object class
	 */
	private class Query {
		private int s1;
		private int s2;

		private Query(int s1, int s2) {
			this.s1 = s1;
			this.s2 = s2;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			Query other = (Query) obj;
			if (s1 == other.s1 && s2 == other.s2)
				return true;
			return false;
		}

		@Override
		public int hashCode() {
			int hash = 1;
			hash = hash * 17 + s1;
			hash = hash * 31 + s2;
			return hash;
		}
	}

	/**
	 * Help class for query with sets (Iterable<Integer> v, Iterable<Integer> w)
	 * @overrides equals() and HashCode() methods of Object class
	 *
	 */
	private class QuerySets {
		private Iterable<Integer> s1;
		private Iterable<Integer> s2;

		private QuerySets(Iterable<Integer> s1, Iterable<Integer> s2) {
			this.s1 = s1;
			this.s2 = s2;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			QuerySets other = (QuerySets) obj;
			if (s1 == null) {
				if (other.s1 != null)
					return false;
			} else if (!s1.equals(other.s1))
				return false;
			if (s2 == null) {
				if (other.s2 != null)
					return false;
			} else if (!s2.equals(other.s2))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			Iterator<Integer> i1 = s1.iterator();
			Iterator<Integer> i2 = s2.iterator();
			int hash1 = 1, hash2 = 1;
			while (i1.hasNext()) {
				hash1 = hash1 * 31 + i1.next().hashCode();
			}
			while (i2.hasNext()) {
				hash2 = hash2 * 17 + i2.next().hashCode();
			}
			hashCode = hashCode + 31 * hash1 + 17 * hash2;
			return hashCode;
		}
	}

	/**
	 * Result object with distance and ancestor; stored as value in the HashTable
	 */
	private class Ancestor {
		private int distance;
		private int ancestor;

		private Ancestor(int d, int a) {
			distance = d;
			ancestor = a;
		}
	}

	/**
	 * Computes the length of shortest ancestral path between v and w 
	 * @param v vertex
	 * @param w vertex
	 * @return length; -1 if no such path
	 */
	public int length(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		Query key = new Query(v, w);
		if (results.containsKey(key))
			return results.get(key).distance;
		// System.out.println("Call BFS in length");
		myBFS.bfs(G, v, w);
		results.put(key, new Ancestor(myBFS.minDist(), myBFS.commonAncestor()));
		return myBFS.minDist();
	}

	/**
	 * Finds a common ancestor of v and w that participates in the shortest ancestral path
	 * @param v vertex
	 * @param w vertex
	 * @return ancestor; -1 if no such path
	 */
	public int ancestor(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		Query key = new Query(v, w);
		if (results.containsKey(key))
			return results.get(key).ancestor;
		// System.out.println("Call BFS in ancestor");
		myBFS.bfs(G, v, w);
		results.put(key, new Ancestor(myBFS.minDist(), myBFS.commonAncestor()));
		return myBFS.commonAncestor();

	}

	/**
	 * Computes the length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
	 * @param v set of vertices
	 * @param w set of vertices 
	 * @return length; -1 if no such path
	 */
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		validateVertices(v);
		validateVertices(w);
		QuerySets key = new QuerySets(v, w);
		if (resultsIter.containsKey(key))
			return resultsIter.get(key).distance;
		myBFS.bfs1(G, v, w);
		resultsIter.put(key, new Ancestor(myBFS.minDist(), myBFS.commonAncestor()));
		// System.out.println("Call iterable length");
		return myBFS.minDist();
	}

	/**
	 * Finds the common ancestor that participates in the shortest ancestral path
	 * @param v set of vertices
	 * @param w set of vertices
	 * @return ancestor; -1 if no such path
	 */
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		validateVertices(v);
		validateVertices(w);
		QuerySets key = new QuerySets(v, w);
		if (resultsIter.containsKey(key))
			return resultsIter.get(key).ancestor;
		// System.out.println("Call iterable ancestor");
		myBFS.bfs1(G, v, w);
		resultsIter.put(key, new Ancestor(myBFS.minDist(), myBFS.commonAncestor()));
		return myBFS.commonAncestor();
	}

	/**
	 * throws IllegalArgumentException unless {@code 0 <= v < V}
	 * @param v vertex
	 */
	private void validateVertex(int v) {
		int V = G.V();
		if (v < 0 || v >= V)
			throw new java.lang.IllegalArgumentException("vertex " + v
					+ " is not between 0 and " + (V - 1));
	}

	/**
	 * throws IllegalArgumentException unless {@code 0 <= v < V}
	 * @param vertices set of vertices
	 */
	private void validateVertices(Iterable<Integer> vertices) {
		if (vertices == null) {
			throw new java.lang.IllegalArgumentException(
					"Vertices cannot be null");
		}
		int V = G.V();
		for (int v : vertices) {
			if (v < 0 || v >= V) {
				throw new java.lang.IllegalArgumentException("vertex " + v
						+ " is not between 0 and " + (V - 1));
			}
		}
	}

	// do unit testing of this class
	public static void main(String[] args) {
		In in = new In(args[0]);
		Digraph G = new Digraph(in);
		SAP mySAP = new SAP(G);

		System.out.println("2, 6 ancestor: " + mySAP.ancestor(2, 6));
		System.out.println("2, 6 length: " + mySAP.length(2, 6));

		System.out.println("7, 0 ancestor: " + mySAP.ancestor(7, 0));
		System.out.println("7, 0 length: " + mySAP.length(7, 0));

		System.out.println("Repeat: ");
		System.out.println("2, 6 ancestor: " + mySAP.ancestor(2, 6));
		System.out.println("2, 6 length: " + mySAP.length(2, 6));

		SAP.Query item1 = mySAP.new Query(1, 2);
		SAP.Query item2 = mySAP.new Query(1, 2);
		System.out.println("item1.equals(item2)" + item1.equals(item2));
		mySAP.results.put(item1, mySAP.new Ancestor(1, 2));
		System.out.println(mySAP.results.containsKey(item2));

		System.out.println("Test Iterables:");
		SAP sap = new SAP(G);
		Set<Integer> a1 = new TreeSet<Integer>();
		a1.add(1);
		a1.add(5);
		a1.add(8);
		Set<Integer> b1 = new TreeSet<Integer>();
		b1.add(11);
		b1.add(3);

		int length = sap.length(a1, b1);
		int ancestor = sap.ancestor(a1, b1);
		System.out.println("length: " + length + " ancestor:" + ancestor);
		int length1 = sap.length(a1, b1);
		int ancestor1 = sap.ancestor(a1, b1);
		System.out.println("length1: " + length1 + " ancestor1:" + ancestor1);

		System.out.println("Repeat 7, 0");
		System.out.println("7, 0 ancestor: " + mySAP.ancestor(7, 0));
		System.out.println("7, 0 length: " + mySAP.length(7, 0));
		/*
		 * int v = 4; int w = 12; System.out.println("V: " + G.V());
		 * System.out.println("E:" + G.E()); int length = sap.length(v, w); int
		 * ancestor = sap.ancestor(v, w); System.out.println("length: " + length
		 * + "ancestor:" + ancestor);
		 */
		/*
		 * while (!StdIn.isEmpty()) { System.out.println("Start:"); int v =
		 * StdIn.readInt(); System.out.println("v:"); int w = StdIn.readInt();
		 * int length = sap.length(v, w); int ancestor = sap.ancestor(v, w);
		 * System.out.println("length: " + length + "ancestor:" + ancestor); }
		 */
	}
}
