package dev.kkorolyov.simplelogs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.AfterClass;
import org.junit.Test;

import dev.kkorolyov.simplelogs.Logger.Level;

@SuppressWarnings("javadoc")
public class LoggerTest {	// TODO Assertions
	private static final File testFile = new File("TestLog.txt");
	private static PrintWriter[] printers;
	
	static {
		try {
			printers = new PrintWriter[]{	new PrintWriter(System.out),
																		new PrintWriter(System.err),
																		new PrintWriter(new FileWriter(testFile))};
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		testFile.delete();
	}
	
	@Test
	public void testExceptionSevere() {
		for (Level level : Level.values()) {
			System.out.println("Printing SEVERE exception with " + level + "-leveled loggers:");
			for (Logger logger : getAllLoggers(level))
				logger.exception(new Exception("TestException"), Level.SEVERE);
		}
		System.out.println();
	}

	@Test
	public void testExceptionWarning() {
		for (Level level : Level.values()) {
			System.out.println("Printing WARNING exception with " + level + "-leveled loggers:");
			for (Logger logger : getAllLoggers(level))
				logger.exception(new Exception("TestException"), Level.WARNING);
		}
		System.out.println();
	}

	@Test
	public void testSevere() {
		for (Level level : Level.values()) {
			System.out.println("Printing SEVERE message with " + level + "-leveled loggers:");
			for (Logger logger : getAllLoggers(level))
				logger.severe("Test SEVERE message");
		}
		System.out.println();
	}

	@Test
	public void testWarning() {
		for (Level level : Level.values()) {
			System.out.println("Printing WARNING message with " + level + "-leveled loggers:");
			for (Logger logger : getAllLoggers(level))
				logger.warning("Test WARNING message");
		}
		System.out.println();
	}

	@Test
	public void testInfo() {
		for (Level level : Level.values()) {
			System.out.println("Printing INFO message with " + level + "-leveled loggers:");
			for (Logger logger : getAllLoggers(level))
				logger.info("Test INFO message");
		}
		System.out.println();
	}

	@Test
	public void testDebug() {
		for (Level level : Level.values()) {
			System.out.println("Printing DEBUG message with " + level + "-leveled loggers:");
			for (Logger logger : getAllLoggers(level))
				logger.debug("Test DEBUG message");
		}
		System.out.println();
	}

	@Test
	public void testLog() {
		for (Level level : Level.values()) {
			System.out.println("Printing " + level + " message with " + level + "-leveled loggers:");
			for (Logger logger : getAllLoggers(level))
				logger.log(level + " message", level);
		}
		System.out.println();
	}
	
	@Test
	public void testDisable() {
		for (Level level : Level.values()) {
			System.out.println("Printing " + level + " message with disabled " + level + "-leveled loggers:");
			for (Logger logger : getAllLoggers(level)) {
				logger.setEnabled(false);
				logger.log("YOU SEE NOTHING", level);
				
				logger.setEnabled(true);
				logger.log("YOU SEE SOMETHING", level);
			}
		}
		System.out.println();
	}
	
	private static Logger[] getAllLoggers(Level level) {
		Logger[] loggers = new Logger[printers.length];
		for (int i = 0; i < printers.length; i++) {
			loggers[i] = Logger.getLogger("TestLogger" + i, level, printers[i]);
		}
		return loggers;
	}
}
