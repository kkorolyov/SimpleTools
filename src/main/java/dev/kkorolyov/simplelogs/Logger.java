package dev.kkorolyov.simplelogs;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.kkorolyov.simplelogs.append.Appender;
import dev.kkorolyov.simplelogs.append.Appenders;
import dev.kkorolyov.simplelogs.format.Formatter;
import dev.kkorolyov.simplelogs.format.Formatters;

/**
 * Simple logging interface for multiple levels.
 */
public class Logger {
	private static final Map<String, Logger> instances = new HashMap<>();

	private int level;
	private Formatter formatter;
	private Set<Appender> appenders = new HashSet<>();

	private Set<Logger> parents = new HashSet<>();

	/**
	 * Applies logging properties defined in a file.
	 * Properties should be defined in the format:
	 * <p>{@code LOGGER=LEVEL, WRITERS...}</p>
	 * <ul>
	 * <li>{@code LOGGER} - name of a logger</li>
	 * <li>{@code LEVEL} - the logger's logging level</li>
	 * <li>{@code WRITERS} - list of comma-delimited files or streams the logger logs to</li>
	 * </ul>
	 * <p>Valid output streams:</p>
	 * <ul>
	 * <li>OUT - {@code System.out}</li>
	 * <li>ERR - {@code System.err}</li>
	 * </ul>
	 * @param logProps path to logging properties file
	 * @return {@code true} if property application successful
	 */
	public static boolean applyProps(Path logProps) {
		try {
			Class.forName("dev.kkorolyov.simplelogs.PropsApplier").getDeclaredMethod("apply", Path.class).invoke(null, logProps);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Retrieves the logger associated with the fully-qualified name of the class calling this method.
	 * If such a logger does not exist, a new logger associated with {@code name} is created with level {@value Level#INFO}, the simple formatter, and an appender to {@code System.err}.
	 * @return logger associated with the fully-qualified name of the class calling this method
	 */
	public static Logger getLogger() {
		return getLogger(findInvoker().getClassName());
	}

	/**
	 * Retrieves the logger associated with {@code name}.
	 * If such a logger does not exist, a new logger associated with {@code name} is created with level {@value Level#INFO}, the simple formatter, and an appender to {@code System.err}.
	 * @param name logger name
	 * @return logger associated with {@code name}
	 */
	public static Logger getLogger(String name) {
		Logger instance = instances.get(name);

		return instance != null ? instance : getLogger(name, Level.INFO, Formatters.simple(), Appenders.err(Level.INFO));
	}

	/**
	 * Retrieves the logger associated with the fully-qualified name of the class calling this method and sets its level and writers to the specified parameters.
	 * @param level logging level
	 * @param formatter message formatter
	 * @param appenders output appenders
	 * @return logger associated with the fully-qualified name of the class calling this method
	 */
	public static Logger getLogger(int level, Formatter formatter, Appender... appenders) {
		return getLogger(findInvoker().getClassName(), level, formatter, appenders);
	}

	/**
	 * Retrieves the logger associated with {@code name} and sets its level and writers to the specified parameters.
	 * @param name logger name
	 * @param level logging level
	 * @param formatter message formatter
	 * @param appenders output appenders
	 * @return logger associated with {@code name}
	 */
	public static Logger getLogger(String name, int level, Formatter formatter, Appender... appenders) {
		Logger instance = instances.get(name);

		if (instance == null) {
			instance = new Logger(level, formatter, appenders);
			register(name, instance);
		} else {
			instance.setLevel(level);
			instance.setFormatter(formatter);
			instance.setAppenders(appenders);
		}
		return instance;
	}

	private static void register(String name, Logger logger) {
		instances.entrySet().parallelStream()
						 .filter(entry -> name.matches(entry.getKey() + "\\..+"))	// Add all parents to this logger
						 .forEach(entry -> logger.parents.add(entry.getValue()));

		instances.entrySet().parallelStream()
						 .filter(entry -> entry.getKey().matches(name + "\\..+"))	// Add this logger as parent to all child loggers
						 .forEach(entry -> entry.getValue().parents.add(logger));

		instances.put(name, logger);
	}

	private static StackTraceElement findInvoker() {
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();

		for (int i = 2; i < stackTrace.length; i++) {
			if (!stackTrace[i].getClassName().equals(Logger.class.getName())) return stackTrace[i];	// Return latest non-Logger invoker
		}
		throw new IllegalStateException("Not invoked from outside of Logger class");	// Should not happen
	}

	private Logger(int level, Formatter formatter, Appender... appenders) {
		setLevel(level);
		setFormatter(formatter);
		setAppenders(appenders);
	}

	/**
	 * Logs a message at the {@code FATAL} level.
	 * @param message message to log, with '{}' denoting injection points for each arg in {@code args}
	 * @param args arguments which are lazily resolved to their string representations and injected into {@code message} at logging time
	 */
	public void fatal(String message, Object... args) {
		log(Level.FATAL, message, args);
	}

	/**
	 * Logs a message at the {@code SEVERE} level.
	 * @param message message to log, with '{}' denoting injection points for each arg in {@code args}
	 * @param args arguments which are lazily resolved to their string representations and injected into {@code message} at logging time
	 */
	public void severe(String message, Object... args) {
		log(Level.SEVERE, message, args);
	}

	/**
	 * Logs a message at the {@code WARNING} level.
	 * @param message message to log, with '{}' denoting injection points for each arg in {@code args}
	 * @param args arguments which are lazily resolved to their string representations and injected into {@code message} at logging time
	 */
	public void warning(String message, Object... args) {
		log(Level.WARNING, message, args);
	}

	/**
	 * Logs a message at the {@code INFO} level.
	 * @param message message to log, with '{}' denoting injection points for each arg in {@code args}
	 * @param args arguments which are lazily resolved to their string representations and injected into {@code message} at logging time
	 */
	public void info(String message, Object... args) {
		log(Level.INFO, message, args);
	}

	/**
	 * Logs a message at the {@code DEBUG} level.
	 * @param message message to log, with '{}' denoting injection points for each arg in {@code args}
	 * @param args arguments which are lazily resolved to their string representations and injected into {@code message} at logging time
	 */
	public void debug(String message, Object... args) {
		log(Level.DEBUG, message, args);
	}

	/**
	 * Logs a {@link Throwable} at the {@code SEVERE} level.
	 * @param e throwable to log
	 */
	public void exception(Throwable e) {
		exception(Level.SEVERE, e);
	}

	/**
	 * Logs a {@link Throwable} at a specified logging level.
	 * @param e throwable to log
	 * @param level level to log at
	 */
	public void exception(int level, Throwable e) {
		if (logs(level)) {  // Avoid needlessly formatting exception stack
			log(level, formatException(e));
		}
	}
	private static String formatException(Throwable e) {
		StringJoiner joiner = new StringJoiner(System.lineSeparator() + "\tat ", e.toString(), "");

		for (StackTraceElement element : e.getStackTrace()) joiner.add(element.toString());

		return joiner.toString();
	}

	/**
	 * Attempts to log a message.
	 * The message is logged only if its level is {@code <=} this logger's level and this logger has at least 1 appender.
	 * @param level granularity to log at
	 * @param message message to log, with '{}' denoting injection points for each arg in {@code args}
	 * @param args arguments which are lazily resolved to their string representations and injected into {@code message} at logging time
	 */
	public void log(int level, String message, Object... args) {
		if (logs(level)) {
			String formattedMessage = formatter.format(Instant.now(), findInvoker(), level, resolve(message, args));

			appendToAll(level, formattedMessage);
			for (Logger parent : parents) parent.appendToAll(level, formattedMessage);
		}
	}

	private String resolve(String message, Object... args) {
		String result = message;

		if (args != null) {
			for (Object arg : args) {
				String replacement;

				if (arg == null) replacement = "null";
				else if (arg instanceof  Supplier) replacement = ((Supplier) arg).get().toString();
				else replacement = arg.toString();

				result = result.replaceFirst(Pattern.quote("{}"), Matcher.quoteReplacement(replacement));
			}
		}
		return result;
	}

	private void appendToAll(int level, String message) {
		for (Appender appender : appenders) appender.append(level, message);
	}

	/**
	 * @param level granularity level
	 * @return {@code true} if this logger logs and has at least 1 appender which accepts messages of a certain level
	 */
	public boolean logs(int level) {
		return level <= this.level && hasAcceptingAppender(level);
	}
	private boolean hasAcceptingAppender(int level) {
		for (Appender appender : appenders) {
			if (appender.logs(level)) return true;
		}
		for (Logger parent : parents) {
			for (Appender appender : parent.appenders) {
				if (appender.logs(level)) return true;
			}
		}
		return false;
	}

	/** @return maximum level of messages logged by this logger */
	public int getLevel() {
		return level;
	}
	/** @param level new logging level */
	public void setLevel(int level) {
		this.level = level;
	}

	/** @return current message formatter */
	public Formatter getFormatter() {
		return formatter;
	}
	/** @param formatter new message formatter */
	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	/**
	 * @param toAdd appender to add
	 * @return {@code true} if this logger did not contain {@code toAdd}
	 */
	public boolean addAppender(Appender toAdd) {
		return appenders.add(toAdd);
	}
	/**
	 * @param toRemove appender to remove
	 * @return {@code true} if this logger contained {@code toRemove}
	 */
	public boolean removeAppender(Appender toRemove) {
		return appenders.remove(toRemove);
	}

	/** @return all current message appenders */
	public Iterable<Appender> getAppenders() {
		return appenders;
	}
	/** @param appenders new appenders; if {@code null} or omitted, clears existing appenders */
	public void setAppenders(Appender... appenders) {
		this.appenders.clear();

		if (appenders != null) this.appenders.addAll(Arrays.asList(appenders));
	}
}
