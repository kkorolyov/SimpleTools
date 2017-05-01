package dev.kkorolyov.simplelogs.format;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import dev.kkorolyov.simplelogs.Logger.Level;

/**
 * Formats a message into a simple, readable representation.
 */
public class SimpleFormatter implements Formatter {
	@Override
	public String execute(Level level, Instant instant, String message) {
		StringBuilder builder = new StringBuilder();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.mm.dd-HH:mm:ss:SSS");

		String sInstant = formatter.format(instant);

		return builder.toString();
	}
}
