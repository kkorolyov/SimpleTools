package dev.kkorolyov.simplelogs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

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
		System.out.println(findInvoker().getClassName() + "#" + findInvoker().getMethodName());
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
						 .filter(entry -> name.contains(entry.getKey()))	// Add all parents to this logger
						 .forEach(entry -> logger.parents.add(entry.getValue()));

		instances.entrySet().parallelStream()
						 .filter(entry -> entry.getKey().contains(name))	// Add this logger as parent to all child loggers
						 .forEach(entry -> entry.getValue().parents.add(logger));

		instances.put(name, logger);
	}

	private static StackTraceElement findInvoker() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		System.out.println(Arrays.toString(stackTrace));
		StackTraceElement callingMethod = null;

		for (int i = 2; i < stackTrace.length; i++) {
			callingMethod = stackTrace[i];
			boolean callingMethodFound = true;
			for (Method classMethod : Logger.class.getDeclaredMethods()) {
				if (stackTrace[i].getMethodName().equals(classMethod.getName())) {
					callingMethodFound = false;	// Search until called by a method outside this class
					break;
				}
			}
			if (callingMethodFound)
				break;
		}
		return callingMethod;
	}

	private Logger(int level, Formatter formatter, Appender... appenders) {
		setLevel(level);
		setFormatter(formatter);
		setAppenders(appenders);
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
	 * Logs an exception at the {@code SEVERE} level.
	 * @param e exception to log
	 */
	public void exception(Throwable e) {
		exception(Level.SEVERE, e);
	}

	/**
	 * Logs an exception at a specified logging level.
	 * @param e exception to log
	 * @param level level to log at
	 */
	public void exception(int level, Throwable e) {
		if (willLog(level)) {  // Avoid needlessly formatting exception stack
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
		if (willLog(level)) {
			String formattedMessage = formatter.format(Instant.now(), findInvoker(), level, resolve(message, args));

			append(level, formattedMessage);
			for (Logger parent : parents) append(level, formattedMessage);
		}
	}
	private boolean willLog(int level) {
		return isLoggable(level) && (!appenders.isEmpty() || !parents.isEmpty());
	}

	private String resolve(String message, Object... args) {
		String result = message;

		if (args != null) {
			for (Object arg : args) result = result.replaceFirst("\\{}", arg.toString());
		}
		return result;
	}

	private void append(int level, String message) {
		for (Appender appender : appenders) appender.append(level, message);
	}

	/**
	 * Checks if messages of a specified level would be logged by this logger.
	 * @param level logging level to test
	 * @return {@code true} if a message of the specified level would be logged by this logger
	 */
	public boolean isLoggable(int level) {
		return level <= getLevel();  // Greater level == finer granularity
	}

	/**
	 * @return maximum level of messages logged by this logger
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level new logging level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @param formatter new message formatter
	 */
	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	/** @param toAdd appender to add */
	public void addAppender(Appender toAdd) {
		appenders.add(toAdd);
	}
	/** @param toRemove appender to remove */
	public void removeAppender(Appender toRemove) {
		appenders.remove(toRemove);
	}
	/** @param appenders new appenders; if {@code null}, clears existing appenders */
	public void setAppenders(Appender... appenders) {
		this.appenders.clear();

		if (appenders != null) this.appenders.addAll(Arrays.asList(appenders));
	}
}
