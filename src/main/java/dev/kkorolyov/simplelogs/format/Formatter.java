package dev.kkorolyov.simplelogs.format;

import java.time.Instant;

import dev.kkorolyov.simplelogs.Logger.Level;

/**
 * Formats log messages.
 */
@FunctionalInterface
public interface Formatter {
	/**
	 * Formats a message.
	 * @param level message level
	 * @param instant instant at which message is logged
	 * @param message message
	 * @return formatted message
	 */
	String execute(Level level, Instant instant, String message);
}
