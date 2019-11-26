package dev.kkorolyov.simplefuncs.function;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Memoizes results of function invocations.
 * @param <T> input type
 * @param <R> result type
 */
public final class Memoizer<T, R> {
	private static final Object NULL = new Object();

	private final Map<T, R> cache = new ConcurrentHashMap<>();

	/**
	 * @param base base supplier
	 * @param <T> result type
	 * @return memoized variant of {@code base}
	 */
	public static <T> Supplier<T> memoize(Supplier<? extends T> base) {
		Function<Object, T> transform = memoize(t -> base.get());
		return () -> transform.apply(NULL);
	}

	/**
	 * @param base base function
	 * @param <T> input type
	 * @param <R> result type
	 * @return memoized variant of {@code base}
	 */
	public static <T, R> Function<T, R> memoize(Function<? super T, ? extends R> base) {
		return new Memoizer<T, R>().memoizeFunction(base);
	}
	/**
	 * @param base base bi-function
	 * @param <T> first input arg type
	 * @param <U> second input arg type
	 * @param <R> result type
	 * @return memoized variant of {@code base}
	 */
	public static <T, U, R> BiFunction<T, U, R> memoize(BiFunction<? super T, ? super U, ? extends R> base) {
		Function<T, Function<U, R>> transform = memoize(
				t1 -> memoize(
						u1 -> base.apply(t1, u1)
				)
		);
		return (t, u) -> transform.apply(t).apply(u);
	}

	private Function<T, R> memoizeFunction(Function<? super T, ? extends R> function) {
		return in -> cache.computeIfAbsent(in, function);
	}
}
