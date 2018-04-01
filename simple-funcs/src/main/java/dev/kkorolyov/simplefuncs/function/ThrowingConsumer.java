package dev.kkorolyov.simplefuncs.function;

import java.util.function.Consumer;

/**
 * A {@link Consumer} which may throw a checked exception.
 * @param <T> argument type
 * @param <E> exception type
 */
public interface ThrowingConsumer<T, E extends Exception> extends Consumer<T> {
	/**
	 * Invokes the bi-consumer.
	 * @param t argument
	 * @throws E if an exception of type {@code E} occurs during invocation
	 */
	void acceptThrowing(T t) throws E;

	@Override
	default void accept(T t) {
		try {
			acceptThrowing(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
