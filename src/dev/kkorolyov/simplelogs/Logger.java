package dev.kkorolyov.simplelogs;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
	
	private static final Map<String, Logger> instances = new HashMap<>();
	private static Level globalLevel = Level.INFO;	// Default INFO
	private static boolean globalEnabled = true;	// Default true
	
	private Printer printer;
	private Level level;
	private boolean enabled;
	
	/**
	 * Retrieves a logger of the specified name, if it exists.
	 * If such a logger does not exist, a new logger is created using the specified name, a default printer to {@code System.err}, and a default logging level of {@code INFO}.
	 * @param name logger name
	 * @return appropriate logger
	 */
	public static Logger getLogger(String name) {
		Logger instance = instances.get(name);
		
		return instance != null ? instance : getLogger(name, new SysErrPrinter(), globalLevel);
	}
	/**
	 * Retrieves a logger of the specified name, if it exists.
	 * If such a logger already exists, its printer and logging level are set to the specified parameters.
	 * If such a logger does not exist, a new logger is created using the specified name, printer, and logging level.
	 * @param name logger name
	 * @param printer printer the logger will use for message printing
	 * @param level level of messages to print
	 * @return appropriate logger
	 */
	public static Logger getLogger(String name, Printer printer, Level level) {
		Logger instance = instances.get(name);
		
		if (instance == null) {
			instance = new Logger(printer, level);
			
			instances.put(name, instance);
		}
		else {
			instance.setPrinter(printer);
			instance.setLevel(level);
		}
		return instance;
	}
	
	private Logger(Printer printer, Level level) {
		this.printer = printer;
		this.level = level;
		this.enabled = globalEnabled;	
	}
	
	/**
	 * Sets the logging level of all current {@code Logger} instances, and the default level of all future instances.
	 * @param level logging level to set
	 */
	public static void setGlobalLevel(Level level) {
		globalLevel = level;
		
		syncLevel();
	}
	private static void syncLevel() {
		for (Logger instance : instances.values())
			instance.level = globalLevel;
	}
	
	/**
	 * Enables all current {@code Logger} instances. 
	 */
	public static void enableAll() {
		setGlobalEnabled(true);
	}
	/**
	 * Disables all current {@code Logger} instances. 
	 */
	public static void disableAll() {
		setGlobalEnabled(false);
	}
	/**
	 * Sets the enabled status of all current {@code Logger} instances, as well as the default enabled status of all future instances.
	 * @param enabled {@code true} enables, {@code false} disables
	 */
	public static void setGlobalEnabled(boolean enabled) {
		globalEnabled = enabled;
		
		syncEnabled();
	}
	private static void syncEnabled() {
		for (Logger instance : instances.values())
			instance.enabled = globalEnabled;
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
		if (!enabled || !isLoggable(level))	// Avoid needlessly formatting exception stack
			return;
		
		log(formatException(e), level);
	}
	private static String formatException(Exception e) {
		StringBuilder messageBuilder = new StringBuilder(e.toString());
		
		for (StackTraceElement element : e.getStackTrace()) {
			messageBuilder.append(System.lineSeparator() + '\t' + element.toString());
		}
		return messageBuilder.toString();
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
	 * Attempts to log a message at a specific level of granularity.
	 * If this logger is disabled, the message is not logged.
	 * If the specified granularity level is finer than this logger's current granularity level, the message is not logged.
	 * @param message message to log
	 * @param level message's level of granularity
	 */
	public void log(String message, Level level) {
		if (!enabled || !isLoggable(level))
			return;
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement originalCaller = null;
		for (int i = 2; i < stackTrace.length; i++) {
			originalCaller = stackTrace[i];
			boolean originalCallerFound = true;
			for (Method classMethod : Logger.class.getDeclaredMethods()) {
				if (stackTrace[i].getMethodName().equals(classMethod.getName())) {
					originalCallerFound = false;	// Search until called by a method outside this class
					break;
				}
			}
			if (originalCallerFound)
				break;
		}
		printer.print(formatMessage(message, level, originalCaller));
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
	 * Enables logging.
	 */
	public void enable() {
		enabled = true;
	}
	/**
	 * Disables logging.
	 */
	public void disable() {
		enabled = false;
	}
	
	/** 
	 * Checks if a message of a specified level would be logged by this logger.
	 * @param level logging level to test
	 * @return {@code true} if a message of the specified level would be logged by this logger
	 */
	public boolean isLoggable(Level level) {
		return this.level.compareTo(level) >= 0;	// Greater level == finer granularity
	}
	
	/**
	 * Sets the printer of this logger.
	 * @param printer new printer to use for printing messages
	 */
	public void setPrinter(Printer printer) {
		this.printer = printer;
	}
	/**
	 * Sets the level of this logger.
	 * @param level new level
	 */
	public void setLevel(Level level) {
		this.level = level;
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
