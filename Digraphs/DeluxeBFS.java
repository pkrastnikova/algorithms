import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class DeluxeBFS {

	private static final int INFINITY = Integer.MAX_VALUE;
	private boolean[] marked1; // marked[v] = is there an s->v path?
	private int[] edgeTo1; // edgeTo[v] = last edge on shortest s->v path
	private int[] distTo1; // distTo[v] = length of shortest s->v path
	private boolean[] marked2; // marked[v] = is there an t->v path?
	private int[] edgeTo2; // edgeTo[v] = last edge on shortest t->v path
	private int[] distTo2; // distTo[v] = length of shortest t->v path
	private Set<Integer> changes1; // track updated notes for source s
	private Set<Integer> changes2; // track updated notes for source t
	private int minDist;
	private int commonAncestor;

	/**
	 * Computes the shortest path from {@code s} and every other vertex in graph
	 * {@code G}.
	 * 
	 * @param G
	 *            the digraph
	 * @param s
	 *            the source vertex
	 * @throws IllegalArgumentException
	 *             unless {@code 0 <= v < V}
	 */
	public DeluxeBFS(Digraph G) {
		marked1 = new boolean[G.V()];
		distTo1 = new int[G.V()];
		edgeTo1 = new int[G.V()];
		marked2 = new boolean[G.V()];
		distTo2 = new int[G.V()];
		edgeTo2 = new int[G.V()];
		minDist = INFINITY;
		commonAncestor = -1;
		for (int v = 0; v < G.V(); v++) {
			distTo1[v] = INFINITY;
			distTo2[v] = INFINITY;
		}
		changes1 = new HashSet<Integer>();
		changes2 = new HashSet<Integer>();

	}

	

	public int commonAncestor() {
		return this.commonAncestor;
	}

	public int minDist() {
		if (this.commonAncestor == -1)
			this.minDist = -1;
		return this.minDist;
	}
	
	// reset the three arrays for s and t only for changed items
	// => do not need to initialize the whole arrays for every new search
	private void resetValues() {
		if (changes1.isEmpty() && changes2.isEmpty())
			return;

		for (int i1 : changes1) {
			// System.out.println("i1: " + i1);
			marked1[i1] = false;
			distTo1[i1] = INFINITY;
			edgeTo1[i1] = 0;

		}
		changes1.clear();

		for (int i2 : changes2) {
			// System.out.println("i2:" + i2);
			marked2[i2] = false;
			distTo2[i2] = INFINITY;
			edgeTo2[i2] = 0;

		}
		changes2.clear();

		this.minDist = INFINITY;
		this.commonAncestor = -1;

	}

	// BFS from 2 single sources
	public void bfs(Digraph G, int s, int t) {
		validateVertex(s);
		validateVertex(t);
		resetValues();
		if (s == t) {
			minDist = 0;
			commonAncestor = s;
			changes1.add(s);
			//changes2.add(s);
			return;
		}
		

		Queue<Integer> q = new Queue<Integer>();
		Queue<Integer> h = new Queue<Integer>();

		marked1[s] = true;
		changes1.add(s);
		distTo1[s] = 0;
		marked2[t] = true;
		changes2.add(t);
		distTo2[t] = 0;
		q.enqueue(s);
		q.enqueue(t);
		h.enqueue(1);
		h.enqueue(2);

		while (!q.isEmpty()) {

			int v = q.dequeue();
			int sourceIndex = h.dequeue();

			for (int w : G.adj(v)) {
				if (sourceIndex == 1) {
					if (!marked1[w]) {
						if (distTo1[v] + 1 > minDist)
							break;
						edgeTo1[w] = v;
						distTo1[w] = distTo1[v] + 1;
						marked1[w] = true;
						changes1.add(w);
						q.enqueue(w);
						h.enqueue(1);
						if (marked2[w] == true) {
							// check total distance
							if (distTo1[w] + distTo2[w] < minDist) {
								this.minDist = distTo1[w] + distTo2[w];
								this.commonAncestor = w;
							}
						}
					}
				}

				else if (sourceIndex == 2) {
					if (!marked2[w]) {
						if (distTo2[v] + 1 > minDist)
							break;
						edgeTo2[w] = v;
						distTo2[w] = distTo2[v] + 1;
						marked2[w] = true;
						changes2.add(w);
						q.enqueue(w);
						h.enqueue(2);
						if (marked1[w] == true) {
							// check total distance
							if (distTo1[w] + distTo2[w] < minDist) {
								this.minDist = distTo1[w] + distTo2[w];
								this.commonAncestor = w;
							}
						}
					}
				}
			}
		}
	}

	// BFS from multiple sources - try 1 (slower)
	// put in the queue all nodes from sA and then all nodes from sB
	public void bfs(Digraph G, Iterable<Integer> sA, Iterable<Integer> sB) {
		validateVertices(sA);
		validateVertices(sB);
		resetValues();
		Queue<Integer> q = new Queue<Integer>();
		Queue<Integer> h = new Queue<Integer>();
		for (int s : sA) {
			marked1[s] = true;
			distTo1[s] = 0;
			changes1.add(s);
			q.enqueue(s);
			h.enqueue(1);
		}
		for (int t : sB) {
			marked2[t] = true;
			distTo2[t] = 0;
			changes2.add(t);
			q.enqueue(t);
			h.enqueue(2);
		}
		while (!q.isEmpty()) {

			int v = q.dequeue();
			int sourceIndex = h.dequeue();

			for (int w : G.adj(v)) {
				if (sourceIndex == 1) {
					if (!marked1[w]) {
						if (distTo1[v] + 1 > minDist)
							break;
						edgeTo1[w] = v;
						distTo1[w] = distTo1[v] + 1;
						marked1[w] = true;
						changes1.add(w);
						q.enqueue(w);
						h.enqueue(1);
						if (marked2[w] == true) {
							// check total distance
							if (distTo1[w] + distTo2[w] < minDist) {
								this.minDist = distTo1[w] + distTo2[w];
								this.commonAncestor = w;
							}
						}
					}
				}

				else if (sourceIndex == 2) {
					if (!marked2[w]) {
						if (distTo2[v] + 1 > minDist)
							break;
						edgeTo2[w] = v;
						distTo2[w] = distTo2[v] + 1;
						marked2[w] = true;
						changes2.add(w);
						q.enqueue(w);
						h.enqueue(2);
						if (marked1[w] == true) {
							// check total distance
							if (distTo1[w] + distTo2[w] < minDist) {
								this.minDist = distTo1[w] + distTo2[w];
								this.commonAncestor = w;
							}
						}
					}
				}
			}
		}
	}
	// BFS from multiple sources - try 2
	// put in the queue nodes alternating between sA and sB - one from sA, one from sB ...
		public void bfs1(Digraph G, Iterable<Integer> sA, Iterable<Integer> sB) {
			validateVertices(sA);
			validateVertices(sB);
			resetValues();
			Iterator<Integer> itA = sA.iterator();
			Iterator<Integer> itB = sB.iterator();
			Queue<Integer> q = new Queue<Integer>();
			Queue<Integer> h = new Queue<Integer>();
			//System.out.println();
			while (itA.hasNext() || itB.hasNext()) {
				if (itA.hasNext())
					{int s = itA.next();
					 marked1[s] = true;
					 distTo1[s] = 0;
					 changes1.add(s);
					 q.enqueue(s);
					 h.enqueue(1);
					 //System.out.print("s: " + s);
					
					}
				if (itB.hasNext())
					{int t = itB.next();
					 marked2[t] = true;
					 distTo2[t] = 0;
					 changes2.add(t);
					 q.enqueue(t);
					 h.enqueue(2);
					 //System.out.println(" t: " + t);
					}
				
			}
			
			processQueue(G, q, h);
			
		}
		private void processQueue(Digraph G, Queue<Integer> q, Queue<Integer> h) {
			while (!q.isEmpty()) {

				int v = q.dequeue();
				int sourceIndex = h.dequeue();

				for (int w : G.adj(v)) {
					if (sourceIndex == 1) {
						if (!marked1[w]) {
							if (distTo1[v] + 1 > minDist)
								break;
							edgeTo1[w] = v;
							distTo1[w] = distTo1[v] + 1;
							marked1[w] = true;
							changes1.add(w);
							q.enqueue(w);
							h.enqueue(1);
							if (marked2[w] == true) {
								// check total distance
								if (distTo1[w] + distTo2[w] < minDist) {
									this.minDist = distTo1[w] + distTo2[w];
									this.commonAncestor = w;
								}
							}
						}
					}

					else if (sourceIndex == 2) {
						if (!marked2[w]) {
							if (distTo2[v] + 1 > minDist)
								break;
							edgeTo2[w] = v;
							distTo2[w] = distTo2[v] + 1;
							marked2[w] = true;
							changes2.add(w);
							q.enqueue(w);
							h.enqueue(2);
							if (marked1[w] == true) {
								// check total distance
								if (distTo1[w] + distTo2[w] < minDist) {
									this.minDist = distTo1[w] + distTo2[w];
									this.commonAncestor = w;
								}
							}
						}
					}
				}
			}
		}
	/**
	 * Is there a directed path from the source {@code s} (or sources) to vertex
	 * {@code v}?
	 * 
	 * @param v
	 *            the vertex
	 * @return {@code true} if there is a directed path, {@code false} otherwise
	 * @throws IllegalArgumentException
	 *             unless {@code 0 <= v < V}
	 */
	// not used
		public boolean hasPathTo(int v) {
		validateVertex(v);
		return marked1[v];
	}

	/**
	 * Returns the number of edges in a shortest path from the source {@code s}
	 * (or sources) to vertex {@code v}?
	 * 
	 * @param v
	 *            the vertex
	 * @return the number of edges in a shortest path
	 * @throws IllegalArgumentException
	 *             unless {@code 0 <= v < V}
	 */
	// not used	
	public int distTo(int v) {
		validateVertex(v);
		return distTo1[v];
	}

	/**
	 * Returns a shortest path from {@code s} (or sources) to {@code v}, or
	 * {@code null} if no such path.
	 * 
	 * @param v
	 *            the vertex
	 * @return the sequence of vertices on a shortest path, as an Iterable
	 * @throws IllegalArgumentException
	 *             unless {@code 0 <= v < V}
	 */
	// not used
	public Iterable<Integer> pathTo(int v) {
		validateVertex(v);

		if (!hasPathTo(v))
			return null;
		Stack<Integer> path = new Stack<Integer>();
		int x;
		for (x = v; distTo1[x] != 0; x = edgeTo1[x])
			path.push(x);
		path.push(x);
		return path;
	}

	// throw an IllegalArgumentException unless {@code 0 <= v < V}
	private void validateVertex(int v) {
		int V = marked1.length;
		if (v < 0 || v >= V)
			throw new IllegalArgumentException("vertex " + v
					+ " is not between 0 and " + (V - 1));
	}

	// throw an IllegalArgumentException unless {@code 0 <= v < V}
	private void validateVertices(Iterable<Integer> vertices) {
		if (vertices == null) {
			throw new IllegalArgumentException("argument is null");
		}
		int V = marked1.length;
		for (int v : vertices) {
			if (v < 0 || v >= V) {
				throw new IllegalArgumentException("vertex " + v
						+ " is not between 0 and " + (V - 1));
			}
		}
	}

	/**
	 * Unit tests the {@code BreadthFirstDirectedPaths} data type.
	 *
	 * @param args
	 *            the command-line arguments
	 */
	public static void main(String[] args) {
		In in = new In(args[0]);
		Digraph G = new Digraph(in);
		DeluxeBFS bfs = new DeluxeBFS(G);

		bfs.bfs(G, 3, 3);
		System.out.println("Common ancestor: " + bfs.commonAncestor());
		System.out.println("Common distance: " + bfs.minDist());
		System.out.println("Second search: ");
		bfs.bfs(G, 5, 1);
		System.out.println("Common ancestor2: " + bfs.commonAncestor());
		System.out.println("Common distance2: " + bfs.minDist());

		bfs.bfs(G, 1, 6);
		System.out.println("Common ancestor3: " + bfs.commonAncestor());
		System.out.println("Common distance3: " + bfs.minDist());

		bfs.bfs(G, 6, 5);
		System.out.println("Common ancestor4: " + bfs.commonAncestor());
		System.out.println("Common distance4: " + bfs.minDist());

		bfs.bfs(G, 5, 1);
		System.out.println("Common ancestor4: " + bfs.commonAncestor());
		System.out.println("Common distance4: " + bfs.minDist());

		bfs.bfs(G, 5, 5);
		System.out.println("Common ancestor4: " + bfs.commonAncestor());
		System.out.println("Common distance4: " + bfs.minDist());

		Set<Integer> a = new TreeSet<Integer>();
		a.add(8);
		a.add(6);
		a.add(5);
		Set<Integer> b = new TreeSet<Integer>();
		b.add(1);
		b.add(5);

		bfs.bfs(G, a, b);
		System.out.println("Common ancestor5: " + bfs.commonAncestor());
		System.out.println("Common distance5: " + bfs.minDist());
		// StdOut.println(G);

		/*
		 * int s = Integer.parseInt(args[1]); DeluxeBFS bfs = new DeluxeBFS(G,
		 * s, 0); bfs.bfs(G, s, 0); for (int v = 0; v < G.V(); v++) { if
		 * (bfs.hasPathTo(v)) { StdOut.printf("%d to %d (%d):  ", s, v,
		 * bfs.distTo(v)); for (int x : bfs.pathTo(v)) { if (x == s)
		 * StdOut.print(x); else StdOut.print("->" + x); } StdOut.println(); }
		 * 
		 * else { StdOut.printf("%d to %d (-):  not connected\n", s, v); }
		 * 
		 * }
		 * 
		 * bfs.bfs(G, 1,0 ); System.out.println("Second search: "); for (int v =
		 * 0; v < G.V(); v++) { if (bfs.hasPathTo(v)) {
		 * StdOut.printf("%d to %d (%d):  ", 1, v, bfs.distTo(v)); for (int x :
		 * bfs.pathTo(v)) { if (x == 1) StdOut.print(x); else StdOut.print("->"
		 * + x); } StdOut.println(); }
		 * 
		 * else { StdOut.printf("%d to %d (-):  not connected\n", 1, v); }
		 * 
		 * }
		 */
	}

}
