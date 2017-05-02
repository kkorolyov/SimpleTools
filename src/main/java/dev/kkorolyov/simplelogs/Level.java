package dev.kkorolyov.simplelogs;

/**
 * Defines log level constants.
 */
public final class Level {
	public static final int OFF = 0;
	public static final int SEVERE = 100;
	public static final int WARNING = 200;
	public static final int INFO = 300;
	public static final int DEBUG = 400;

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
			default:
				return String.valueOf(level);
		}
	}
}
