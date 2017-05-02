package dev.kkorolyov.simplelogs;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

import dev.kkorolyov.simplelogs.append.Appender;
import dev.kkorolyov.simplelogs.format.Formatter;

/**
 * Simple logging interface for multiple levels.
 */
public class Logger {
	private static final Map<String, Logger> instances = new HashMap<>();
	
	private Logger parent;

	private Level level;

	private Formatter formatter;
	private Set<Appender> appenders = new HashSet<>();
	
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
	 * @param root path prefixing all log files defined in the logging properties file
	 * @return {@code true} if property application successful
	 */
	public static boolean applyProps(Path logProps, Path root) {
		try {
			Class.forName("dev.kkorolyov.simplelogs.PropsApplier").getDeclaredMethod("apply", Path.class, Path.class).invoke(null, logProps, root);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Retrieves the logger of the specified name, if it exists.
	 * If such a logger does not exist, a new logger is created using the specified name, a default logging level of {@code INFO}, and a default writer to {@code System.err}.
	 * @param name logger name
	 * @return appropriate logger
	 */
	public static Logger getLogger(String name) {
		Logger instance = instances.get(name);
		
		return instance != null ? instance : getLogger(name, Level.INFO, new PrintWriter(System.err));
	}
	/**
	 * Retrieves the logger of the specified name, if it exists, and sets its level and writers to the specified parameters.
	 * If such a logger does not exist, a new logger is created according to the specified parameters.
	 * @param name logger name
	 * @param level logging level
	 * @param writers log writers
	 * @return appropriate logger
	 */
	public static Logger getLogger(String name, Level level, PrintWriter... writers) {
		Logger instance = instances.get(name);
		
		if (instance == null) {
			instance = new Logger(level, writers);
			
			instances.put(name, instance);
			applyParents();
		}
		else {
			instance.setLevel(level);
			instance.setWriters(writers);
		}
		return instance;
	}
	
	private static void applyParents() {
		for (String loggerName : instances.keySet())
			instances.get(loggerName).setParent(findParent(loggerName));
	}
	private static Logger findParent(String name) {
		String loggerPath = name;
		int nextLevel;
		
		while ((nextLevel = loggerPath.lastIndexOf('.')) >= 0) {
			loggerPath = loggerPath.substring(0, nextLevel);
			
			if (instances.containsKey(loggerPath))
				return instances.get(loggerPath);
		}
		return instances.get("");	// Return root logger or null
	}
	
	private Logger(Level level, PrintWriter... writers) {
		setLevel(level);
		setWriters(writers);
		setEnabled(true);
	}
	
	/**
	 * Logs an exception at the {@code SEVERE} level.
	 * @param e exception to log
	 */
	public void exception(Exception e) {
		exception(e, Level.SEVERE);
	}
	/**
	 * Logs an exception at a specified logging level.
	 * @param e exception to log
	 * @param level level to log at
	 */
	public void exception(Exception e, Level level) {
		if ((!enabled || !isLoggable(level) || writers.size() <= 0) && parent == null)	// Avoid needlessly formatting exception stack
			return;
		
		log(formatException(e), level);
	}
	private static String formatException(Exception e) {
		StringBuilder messageBuilder = new StringBuilder(e.toString());
		
		for (StackTraceElement element : e.getStackTrace()) {
			messageBuilder.append(System.lineSeparator() + "\tat " + element.toString());
		}
		return messageBuilder.toString();
	}
	
	/**
	 * Logs a lazy message at the {@code SEVERE} level.
	 * @param message message to log
	 */
	public void severe(LazyMessage message) {
		log(message, Level.SEVERE);
	}
	/**
	 * Logs a lazy message at the {@code WARNING} level.
	 * @param message message to log
	 */
	public void warning(LazyMessage message) {
		log(message, Level.WARNING);
	}
	/**
	 * Logs a lazy message at the {@code INFO} level.
	 * @param message message to log
	 */
	public void info(LazyMessage message) {
		log(message, Level.INFO);
	}
	/**
	 * Logs a lazy message at the {@code DEBUG} level.
	 * @param message message to log
	 */
	public void debug(LazyMessage message) {
		log(message, Level.DEBUG);
	}
	
	/**
	 * Logs a message at the {@code SEVERE} level.
	 * @param message message to log
	 */
	public void severe(String message) {
		log(message, Level.SEVERE);
	}
	/**
	 * Logs a message at the {@code WARNING} level.
	 * @param message message to log
	 */
	public void warning(String message) {
		log(message, Level.WARNING);
	}
	/**
	 * Logs a message at the {@code INFO} level.
	 * @param message message to log
	 */
	public void info(String message) {
		log(message, Level.INFO);
	}
	/**
	 * Logs a message at the {@code DEBUG} level.
	 * @param message message to log
	 */
	public void debug(String message) {
		log(message, Level.DEBUG);
	}
	
	/**
	 * Attempts to log a lazy message.
	 * The message creation function is executed only if the resultant message would be logged.
	 * @param message message to log
	 * @param level message's level of granularity
	 * @see #log(String, Level)
	 */
	public void log(LazyMessage message, Level level) {
		if ((!enabled || !isLoggable(level) || writers.size() <= 0) && parent == null)
			return;
		
		log(message.execute(), level);
	}
	/**
	 * Attempts to log a message at a specific level of granularity.
	 * If this logger is disabled, the specified granularity level is finer than this logger's current granularity level, or this logger does not have a valid writer, the message is not logged.
	 * @param message message to log
	 * @param level message's level of granularity
	 */
	public void log(String message, Level level) {
		if ((!enabled || !isLoggable(level) || writers.size() <= 0) && parent == null)	// Avoid needlessly finding calling method
			return;
		
		StackTraceElement caller = findCallingMethod(Thread.currentThread().getStackTrace());
		String formattedMessage = formatter.format(Instant.now(), caller, level, message);

		if (isLoggable(level)) {
			for (Appender appender : appenders) appender.append(level, message);
		}
		if (parent != null) parent.log(message, level);
	}

	private static StackTraceElement findCallingMethod(StackTraceElement[] stackTrace) {
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

	/**
	 * Checks if a message of a specified level would be logged by this logger.
	 * @param level logging level to test
	 * @return {@code true} if a message of the specified level would be logged by this logger
	 */
	public boolean isLoggable(Level level) {
		return this.level.compareTo(level) >= 0;	// Greater level == finer granularity
	}
	
	/** @return logging level */
	public Level getLevel() {
		return level;
	}
	/**
	 * Sets the level of this logger.
	 * @param newLevel new logging level
	 */
	public void setLevel(Level newLevel) {
		level = newLevel;
	}
	
	/** @param toAdd writer to add */
	public void addWriter(PrintWriter toAdd) {
		writers.add(toAdd);
	}
	/** @param toRemove writer to remove */
	public void removeWriter(PrintWriter toRemove) {
		writers.remove(toRemove);
	}
	
	/** @return all writers */
	public PrintWriter[] getWriters() {
		return writers.toArray(new PrintWriter[writers.size()]);
	}
	/**
	 * Sets the writers used by this logger.
	 * All subsequent messages logged by this logger will be written by these writers.
	 * @param newWriters new writers; if {@code null}, will simply clear all writers
	 */
	public void setWriters(PrintWriter... newWriters) {
		writers.clear();
		
		if (newWriters != null) {
			for (PrintWriter writer : newWriters)
				addWriter(writer);
		}
	}
	
	/** @return {@code true} if logger enabled */
	public boolean isEnabled() {
		return enabled;
	}
	/** @param enabled if {@code true}, enables logger; else, disables logger */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	private void setParent(Logger newParent) {
		if (this != newParent)
			parent = newParent;
	}
	
	/**
	 * Specifies the level of granularity for message logging.
	 */
	public static enum Level {
		/** The SEVERE granularity level */
		SEVERE(),
		/** The WARNING granularity level */
		WARNING(),
		/** The INFO granularity level */
		INFO(),
		/** The DEBUG granularity level */
		DEBUG();
	}
}
