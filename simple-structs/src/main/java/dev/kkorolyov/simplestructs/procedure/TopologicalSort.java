package dev.kkorolyov.simplestructs.procedure;

import dev.kkorolyov.simplestructs.Graph;
import dev.kkorolyov.simplestructs.Graph.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;

/**
 * Factory of topological sorting procedures on a graph.
 * Each procedure returns some topological sorting of a graph as a list of nodes.
 */
public final class TopologicalSort {
	private TopologicalSort() {}

	/**
	 * Returns a topological sorting procedure which:
	 * <pre>
	 * uses depth-first search to visit nodes
	 * throws {@link IllegalStateException} when executed on a cyclic graph
	 * has runtime O(V + E), space O(V) (V = number of nodes, E = number of edges)
	 * </pre>
	 * @param graph graph to get topological sort for
	 * @param <T> node type
	 * @return procedure which sorts topologically using depth-first search
	 */
	public static <T> Procedure<List<T>> dfs(Graph<T> graph) {
		return new Procedure<>() {
			private final Collection<Node<T>> unseen = new HashSet<>();  // Nodes to visit
			private final Collection<Node<T>> visited = new HashSet<>();  // Nodes seen across all visits
			private final Deque<T> sort = new ArrayDeque<>();

			/**
			 * @return topologically-sorted list of nodes
			 * @throws IllegalStateException if the associated graph is not a directed acyclic graph
			 */
			@Override
			public List<T> execute() {
				unseen.addAll(graph.getNodes());
				while (!unseen.isEmpty()) {
					visit(unseen.iterator().next());
				}
				List<T> result = new ArrayList<>(sort);

				clear();

				return result;
			}
			private void visit(Node<T> node) {
				if (!visited.contains(node)) {
					if (!unseen.contains(node)) throw new IllegalStateException(graph + " is not a directed acyclic graph");

					unseen.remove(node);

					for (Node<T> outbound : node.getOutbounds()) visit(outbound);

					visited.add(node);

					sort.push(node.getValue());
				}
			}

			private void clear() {
				unseen.clear();
				visited.clear();
				sort.clear();
			}
		};
	}
}
