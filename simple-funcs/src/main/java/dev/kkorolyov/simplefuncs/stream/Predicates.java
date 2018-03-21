package dev.kkorolyov.simplefuncs.stream;

/**
 * Some additional test methods.
 */
public final class Predicates {
	private Predicates() {}

	/**
	 * @param s char sequence to test
	 * @return {@code true} if {@code s} is {@code null} or has length {@code 0}
	 */
	public static boolean isEmpty(CharSequence s) {
		return s == null || s.length() <= 0;
	}
	/**
	 * @param s char sequence to test
	 * @return {@code true} if {@code s} is neither {@code null} nor has length {@code 0}
	 */
	public static boolean nonEmpty(CharSequence s) {
		return !isEmpty(s);
	}
}
