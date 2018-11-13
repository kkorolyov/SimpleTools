package dev.kkorolyov.simplestructs;

import dev.kkorolyov.simplestructs.Graph.Node;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static dev.kkorolyov.simplefuncs.stream.Iterables.append;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableCollection;

/**
 * A collection of values connected by outbound and inbound edges to other values.
 * @param <T> value type
 */
public class Graph<T> implements Iterable<Node<T>> {
	private final Map<T, Node<T>> nodes = new HashMap<>();

	/**
	 * @param value value to check
	 * @return whether this graph contains {@code value}
	 */
	public boolean contains(T value) {
		return get(value) != null;
	}

	/**
	 * @param value value to get node for
	 * @return node with {@code value} in this graph, if any
	 */
	public Node<T> get(T value) {
		return nodes.get(value);
	}

	/** @see #add(Object, Iterable) */
	@SafeVarargs
	public final Graph<T> add(T value, T... outbounds) {
		return add(value, Arrays.asList(outbounds));
	}
	/**
	 * Adds or updates a value in this graph.
	 * @param value value to add or update
	 * @param outbounds values to add as outbound connections from {@code value}
	 * @return {@code this}
	 */
	public Graph<T> add(T value, Iterable<T> outbounds) {
		computeIfAbsent(value)
				.addEdges(computeIfAbsent(outbounds));

		return this;
	}

	/** @see #addUndirected(Object, Iterable) */
	public Graph<T> addUndirected(T value, T... connecteds) {
		return addUndirected(value, Arrays.asList(connecteds));
	}
	/**
	 * Like {@link #add(Object, Iterable)}, but also adds an inverse edge between connected node pairs.
	 */
	public Graph<T> addUndirected(T value, Iterable<T> connecteds) {
		computeIfAbsent(value)
				.addEdgesUndirected(computeIfAbsent(connecteds));

		return this;
	}

	/** @see #remove(Iterable) */
	@SafeVarargs
	public final Graph<T> remove(T value, T... values) {
		return remove(append(singleton(value), values));
	}
	/**
	 * Removes {@code values} from this graph.
	 * @param values values to remove
	 * @return {@code this}
	 */
	public Graph<T> remove(Iterable<T> values) {
		find(values)
				.forEach(Node::destroy);

		return this;
	}

	/** @see #sever(Object, Iterable) */
	@SafeVarargs
	public final Graph<T> sever(T value, T outbound, T... outbounds) {
		return sever(value, append(singleton(outbound), outbounds));
	}
	/**
	 * Removes outbound edges from a value in this graph.
	 * @param value value to remove outbound edges for
	 * @param outbounds connected values to remove outbound edges from {@code value} for
	 * @return {@code this}
	 */
	public Graph<T> sever(T value, Iterable<T> outbounds) {
		find(value)
				.ifPresent(node -> node.removeEdges(find(outbounds)));

		return this;
	}

	/** @see #severUndirected(Object, Iterable) */
	public Graph<T> severUndirected(T value, T connected, T... connecteds) {
		return severUndirected(value, append(singleton(connected), connecteds));
	}
	/**
	 * Like {@link #sever(Object, Iterable)}, but also removes the inverse edge between connected node pairs.
	 */
	public Graph<T> severUndirected(T value, Iterable<T> connecteds) {
		find(value)
				.ifPresent(node -> node.removeEdgesUndirected(find(connecteds)));

		return this;
	}

	private Node<T> computeIfAbsent(T value) {
		return nodes.computeIfAbsent(value, k -> new Node<>(k, this));
	}
	private Iterable<Node<T>> computeIfAbsent(Iterable<T> values) {
		return StreamSupport.stream(values.spliterator(), false)
				.map(this::computeIfAbsent)
				::iterator;
	}

	private Optional<Node<T>> find(T value) {
		return Optional.ofNullable(nodes.get(value));
	}
	private Iterable<Node<T>> find(Iterable<T> values) {
		return StreamSupport.stream(values.spliterator(), false)
				.map(nodes::get)
				.filter(Objects::nonNull)
				::iterator;
	}

	/** @return view over all nodes in this graph */
	public Collection<Node<T>> getNodes() {
		return unmodifiableCollection(nodes.values());
	}
	/** @return view over all values in this graph */
	public Collection<T> getValues() {
		return unmodifiableCollection(nodes.keySet());
	}

	/**
	 * Removes all values in this graph.
	 */
	public void clear() {
		nodes.clear();
	}

	/** @return iterator over all nodes in this graph */
	@Override
	public Iterator<Node<T>> iterator() {
		return nodes.values().iterator();
	}

	/**
	 * An individual vertex with outbound and inbound edges in a {@link Graph}.
	 * @param <T> node type
	 */
	public static final class Node<T> {
		private final T value;
		private final Collection<Node<T>> outbounds = new HashSet<>();
		private final Collection<Node<T>> inbounds = new HashSet<>();
		private final Graph<T> graph;

		private Node(T value, Graph<T> graph) {
			this.value = value;
			this.graph = graph;
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
		 * Adds 2-way edge pairs from this node to each node in {@code connecteds}.
		 * @param connecteds other nodes to connect to this node in both directions
		 */
		private void addEdgesUndirected(Iterable<Node<T>> connecteds) {
			for (Node<T> connected : connecteds) {
				outbounds.add(connected);
				inbounds.add(connected);
				connected.outbounds.add(this);
				connected.inbounds.add(this);
			}
		}
		/**
		 * Removes 2-way edge pairs from this node to each node in {@code connecteds}.
		 * @param connecteds other nodes to disconnect from this node in both directions
		 */
		private void removeEdgesUndirected(Iterable<Node<T>> connecteds) {
			for (Node<T> connected : connecteds) {
				outbounds.remove(connected);
				inbounds.remove(connected);
				connected.outbounds.remove(this);
				connected.inbounds.remove(this);
			}
		}

		/**
		 * Removes this node and all connections to it from the graph.
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

			graph.nodes.remove(value);
		}

		/** @return all nodes connected by an outbound edge from this node */
		public Collection<Node<T>> getOutbounds() {
			return unmodifiableCollection(outbounds);
		}
		/** @return all nodes connected by an inbound edge to this node */
		public Collection<Node<T>> getInbounds() {
			return unmodifiableCollection(inbounds);
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
