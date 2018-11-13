package dev.kkorolyov.simplestructs;

import dev.kkorolyov.simplefuncs.stream.Iterables;
import dev.kkorolyov.simplestructs.Graph.Node;
import dev.kkorolyov.simplestructs.procedure.TopologicalSort;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * A collection of nodes connected by outbound and inbound edges to other nodes.
 * @param <T> node type
 */
public class Graph<T> implements Iterable<Node<T>> {
	private final Map<T, Node<T>> nodes = new HashMap<>();

	/**
	 * @return topologically-sorted list of all nodes in this graph
	 * @throws IllegalStateException if this graph is not a directed acyclic graph
	 * @deprecated prefer directly invoking a {@link TopologicalSort} procedure
	 */
	@Deprecated
	public List<T> sortTopological() {
		return TopologicalSort.dfs(this).execute();
	}

	/**
	 * @param node node to check
	 * @return whether this graph contains {@code node}
	 */
	public boolean contains(T node) {
		return nodes.keySet().contains(node);
	}

	/** @see #add(Object, Iterable) */
	@SafeVarargs
	public final Graph<T> add(T node, T... outbounds) {
		return add(node, Arrays.asList(outbounds));
	}
	/**
	 * Adds or updates a node in this graph.
	 * @param node node to add or update
	 * @param outbounds nodes to add as outbound connections from {@code node}
	 * @return {@code this}
	 */
	public Graph<T> add(T node, Iterable<T> outbounds) {
		getNode(node).addEdges(getNodes(outbounds));

		return this;
	}

	/** @see #remove(Object, Iterable) */
	@SafeVarargs
	public final Graph<T> remove(T node, T... outbounds) {
		return remove(node, Arrays.asList(outbounds));
	}
	/**
	 * Removes outbound edges from a node in this graph.
	 * @param node node to update
	 * @param outbounds nodes to remove outbound connections from {@code node} for
	 * @return {@code this}
	 */
	public Graph<T> remove(T node, Iterable<T> outbounds) {
		getNode(node).removeEdges(getNodes(outbounds));

		for (T n : Iterables.append(outbounds, node)) {
			if (!getNode(n).isConnected()) nodes.remove(n);
		}
		return this;
	}

	private Node<T> getNode(T value) {
		return nodes.computeIfAbsent(value, Node::new);
	}
	private Iterable<Node<T>> getNodes(Iterable<T> values) {
		return StreamSupport.stream(values.spliterator(), false)
				.map(this::getNode)::iterator;
	}

	/** @return all connected nodes in this graph */
	public Collection<Node<T>> getNodes() {
		return nodes.values();
	}
	/** @return values of all nodes in this graph */
	public Collection<T> getNodeValues() {
		return nodes.keySet();
	}

	@Override
	public Iterator<Node<T>> iterator() {
		return nodes.values().iterator();
	}

	/**
	 * An individual vertex with outbound and inbound edges in a {@link Graph}.
	 * @param <T> node type
	 */
	public static class Node<T> {
		private final T value;
		private final Collection<Node<T>> outbounds = new HashSet<>();
		private final Collection<Node<T>> inbounds = new HashSet<>();

		Node(T value) {
			this.value = value;
		}

		/**
		 * Adds outbound edges from this node to each node in {@code outbounds} and inbound edges from each node in {@code outbounds} to this node.
		 * @param outbounds outbound nodes to connect to this node
		 */
		private void addEdges(Iterable<Node<T>> outbounds) {
			for (Node<T> outbound : outbounds) {
				this.outbounds.add(outbound);
				outbound.inbounds.add(this);
			}
		}
		/**
		 * Removes outbound edges from this node to each node in {@code outbounds} and inbound edges from each node in {@code outbounds} to this node.
		 * @param outbounds outbound nodes to disconnect from this node
		 */
		private void removeEdges(Iterable<Node<T>> outbounds) {
			for (Node<T> outbound : outbounds) {
				this.outbounds.remove(outbound);
				outbound.inbounds.remove(this);
			}
		}

		/**
		 * Removes all outbound and inbound edges between this node and connected nodes.
		 */
		private void destroy() {
			for (Node<T> outbound : outbounds) {
				outbound.inbounds.remove(this);
			}
			for (Node<T> inbound : inbounds) {
				inbound.outbounds.remove(this);
			}
			outbounds.clear();
			inbounds.clear();
		}

		/** @return all nodes connected by an outbound edge from this node */
		public Collection<Node<T>> getOutbounds() {
			return outbounds;
		}
		/** @return all nodes connected by an inbound edge to this node */
		public Collection<Node<T>> getInbounds() {
			return inbounds;
		}

		/** @return number of outbound edges from this node */
		public int outDegree() {
			return outbounds.size();
		}
		/** @return number of inbound edges to this node */
		public int inDegree() {
			return inbounds.size();
		}

		/** @return whether this node has an outbound or inbound edge to at least 1 other node */
		public boolean isConnected() {
			return outDegree() > 0 || inDegree() > 0;
		}

		/** @return node value */
		public T getValue() {
			return value;
		}
	}
}
