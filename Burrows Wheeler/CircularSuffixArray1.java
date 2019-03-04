// first variant of CircularSuffixArray

/**
 * Sort an array of circular suffixes of a string
 *
 * Use LSD Radix sort algorithm (slower than 3-way quicksort):
 * 1. Create count[] array with frequencies - each element from Ascii code 0-255
 * table will have number of occurrences of that char
 * 2. Cumulative count - recalculate count array so that for each char it points to
 * the index in the original array it should go (if multiple occurrences of same letter  
 * - range of indices that char will occupy)
 * Note: Use offset of 1 for count so that the sorted array starts from index 0
 * 3. Read characters from the original array starting from the last letter, 
 * and put them in respective indices according to count
 *
 * @author pkrastnikova
 *
 */

public class CircularSuffixArray1 {
   private int N;
   private int indices [];
   private int R = 256;
   private String s;
   private int count [];   
	
   public CircularSuffixArray1(String s) {    // circular suffix array of s
       if (s == null) throw new java.lang.IllegalArgumentException();
	   N = s.length();
	   indices = new int[N];
	   
	   // initialize indices - use reference to suffixes
	   // Example: first suffix will have reference index 0, next - index 1 and so forth
	   for (int i = 0; i < N; i++){
		   indices[i] = i;
	   }
	   this.s = s;
	   count = new int[R+1];
	  
	   // count occurrences
       for (int i = 0; i < N; i++) {
           count[s.charAt(i)+1] ++;     
       }
       
       // cumulative count
       for (int r1 = 0; r1 < R; r1++) {
           count[r1+1]+=count[r1];    
       }
       
       // start sorting by last letter of each suffix
       // then by second last and so forth to the first
       for (int d = N-1; d >= 0; d--) {
		   sort(d);
	   }
   }
   
   // find d-th character in a suffix
   private char findD(int suffixIndex, int d) {
	   return (s.charAt((suffixIndex+d) % N));
   }
   
   // sort by d-th character
   private void sort(int d) {   
       int aux[] = new int [N];
	   int tempCount [] = new int[R+1];
	   
	   for (int k = 0; k < R+1; k++) {
	       tempCount[k] = count[k];
	   }
	 
	   for (int i = 0; i < N; i++) {
	       int index = indices[i];
	       char ch = findD(index, d);
	   	   aux[tempCount[ch]] = index;
	   	   tempCount[ch]++;
	   	   
	   }
	   for (int i = 0; i < N; i++){
	       indices[i] = aux[i];
	   }
	   /*System.out.println("d: " + d);
	   for (int i = 0; i < N; i++){
           System.out.print(indices[i]+ " ");
       }
	   System.out.println();
	   */
   }
   
   // returns length of string s
   public int length() {                    
	   return N;
   }
   
   // returns index of ith sorted suffix
   public int index(int i) {                
     if (i < 0 || i > N-1) throw new java.lang.IllegalArgumentException();	
	   return indices[i];
   }
   
   public static void main(String[] args)  // unit testing (required)
   {
	   CircularSuffixArray1 sf = new CircularSuffixArray1("ABRACADABRA!");
	   System.out.println("Results: ");
	   for (int k = 0; k< sf.N; k++){
        	System.out.print(sf.indices[k] + " ");
        }
	   System.out.println();
	   System.out.println(sf.index(2));
   }
}
