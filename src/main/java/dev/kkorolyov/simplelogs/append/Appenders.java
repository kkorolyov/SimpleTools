package dev.kkorolyov.simplelogs.append;

/**
 * Provides preset appenders.
 */
public class Appenders {
	/**
	 * Returns a new appender which appends to {@code System.err}.
	 * @param threshold initial appender threshold
	 * @return new appender which appends to {@code System.err}
	 */
	public static Appender err(int threshold) {
		return new Appender(threshold) {
			@Override
			public void append(int level, String message) {
				if (level <= threshold) System.err.println(message);
			}
		};
	}

	/**
	 * Returns a new appender which appends to {@code System.out}.
	 * @param threshold initial appender threshold
	 * @return new appender which appends to {@code System.out}
	 */
	public static Appender out(int threshold) {
		return new Appender(threshold) {
			@Override
			public void append(int level, String message) {
				if (level <= threshold) System.out.println(message);
			}
		};
	}
}
