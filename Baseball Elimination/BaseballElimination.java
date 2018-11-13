/**
 * An immutable data type that represents a sports division and
 * determines which teams are mathematically eliminated
 * Dependencies: FlowEdge.java, FlowNetwork.java, FordFulkerson.java, Bag.java
 * @author pkrastnikova 
 */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
	private int n; // number of teams
	private int[] wins; // wins of each team i
	private int[] losses; // losses of each team i
	private int[] remain; // remaining games for each team i 
	private int[][] games; // scheduled games between team i and team j
	private String[] teams; // names of teams
	private ST<String, Integer> teamsST; // symbol table <team name, team index>
	private boolean eliminated; // is the team eliminated?
	private Bag<String> R = new Bag<String>(); // certificate of elimination set of teams

	/**
	 * Creates a baseball division from given filename 
	 * Initializes data structures
	 * @param filename text file with teams data
	 */
	public BaseballElimination(String filename) { 
													
		In textFile = new In(filename);
		n = textFile.readInt(); // number of teams
		// System.out.println("Number of teams: " + n);
		wins = new int[n];
		losses = new int[n];
		remain = new int[n];
		games = new int[n][n];
		teams = new String[n];
		teamsST = new ST<String, Integer>(); 
		for (int i = 0; i < n; i++) {
			teams[i] = textFile.readString();
			teamsST.put(teams[i], i);
			wins[i] = textFile.readInt();
			losses[i] = textFile.readInt();
			remain[i] = textFile.readInt();
			for (int j = 0; j < n; j++) {
				games[i][j] = textFile.readInt();
			}
		}
	}

	
	/**
	 * Returns number of teams
	 * @return number of teams
	 */
	public int numberOfTeams() {
		return n;
	}

	
	/**
	 * Returns all teams by name
	 * @return teams by name as iterable
	 */
	public Iterable<String> teams() {
		return teamsST.keys();
	}

	/**
	 * Returns number of wins for given team
	 * @param team the team
	 * @return number of wins
	 */
	public int wins(String team) {
		if (!teamsST.contains(team))
			throw new java.lang.IllegalArgumentException();
		return wins[teamsST.get(team)];
	}

	/**
	 * Returns number of losses for given team
	 * @param team the team
	 * @return number of losses
	 */
	public int losses(String team) {
		if (!teamsST.contains(team))
			throw new java.lang.IllegalArgumentException();
		return losses[teamsST.get(team)];
	}

	/**
	 * Returns number of remaining games for given team
	 * @param team the team
	 * @return number of remaining games
	 */
	public int remaining(String team) {
		if (!teamsST.contains(team))
			throw new java.lang.IllegalArgumentException();
		return remain[teamsST.get(team)];
	}

	/**
	 * Returns number of scheduled games between team1 and team2
	 * @param team the team
	 * @return number of scheduled games
	 */
	public int against(String team1, String team2) {
		if (!teamsST.contains(team1) || !teamsST.contains(team2))
			throw new java.lang.IllegalArgumentException();
		return games[teamsST.get(team1)][teamsST.get(team2)];
	}

	// Creates graph for all teams except the team to be eliminated
	// Vertices: scheduled games; teams; source; target
	// Use FlowNetwork and FlowEdge classes from algs4
	private void buildFlowNetwork(String team) {
		eliminated = false;
		int V = n - 1 + (n - 1) * (n - 2) / 2 + 3; // all vertices: number of team nodes + number of game nodes
												   //+ source + target + team to be eliminated
		
		ST<Integer, int[]> gamesST = new ST<Integer, int[]>(); //symbol table <game vertex, array of 3 elements> -
		// array contains team1 index, team2 index and the remaining games between two
		// game vertices are assigned consecutive numbers >= n, since the first (n-1) are reserved for team nodes
		
		int teamIndex = teamsST.get(team); // index of team to be eliminated
		
		int vertex = n; // first game vertex
		int source = V - 2;
		int target = V - 1;
		
		//fill in gamesST to define game nodes
		for (int i = 0; i < n; i++) {
			if (i != teamIndex) {
				for (int j = i + 1; j < n; j++) {
					if (j != teamIndex && j != i) {
						int[] game = new int[] { i, j, games[i][j] }; 
						gamesST.put(vertex, game); // games between team i and team j
						vertex++;
					}
				}
			}
		}

		/*
		 * for (Integer i:gamesST.keys()) { System.out.println("Key/Vertex: " +
		 * i); System.out.println("Team1: " + gamesST.get(i)[0]);
		 * System.out.println("Team2: " + gamesST.get(i)[1]);
		 * System.out.println("Number of gamer between Team1 and Team2: "
		 * +gamesST.get(i)[2]); }
		 */
		
		// use FlowNetwork class with FlowEdges
		FlowNetwork net = new FlowNetwork(V);

		// add edges from source to game vertices and from game vertices to team vertices
		for (Integer v : gamesST.keys()) {
			FlowEdge edgeSourceGame = new FlowEdge(source, v, gamesST.get(v)[2]);
			net.addEdge(edgeSourceGame);
			FlowEdge edgeTeam1 = new FlowEdge(v, gamesST.get(v)[0],Double.POSITIVE_INFINITY);
			net.addEdge(edgeTeam1);
			FlowEdge edgeTeam2 = new FlowEdge(v, gamesST.get(v)[1],Double.POSITIVE_INFINITY);
			net.addEdge(edgeTeam2);
		}

		// add edges from team vertices to target
		for (String t : teamsST.keys()) {
			int teamID = teamsST.get(t);
			if (teamID != teamIndex) {
				int xCapacity = wins[teamIndex] + remain[teamIndex] - wins[teamID];
				FlowEdge edgeTeamTarget = new FlowEdge(teamID, target,xCapacity);
				net.addEdge(edgeTeamTarget);
			}
		}

		// System.out.println(net.toString());

		FordFulkerson maxflow = new FordFulkerson(net, source, target);
		// System.out.println("MaxFlow: " + maxflow.value());

		// check if all edges from source are full
		for (FlowEdge e : net.adj(source)) {
			if (e.flow() != e.capacity()) {
				eliminated = true;
				break;
			}
		}

		// find mincut
		if (eliminated) {
			Bag<String> R2 = new Bag<String>();
			for (String t : teamsST.keys()) {
				if (maxflow.inCut(teamsST.get(t))) {
					R2.add(t);
				}
			}
			R = R2; // certificate of elimination set
		}

	}

	// Trivial elimination - if the sum of team's wins + team's remaining games is less than wins of any team,
	// there's no way for the team to win
	private void trivialElimination(String team) {
		eliminated = false;
		Bag<String> R1 = new Bag<String>();
		for (String t : teams()) {
			if (!t.equals(team)) {
				if (wins(team) + remaining(team) < wins(t)) {
					R1.add(t);
					eliminated = true;
				}
			}
		}
		R = R1; // certificate of elimination set
	}

	/**
	 * Is given team eliminated?
	 * @param team the team to be eliminated
	 * @return {@code true} if eliminated, {@code false} otherwise
	 */
	public boolean isEliminated(String team) {
		if (!teamsST.contains(team))
			throw new java.lang.IllegalArgumentException();
		trivialElimination(team);  // check if trivially eliminated
		if (eliminated) {
			// System.out.println("Trivially eliminated : " + team);
			return true;
		}
		// if the team is not trivially eliminated then build the FlowNetwork to determine 
		buildFlowNetwork(team);
		return eliminated;
	}

	/**
	 * Returns subset R of teams that eliminates given team; null if not eliminated
	 * @param team the team to be eliminated
	 * @return the set of teams; null if no such set
	 */
	public Iterable<String> certificateOfElimination(String team) {
		if (!teamsST.contains(team))
			throw new java.lang.IllegalArgumentException();
		if (isEliminated(team))
			return R;
		else
			return null;

	}

	public static void main(String[] args) {

		// System.out.println("Is eliminated: " +
		// season.isEliminated("Montreal"));

		BaseballElimination division = new BaseballElimination(args[0]);
		for (String team : division.teams()) {
			if (division.isEliminated(team)) {
				StdOut.print(team + " is eliminated by the subset R = { ");
				for (String t : division.certificateOfElimination(team)) {
					StdOut.print(t + " ");
				}
				StdOut.println("}");
			} else {
				StdOut.println(team + " is not eliminated");
			}
		}
	}

}
