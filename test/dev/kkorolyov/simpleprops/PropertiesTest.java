package dev.kkorolyov.simpleprops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class PropertiesTest {	// TODO Finish
	private static final int NUM_FILES = 5;
	private static final String[] ALL_FILENAMES = new String[NUM_FILES];
	@SuppressWarnings("unchecked")
	private static final Map<String, String>[] ALL_DEFAULT_PROPERTIES = new HashMap[NUM_FILES];
	private static final String[][][] ALL_DEFAULT_PROPERTIES_ARRAY = new String[NUM_FILES][][];
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		buildFilenames();
		buildPropertiesCollection();
		
		for (int i = 0; i < NUM_FILES; i++) {
			if (i % 2 == 0)
				Properties.getInstance(ALL_FILENAMES[i], ALL_DEFAULT_PROPERTIES[i]);
			else
				Properties.getInstance(ALL_FILENAMES[i], ALL_DEFAULT_PROPERTIES_ARRAY[i]);
		}
	}
	@Before
	public void setUpBefore() throws IOException {
		for (String filename : ALL_FILENAMES)
			Properties.getInstance(filename).loadDefaults();
	}
	
	@After
	public void tearDownAfter() throws IOException {
		for (String filename : ALL_FILENAMES)
			new File(filename).delete();
	}
	
	@Test
	public void testLoadDefaults() throws IOException {
		for (int i = 0; i < NUM_FILES; i++) {
			String currentFilename = ALL_FILENAMES[i];
			Map<String, String> currentDefaults = ALL_DEFAULT_PROPERTIES[i];
			Properties currentInstance = Properties.getInstance(currentFilename);
						
			assertEquals(currentDefaults.size(), currentInstance.size());
			
			for (String key : currentDefaults.keySet()) {
				assertEquals(currentDefaults.get(key), currentInstance.getValue(key));
			}
		}
	}

	@Test
	public void testAddProperty() throws IOException {
		for (int i = 0; i < NUM_FILES; i++) {
			String currentFilename = ALL_FILENAMES[i];
			Map<String, String> currentDefaults = ALL_DEFAULT_PROPERTIES[i];
			Properties currentInstance = Properties.getInstance(currentFilename);
			
			String 	newKey = "NEW-KEY" + i,
							newValue = "NEW-VAL" + i;
			currentInstance.addProperty(newKey, newValue);
			
			assertTrue(currentInstance.size() == currentDefaults.size() + 1);
			assertEquals(newValue, currentInstance.getValue(newKey));
		}
	}

	@Test
	public void testClear() throws IOException {
		for (int i = 0; i < NUM_FILES; i++) {
			String currentFilename = ALL_FILENAMES[i];
			Map<String, String> currentDefaults = ALL_DEFAULT_PROPERTIES[i];
			Properties currentInstance = Properties.getInstance(currentFilename, currentDefaults);	// Test newDefaults ignoring
			
			currentInstance.addProperty("Extra", "Extra");
			
			assertTrue(currentInstance.size() == currentDefaults.size() + 1);

			currentInstance.clear();
			
			assertEquals(currentDefaults.size(), currentInstance.size());
		}
	}

	@Test
	public void testSaveToFile() throws IOException {
		for (int i = 0; i < NUM_FILES; i++) {
			String currentFilename = ALL_FILENAMES[i];
			Map<String, String> currentDefaults = ALL_DEFAULT_PROPERTIES[i];
			Properties currentInstance = Properties.getInstance(currentFilename);	// Test newDefaults ignoring
			
			String 	changedKey = currentDefaults.keySet().iterator().next(),	// 1st key
							newValue = "NEW-VAL" + i;
			currentInstance.addProperty(changedKey, newValue);
			
			currentInstance.saveToFile();

			assertEquals(currentDefaults.size(), currentInstance.size());
			assertTrue(!currentInstance.getValue(changedKey).equals(currentDefaults.get(changedKey)));
			
			currentInstance.loadDefaults();
			
			assertEquals(currentDefaults.size(), currentInstance.size());
			assertEquals(currentDefaults.get(changedKey), currentInstance.getValue(changedKey));
			
			currentInstance.loadFromFile();
			
			assertEquals(currentDefaults.size(), currentInstance.size());
			assertTrue(!currentInstance.getValue(changedKey).equals(currentDefaults.get(changedKey)));
		}
	}
	
	private static void buildFilenames() {		
		for (int i = 0; i < NUM_FILES; i++) {
			ALL_FILENAMES[i] = "TestPropFile" + i + ".txt";
		}
	}
	private static void buildPropertiesCollection() {
		for (int i = 0; i < ALL_DEFAULT_PROPERTIES.length; i++) {
			ALL_DEFAULT_PROPERTIES[i] = buildPropertiesMap(i + 1);
			
			ALL_DEFAULT_PROPERTIES_ARRAY[i] = buildProperties(i + 1);
		}
	}

	private static String[][] buildProperties(int numProperties) {
		String[][] properties = new String[numProperties][2];
		
		for (int i = 0; i < properties.length; i++) {
			properties[i][0] = "KEY" + i;
			properties[i][1] = "VAL" + i;
		}
		return properties;
	}
	private static Map<String, String> buildPropertiesMap(int numProperties) {
		Map<String, String> properties = new HashMap<>();
		
		for (int i = 0; i < numProperties; i++) {
			properties.put("KEY" + i, "VAL" + i);
		}
		return properties;
	}
}
