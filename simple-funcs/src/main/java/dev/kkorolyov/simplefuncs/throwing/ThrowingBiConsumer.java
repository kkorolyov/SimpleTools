package dev.kkorolyov.simplefuncs.throwing;

import java.util.function.BiConsumer;

/**
 * A {@link BiConsumer} which may throw a checked exception.
 * @param <T> first argument type
 * @param <U> second argument type
 * @param <E> exception type
 */
public interface ThrowingBiConsumer<T, U, E extends Exception> extends BiConsumer<T, U>{
	/**
	 * Invokes the bi-consumer.
	 * @param t first argument
	 * @param u second argument
	 * @throws E if an exception of type {@code E} occurs during invocation
	 */
	void acceptThrowing(T t, U u) throws E;

	@Override
	default void accept(T t, U u) {
		try {
			acceptThrowing(t, u);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
