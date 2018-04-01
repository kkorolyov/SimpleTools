package dev.kkorolyov.simplefuncs.function;

import java.util.function.BiPredicate;

/**
 * A {@link BiPredicate} which may throw a checked exception.
 * @param <T> first argument type
 * @param <U> second argument type
 * @param <E> exception type
 */
public interface ThrowingBiPredicate<T, U, E extends Exception> extends BiPredicate<T, U> {
	/**
	 * Invokes the bi-predicate.
	 * @param t first argument
	 * @param u second argument
	 * @return whether {@code t} and {@code u} match the predicate
	 * @throws E if an exception of type {@code E} occurs during invocation
	 */
	boolean testThrowing(T t, U u) throws E;

	@Override
	default boolean test(T t, U u) {
		try {
			return testThrowing(t, u);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
