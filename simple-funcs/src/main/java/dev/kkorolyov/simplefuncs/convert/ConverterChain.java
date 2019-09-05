package dev.kkorolyov.simplefuncs.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * A sequence of {@link Converter} calls.
 * Useful for generating a converter {@code A -> D} using converters {@code [A -> B, B -> C, C -> D]}
 * @param <T> input type
 * @param <R> output type
 */
public final class ConverterChain<T, R> implements Converter<T, R> {
	private final Collection<Converter> converters = new ArrayList<>();

	/**
	 * Generates a fresh converter chain from a source type.
	 * @param c source element type
	 * @param <T> input type
	 * @return new converter chain with source type {@code c}
	 */
	public static <T> ConverterChain<T, T> from(Class<T> c) {
		return new ConverterChain<>();
	}

	private ConverterChain() {}

	/**
	 * Adds a converter as the next link in this chain.
	 * @param delegate converter to add
	 * @param <R1> delegate output type
	 * @return {@code this}
	 */
	public <R1> ConverterChain<T, R1> add(Converter<? super R, ? extends R1> delegate) {
		converters.add(delegate);
		return (ConverterChain<T, R1>) this;
	}

	@Override
	public R convert(T in) {
		Object result = in;
		for (Converter converter : converters) {
			result = converter.convert(result);
		}
		return (R) result;
	}
	@Override
	public Collection<R> convert(Iterable<? extends T> in) {
		Collection results = StreamSupport.stream(in.spliterator(), false).collect(toList());
		for (Converter converter : converters) {
			results = converter.convert(results);
		}
		return (Collection<R>) results;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConverterChain<?, ?> that = (ConverterChain<?, ?>) o;
		return converters.equals(that.converters);
	}
	@Override
	public int hashCode() {
		return Objects.hash(converters);
	}

	@Override
	public String toString() {
		return "ConverterChain{" +
				"converters=" + converters +
				'}';
	}
}
