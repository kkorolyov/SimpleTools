package dev.kkorolyov.simplestructs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static dev.kkorolyov.simplefuncs.stream.Iterables.append;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableList;

/**
 * A tree with nodes connected to other nodes by a given iterable element.
 * A "terminal" node represents a complete value.
 * @param <T> element type
 */
public class Trie<T> {
	private final Node root = new Node();

	/** @see #contains(Iterable) */
	public boolean contains(T element, T... elements) {
		return contains(append(singleton(element), elements));
	}
	/** @see #contains(Iterable) */
	public boolean contains(T[] value) {
		return contains(Arrays.asList(value));
	}
	/**
	 * @param value value to check
	 * @return whether this trie contains {@code value} at a terminal node
	 */
	public boolean contains(Iterable<T> value) {
		Node node = get(value);
		return node != null && node.isTerminal();
	}

	/** @see #get(Iterable) */
	public Node get(T element, T... elements) {
		return get(append(singleton(element), elements));
	}
	/** @see #get(Iterable) */
	public Node get(T[] value) {
		return get(Arrays.asList(value));
	}
	/**
	 * @param value value to get node for
	 * @return node located by traversing the elements of {@code value} in this trie, or {@code null} if no such node
	 */
	public Node get(Iterable<T> value) {
		Iterator<T> it = value.iterator();

		if (!it.hasNext()) return null;

		Node node = root;
		while (it.hasNext() && (node = node.get(it.next())) != null) ;
		return node;
	}

	/** @see #add(Iterable) */
	public Trie<T> add(T element, T... elements) {
		return add(append(singleton(element), elements));
	}
	/** @see #add(Iterable) */
	public Trie<T> add(T[] value) {
		return add(Arrays.asList(value));
	}
	/**
	 * Adds a complete value to this trie.
	 * @param value value to add
	 * @return {@code this}
	 */
	public Trie<T> add(Iterable<T> value) {
		List<T> builder = new ArrayList<>();

		Iterator<T> it = value.iterator();
		Node node = root;
		while (it.hasNext()) {
			T element = it.next();
			builder.add(element);

			node = node.computeIfAbsent(element, builder);
		}
		if (!node.getValue().isEmpty()) node.terminal = true;

		return this;
	}

	/**
	 * An individual node in a {@link Trie}.
	 * A node which {@link #isTerminal()} represents a complete value in the associated trie.
	 */
	public final class Node {
		private final List<T> value;
		private final Map<T, Node> children = new HashMap<>();
		private boolean terminal;

		private Node() {
			this(emptyList());
		}
		private Node(List<T> value) {
			this.value = unmodifiableList(new ArrayList<>(value));
		}

		/**
		 * @param element element to check
		 * @return whether this node contains a child node at {@code element}
		 */
		public boolean contains(T element) {
			return get(element) != null;
		}

		/**
		 * @param element element to get child node for
		 * @return child node at {@code element}, or {@code null} if no such node
		 */
		public Node get(T element) {
			return children.get(element);
		}
		private Node computeIfAbsent(T element, List<T> value) {
			return children.computeIfAbsent(element, k -> new Node(value));
		}

		/** @return keys of child nodes */
		public Collection<T> getKeys() {
			return children.keySet();
		}

		/** @return full node value */
		public List<T> getValue() {
			return value;
		}

		/** @return whether this node represents a complete value */
		public boolean isTerminal() {
			return terminal;
		}
	}
}
