package dev.kkorolyov.simplefuncs.stream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import static java.util.Collections.singleton;

/**
 * Additional utility methods for generating or otherwise working with {@link Iterable}s.
 */
public final class Iterables {
	private Iterables() {}

	/**
	 * Checks whether two iterables are composed of the same elements in the same iteration order.
	 * @param iterable first iterable
	 * @param iterable1 second iterable
	 * @return whether {@code iterable} and {@code iterable1} are composed of the same elements in the same iteration order
	 */
	public static boolean matches(Iterable<?> iterable, Iterable<?> iterable1) {
		Iterator<?> it = iterable.iterator();
		Iterator<?> it1 = iterable1.iterator();

		while (it.hasNext() && it1.hasNext()) {
			if (!Objects.equals(it.next(), it1.next())) return false;
		}
		return !(it.hasNext() || it1.hasNext());
	}

	/** @see #concat(Iterable) */
	@SafeVarargs
	public static <T> Iterable<T> concat(Iterable<? extends T>... iterables) {
		return concat(Arrays.asList(iterables));
	}
	/**
	 * Returns an iterable which iterates over all elements of the given iterables in sequence.
	 * @param iterables iterables to iterate
	 * @param <T> iterated elements type
	 * @return iterable which iterates over all {@code iterables} in sequence
	 */
	public static <T> Iterable<T> concat(Iterable<Iterable<? extends T>> iterables) {
		return () -> new MultiIterator<>(iterables);
	}

	/**
	 * Convenience overload of {@link #append(Iterable, Object[])} for "at least one" var-args usages.
	 * @see #append(Iterable, Object[])
	 */
	@SafeVarargs
	public static <T> Iterable<T> append(T initial, T... others) {
		return append(singleton(initial), others);
	}
	/**
	 * Returns an iterable which iterates first over all elements in {@code iterable}, then over all {@code others}
	 * @param initial initial iterable to iterate
	 * @param others subsequent elements to iterate
	 * @param <T> iterated element type
	 * @return iterable which iterates over {@code iterable} elements, then over {@code others}
	 */
	@SafeVarargs
	public static <T> Iterable<T> append(Iterable<? extends T> initial, T... others) {
		return () -> new ExtraArrayIterator<>(initial.iterator(), others);
	}

	private static class MultiIterator<T> implements Iterator<T> {
		private final Iterator<Iterable<? extends T>> delegates;
		private Iterator<? extends T> current;

		MultiIterator(Iterable<Iterable<? extends T>> delegates) {
			this.delegates = delegates.iterator();
		}

		@Override
		public boolean hasNext() {
			return (current != null && current.hasNext())
					|| findNext();
		}
		@Override
		public T next() {
			if (current == null || !current.hasNext()) findNext();
			return current.next();
		}

		private boolean findNext() {
			while (current == null || !current.hasNext()) {
				if (delegates.hasNext()) {
					current = delegates.next().iterator();
				} else {
					return false;
				}
			}
			return true;
		}
	}

	private static class ExtraArrayIterator<T> implements Iterator<T> {
		private final Iterator<? extends T> delegate;
		private final T[] array;
		private int arrayIndex;

		ExtraArrayIterator(Iterator<? extends T> delegate, T[] array) {
			this.delegate = delegate;
			this.array = array;
		}

		@Override
		public boolean hasNext() {
			return delegate.hasNext() || arrayIndex < array.length;
		}
		@Override
		public T next() {
			return delegate.hasNext()
					? delegate.next()
					: array[arrayIndex++];
		}
	}
}
