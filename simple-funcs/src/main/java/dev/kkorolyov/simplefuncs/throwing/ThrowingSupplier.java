package dev.kkorolyov.simplefuncs.throwing;

import java.util.function.Supplier;

/**
 * A {@link Supplier} which may throw a checked exception.
 * @param <T> supplied type
 * @param <E> exception type
 */
public interface ThrowingSupplier<T, E extends Exception> extends Supplier<T> {
	/**
	 * Invokes the supplier.
	 * @return supplied result
	 * @throws E if an exception of type {@code E} occurs during invocation
	 */
	T getThrowing() throws E;

	@Override
	default T get() {
		try {
			return getThrowing();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
