package dev.kkorolyov.simplelogs;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Simple logging interface for multiple levels.
 */
public class Logger {
	private static final char COARSE_TIME_SEPARATOR = '.',
														COARSE_TO_FINE_TIME_SEPARATOR = '-',
														FINE_TIME_SEPARATOR = ':',
														FINE_TO_FINER_TIME_SEPARATOR = '.',
														FIELD_SEPARATOR = ' ',
														INVOKER_SEPARATOR = '-';
	private static final String MESSAGE_SEPARATOR = ": ";
	private static final String HIERARCHY_DELIMETER = ".";
	
	private static final Level DEFAULT_LEVEL = Level.INFO;
	private static final PrintWriter[] DEFAULT_WRITERS = {new PrintWriter(System.err)};
	
	private static final Map<String, Logger> instances = new HashMap<>();
	
	private Level level;
	private Set<PrintWriter> writers = new HashSet<>();
	private boolean enabled;
	private Logger parent;
	
	/**
	 * Retrieves the logger of the specified name, if it exists.
	 * If such a logger does not exist, a new logger is created using the specified name, a default logging level of {@code INFO}, and a default writer to {@code System.err}.
	 * @param name logger name
	 * @return appropriate logger
	 */
	public static Logger getLogger(String name) {
		Logger instance = instances.get(name);
		
		return instance != null ? instance : getLogger(name, DEFAULT_LEVEL, DEFAULT_WRITERS);
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
		
		while ((nextLevel = loggerPath.lastIndexOf(HIERARCHY_DELIMETER)) >= 0) {
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
		if (!enabled || !isLoggable(level) || writers.size() <= 0)	// Avoid needlessly formatting exception stack
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
	 * @see #log(String, Level)
	 */
	public void log(LazyMessage message, Level level) {
		if (!enabled || !isLoggable(level) || writers.size() <= 0)
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
		if (!enabled || !isLoggable(level) || writers.size() <= 0)	// Avoid needlessly finding calling method
			return;
		
		StackTraceElement caller = findCallingMethod(Thread.currentThread().getStackTrace());
		String formattedMessage = formatMessage(message, level, caller);
		
		log(formattedMessage, level, caller);
	}
	private void log(String message, Level level, StackTraceElement originalCaller) {
		if (!enabled || !isLoggable(level) || writers.size() <= 0)
			return;
		
		for (PrintWriter writer : writers) {
			writer.println(message);
			writer.flush();
		}
		if (parent != null)
			parent.log(message, level, originalCaller);
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
	private static String formatMessage(String message, Level messageLevel, StackTraceElement caller) {
		StringBuilder messageBuilder = new StringBuilder();
		
		Calendar currentTime = Calendar.getInstance();
		int month = currentTime.get(Calendar.MONTH) + 1,
				day = currentTime.get(Calendar.DAY_OF_MONTH),
				year = currentTime.get(Calendar.YEAR),
				hour = currentTime.get(Calendar.HOUR_OF_DAY),
				minute = currentTime.get(Calendar.MINUTE),
				second = currentTime.get(Calendar.SECOND),
				millisecond = currentTime.get(Calendar.MILLISECOND);
		String 	sMonth = String.valueOf(month),
						sDay = String.valueOf(day),
						sYear = String.valueOf(year),
						sHour = (hour < 10) ? '0' + String.valueOf(hour) : String.valueOf(hour),
						sMinute = (minute < 10) ? '0' + String.valueOf(minute) : String.valueOf(minute),
						sSecond = (second < 10) ? '0' + String.valueOf(second) : String.valueOf(second),
						sMillisecond = String.valueOf(millisecond);
						
		messageBuilder.append(sMonth + COARSE_TIME_SEPARATOR + sDay + COARSE_TIME_SEPARATOR + sYear + COARSE_TO_FINE_TIME_SEPARATOR +
													sHour + FINE_TIME_SEPARATOR + sMinute + FINE_TIME_SEPARATOR + sSecond + FINE_TO_FINER_TIME_SEPARATOR +
													sMillisecond + FIELD_SEPARATOR);
		messageBuilder.append(caller.getClassName() + INVOKER_SEPARATOR + caller.getMethodName());
		messageBuilder.append(System.lineSeparator());
		messageBuilder.append(messageLevel + MESSAGE_SEPARATOR + message);
		
		return messageBuilder.toString();
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
