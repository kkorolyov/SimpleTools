package dev.kkorolyov.simplelogs;

/**
 * Defines log level constants.
 */
public final class Level {
	public static final int OFF = Integer.MIN_VALUE;
	public static final int SEVERE = 100;
	public static final int WARNING = 200;
	public static final int INFO = 300;
	public static final int DEBUG = 400;
	public static final int ALL = Integer.MAX_VALUE;

	private Level() {}

	public static String toString(int level) {
		switch (level) {
			case OFF:
				return "OFF";
			case SEVERE:
				return "SEVERE";
			case WARNING:
				return "WARNING";
			case INFO:
				return "INFO";
			case DEBUG:
				return "DEBUG";
			case ALL:
				return "ALL";
			default:
				return String.valueOf(level);
		}
	}
}
