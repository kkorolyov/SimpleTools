package dev.kkorolyov.simplelogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.junit.Test;

import dev.kkorolyov.simplelogs.Logger.Level;

@SuppressWarnings("javadoc")
public class LoggerTest {
	@Test
	public void testGetLoggerParams() {
		PrintWriter[] expectedWriters = new PrintWriter[]{new PrintWriter(System.out), new PrintWriter(System.err)};
		for (Level expectedLevel : Level.values()) {
			Logger logger = Logger.getLogger("test.getLoggerParams", expectedLevel, expectedWriters);

			assertEquals(expectedLevel, logger.getLevel());
			assertEquals(expectedWriters.length, logger.getWriters().length);
			for (PrintWriter expectedWriter : expectedWriters) {
				boolean found = false;
				
				for (PrintWriter actualWriter : logger.getWriters()) {
					if (expectedWriter == actualWriter) {
						found = true;
						break;
					}
				}
				assertTrue(found);
			}
		}
	}
	
	@Test
	public void testLog() {
		for (Level loggerLevel : Level.values()) {
			for (Level messageLevel : Level.values()) {
				Logger logger = Logger.getLogger("test.log", loggerLevel, buildWriterStub(loggerLevel, messageLevel));
				logger.log("", messageLevel);
			}
		}
	}
	@Test
	public void testSevere() {
		for (Level loggerLevel : Level.values()) {
			Logger logger = Logger.getLogger("test.severe", loggerLevel, buildWriterStub(loggerLevel, Level.SEVERE));
			logger.severe("");
		}
	}
	@Test
	public void testWarning() {
		for (Level loggerLevel : Level.values()) {
			Logger logger = Logger.getLogger("test.warning", loggerLevel, buildWriterStub(loggerLevel, Level.WARNING));
			logger.warning("");
		}
	}
	@Test
	public void testInfo() {
		for (Level loggerLevel : Level.values()) {
			Logger logger = Logger.getLogger("test.severe", loggerLevel, buildWriterStub(loggerLevel, Level.INFO));
			logger.info("");
		}
	}
	@Test
	public void testDebug() {
		for (Level loggerLevel : Level.values()) {
			Logger logger = Logger.getLogger("test.severe", loggerLevel, buildWriterStub(loggerLevel, Level.DEBUG));
			logger.debug("");
		}
	}
	
	@Test
	public void testExceptionLevel() {
		for (Level loggerLevel : Level.values()) {
			for (Level messageLevel : Level.values()) {
				Logger logger = Logger.getLogger("test.exceptionLevel", loggerLevel, buildWriterStub(loggerLevel, messageLevel));
				logger.exception(new Exception(), messageLevel);
			}
		}
	}
	@Test
	public void testException() {
		for (Level loggerLevel : Level.values()) {
			Logger logger = Logger.getLogger("test.exception", loggerLevel, buildWriterStub(loggerLevel, Level.SEVERE));
			logger.exception(new Exception());
		}
	}
	
	@Test
	public void testIsLoggable() {
		Logger logger = Logger.getLogger("test.isLoggable");
		
		for (Level loggerLevel : Level.values()) {
			logger.setLevel(loggerLevel);
			
			for (Level messageLevel : Level.values()) {
				if (messageLevel.compareTo(loggerLevel) <= 0)
					assertTrue(logger.isLoggable(messageLevel));
				else
					assertFalse(logger.isLoggable(messageLevel));
			}
		}
	}
	
	private static PrintWriter buildWriterStub(Level loggerLevel, Level messageLevel) {
		return new PrintWriter(new Writer() {
			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				if (messageLevel.compareTo(loggerLevel) > 0)
					fail("Should not log when message level > logger level");
			}
			@Override
			public void flush() throws IOException {
				//
			}
			@Override
			public void close() throws IOException {
				//
			}
		});
	}
}
