package dev.kkorolyov.simplefuncs.convert;

import java.util.Collection;
import java.util.Objects;

/**
 * Similar to {@link Converter}, but able to convert bi-directionally.
 * @param <T> input element type
 * @param <R> output element type
 */
public final class BiConverter<T, R> {
	private final Converter<? super T, ? extends R> outgoing;
	private final Converter<? super R, ? extends T> incoming;

	/**
	 * Constructs a new bi-converter.
	 * @param outgoing {@code T -> R} conversion handler
	 * @param incoming {@code R -> T} conversion handler
	 */
	public BiConverter(Converter<? super T, ? extends R> outgoing, Converter<? super R, ? extends T> incoming) {
		this.outgoing = outgoing;
		this.incoming = incoming;
	}

	/**
	 * Converts an element using the outgoing handler.
	 * @param in element to convert
	 * @return outgoing conversion of {@code in} to {@code R} type
	 */
	public R convertOut(T in) {
		return outgoing.convert(in);
	}
	/**
	 * Converts elements using the outgoing handler.
	 * @param in elements to convert
	 * @return outgoing conversions of all elements in {@code in} to {@code R} type, in input order
	 */
	public Collection<? extends R> convertOut(Iterable<? extends T> in) {
		return outgoing.convert(in);
	}

	/**
	 * Converts an element using the incoming handler.
	 * @param in element to convert
	 * @return incoming conversion of {@code in} to {@code T} type
	 */
	public T convertIn(R in) {
		return incoming.convert(in);
	}
	/**
	 * Converts elements using the incoming handler.
	 * @param in elements to convert
	 * @return incoming conversions of all elements in {@code in} to {@code T} type, in input order
	 */
	public Collection<? extends T> convertIn(Iterable<? extends R> in) {
		return incoming.convert(in);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BiConverter<?, ?> that = (BiConverter<?, ?>) o;
		return outgoing.equals(that.outgoing) &&
				incoming.equals(that.incoming);
	}
	@Override
	public int hashCode() {
		return Objects.hash(outgoing, incoming);
	}

	@Override
	public String toString() {
		return "BiConverter{" +
				"outgoing=" + outgoing +
				", incoming=" + incoming +
				'}';
	}
}
