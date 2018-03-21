package dev.kkorolyov.simplefuncs.stream;

import java.util.StringJoiner;
import java.util.stream.Collector;

/**
 * Generates additional {@link Collector}s not found in the standard {@link java.util.stream.Collectors}.
 */
public final class Collectors {
	private Collectors() {}

	/**
	 * Returns a collector which concatenates elements into a string.
	 * If there were no elements to collect, the result is {@code ""}, instead of the standard {@code prefix + suffix}.
	 * @param delimiter delimiter between elements
	 * @param prefix prefix appended at the start of all elements
	 * @param suffix suffix appended at the end of all elements
	 * @return collector which concatenates elements into a string and defaults to {@code ""} if no non-empty elements concatenated
	 */
	public static Collector<CharSequence, ?, String> joiningDefaultEmpty(String delimiter, String prefix, String suffix) {
		return Collector.of(() -> new StringJoiner(delimiter, prefix, suffix),
				StringJoiner::add,
				StringJoiner::merge,
				joiner -> joiner.length() > (prefix.length() + suffix.length())
						? joiner.toString()
						: "");
	}
}
