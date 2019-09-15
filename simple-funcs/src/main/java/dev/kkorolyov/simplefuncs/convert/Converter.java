package dev.kkorolyov.simplefuncs.convert;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static dev.kkorolyov.simplefuncs.stream.Iterables.append;
import static java.util.stream.Collectors.toList;

/**
 * Converts elements from {@code T} to {@code R}.
 * @param <T> input type
 * @param <R> output type
 */
@FunctionalInterface
public interface Converter<T, R> {
	/**
	 * Generates a converter which converts inputs matching a given test.
	 * @param test filter accepting {@code T}s to convert
	 * @param delegate converts accepted {@code T}s
	 * @param <T> input type
	 * @param <R> output type
	 * @return converter converting {@code T}s matching {@code test} using {@code delegate}
	 */
	static <T, R> Converter<T, Optional<R>> selective(Predicate<? super T> test, Converter<? super T, ? extends R> delegate) {
		return in -> Optional.of(in)
				.filter(test)
				.map(delegate::convert);
	}

	/** @see #reducing(Iterable) */
	@SafeVarargs
	static <T, R> Converter<T, ? extends Optional<? extends R>> reducing(Converter<? super T, Optional<R>> delegate, Converter<? super T, ? extends Optional<? extends R>>... delegates) {
		return reducing(append(delegate, delegates));
	}
	/**
	 * Generates a converter which converts inputs using the first matching selective delegate.
	 * @param delegates convert {@code T}s
	 * @param <T> input type
	 * @param <R> output type
	 * @return converter converting {@code T}s using the first non-empty-returning converter from {@code delegates}
	 */
	static <T, R> Converter<T, ? extends Optional<? extends R>> reducing(Iterable<? extends Converter<? super T, ? extends Optional<? extends R>>> delegates) {
		return in -> StreamSupport.stream(delegates.spliterator(), false)
				.map(converter -> converter.convert(in))
				.flatMap(Optional::stream)
				.findFirst();
	}

	/**
	 * Converts a {@code T} to an {@code R}.
	 * @param in input to convert
	 * @return conversion of {@code in} to {@code R} type
	 */
	R convert(T in);

	/**
	 * Converts multiple {@code T}s to {@code R}s.
	 * @param in inputs to convert
	 * @return conversions of all elements in {@code in} to {@code R} type, in input order
	 */
	default Collection<R> convert(Iterable<? extends T> in) {
		return StreamSupport.stream(in.spliterator(), false)
				.map(this::convert)
				.collect(toList());
	}
}
