package dev.kkorolyov.simplestructs.procedure;

import dev.kkorolyov.simplestructs.Graph;
import dev.kkorolyov.simplestructs.Graph.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.BinaryOperator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

/**
 * Factory of shortest path procedures on a graph.
 * Each procedure accepts {@code (start, end)} arguments and returns a list of nodes denoting the shortest path from {@code start} to {@code end}.
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
	public static <T> Procedure.Binary<T, T, List<T>> bfs(Graph<T, ?> graph) {
		return new Procedure.Binary<>() {
			private final Queue<Node<T, ?>> unseen = new ArrayDeque<>();  // Queue of nodes to visit
			private final Collection<Node<T, ?>> visited = new HashSet<>();
			private final Map<T, T> previous = new HashMap<>();  // Previous value in shortest path from start

			@Override
			public List<T> execute(T start, T end) {
				Node<T, ?> startNode = graph.get(start);
				Node<T, ?> endNode = graph.get(end);

				if (startNode != null && endNode != null) unseen.add(startNode);
				outer:
				for (Node<T, ?> node = unseen.poll(); node != null; node = unseen.poll()) {
					for (Node<T, ?> outbound : node.getOutbounds()) {
						if (visited.add(outbound)) {
							unseen.add(outbound);
							previous.put(outbound.getValue(), node.getValue());

							if (outbound.equals(endNode)) break outer;
						}
					}
				}

				List<T> result = backtrack(previous, end);

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

	/** {@link #dijkstra(Graph, BinaryOperator)} with a convenience adder for numerical edges */
	public static <T, E extends Number & Comparable<E>> Procedure.Binary<T, T, List<T>> dijkstra(Graph<T, E> graph) {
		return dijkstra(
				graph,
				(num, num1) -> {
					if (num instanceof Double) {
						return (E) (Double) (num.doubleValue() + num1.doubleValue());
					} else if (num instanceof Float) {
						return (E) (Float) (num.floatValue() + num1.floatValue());
					} else if (num instanceof Long) {
						return (E) (Long) (num.longValue() + num1.longValue());
					} else {
						return (E) (Integer) (num.intValue() + num.intValue());
					}
				}
		);
	}
	/**
	 * Returns a shortest path procedure which:
	 * <pre>
	 * respects edge weights
	 * uses Dijkstra's algorithm to find a path
	 * has runtime O(E log V), space O(V) (V = number of nodes, E = number of edges)
	 * </pre>
	 * @param graph graph to get shortest path from
	 * @param adder adds 2 edges together
	 * @param <T> node type
	 * @param <E> edge type
	 * @return procedure which gets shortest path using Dijkstra's algorithm
	 */
	public static <T, E extends Comparable<E>> Procedure.Binary<T, T, List<T>> dijkstra(Graph<T, E> graph, BinaryOperator<E> adder) {
		return new Procedure.Binary<T, T, List<T>>() {
			private final Comparator<E> edgeComparator = nullsLast(naturalOrder());

			private final Map<Node<T, E>, E> cost = new HashMap<>();  // Nodes in graph mapped to their costs from start node
			private final PriorityQueue<Node<T, E>> unseen = new PriorityQueue<>(comparing(cost::get, edgeComparator));  // Prioritized queue of nodes to visit
			private final Map<T, T> previous = new HashMap<>();  // Previous value in shortest path from start

			@Override
			public List<T> execute(T start, T end) {
				Node<T, E> startNode = graph.get(start);
				Node<T, E> endNode = graph.get(end);

				if (startNode != null && endNode != null) unseen.add(startNode);
				for (Node<T, E> node = unseen.poll(); node != null; node = unseen.poll()) {
					if (node.equals(endNode)) break;

					for (Node.RelatedNode<T, E> relatedOutbound : node.getOutboundRelations()) {
						E incomingCost = cost.get(node);
						E newCost = (incomingCost == null || relatedOutbound.getEdge() == null) ? relatedOutbound.getEdge() : adder.apply(incomingCost, relatedOutbound.getEdge());

						E oldCost = cost.get(relatedOutbound.getNode());
						if ((oldCost == null && newCost == null) || edgeComparator.compare(newCost, oldCost) < 0) {
							previous.put(relatedOutbound.getNode().getValue(), node.getValue());

							unseen.remove(relatedOutbound.getNode());
							cost.put(relatedOutbound.getNode(), newCost);
							unseen.add(relatedOutbound.getNode());
						}
					}
				}

				List<T> result = backtrack(previous, end);

				clear();

				return result;
			}

			private void clear() {
				cost.clear();
				unseen.clear();
				previous.clear();
			}
		};
	}

	private static <T> List<T> backtrack(Map<T, T> previous, T end) {
		List<T> result = new ArrayList<>();
		for (T value = previous.containsKey(end) ? end : null; value != null; value = previous.get(value)) {
			result.add(value);
		}
		Collections.reverse(result);

		return result;
	}
}
