
/**
 * Implements Seam-carving technique for image resize
 * Provides methods for vertical and horizontal seam removing
 * width(), height() and energy() methods take constant time
 * other methods run in time proportional to (width x height) of the picture
 * Energy of the pixel is calculated by Dual-gradient energy function
 *  
 * Dependencies: Picture.java
 * 
 * @param picture the image
 * @throws  IllegalArgumentexception when called with null argument
 * @author pkrastnikova
 */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
   private int width; // number of columns
   private int height; // number of rows
   
   private double [][] pixelsEnergy; // energy of each pixel
   private int [][] pixelsColor;  // color of each pixel
   
   /**
    * Creates a seam carver object based on the given picture
    * Initializes width, height and main data structures
    * [column x] [row y] notation is used
    * @param picture
    */
   public SeamCarver(Picture picture)    {            
	   if (picture == null) throw new java.lang.IllegalArgumentException(); 
	   this.width = picture.width();
	   this.height = picture.height();
	   pixelsEnergy = new double [width][height]; //[column x][row y]
	   pixelsColor = new int [width][height];
	   for (int x = 0; x < width; x++) {
		   for (int y = 0; y < height; y++) {
			   pixelsColor[x][y] = picture.getRGB(x, y);
		   }
	   }
	   for (int x = 0; x < width; x++) {
           for (int y = 0; y < height; y++) {
               pixelsEnergy[x][y] = energy(x, y);            
           }
       } 
   }
  
   /**
    * Returns current picture by creating a new picture object, using current pixelsColor array
    * @return picture
    */
   public Picture picture()  {                        // current picture
	   Picture currentPicture = new Picture(width(), height());
	   for (int x = 0; x < width(); x++) {
		   for (int y = 0; y < height(); y++) {
			   currentPicture.setRGB(x, y, pixelsColor[x][y]);
		   }
	   }
	   return currentPicture;
   }
   
   /**
    * Returns width of the current picture
    * @return width
    */
   public int width()  {                          // width of current picture
	   return this.width;
   }
   
   /**
    * Returns height of the current picture
    * @return height
    */
   public int height()  {                         // height of current picture
	   return this.height;
   }
   
   /**
    * Computes the energy of pixel (x, y) using dual-gradient energy function
    * Border pixels always have energy of 1000
    * Uses helper methods squareX(), squareY()
    * @param x pixel column
    * @param y pixel row
    * @throws IllegalArgumentException when x, y are outside prescribed range
    * @return pixel energy
    */
   public  double energy(int x, int y) {              // energy of pixel at column x and row y
	   if (x < 0 || x >= width() || y < 0 || y >= height()) throw new java.lang.IllegalArgumentException();
	   if (x == 0 || y == 0 || x == width() - 1 || y == height() -1) return 1000;
	   else return Math.sqrt(squareX(x, y) + squareY(x, y));
	   
   }
   
   /** 
    * Computes the central differences for x-gradient, 
    * based on horizontal neighbors (x+1, y) and (x-1, y)  
    * @param x column
    * @param y row
    * @return sum of squares of x-gradient RGB central differences
    */
   private int squareX(int x, int y) {
	   int red = ((pixelsColor[x+1][y] >> 16) & 0xFF) - ((pixelsColor[x-1][y] >> 16) & 0xFF);
	   int green = ((pixelsColor[x+1][y] >> 8) & 0xFF) - ((pixelsColor[x-1][y] >> 8) & 0xFF);
	   int blue = ((pixelsColor[x+1][y] >> 0) & 0xFF) - ((pixelsColor[x-1][y] >> 0) & 0xFF);
	   return red*red + green*green + blue*blue;
   }
   
   /** 
    * Computes the central differences for y-gradient, 
    * based on vertical neighbors (x, y+1) and (x, y-1)  
    * @param x column
    * @param y row
    * @return sum of squares of y-gradient RGB central differences
    */
   private int squareY(int x, int y) {
       int red = ((pixelsColor[x][y+1] >> 16) & 0xFF) - ((pixelsColor[x][y-1]>> 16) & 0xFF);
       int green = ((pixelsColor[x][y+1] >> 8) & 0xFF) - ((pixelsColor[x][y-1] >> 8) & 0xFF);
       int blue = ((pixelsColor[x][y+1] >> 0) & 0xFF) - ((pixelsColor[x][y-1] >> 0) & 0xFF);
	   return red*red + green*green + blue*blue;
   }
   
   /**
    * Transposes current energy and color matrixes 
    * Used to find and remove horizontal seam
    */
   private void transpose() {
	   double[][] transposedEnergy = new double[height()][width()];
	   int[][] transposedColor = new int[height()][width()];
	   for (int col = 0; col < height(); col++) {
		   for (int row = 0; row < width(); row++) { 
	            transposedEnergy[col][row] = pixelsEnergy[row][col];
	            transposedColor[col][row] = pixelsColor[row][col];
	       }
	   }   
	   this.pixelsEnergy = transposedEnergy;
	   this.pixelsColor = transposedColor;
	    
       // exchange width() and height()    
	   int tempWidth = width();
	   this.width = height();
	   this.height = tempWidth;           
   }
  
   /**
    * Finds vertical seam (pixels with minimum total energy on the vertical path) 
    * Helper method: findSeam()
    * @return array of indices for the vertical seam 
    */
   public int[] findVerticalSeam() {  // sequence of indices for vertical seam
	   int [] results = findSeam();
	   return results;		   
   }
   
   /**
    * Finds horizontal seam (pixels with minimum total energy on the horizontal path) 
    * Helper methods: findSeam(), transpose()
    * @return array of indices for the horizontal seam 
    */
   public int[] findHorizontalSeam() {  // sequence of indices for horizontal seam
	   transpose(); 
	   int [] results = findSeam();
	   transpose();
       return results;
   }
  
   /**
    * Finds a vertical seam
    * Uses two additional arrays: distTo to save distance from source to each vertex (pixel), and
    * edgeTo to store the last edge on the path to the given vertex 
    * @return array of indices, the vertical seam
    */
   private int[] findSeam() {
	   double[][] distTo = new double [width()][height()];
	   int[][] edgeTo = new int [width()][height()];
	   	      
	   //initialize distTo[][]  - top row with 0s; others with Infinity
	   for (int i = 0; i < width(); i++) {
		   distTo[i][0] = 0;
	   }
	   for (int i = 0; i < width(); i++) {
		   for (int j = 1; j < height(); j++) {
			   distTo[i][j] = Double.POSITIVE_INFINITY;
		   }
	   }
	   
	   // traverse the matrix by rows
	   // for each pixel update distTo and edgeTo of the 3 (or 2) outgoing vertices
	   for (int j = 0; j < height() - 1; j++) { //rows
		   for (int i = 0; i < width(); i++) { //columns
			   if (i == 0) { // first column
				   relax (0, j, 0, j+1, distTo, edgeTo);
				   if (width() > 1) relax (0, j, 1, j+1, distTo, edgeTo);
				   
			   }
			   else if (i == width() - 1) { // last column
				   relax (width()-1, j, width()-2, j+1, distTo, edgeTo);
				   relax (width()-1, j, width()-1, j+1, distTo, edgeTo);
			   }
			   else {
				   relax (i, j, i-1, j+1, distTo, edgeTo);
				   relax (i, j, i, j+1, distTo, edgeTo);
				   relax (i, j, i+1, j+1, distTo, edgeTo);
			   }
		   }
	   }
	   
	   
	   // find pixel from the bottom row with the shortest path (min distTo value)
	   double minDist = distTo[0][height()-1];
	   int minX = 0;
	  
	   for (int i = 1; i < width(); i++) {
	   	   if (distTo[i][height()-1] < minDist) {
	   		   minDist = distTo[i][height()-1];
	   		   minX = i; 
	   	   }
	   }
	  
	   int [] results = new int [height()];
	   results[height()-1] = minX;
	   
	   
	   // find the path from min bottom row pixel up to the top row
	   for (int i = height()-1; i > 0; i--) {
		   minX = edgeTo[minX][i];
		   minDist += distTo[minX][i-1];
		   results[i-1] = minX;
	   }
	   
	   return results;
   }
   
   /**
    * Private method for updating distTo and edgeTo for edge v -> w, using edge-relaxation technique
    * @param xFrom x-coordinate of v
    * @param yFrom y-coordinate of v
    * @param xTo x-coordinate of w
    * @param yTo y-coordinate of w
    * @param distTo[][] distances from source to each vertex
    * @param edgeTo[][] last edge (previous vertex) on the path to each vertex
    */
   private void relax (int xFrom, int yFrom, int xTo, int yTo, double [][] distTo, int [][] edgeTo){
	   if (distTo[xTo][yTo] > distTo[xFrom][yFrom] + pixelsEnergy[xTo][yTo]) {
		   distTo[xTo][yTo] = distTo[xFrom][yFrom] + pixelsEnergy[xTo][yTo];
		   edgeTo[xTo][yTo] = xFrom;
	   }
   }
   
   /**
    * Checks if the seam is a valid one
    * @throws IllegalArgumentException when seam is not valid
    * @param seam
    */
   private void validateSeam(int[] seam){
	   if (seam.length != height) throw new java.lang.IllegalArgumentException();
	   for (int i = 0; i < seam.length; i++) {
			   if (seam[i] < 0 || seam[i] >= width) throw new java.lang.IllegalArgumentException();
			   if (i < seam.length -1 && Math.abs(seam[i] - seam[i+1]) > 1) throw new java.lang.IllegalArgumentException();
	   }
   }
   
   /** 
    * Removes vertical seam
    * Helper method: removeSeam()
    * @throws IllegalArgumentException when called with null argument
    * @throws IllegalArgumentException when the picture has < = 1 columns 
    * @param seam the seam to be removed
    */
   public void removeVerticalSeam(int[] seam) {
	   /*System.out.println("removeVertical: ");
	   System.out.println("Initial width: " + width());
	   System.out.println("Initial height: " + height());
	   for (int i = 0; i < height(); i++) {
           for (int j = 0; j < width(); j++)
               StdOut.printf("%9.2f ", pixelsEnergy[j][i]);
           StdOut.println();
       }*/
	   
	   if (seam == null) throw new java.lang.IllegalArgumentException();
	   if (width() <= 1) throw new java.lang.IllegalArgumentException();
	   validateSeam(seam);
	   removeSeam(seam);
	   
	   /*System.out.println("After removeVertical, width, height: " + width() + height());
	   for (int i = 0; i < height(); i++) {
           for (int j = 0; j < width(); j++)
               StdOut.printf("%9.2f ", pixelsEnergy[j][i]);
           StdOut.println();
       }*/
   }
   
   /** 
    * Removes horizontal seam
    * Helper methods: removeSeam(), transpose()
    * @throws IllegalArgumentException when called with null argument
    * @throws IllegalArgumentException when the image has 1 or 0 rows 
    * @param seam the seam to be removed
    */
   public void removeHorizontalSeam(int[] seam)  { // remove horizontal seam from current picture
	   /*System.out.println("removeHorizontal: ");
	   System.out.println("Initial width: " + width());
	   System.out.println("Initial height: " + height());
	   System.out.println("Initial state before transpose: ");
	   for (int i = 0; i < height(); i++) {
           for (int j = 0; j < width(); j++)
               StdOut.printf("%9.2f ", pixelsEnergy[j][i]);
           StdOut.println();
       }*/
	   
	   if (seam == null) throw new java.lang.IllegalArgumentException();
	   if (height() <= 1) throw new java.lang.IllegalArgumentException();
	   
	   transpose();
	   
	   /*System.out.println("removeHorizontal, after first transpose:");
	   for (int i = 0; i < height(); i++) {
           for (int j = 0; j < width(); j++)
               StdOut.printf("%9.2f ", pixelsEnergy[j][i]);
           StdOut.println();
       }*/
	   
	   validateSeam(seam);
	   removeSeam(seam);

	   /*System.out.println("After removeHorizontal, width, height : "  + width() + height());
	   for (int i = 0; i < height(); i++) {
           for (int j = 0; j < width(); j++)
               StdOut.printf("%9.2f ", pixelsEnergy[j][i]);
           StdOut.println();
       }*/
	   
	   transpose();

	   /*System.out.println("After transpose back, width, height : " + width()+ height());
	   for (int i = 0; i < height(); i++) {
           for (int j = 0; j < width(); j++)
               StdOut.printf("%9.2f ", pixelsEnergy[j][i]);
           StdOut.println();
       }*/
	 
   }
   
   
   /**
    * Private method for removing a vertical seam
    * @param seam the seam to be removed
    */
   private void removeSeam(int [] seam) {   // remove vertical seam from current picture
	  
	   // shift cells
	   for (int i = 0; i < height(); i++) { //rows
		   double [] tempEnergy = new double [width()];
		   int [] tempColor = new int [width()];
		   for (int j = 0; j < width(); j++) { //columns
			   tempEnergy[j] =  pixelsEnergy[j][i];
			   tempColor[j] =  pixelsColor[j][i];
		   }
		   System.arraycopy(tempEnergy, seam[i]+1, tempEnergy, seam[i], width()-seam[i]-1);
		   System.arraycopy(tempColor, seam[i]+1, tempColor, seam[i], width()-seam[i]-1);
		   for (int j = 0; j < width()-1; j++) {
			   pixelsEnergy[j][i] = tempEnergy[j];
			   pixelsColor[j][i] = tempColor[j];
		   }
	   }
	   
	   this.width--;
	   
	   //System.out.println("Width at the end of removeSeam:" + width);
	   //System.out.println("After shift: ");
	   /*for (int i = 0; i < height(); i++) {
           for (int j = 0; j < width(); j++)
               StdOut.printf("%9.2f ", pixelsEnergy[j][i]);
           StdOut.println();
       }*/
	  
	   
	   
	   // recalculate energy for removed element and the element before
	   for (int i = 1; i < seam.length-1; i++) {
		   int delIndex = seam[i];
		   if (delIndex < width() && delIndex >= 0) {
			   pixelsEnergy[delIndex][i] = energy(delIndex,i);
		   }
		  
		   if (delIndex-1 >= 0) {
			   pixelsEnergy[delIndex-1][i] = energy(delIndex-1,i);
		   }
	   }
	 
	   /*System.out.println("After recalculate energy: ");
	   for (int i = 0; i < height(); i++) {
           for (int j = 0; j < width(); j++)
               StdOut.printf("%9.2f ", pixelsEnergy[j][i]);
           StdOut.println();
       }
	   */
	  
	   //System.out.println("Width after resize:" + width());
	   //System.out.println("Height after resize:" + height());
	   
   }
   
   
   public static void main(String[] args) {
	   Picture picture = new Picture(args[0]);
       SeamCarver carver = new SeamCarver(picture);
       System.out.println("width: " + carver.width());
       System.out.println("height: " + carver.height());
	   int[] verticalSeam = carver.findVerticalSeam();
 
       for (int i = 0; i< verticalSeam.length; i++) {
    	   System.out.print(verticalSeam[i] + " ");
       }
       carver.removeVerticalSeam(verticalSeam);
       
	    /*  carver.picture()
	      carver.findHorizontalSeam()
	      carver.removeHorizontalSeam()
	      carver.picture()
	      carver.findHorizontalSeam()
	   
	   Picture picture = new Picture(args[0]);
       SeamCarver carver = new SeamCarver(picture);
       System.out.println("width: " + carver.width());
       System.out.println("height: " + carver.height());
       
       carver.picture();
       System.out.println("Step1: FindHorizontal: ");
       int[] horizontalSeam = carver.findHorizontalSeam();
       
       for (int i = 0; i< horizontalSeam.length; i++) {
    	   System.out.print(horizontalSeam[i] + " ");
       }
       System.out.println();
       System.out.println("Step2: RemoveHorizontal: ");
       carver.removeHorizontalSeam(horizontalSeam);
       carver.picture();
       System.out.println("Step3: FindHorizontal: ");
       horizontalSeam = carver.findHorizontalSeam();
       
       for (int i = 0; i< horizontalSeam.length; i++) {
    	   System.out.print(horizontalSeam[i] + " ");
       }
       
       System.out.println("Step0: FindVertical: ");
       int[] verticalSeam = carver.findVerticalSeam();
       System.out.println("Step01: RemoveVertical: ");
       
       for (int i = 0; i< verticalSeam.length; i++) {
    	   System.out.print(verticalSeam[i] + " ");
       }
       carver.removeVerticalSeam(verticalSeam);
        
       //Picture newPicture = new Picture(carver.picture());
       //newPicture.show();
       
      */

   }
}