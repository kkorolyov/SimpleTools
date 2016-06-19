package dev.kkorolyov.simpleprops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class PropertiesTest {	// TODO Finish
	private static final int NUM_FILES = 5;
	private static final File[] ALL_FILES = new File[NUM_FILES];
	private static final Properties[] ALL_DEFAULTS = new Properties[NUM_FILES];
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		buildFiles();
		buildDefaults();
	}
	
	@After
	public void tearDownAfter() throws IOException {
		for (File file : ALL_FILES)
			file.delete();
	}
	
	@Test
	public void testLoadDefaults() throws IOException {
		for (int i = 0; i < NUM_FILES; i++) {
			Properties currentDefaults = ALL_DEFAULTS[i];
			Properties currentInstance = createInstance(i);
						
			assertEquals(currentDefaults.size(), currentInstance.size());
			
			for (String key : currentDefaults.keys()) {
				assertEquals(currentDefaults.get(key), currentInstance.get(key));
			}
		}
	}

	@Test
	public void testAddProperty() throws IOException {
		for (int i = 0; i < NUM_FILES; i++) {
			Properties currentDefaults = ALL_DEFAULTS[i];
			Properties currentInstance = createInstance(i);
			
			String 	newKey = "NEW-KEY" + i,
							newValue = "NEW-VAL" + i;
			currentInstance.put(newKey, newValue);
			
			assertTrue(currentInstance.size() == currentDefaults.size() + 1);
			assertEquals(newValue, currentInstance.get(newKey));
		}
	}

	@Test
	public void testClear() throws IOException {
		for (int i = 0; i < NUM_FILES; i++) {
			Properties currentDefaults = ALL_DEFAULTS[i];
			Properties currentInstance = createInstance(i);
			
			currentInstance.put("Extra", "Extra");
			assertEquals(currentDefaults.size() + 1, currentInstance.size());

			currentInstance.clear();
			assertEquals(0, currentInstance.size());
		}
	}
	
	@Test
	public void testMatchesFile() {
		for (int i = 0; i < NUM_FILES; i++) {
			Properties currentInstance = createInstance(i);
			
			assertTrue(currentInstance.matchesFile());
			
			currentInstance.put("NoMatch", "NoMatch");
			assertFalse(currentInstance.matchesFile());
		}
	}

	@Test
	public void testSaveToFile() throws IOException {
		for (int i = 0; i < NUM_FILES; i++) {
			Properties currentDefaults = ALL_DEFAULTS[i];
			Properties currentInstance = createInstance(i);
			
			String 	changedKey = currentDefaults.keys().get(0),	// 1st key
							newValue = "NEW-VAL" + i;
			currentInstance.put(changedKey, newValue);
			
			currentInstance.saveFile();

			assertEquals(currentDefaults.size(), currentInstance.size());
			assertFalse(currentInstance.get(changedKey).equals(currentDefaults.get(changedKey)));
			
			currentInstance.loadDefaults();
			
			assertEquals(currentDefaults.size(), currentInstance.size());
			assertEquals(currentDefaults.get(changedKey), currentInstance.get(changedKey));
			
			currentInstance.loadFile();
			
			assertEquals(currentDefaults.size(), currentInstance.size());
			assertFalse(currentInstance.get(changedKey).equals(currentDefaults.get(changedKey)));
		}
	}
	
	private static Properties createInstance(int i) {
		return new Properties(ALL_FILES[i], ALL_DEFAULTS[i]);
	}
	
	private static void buildFiles() {		
		for (int i = 0; i < NUM_FILES; i++) {
			ALL_FILES[i] = new File("TestPropFile" + i + ".txt");
		}
	}
	private static void buildDefaults() {
		for (int i = 0; i < ALL_DEFAULTS.length; i++) {
			Properties currentDefaults = new Properties();
			for (int j = 0; j < i + 1; j++) {
				currentDefaults.put("KEY" + j, "VAL" + j);
			}
			ALL_DEFAULTS[i] = currentDefaults;
		}
	}
}
