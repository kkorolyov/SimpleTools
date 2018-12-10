package dev.kkorolyov.simplestructs;

import dev.kkorolyov.simplestructs.Graph.Node;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static dev.kkorolyov.simplefuncs.stream.Iterables.append;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * A collection of values connected by weighted outbound and inbound edges to other values.
 * @param <T> value type
 * @param <E> edge type
 */
public final class Graph<T, E> implements Iterable<Node<T, E>> {
	private final Map<T, Node<T, E>> nodes = new HashMap<>();

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
	public Node<T, E> get(T value) {
		return nodes.get(value);
	}

	/**
	 * @param value value to get outbound connected values for
	 * @return values connected to {@code value} by outbound edges
	 */
	public Collection<T> getOutbounds(T value) {
		return getEdgeValues(value, Node::getOutbounds);
	}
	/**
	 * @param value value to get inbound connected values for
	 * @return values connected to {@code value} by inbound edges
	 */
	public Collection<T> getInbounds(T value) {
		return getEdgeValues(value, Node::getInbounds);
	}
	private Collection<T> getEdgeValues(T value, Function<? super Node<T, E>, ? extends Collection<Node<T, E>>> edgesMapper) {
		Node<T, E> node = get(value);

		return node == null
				? emptySet()
				: edgesMapper.apply(node).stream()
				.map(Node::getValue)
				.collect(toSet());
	}

	/**
	 * @param value value to get outbound degree for
	 * @return number of outbound edges from {@code value}
	 */
	public int outDegree(T value) {
		Node<T, E> node = get(value);

		return node == null
				? 0
				: node.outDegree();
	}
	/**
	 * @param value value to get inbound degree for
	 * @return number of inbound edges to {@code value}
	 */
	public int inDegree(T value) {
		Node<T, E> node = get(value);

		return node == null
				? 0
				: node.inDegree();
	}

	/**
	 * @param value value to check for connectivity
	 * @return whether {@code value} is in this graph and connected to at least one other value
	 */
	public boolean isConnected(T value) {
		Node<T, E> node = get(value);

		return node != null && node.isConnected();
	}

	/** @see #add(Object, Map) */
	public Graph<T, E> add(T value, T outbound, E edge) {
		return add(value, singletonMap(outbound, edge));
	}
	/**
	 * Adds or updates a value in this graph.
	 * @param value value to add or update
	 * @param outbounds values and their respective edges to add as outbound connections from {@code value}
	 * @return {@code this}
	 */
	public Graph<T, E> add(T value, Map<? extends T, ? extends E> outbounds) {
		computeIfAbsent(value)
				.addEdges(outbounds.entrySet().stream()
						.collect(toMap(
								entry -> computeIfAbsent(entry.getKey()),
								Entry::getValue
						))
				);
		return this;
	}

	/** @see #add(Object, Iterable) */
	@SafeVarargs
	public final Graph<T, E> add(T value, T... outbounds) {
		return add(value, Arrays.asList(outbounds));
	}
	/**
	 * Adds or updates a value in this graph.
	 * @param value value to add or update
	 * @param outbounds values to add as outbound connections from {@code value}
	 * @return {@code this}
	 */
	public Graph<T, E> add(T value, Iterable<T> outbounds) {
		computeIfAbsent(value)
				.addEdges(computeIfAbsent(outbounds));

		return this;
	}

	/** @see #addUndirected(Object, Map) */
	public Graph<T, E> addUndirected(T value, T connected, E edge) {
		return addUndirected(value, singletonMap(connected, edge));
	}
	/**
	 * Like {@link #add(Object, Map)}, but also adds an inverse edge between connected node pairs.
	 */
	public Graph<T, E> addUndirected(T value, Map<? extends T, ? extends E> connecteds) {
		computeIfAbsent(value)
				.addEdgesUndirected(connecteds.entrySet().stream()
						.collect(toMap(
								entry -> computeIfAbsent(entry.getKey()),
								Entry::getValue
						))
				);
		return this;
	}

	/** @see #addUndirected(Object, Iterable) */
	@SafeVarargs
	public final Graph<T, E> addUndirected(T value, T connected, T... connecteds) {
		return addUndirected(value, append(singleton(connected), connecteds));
	}
	/**
	 * Like {@link #add(Object, Iterable)}, but also adds an inverse edge between connected node pairs.
	 */
	public Graph<T, E> addUndirected(T value, Iterable<T> connecteds) {
		computeIfAbsent(value)
				.addEdgesUndirected(computeIfAbsent(connecteds));

		return this;
	}

	/** @see #remove(Iterable) */
	@SafeVarargs
	public final Graph<T, E> remove(T value, T... values) {
		return remove(append(singleton(value), values));
	}
	/**
	 * Removes {@code values} from this graph.
	 * @param values values to remove
	 * @return {@code this}
	 */
	public Graph<T, E> remove(Iterable<T> values) {
		find(values)
				.forEach(Node::destroy);

		return this;
	}

	/** @see #sever(Object, Iterable) */
	@SafeVarargs
	public final Graph<T, E> sever(T value, T outbound, T... outbounds) {
		return sever(value, append(singleton(outbound), outbounds));
	}
	/**
	 * Removes outbound edges from a value in this graph.
	 * @param value value to remove outbound edges for
	 * @param outbounds connected values to remove outbound edges from {@code value} for
	 * @return {@code this}
	 */
	public Graph<T, E> sever(T value, Iterable<T> outbounds) {
		find(value)
				.ifPresent(node -> node.removeEdges(find(outbounds)));

		return this;
	}

	/** @see #severUndirected(Object, Iterable) */
	public Graph<T, E> severUndirected(T value, T connected, T... connecteds) {
		return severUndirected(value, append(singleton(connected), connecteds));
	}
	/**
	 * Like {@link #sever(Object, Iterable)}, but also removes the inverse edge between connected node pairs.
	 */
	public Graph<T, E> severUndirected(T value, Iterable<T> connecteds) {
		find(value)
				.ifPresent(node -> node.removeEdgesUndirected(find(connecteds)));

		return this;
	}

	private Node<T, E> computeIfAbsent(T value) {
		return nodes.computeIfAbsent(value, k -> new Node<>(k, this));
	}
	private Iterable<Node<T, E>> computeIfAbsent(Iterable<T> values) {
		return StreamSupport.stream(values.spliterator(), false)
				.map(this::computeIfAbsent)
				::iterator;
	}

	private Optional<Node<T, E>> find(T value) {
		return Optional.ofNullable(nodes.get(value));
	}
	private Iterable<Node<T, E>> find(Iterable<T> values) {
		return StreamSupport.stream(values.spliterator(), false)
				.map(nodes::get)
				.filter(Objects::nonNull)
				::iterator;
	}

	/** @return view over all nodes in this graph */
	public Collection<Node<T, E>> getNodes() {
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
	public Iterator<Node<T, E>> iterator() {
		return nodes.values().iterator();
	}

	/**
	 * An individual vertex with outbound and inbound edges in a {@link Graph}.
	 * @param <T> value type
	 * @param <E> edge type
	 */
	public static final class Node<T, E> {
		private final T value;
		private final Map<Node<T, E>, RelatedNode<T, E>> outbounds = new HashMap<>();
		private final Map<Node<T, E>, RelatedNode<T, E>> inbounds = new HashMap<>();
		private final Graph<T, E> graph;

		private Node(T value, Graph<T, E> graph) {
			this.value = value;
			this.graph = graph;
		}

		/**
		 * Adds outbound edges from this node to each node in {@code outbounds} and inbound edges from each node in {@code outbounds} to this node.
		 * @param outbounds outbound nodes with connection metadata to connect to this node
		 */
		private void addEdges(Map<Node<T, E>, ? extends E> outbounds) {
			outbounds.forEach((outbound, edge) -> {
				this.outbounds.put(outbound, new RelatedNode<>(outbound, edge));
				outbound.inbounds.put(this, new RelatedNode<>(this, edge));
			});
		}
		/**
		 * Adds outbound edges from this node to each node in {@code outbounds} and inbound edges from each node in {@code outbounds} to this node.
		 * @param outbounds outbound nodes to connect to this node with {@code null} connection metadata
		 */
		private void addEdges(Iterable<Node<T, E>> outbounds) {
			for (Node<T, E> outbound : outbounds) {
				this.outbounds.put(outbound, new RelatedNode<>(outbound, null));
				outbound.inbounds.put(this, new RelatedNode<>(this, null));
			}
		}

		/**
		 * Removes outbound edges from this node to each node in {@code outbounds} and inbound edges from each node in {@code outbounds} to this node.
		 * @param outbounds outbound nodes to disconnect from this node
		 */
		private void removeEdges(Iterable<Node<T, E>> outbounds) {
			for (Node<T, E> outbound : outbounds) {
				this.outbounds.remove(outbound);
				outbound.inbounds.remove(this);
			}
		}

		/**
		 * Adds 2-way edge pairs from this node to each node in {@code connecteds}.
		 * @param connecteds other nodes with connection metadata to connect to this node in both directions
		 */
		private void addEdgesUndirected(Map<Node<T, E>, ? extends E> connecteds) {
			connecteds.forEach((connected, edge) -> {
				RelatedNode<T, E> connectedRelated = new RelatedNode<>(connected, edge);
				RelatedNode<T, E> thisRelated = new RelatedNode<>(this, edge);

				outbounds.put(connected, connectedRelated);
				inbounds.put(connected, connectedRelated);
				connected.outbounds.put(this, thisRelated);
				connected.inbounds.put(this, thisRelated);
			});
		}
		/**
		 * Adds 2-way edge pairs from this node to each node in {@code connecteds}.
		 * @param connecteds other nodes to connect to this node with {@code null} connection metadata in both directions
		 */
		private void addEdgesUndirected(Iterable<Node<T, E>> connecteds) {
			for (Node<T, E> connected : connecteds) {
				RelatedNode<T, E> connectedRelated = new RelatedNode<>(connected, null);
				RelatedNode<T, E> thisRelated = new RelatedNode<>(this, null);

				outbounds.put(connected, connectedRelated);
				inbounds.put(connected, connectedRelated);
				connected.outbounds.put(this, thisRelated);
				connected.inbounds.put(this, thisRelated);
			}
		}

		/**
		 * Removes 2-way edge pairs from this node to each node in {@code connecteds}.
		 * @param connecteds other nodes to disconnect from this node in both directions
		 */
		private void removeEdgesUndirected(Iterable<Node<T, E>> connecteds) {
			for (Node<T, E> connected : connecteds) {
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
			for (Node<T, E> outbound : outbounds.keySet()) {
				outbound.inbounds.remove(this);
			}
			for (Node<T, E> inbound : inbounds.keySet()) {
				inbound.outbounds.remove(this);
			}
			outbounds.clear();
			inbounds.clear();

			graph.nodes.remove(value);
		}

		/** @return all nodes connected by an outbound edge from this node */
		public Collection<Node<T, E>> getOutbounds() {
			return unmodifiableCollection(outbounds.keySet());
		}
		/** @return all nodes connected by an outbound edge from this node, along with connection metadata */
		public Collection<RelatedNode<T, E>> getOutboundRelations() {
			return unmodifiableCollection(outbounds.values());
		}

		/** @return all nodes connected by an inbound edge to this node */
		public Collection<Node<T, E>> getInbounds() {
			return unmodifiableCollection(inbounds.keySet());
		}
		/** @return all nodes connected by an inbound edge to this node, along with connection metadata */
		public Collection<RelatedNode<T, E>> getInboundRelations() {
			return unmodifiableCollection(inbounds.values());
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

		/**
		 * A connected {@link Node} along with additional connection metadata.
		 * @param <T> value type
		 * @param <E> edge type
		 */
		public static final class RelatedNode<T, E> {
			private final Node<T, E> node;
			private final E edge;

			private RelatedNode(Node<T, E> node, E edge) {
				this.node = node;
				this.edge = edge;
			}

			/** @return connected node */
			public Node<T, E> getNode() {
				return node;
			}
			/** @return connection metadata */
			public E getEdge() {
				return edge;
			}
		}
	}
}
