package dev.kkorolyov.simplelogs.format;

import java.time.Instant;

/**
 * Formats log messages.
 */
@FunctionalInterface
public interface Formatter {
	/**
	 * Formats a message.
	 * @param instant instant at which message is logged
	 * @param invoker element invoking logger
	 * @param level message level
	 * @param message logged message
	 * @return formatted message
	 */
	String format(Instant instant, StackTraceElement invoker, int level, String message);
}
