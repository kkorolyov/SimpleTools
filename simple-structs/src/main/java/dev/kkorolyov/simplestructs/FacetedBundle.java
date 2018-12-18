package dev.kkorolyov.simplestructs;

import dev.kkorolyov.simplestructs.FacetedBundle.Entry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
public class FacetedBundle<K, F, T> implements Iterable<Entry<F, T>> {
	private final Indices<K> indices = new Indices<>();
	private final FacetSets<F> facetSets = new FacetSets<>();
	private final List<Entry<F, T>> elements = new ArrayList<>();

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
	public Entry<F, T> get(K key) {
		int index = indices.getIndex(key);

		return index < elements.size()
				? elements.get(index)
				: null;
	}

	/**
	 * @param facets facets to stream intersection for
	 * @return stream over the intersection of all elements with {@code facets} applied
	 */
	public Stream<T> stream(Iterable<F> facets) {
		BitSet intersection = new BitSet(elements.size());
		intersection.set(0, elements.size());

		for (F facet : facets) {
			intersection.and(facetSets.getFacetSet(facet));
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
	public Entry<F, T> put(K key, T element) {
		int index = indices.getIndex(key);

		padUntil(index);

		Entry<F, T> entry = elements.get(index);
		if (entry == null) {
			entry = new Entry<>(index, facetSets);
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
		int index = indices.remove(key);

		boolean result = index >= 0 && index < elements.size();
		if (result) {
			facetSets.clear(index);
			elements.set(index, null);
		}
		return result;
	}

	@Override
	public Iterator<Entry<F, T>> iterator() {
		return elements.iterator();
	}

	private static class Indices<K> {
		private final Map<K, Integer> indices = new HashMap<>();
		private final Queue<Integer> reclaimedIndices = new ArrayDeque<>();

		int getIndex(K key) {
			return indices.computeIfAbsent(key, k ->
					!reclaimedIndices.isEmpty()
							? reclaimedIndices.poll()
							: indices.size()
			);
		}

		int remove(K key) {
			Integer index = indices.remove(key);
			if (index != null) {
				reclaimedIndices.add(index);
			}
			return index != null ? index : -1;
		}
	}

	private static class FacetSets<F> {
		private final Map<F, BitSet> facetSets = new HashMap<>();

		BitSet getFacetSet(F key) {
			return facetSets.computeIfAbsent(key, k -> new BitSet());
		}

		void clear(int index) {
			for (BitSet facetSet : facetSets.values()) {
				facetSet.clear(index);
			}
		}
	}

	/**
	 * A wrapper around an element supporting setting of facets to mark element in specific ways.
	 * @param <F> facet type
	 * @param <T> element type
	 */
	public static final class Entry<F, T> {
		private final int index;
		private final FacetSets<F> facetSets;

		private T element;

		private Entry(int index, FacetSets<F> facetSets) {
			this.index = index;
			this.facetSets = facetSets;
		}

		/** @see #addFacets(Iterable) */
		@SafeVarargs
		public final Entry<F, T> addFacets(F facet, F... facets) {
			return addFacets(append(singleton(facet), facets));
		}
		/**
		 * Adds facets to this entry.
		 * @param facets facets to add
		 * @return {@code this}
		 */
		public Entry<F, T> addFacets(Iterable<F> facets) {
			for (F facet : facets) {
				facetSets.getFacetSet(facet).set(index);
			}
			return this;
		}

		/** @see #removeFacets(Iterable) */
		@SafeVarargs
		public final Entry<F, T> removeFacets(F facet, F... facets) {
			return removeFacets(append(singleton(facet), facets));
		}
		/**
		 * Removes facets from this entry.
		 * @param facets facets to remove
		 * @return {@code this}
		 */
		public Entry<F, T> removeFacets(Iterable<F> facets) {
			for (F facet : facets) {
				facetSets.getFacetSet(facet).clear(index);
			}
			return this;
		}

		/**
		 * Sets all facets on this entry to {@code facets}.
		 * @param facets facets to set
		 * @return {@code this}
		 */
		public Entry<F, T> setFacets(Iterable<F> facets) {
			facetSets.clear(index);
			return addFacets(facets);
		}

		/** @return wrapped element */
		public T getElement() {
			return element;
		}
		private Entry<F, T> setElement(T element) {
			this.element = element;
			return this;
		}
	}
}
