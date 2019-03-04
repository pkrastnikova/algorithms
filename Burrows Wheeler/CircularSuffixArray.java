
// Second variant of CircularSuffixArray using Quicksort

/**
 * Sort an array of circular suffixes of a string
 * Use 3-way Quicksort algorithm - partition by the first letter (d-th character in the first 
 * suffix) - suffixes that start with letter less than d move before the first suffix;
 * suffixes that start with letter bigger than d go after first suffix
 * those that start with same letter go next to current suffix
 * then recursively sort each partition using the same rule
*/

public class CircularSuffixArray {
	   private int N; // number of suffixes
	   private int indices []; //array with int references to each suffix
	   private String s;
	
	   public CircularSuffixArray(String s) {   
	      if (s == null) throw new java.lang.IllegalArgumentException();
		  N = s.length();	   
		  indices = new int[N];
		   
		   // Initialize the index array
		  for (int i = 0; i < N; i++){
			  indices[i] = i;
			  System.out.print(indices[i]);
		   }
		  System.out.println();
		  this.s = s;
		   
		  sort(0, N-1, 0);
		   
	   }
	   
	   // Find d-th character in a suffix; returns corresponding Ascii code
	   // Example: "book" - suffix starting from k will be "kboo", its index is 3
	   // findD(3, 1) = "b"; findD(3, 2) = "o"
	   private int findD(int suffixIndex, int d) {
	       if (d == s.length()) return -1;
		   return (s.charAt((suffixIndex+d) % N));
	   }
	   
	   // exchange 2 elements in indices array   
	   private void exch(int x, int y) {
		   int z = indices[x];
		   indices[x] = indices[y];
		   indices[y]= z;
	   }
	   
	   // core sorting method
	   private void sort(int lo, int hi, int d) {  
	       System.out.print("lo: " + lo);
	       System.out.print(" hi: " + hi);
	       System.out.println(" d: " + d);
		   if (hi <= lo || hi < 0) return; 
	       
	        int lt = lo, gt = hi;
	        int v = findD(indices[lo], d); 
	        System.out.println("v: " + (char)v);
	        int i = lo + 1;
	        
	        // partition by d-th char
	        while (i <= gt) { 
	            int t = findD(indices[i], d); 
	            System.out.println("t: " + (char)t);
	            if (t < v) exch(lt++, i++);
	            else if (t > v) exch(i, gt--); 
	            else i++; 
	        } 
	        for (int k = 0; k< N; k++){
	        	System.out.print(indices[k] + " ");
	        }
	        System.out.println();
	       
	        // recursively sort each partition
	        System.out.print("lo: " + lo);
	        System.out.print(" lt: " + lt);
	        System.out.println();
	        System.out.println();
	        
	        sort(lo, lt-1, d);
	        System.out.println("Sort1 completed: " + lo + (lt-1) +  d);
	        System.out.println();
	        
	        if (v >= 0) sort(lt, gt, d+1); 
	        System.out.println("Sort2 completed: " + lt + gt +  d+1);
	       
	        sort(gt+1, hi, d); 
	        System.out.println("Sort3 completed: " + (gt+1) + hi +  d);
	        
	    } 
	   
	   // return the length of suffix array
	   public int length() {                    
		   return N;
	   }
	   
	   // returns the original index of ith sorted suffix
	   public int index(int i) {                
	      if (i < 0 || i > N-1) throw new java.lang.IllegalArgumentException();	
		  return indices[i];
	   }
	   
	   public static void main(String[] args) 
	   {
		   //CircularSuffixArray sf = new CircularSuffixArray("couscous");
		   CircularSuffixArray sf = new CircularSuffixArray("banana");
		   System.out.println("Results: ");
		   for (int k = 0; k< sf.N; k++){
	        	System.out.print(sf.indices[k] + " ");
	        }
		   System.out.println();
		   System.out.println(sf.index(0));
		   System.out.println(sf.index(1));
		   System.out.println(sf.index(5));
	   }
}
