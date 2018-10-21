
/**
 * Outcast detection. Given a list of WordNet nouns x1, x2, ..., xn, which noun is the least related
 * to the others? To identify an outcast, compute the sum of the distances between each noun and every
 * other one:
 * di   =   distance(xi, x1)   +   distance(xi, x2)   +   ...   +   distance(xi, xn)
 * and return a noun xt for which dt is maximum. Note that distance(xi, xi) = 0, so it will not contribute to the sum.
 *
 * @param wordnet a WordNet object
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
	private WordNet wordnet;

	public Outcast(WordNet wordnet) { // constructor takes a WordNet object
		this.wordnet = wordnet;
	}
	/**
	 * Given an array of WordNet nouns, return an outcast
	 * @param nouns array of nouns
	 * @return the outcast noun
	 */
	public String outcast(String[] nouns)
	{
		String outcastnoun = nouns[0];
		int maxdist = 0;
		for (int i = 0; i < nouns.length; i++) {
			int dist = 0;
			for (int j = 0; j < nouns.length; j++) {
				dist += wordnet.distance(nouns[i], nouns[j]);
			}
			if (dist > maxdist) {
				maxdist = dist;
				outcastnoun = nouns[i];
			}
		}
		return outcastnoun;
	}

	/**
	 * Unit testing of the class
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
		Outcast outcast = new Outcast(wordnet);
		for (int t = 2; t < args.length; t++) {
			In in = new In(args[t]);
			String[] nouns = in.readAllStrings();
			StdOut.println(args[t] + ": " + outcast.outcast(nouns));
		}
	}
}
