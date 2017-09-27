package dev.kkorolyov.simplelogs;

import dev.kkorolyov.simplelogs.append.Appender;
import dev.kkorolyov.simplelogs.format.Formatter;

/**
 * Used for testing {@link Logger} invoker resolving.
 */
public class Invoker {
	static Logger logger = Logger.getLogger();

	static void refreshLogger(int level, Formatter formatter, Appender... appenders) {
		logger = Logger.getLogger(level, formatter, appenders);
	}

	static void log(int level, String message, Object... args) {
		logger.log(level, message, args);
	}
}
