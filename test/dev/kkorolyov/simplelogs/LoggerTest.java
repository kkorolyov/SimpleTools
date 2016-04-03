package dev.kkorolyov.simplelogs;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;

import dev.kkorolyov.simplelogs.Logger.Level;

@SuppressWarnings("javadoc")
public class LoggerTest {	// TODO Assertions
	private static final File testFile = new File("TestLog.txt");
	private static final Printer[] printers = {	new SysOutPrinter(),
																							new SysErrPrinter(),
																							new FilePrinter(testFile)};
	
	@AfterClass
	public static void tearDownAfterClass() {
		testFile.delete();
	}
	
	@Test
	public void testExceptionSevere() {
		for (Level level : Level.values()) {
			System.out.println("Printing SEVERE exception with " + level + "-leveled loggers:");
			for (Logger logger : getAllLoggers(level))
				logger.exceptionSevere(new Exception("TestException"));
		}
		System.out.println();
	}

	@Test
	public void testExceptionWarning() {
		for (Level level : Level.values()) {
			System.out.println("Printing WARNING exception with " + level + "-leveled loggers:");
			for (Logger logger : getAllLoggers(level))
				logger.exceptionWarning(new Exception("TestException"));
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
				logger.disable();
				logger.log("YOU SEE NOTHING", level);
				
				logger.enable();
				logger.log("YOU SEE SOMETHING", level);
			}
		}
		System.out.println();
	}
	
	private static Logger[] getAllLoggers(Level level) {
		Logger[] loggers = new Logger[printers.length];
		for (int i = 0; i < printers.length; i++) {
			loggers[i] = Logger.getLogger("TestLogger" + i, printers[i], level);
		}
		return loggers;
	}
}
