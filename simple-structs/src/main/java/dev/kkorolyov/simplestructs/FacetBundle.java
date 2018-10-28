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
 * An indexed collection that supports "facets" or markers on individual elements.
 * Provides for efficient retrieval of all elements that have a given subset of facets applied to them.
 * @param <F> facet type
 * @param <T> element type
 */
public class FacetBundle<F, T> implements Iterable<FacetBundle<F, T>.Entry> {
	private final List<Entry> elements = new ArrayList<>();
	private final Map<F, BitSet> facetSets = new HashMap<>();

	/**
	 * @param index index to test
	 * @return whether an element is set at {@code index}
	 */
	public boolean contains(int index) {
		return get(index) != null;
	}

	/**
	 * @param index index to get entry for
	 * @return entry at {@code index}, or {@code null} if no such entry
	 */
	public Entry get(int index) {
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
	 * Puts an element into this bundle at a given index.
	 * Replaces any existing element at that index.
	 * @param index index to set element at
	 * @param element element to set
	 * @return entry containing {@code element}
	 */
	public Entry put(int index, T element) {
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
	 * @param index index of element to remove
	 * @return whether an element existed at {@code index} and was removed
	 */
	public boolean remove(int index) {
		return index < elements.size() && elements.remove(index) != null;
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

	/**
	 * A {@link FacetBundle} which supports arbitrary key-based operations instead of index-based.
	 */
	public static class Mapped<K, F, T> implements Iterable<FacetBundle<F, T>.Entry> {
		private final FacetBundle<F, T> delegate = new FacetBundle<>();
		private final Map<K, Integer> indices = new HashMap<>();

		/**
		 * @param key key to test
		 * @return whether an element is set at {@code key}
		 */
		public boolean contains(K key) {
			return delegate.contains(getIndex(key));
		}

		/**
		 * @param key key to get entry for
		 * @return entry at {@code key}, or {@code null} if no such entry
		 */
		public FacetBundle<F, T>.Entry get(K key) {
			return delegate.get(getIndex(key));
		}

		/**
		 * @param facets facets to get intersection for
		 * @return stream over the intersection of all elements with {@code facets} applied
		 */
		public Stream<T> get(Iterable<F> facets) {
			return delegate.get(facets);
		}

		/**
		 * Puts an element into this bundle at a given key.
		 * Replaces any existing element at that key.
		 * @param key key to set element at
		 * @param element element to set
		 * @return entry containing {@code element}
		 */
		public FacetBundle<F, T>.Entry put(K key, T element) {
			return delegate.put(getIndex(key), element);
		}
		/**
		 * @param key key of element to remove
		 * @return whether an element existed at {@code key} and was removed
		 */
		public boolean remove(K key) {
			return delegate.remove(getIndex(key));
		}

		private int getIndex(K key) {
			return indices.computeIfAbsent(key, k -> indices.size());
		}

		@Override
		public Iterator<FacetBundle<F, T>.Entry> iterator() {
			return delegate.iterator();
		}
	}
}
