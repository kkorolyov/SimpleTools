package dev.kkorolyov.simplelogs.format;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import dev.kkorolyov.simplelogs.Level;

/**
 * Provides preset formatters.
 */
public class Formatters {
	private static final Map<String, Formatter> formatters = new HashMap<>();

	/** @return formatter which formats messages as {@code {instant} {invoker} {level}: {message}} */
	public static Formatter simple() {
		return formatters.computeIfAbsent("simple", k -> (instant, invoker, level, message) -> {
			String sInstant = DateTimeFormatter.ofPattern("yyyy.mm.dd-HH:mm:ss:SSS").format(instant);
			String sInvokerClass = invoker.getClassName();
			String sInvokerMethod = invoker.getMethodName();

			return sInstant + " " + sInvokerClass + "#" + sInvokerMethod +
						 System.lineSeparator() +
						 Level.toString(level) + ": " + message;
		});
	}
}
