package dev.kkorolyov.simplestructs.procedure;

import dev.kkorolyov.simplestructs.Graph;
import dev.kkorolyov.simplestructs.Graph.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Factory of shortest path procedures on a graph.
 * Each procedure accepts {@code (start, end)} arguments and returns a list of nodes denoting the shorted path for {@code start} to {@code end}.
 */
public final class ShortestPath {
	private ShortestPath() {}

	/**
	 * Returns a shortest path procedure which:
	 * <pre>
	 * ignores edge weights
	 * uses breadth-first search to visit nodes
	 * has runtime O(V + E), space O(V) (V = number of nodes, E = number of edges)
	 * </pre>
	 * @param graph graph to get shortest path from
	 * @param <T> node type
	 * @return procedure which gets shortest path using breadth-first search
	 */
	public static <T> Procedure.Binary<T, T, List<T>> bfs(Graph<T> graph) {
		return new Procedure.Binary<>() {
			private final Queue<Node<T>> unseen = new ArrayDeque<>();  // Queue of nodes to visit
			private final Collection<Node<T>> visited = new HashSet<>();
			private final Map<T, T> previous = new HashMap<>();  // Previous value in shortest path from start

			@Override
			public List<T> execute(T start, T end) {
				Node<T> startNode = graph.get(start);
				Node<T> endNode = graph.get(end);

				if (startNode != null && endNode != null) unseen.add(startNode);
				outer:
				for (Node<T> node = unseen.remove(); !unseen.isEmpty(); node = unseen.remove()) {
					for (Node<T> outbound : node.getOutbounds()) {
						if (visited.add(outbound)) {
							unseen.add(outbound);
							previous.put(outbound.getValue(), node.getValue());

							if (outbound.equals(endNode)) break outer;
						}
					}
				}

				List<T> result = new ArrayList<>();
				for (T value = previous.containsKey(end) ? end : null; value != null; value = previous.get(value)) {
					result.add(value);
				}
				Collections.reverse(result);

				clear();

				return result;
			}

			private void clear() {
				unseen.clear();
				visited.clear();
				previous.clear();
			}
		};
	}
}
