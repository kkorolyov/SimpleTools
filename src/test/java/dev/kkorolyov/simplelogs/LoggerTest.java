package dev.kkorolyov.simplelogs;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@SuppressWarnings("javadoc")
public class LoggerTest {
	private static final String LOGGER_PROPS_LOGGER = "logger.props.logger";
	private static final String[] loggerPropsFiles = {"test/loggerPropsOut", "test/loggerPropsOutOut"};
	
	@Parameters(name = "Level({0})")
	public static Object[] data() {
		return Level.values();
	}
	private final Level loggerLevel;
	
	public LoggerTest(Level input) {
		loggerLevel = input;
	}
	
	@Test
	public void testApplyProps() throws IOException {
		Logger.applyProps(buildLoggerPropsStub());
		
		Logger logger = Logger.getLogger(LOGGER_PROPS_LOGGER);
		assertEquals(loggerLevel, logger.getLevel());
		
		for (String filename : loggerPropsFiles)
			assertEquals(0, new File(filename).length());
		
		logger.log("Message", loggerLevel);
		
		for (PrintWriter writer : Logger.getLogger(LOGGER_PROPS_LOGGER).getWriters()) 
			writer.close();
		
		for (String filename : loggerPropsFiles) {
			assertNotEquals(0, new File(filename).length());
			Files.delete(Paths.get(filename));
		}
	}
	
	@Test
	public void testGetLoggerParams() {
		PrintWriter[] expectedWriters = new PrintWriter[]{new PrintWriter(System.out),
																											new PrintWriter(System.err)};
		Logger logger = Logger.getLogger("test.getLoggerParams", loggerLevel, expectedWriters);

		assertEquals(loggerLevel, logger.getLevel());
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
	
	@Test
	public void testException() {
		Logger logger = Logger.getLogger("test.exception", loggerLevel, buildWriterStub(loggerLevel, Level.SEVERE));
		logger.exception(new Exception());
	}
	@Test
	public void testExceptionLevel() {
		for (Level messageLevel : Level.values()) {
			Logger logger = Logger.getLogger("test.exceptionLevel", loggerLevel, buildWriterStub(loggerLevel, messageLevel));
			logger.exception(new Exception(), messageLevel);
		}
	}
	
	@Test
	public void testSevereLazy() {
		Logger logger = Logger.getLogger("test.severe", loggerLevel, buildWriterStub(loggerLevel, Level.SEVERE));
		logger.severe(() -> "");
	}
	@Test
	public void testWarningLazy() {
		Logger logger = Logger.getLogger("test.warning", loggerLevel, buildWriterStub(loggerLevel, Level.WARNING));
		logger.warning(() -> "");
	}
	@Test
	public void testInfoLazy() {
		Logger logger = Logger.getLogger("test.severe", loggerLevel, buildWriterStub(loggerLevel, Level.INFO));
		logger.info(() -> "");
	}
	@Test
	public void testDebugLazy() {
		Logger logger = Logger.getLogger("test.severe", loggerLevel, buildWriterStub(loggerLevel, Level.DEBUG));
		logger.debug(() -> "");
	}
	@Test
	public void testLogLazy() {
		for (Level messageLevel : Level.values()) {
			Logger logger = Logger.getLogger("test.logger", loggerLevel, buildWriterStub(loggerLevel, messageLevel));
			logger.log(() -> "", messageLevel);
		}
	}
	
	@Test
	public void testSevere() {
		Logger logger = Logger.getLogger("test.severe", loggerLevel, buildWriterStub(loggerLevel, Level.SEVERE));
		logger.severe("");
	}
	@Test
	public void testWarning() {
		Logger logger = Logger.getLogger("test.warning", loggerLevel, buildWriterStub(loggerLevel, Level.WARNING));
		logger.warning("");
	}
	@Test
	public void testInfo() {
		Logger logger = Logger.getLogger("test.severe", loggerLevel, buildWriterStub(loggerLevel, Level.INFO));
		logger.info("");
	}
	@Test
	public void testDebug() {
		Logger logger = Logger.getLogger("test.severe", loggerLevel, buildWriterStub(loggerLevel, Level.DEBUG));
		logger.debug("");
	}
	@Test
	public void testLog() {
		for (Level messageLevel : Level.values()) {
			Logger logger = Logger.getLogger("test.logger", loggerLevel, buildWriterStub(loggerLevel, messageLevel));
			logger.log("", messageLevel);
		}
	}
	
	@Test
	public void testHierarchy() {
		boolean[] childResult = {false},
							parentResult = {false};
		Logger child = Logger.getLogger("test.hierarchy." + loggerLevel + ".child", loggerLevel, buildWriterStub(childResult));
		child.log("msg", loggerLevel);
		
		assertTrue(childResult[0]);
		assertFalse(parentResult[0]);
		
		childResult[0] = false;
		
		Logger parent = Logger.getLogger("test.hierarchy." + loggerLevel, loggerLevel, buildWriterStub(parentResult));
		parent.log("msg", loggerLevel);
		
		assertFalse(childResult[0]);
		assertTrue(parentResult[0]);
		
		parentResult[0] = false;
		
		child.log("msg", loggerLevel);
		
		assertTrue(childResult[0]);
		assertTrue(parentResult[0]);
	}
	
	@Test
	public void testIsLoggable() {
		Logger logger = Logger.getLogger("test.isLoggable");
		
		logger.setLevel(loggerLevel);
		
		for (Level messageLevel : Level.values()) {
			if (messageLevel.compareTo(loggerLevel) <= 0)
				assertTrue(logger.isLoggable(messageLevel));
			else
				assertFalse(logger.isLoggable(messageLevel));
		}
	}
	
	@Test
	public void testAddWriter() {
		PrintWriter[] writers = new PrintWriter[]{new PrintWriter(System.err)};
		Logger logger = Logger.getLogger("test.addWriter", loggerLevel, new PrintWriter[]{});
		
		assertEquals(0, logger.getWriters().length);
		assertThat(writers, IsNot.not(IsEqual.equalTo(logger.getWriters())));
		logger.addWriter(writers[0]);
		assertEquals(1, logger.getWriters().length);
		assertArrayEquals(writers, logger.getWriters());
	}
	@Test
	public void testRemoveWriter() {
		PrintWriter[] writers = new PrintWriter[]{new PrintWriter(System.err)};
		Logger logger = Logger.getLogger("test.removeWriter", loggerLevel, writers);
		
		assertEquals(1, logger.getWriters().length);
		assertArrayEquals(writers, logger.getWriters());
		logger.removeWriter(writers[0]);
		assertEquals(0, logger.getWriters().length);
		assertThat(writers, IsNot.not(IsEqual.equalTo(logger.getWriters())));
	}
	
	@Test
	public void testGetLevel() {
		assertEquals(Logger.getLogger("test.getLevel", loggerLevel, (PrintWriter) null).getLevel(), loggerLevel);
	}
	
	@Test
	public void testIsEnabled() {
		Logger logger = Logger.getLogger("test.isEnabled" + loggerLevel);
		
		assertTrue(logger.isEnabled());
		logger.setEnabled(false);
		assertFalse(logger.isEnabled());
	}
	
	private Path buildLoggerPropsStub() throws FileNotFoundException {
		File props = new File("testProps");
		props.deleteOnExit();
		
		try (PrintWriter writer = new PrintWriter(props)) {
			writer.print(LOGGER_PROPS_LOGGER + "=" + loggerLevel);
			for (String file : loggerPropsFiles)
				writer.print(", " + file);
			writer.print(",OUT,ERR");
			writer.flush();
		}
		return props.toPath();
	}
	
	private static PrintWriter buildWriterStub(Level loggerLevel, Level messageLevel) {
		return new PrintWriter(new Writer() {
			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				assertTrue(loggerLevel.compareTo(messageLevel) >= 0);
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
	private static PrintWriter buildWriterStub(boolean[] result) {
		return new PrintWriter(new Writer() {
			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				result[0] = true;
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
