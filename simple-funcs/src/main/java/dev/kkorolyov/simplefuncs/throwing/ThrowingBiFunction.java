package dev.kkorolyov.simplefuncs.throwing;

import java.util.function.BiFunction;

/**
 * A {@link BiFunction} which may throw a checked exception.
 * @param <T> first argument type
 * @param <U> second argument type
 * @param <R> return type
 * @param <E> exception type
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Exception> extends BiFunction<T, U, R> {
	/**
	 * Invokes the bi-function.
	 * @param t first argument
	 * @param u second argument
	 * @return result
	 * @throws E if an exception of type {@code E} occurs during invocation
	 */
	R applyThrowing(T t, U u) throws E;

	@Override
	default R apply(T t, U u) {
		try {
			return applyThrowing(t, u);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
