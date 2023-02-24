import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Library for graph analysis
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2016
 * @author Sajjad
 */
public class GraphLib {
	/**
	 * Takes a random walk from a vertex, up to a given number of steps
	 * So a 0-step path only includes start, while a 1-step path includes start and one of its out-neighbors,
	 * and a 2-step path includes start, an out-neighbor, and one of the out-neighbor's out-neighbors
	 * Stops earlier if no step can be taken (i.e., reach a vertex with no out-edge)
	 * @param g		graph to walk on
	 * @param start	initial vertex (assumed to be in graph)
	 * @param steps	max number of steps
	 * @return		a list of vertices starting with start, each with an edge to the sequentially next in the list;
	 * 			    null if start isn't in graph
	 */
	public static <V,E> List<V> randomWalk(Graph<V,E> g, V start, int steps) {
		ArrayList<V> path = new ArrayList<>();
		path.add(start);

		V current = start;
		ArrayList<V> neighbors = new ArrayList<>();

		while (steps > 0)
		{
			for (V neighbor : g.outNeighbors(current))
			{
				if (neighbor != current) neighbors.add(neighbor);

			}
			int idx = (int) ((Math.random() * (neighbors.size())));

			current = neighbors.get(idx);
			path.add(current);

			if (g.outDegree(current) == 0) return path;

			steps--;
			neighbors = new ArrayList<>();
		}

		return path;
	}
	
	/**
	 * Orders vertices in decreasing order by their in-degree
	 * @param g		graph
	 * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {

		ArrayList<V> vertices = new ArrayList<>();
		for (V vertex : g.vertices())
		{
			vertices.add(vertex);
		}

		vertices.sort((V v1, V v2) -> g.inDegree(v2) - g.inDegree(v1));
		return vertices;
	}

	/**
	 * BFS to find the shortest path tree for a current center of the universe
	 * @param g - a graph
	 * @param source - starting vertex
	 * @return - returns a path tree as a graph
	 * @param <V>
	 * @param <E>
	 */
	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source)
	{
		Queue<V> queue = new LinkedList<>(); //queue to keep track of BFS
		HashSet<V> visited = new HashSet<>(); //set to keep track of visited vertices
		HashMap<V, V> path = new HashMap<>(); //map to backtrack the path
		Graph<V,E> pathGraph = new AdjacencyMapGraph<>();

		queue.add(source); //enqueue the start vertex
		visited.add(source); //set it to visited
		path.put(source, null); //add the source to map -> value null

		while(!queue.isEmpty()) //while queue not empty
		{
			V u = queue.remove(); //dequeue a vertex

			for (V vertex : g.outNeighbors(u)) //for every adjacent vertex V to U
			{
				if (!visited.contains(vertex)) //if that neighbor vertex is not visited
				{

					queue.add(vertex); //enqueue the vertex
					visited.add(vertex); //set it to visited
					path.put(vertex, u ); //add to backtrack map with key as neighbor and value as current vertex
				}
			}
		}

		HashSet<V> vertexCheck = new HashSet<>(); //set to make sure that vertex is not yet created in path graph

		for (Map.Entry<V, V> entry : path.entrySet()) //loop through each item in the backtrack map
		{
			V key = entry.getKey(); //get the key
			V value = entry.getValue(); //get the value

			if (value == null) //if root vertex
			{
				pathGraph.insertVertex(key); //just insert it, but don't point anything
			} else {
				if (!vertexCheck.contains(key)) //if the key is not in set
					pathGraph.insertVertex(key); //then create a vertex of that key in path graph

				if (!vertexCheck.contains(value)) //if value is not in set
					pathGraph.insertVertex(value); //then create a vertex of that value in path graph

				pathGraph.insertDirected(key, value, g.getLabel(key, value)); //insert directed edge between the key and value
			}


		}


		return pathGraph; //return the new graph
	}

	/**
	 *
	 * @param tree - the shortest path tree graph returned by bfs
	 * @param v - a given vertex
	 * @return - a path from the vertex back to the center of the universe.
	 * @param <V>
	 * @param <E>
	 */
	public static <V,E> List<V> getPath(Graph<V,E> tree, V v)
	{
		V curr = v; //keep track of current vertex
		ArrayList<V> shortestPath = new ArrayList<>(); //path from vertex to center of universe

		while(tree.outDegree(curr) != 0) //while the curr isn't the center of universe
		{
			shortestPath.add(curr); //add the curr to the path
			for (V neighbor : tree.outNeighbors(curr)) //loop to the neighbor of the curr
				curr = neighbor; //set curr to the neighbor
		}
		shortestPath.add(curr);
		return shortestPath; //return the shortest path
	}

	/**
	 *
	 * @param graph - Given a graph
	 * @param subgraph - Given a subgraph (the shortest path tree)
	 * @return  - set of vertices that are in the graph but not the subgraph
	 * @param <V>
	 * @param <E>
	 */
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph)
	{

		HashSet<V> vertexSet = new HashSet<>(); //set of all vertices in subgraph
		HashSet<V> missingV = new HashSet<>(); //set that will contain the missing vertices
		for(V vertex : subgraph.vertices()) //for every vertex in subgraph
		{
			vertexSet.add(vertex); //add the vertex to a set
		}
		for (V vertex : graph.vertices()) //for every vertex in the original graph
		{
			if(!vertexSet.contains(vertex)) //if the vertex is not in the subgraph
			{
				missingV.add(vertex); //then add it to the missing vertices sett
			}
		}
		return missingV; //return missing vertices set
	}

	/**
	 *
	 * @param tree - Given shortest path tree
	 * @param root - Given root of tree
	 * @return - Return the average distance-from-root in the shortest path tree
	 * @param <V>
	 * @param <E>
	 */
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root)
	{
		if (tree.numVertices() == 1) return 0; //if only one vertex then return 0

		double totalSep = 0; //start with separation length of 0

		for (V v : tree.vertices()) //for every vertex in the tree
		{
			if (tree.inDegree(v) == 0) //if it is in degree of 0
			{
				totalSep += helper(tree, v, 0, root); //call the helper and add it to total separation
			}
		}

		return totalSep / (tree.numVertices()); //return total sep / num of vertices not including the root
	}

	/**
	 *
	 * @param tree - Given shortest path tree
	 * @param vert - current vertex in tree
	 * @param root - Given root of tree
	 * @param separation - Total separation between each vertex and root
	 * @return - total number of separation
	 * @param <V>
	 * @param <E>
	 */
	public static <V,E> int helper(Graph<V,E> tree, V vert, int separation, V root)
	{

		if (vert == root) return separation; //base case - if root then return the separation

		int totalSep = separation; //set the totalSep to what is passed

		for (V n: tree.outNeighbors(vert)) { //for every neighbor vertex
			totalSep += helper(tree, n, separation + 1, root); //recurse with it and add to total separation
		}

		return totalSep; //return total separation
	}


}
