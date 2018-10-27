package dev.kkorolyov.simplestructs.procedure;

import dev.kkorolyov.simplestructs.Graph;
import dev.kkorolyov.simplestructs.Graph.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A topological sorting of a {@link Graph} using depth-first search.
 * Each execution runs on the current state of the associated graph.
 * @param <T> node type
 */
public class TopologicalDFS<T> {
	private final Graph<T> graph;

	private final Set<Node<T>> unseen = new HashSet<>();	// Nodes to visit
	private final Set<Node<T>> visited = new HashSet<>();	// Nodes seen across all visits
	private final Deque<T> sort = new ArrayDeque<>();

	/**
	 * Constructs a new topological DFS sort.
	 * @param graph graph to sort
	 */
	public TopologicalDFS(Graph<T> graph) {
		this.graph = graph;
	}

	/**
	 * Executes this topological sort.
	 * @return topologically-sorted list of all nodes in the current state of associated graph
	 * @throws IllegalStateException if the associated graph is not a directed acyclic graph
	 */
	public List<T> execute() {
		unseen.addAll(graph.getNodes());
		while (!unseen.isEmpty()) {
			visit(unseen.iterator().next());
		}
		List<T> result = new ArrayList<>(sort);

		unseen.clear();
		visited.clear();
		sort.clear();

		return result;
	}
	private void visit(Node<T> node) {
		if (visited.contains(node)) return;
		else if (!unseen.contains(node)) throw new IllegalStateException(graph + " is not a directed acyclic graph");

		unseen.remove(node);

		for (Node<T> outbound : node.getOutbounds()) visit(outbound);

		visited.add(node);

		sort.push(node.getValue());
	}
}
