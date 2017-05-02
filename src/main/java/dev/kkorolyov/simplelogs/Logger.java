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
	
	private Logger parent;

	private int level;
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
 	 * Retrieves the logger associated with the fully-qualified name of the class calling this method.
	 * If such a logger does not exist, it is initialized as defined in {@link #getLogger(String)}.
	 * @return logger associated with the fully-qualified name of the class calling this method
	 */
	public static Logger getLogger() {
		return getLogger(findCaller().getClassName());
	}
	/**
	 * Retrieves the logger associated with {@code name}.
	 * If such a logger does not exist, a new logger is created with level {@value Level#INFO}, the simple formatter, appender to {@code System.err}, and is associated with {@code name}.
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
		return getLogger(findCaller().getClassName(), level, formatter, appenders);
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
			
			instances.put(name, instance);
			applyParents();
		} else {
			instance.setLevel(level);
			instance.setFormatter(formatter);
			instance.setAppenders(appenders);
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

	private static StackTraceElement findCaller() {
		return Thread.currentThread().getStackTrace()[2];
	}
	
	private Logger(int level, Formatter formatter, Appender... appenders) {
		setLevel(level);
		setFormatter(formatter);
		setAppenders(appenders);
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
	public void severe(LazyParam message) {
		log(message, Level.SEVERE);
	}
	/**
	 * Logs a lazy message at the {@code WARNING} level.
	 * @param message message to log
	 */
	public void warning(LazyParam message) {
		log(message, Level.WARNING);
	}
	/**
	 * Logs a lazy message at the {@code INFO} level.
	 * @param message message to log
	 */
	public void info(LazyParam message) {
		log(message, Level.INFO);
	}
	/**
	 * Logs a lazy message at the {@code DEBUG} level.
	 * @param message message to log
	 */
	public void debug(LazyParam message) {
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
	 * Attempts to log a message.
	 * If {@code level} is finer than this logger's current granularity level or this logger does not have a valid writer, the message is not logged.
	 * @param level granularity to log at
	 * @param message message to log
	 */
	public void log(int level, String message) {
		if ((!isLoggable(level) || !hasAppenders()) && parent == null)	// Avoid needlessly finding calling method
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
	 * Checks if messages of a specified level would be logged by this logger.
	 * @param level logging level to test
	 * @return {@code true} if a message of the specified level would be logged by this logger
	 */
	public boolean isLoggable(int level) {
		return level <= getLevel();	// Greater level == finer granularity
	}
	
	/** @return maximum level of messages logged by this logger */
	public int getLevel() {
		return level;
	}
	/** @param level new logging level */
	public void setLevel(int level) {
		this.level = level;
	}

	/** @param formatter new message formatter */
	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	private boolean hasAppenders() {
		return appenders.size() > 0;
	}
	/** @param appenders new appenders; if {@code null} simply clears all existing appenders */
	public void setAppenders(Appender... appenders) {
		this.appenders.clear();

		if (appenders != null) this.appenders.addAll(Arrays.asList(appenders));
	}

	private boolean isRoot() {
		return parent == null;
	}
	private void setParent(Logger newParent) {
		if (this != newParent)
			parent = newParent;
	}
	
	/**
	 * Defines log level constants.
	 */
	public static final class Level {
		public static int OFF = 0;
		public static int SEVERE = 100;
		public static int WARNING = 200;
		public static int INFO = 300;
		public static int DEBUG = 400;

		private Level() {}
	}
}
