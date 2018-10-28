package dev.kkorolyov.simplestructs;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static dev.kkorolyov.simplefuncs.stream.Iterables.append;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

/**
 * A collection that supports "facets" or markers on individual elements.
 * Provides for efficient retrieval of all elements that have a given subset of facets applied to them.
 * @param <K> key type
 * @param <F> facet type
 * @param <T> element type
 */
public class FacetBundle<K, F, T> implements Iterable<FacetBundle<K, F, T>.Entry> {
	private final List<Entry> elements = new ArrayList<>();
	private final Map<F, BitSet> facetSets = new HashMap<>();
	private final Map<K, Integer> indices = new HashMap<>();

	/**
	 * @param key key to test
	 * @return whether an element is set at {@code key}
	 */
	public boolean contains(K key) {
		return get(key) != null;
	}

	/**
	 * @param key key to get entry for
	 * @return entry at {@code key}, or {@code null} if no such entry
	 */
	public Entry get(K key) {
		int index = getIndex(key);

		return index < elements.size()
				? elements.get(index)
				: null;
	}

	/**
	 * @param facets facets to get intersection for
	 * @return stream over the intersection of all elements with {@code facets} applied
	 */
	public Stream<T> get(Iterable<F> facets) {
		BitSet intersection = new BitSet(elements.size());
		intersection.set(0, elements.size());

		for (F facet : facets) {
			intersection.and(getFacetSet(facet));
		}

		return intersection.stream()
				.mapToObj(elements::get)
				.map(Entry::getElement);
	}

	/**
	 * Puts an element into this bundle at a given key.
	 * Replaces any existing element at that key.
	 * @param key key to set element at
	 * @param element element to set
	 * @return entry containing {@code element}
	 */
	public Entry put(K key, T element) {
		int index = getIndex(key);

		padUntil(index);

		Entry entry = elements.get(index);
		if (entry == null) {
			entry = new Entry(index);
			elements.set(index, entry);
		}
		return entry
				.setElement(element)
				.setFacets(emptySet());
	}
	private void padUntil(int index) {
		while (elements.size() <= index) elements.add(null);
	}

	/**
	 * @param key key of element to remove
	 * @return whether an element existed at {@code key} and was removed
	 */
	public boolean remove(K key) {
		int index = getIndex(key);

		return index < elements.size() && elements.remove(index) != null;
	}

	private int getIndex(K key) {
		return indices.computeIfAbsent(key, k -> indices.size());
	}
	private BitSet getFacetSet(F key) {
		return facetSets.computeIfAbsent(key, k -> new BitSet());
	}

	@Override
	public Iterator<Entry> iterator() {
		return elements.iterator();
	}

	public class Entry {
		private final int index;
		private T element;

		private Entry(int index) {
			this.index = index;
		}

		/** @see #addFacets(Iterable) */
		public Entry addFacets(F facet, F... facets) {
			return addFacets(append(singleton(facet), facets));
		}
		/**
		 * Adds facets to this entry.
		 * @param facets facets to add
		 * @return {@code this}
		 */
		public Entry addFacets(Iterable<F> facets) {
			for (F facet : facets) {
				getFacetSet(facet).set(index);
			}
			return this;
		}

		/** @see #removeFacets(Iterable) */
		public Entry removeFacets(F facet, F... facets) {
			return removeFacets(append(singleton(facet), facets));
		}
		/**
		 * Removes facets from this entry.
		 * @param facets facets to remove
		 * @return {@code this}
		 */
		public Entry removeFacets(Iterable<F> facets) {
			for (F facet : facets) {
				getFacetSet(facet).clear(index);
			}
			return this;
		}

		/**
		 * Sets all facets on this entry to {@code facets}.
		 * @param facets facets to set
		 * @return {@code this}
		 */
		public Entry setFacets(Iterable<F> facets) {
			for (BitSet facetSet : FacetBundle.this.facetSets.values()) {
				facetSet.clear(index);
			}
			return addFacets(facets);
		}

		/** @return wrapped element */
		public T getElement() {
			return element;
		}
		private Entry setElement(T element) {
			this.element = element;
			return this;
		}
	}
}
