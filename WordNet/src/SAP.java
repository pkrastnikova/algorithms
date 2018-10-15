//import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
//import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
	Digraph G;
	
   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G) {
	   this.G = new Digraph(G);
   }

   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w) {
	   return findCommonAncestor(v, w, false);   
   }

   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
   public int ancestor(int v, int w) {
	   return findCommonAncestor(v, w, true);
   }

   // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
   public int length(Iterable<Integer> v, Iterable<Integer> w) {
	   return findCommonAncestor(v, w, false);
   }

   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
	   return findCommonAncestor(v, w, true);
   }
   
  

   private int findCommonAncestor(int v, int w, boolean flag) {
	   int commonAncestor = -1;
	   int minDist = Integer.MAX_VALUE;
	   System.out.println("v: " + v);
	   System.out.println("w: " + w);
	   BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(G, v);
       BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(G, w);
      
       for (int node = 0; node < G.V(); node++)
       {
    	   if (bfs1.hasPathTo(node)) {
    		   if(bfs2.hasPathTo(node)) {
    			   if ((bfs1.distTo(node) + bfs2.distTo(node)) < minDist) {
    				   minDist = bfs1.distTo(node) + bfs2.distTo(node);
    				   commonAncestor = node; }
               }
           }     
       }    
       System.out.println("commonAncestor: " + commonAncestor);
       System.out.println("minDist: " + minDist);
       if (minDist == Integer.MAX_VALUE) {minDist = -1;}
       if (flag) {return commonAncestor;} else {return minDist;} 
    }
   	
   private int findCommonAncestor(Iterable<Integer> v, Iterable<Integer> w, boolean flag) {
	   int commonAncestor = -1;
	   int minDist = Integer.MAX_VALUE;
	   BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(G, v);
       BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(G, w);
      
       for (int node = 0; node < G.V(); node++)
       {
    	   if (bfs1.hasPathTo(node)) {
    		   if(bfs2.hasPathTo(node)) {
    			   if ((bfs1.distTo(node) + bfs2.distTo(node)) < minDist) {
    				   minDist = bfs1.distTo(node) + bfs2.distTo(node);
    				   commonAncestor = node; }
               }
           }     
       }
       if (minDist == Integer.MAX_VALUE) {minDist = -1;}
       if (flag) {return commonAncestor;} else {return minDist;}
    }

   
   // do unit testing of this class
   public static void main(String[] args) {
	    In in = new In(args[0]);
	    //In in = "/Users/jonny/Documents/Pepas documents/Java_learning/Diagraphs/wordnet/digraph5.txt";
	    Digraph G = new Digraph(in);
	    SAP sap = new SAP(G);
	    int v = 1;
	    int w = 6;
	    System.out.println("V: " + G.V());
	    System.out.println("E:" + G.E());
	    int length   = sap.length(v, w);
        int ancestor = sap.ancestor(v, w);
        System.out.println("length: " + length + "ancestor:" + ancestor);
	    /*while (!StdIn.isEmpty()) {
	        int v = StdIn.readInt();
	        int w = StdIn.readInt();
	        int length   = sap.length(v, w);
	        int ancestor = sap.ancestor(v, w);
	        System.out.println("length: " + length + "ancestor:" + ancestor);
	    }*/
	}
}
