package dev.kkorolyov.simplefuncs.throwing;

import java.util.function.Function;

/**
 * A {@link Function} which may throw a checked exception.
 * @param <T> argument type
 * @param <R> result type
 * @param <E> exception type
 */
public interface ThrowingFunction<T, R, E extends Exception> extends Function<T, R> {
	/**
	 * Invokes the function
	 * @param t argument
	 * @return result
	 * @throws E if an exception of type {@code E} occurs during invocation
	 */
	R applyThrowing(T t) throws E;

	@Override
	default R apply(T t) {
		try {
			return applyThrowing(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
