package dev.kkorolyov.simplefuncs.convert;

import java.util.Collection;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * Converts elements from {@code T} to {@code R}.
 * @param <T> input element type
 * @param <R> output element type
 */
@FunctionalInterface
public interface Converter<T, R> {
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
