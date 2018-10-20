import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
	private WordNet wordnet;

	public Outcast(WordNet wordnet) { // constructor takes a WordNet object

		this.wordnet = wordnet;
	}

	public String outcast(String[] nouns) // given an array of WordNet nouns,
											// return an outcast
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

	public static void main(String[] args) {
		/*WordNet wordnet = new WordNet(args[0], args[1]);
		Outcast outcast = new Outcast(wordnet);
		for (int t = 2; t < args.length; t++) {
			In in = new In(args[t]);
			String[] nouns = in.readAllStrings();
			StdOut.println(args[t] + ": " + outcast.outcast(nouns));
		}*/
	}
}
