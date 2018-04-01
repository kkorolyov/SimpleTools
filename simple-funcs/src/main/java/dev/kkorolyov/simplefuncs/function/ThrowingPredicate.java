package dev.kkorolyov.simplefuncs.function;

import java.util.function.Predicate;

/**
 * A {@link Predicate} which may throw a checked exception.
 * @param <T> argument type
 * @param <E> exception type
 */
public interface ThrowingPredicate<T, E extends Exception> extends Predicate<T> {
	/**
	 * Invokes the predicate.
	 * @param t argument
	 * @return whether {@code t} matches the predicate
	 * @throws E if an exception of type {@code E} occurs during invocation
	 */
	boolean testThrowing(T t) throws E;

	@Override
	default boolean test(T t) {
		try {
			return testThrowing(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
