package dev.kkorolyov.simpleprops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import dev.kkorolyov.simpleprops.Properties;

@SuppressWarnings("javadoc")
public class PropertiesTest {	// TODO Finish
	private static final String TEST_FILENAME = "TestProps.txt";
	
	@Before
	public void setUpBefore() throws IOException {	// Erases all defaults
		Properties.setFileName(TEST_FILENAME);
		Properties.setDefaults(new String[][]{{}});
		Properties.init();
	}
	@AfterClass
	public static void cleanUpAfterClass() {
		if (new File(TEST_FILENAME).delete())
			System.out.println("Cleaned up successfully");
	}
	
	@Test
	public void testInit() throws IOException {
		Properties.init();
	}

	@Test
	public void testLoadDefaults() {
		int numPreDefault = 10, numPostDefaultArray = 5, numPostDefaultMap = 3;
		
		for (String[] property : buildProperties(numPreDefault)) {
			Properties.addProperty(property[0], property[1]);	// Expected to be erased
		}
		assertEquals(numPreDefault, Properties.getAllKeys().size());
		
		String[][] testDefaults = buildProperties(numPostDefaultArray);
		
		Properties.setDefaults(testDefaults);
		Properties.loadDefaults();
		
		assertEquals(numPostDefaultArray, Properties.getAllKeys().size());
		for (String[] property : testDefaults) {
			assertEquals(property[1], Properties.getValue(property[0]));
		}
		
		Map<String, String> testDefaultsMap = buildPropertiesMap(numPostDefaultMap);
		
		Properties.setDefaults(testDefaultsMap);
		Properties.loadDefaults();
		
		assertEquals(numPostDefaultMap, Properties.getAllKeys().size());
		for (String key : testDefaultsMap.keySet()) {
			assertEquals(testDefaultsMap.get(key), Properties.getValue(key));
		}
	}

	@Test
	public void testSetFileName() throws FileNotFoundException, IOException {
		String testFileName = "NewTestProps.txt";
		
		Properties.setFileName(testFileName);
		Properties.saveToFile();
		
		assertTrue(new File(testFileName).delete());	// Testing for creation of new file by Properties
	}

	@Test
	public void testLoadFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveToFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetValue() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllKeys() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testClear() {
		fail("Not yet implemented");
	}

	private static String[][] buildProperties(int numProperties) {
		String[][] properties = new String[numProperties][2];
		
		for (int i = 0; i < properties.length; i++) {
			properties[i][0] = "ARRAY-KEY" + i;
			properties[i][1] = "ARRAY-VAL" + i;
		}
		return properties;
	}
	private static Map<String, String> buildPropertiesMap(int numProperties) {
		Map<String, String> properties = new HashMap<>();
		
		for (int i = 0; i < numProperties; i++) {
			properties.put("MAP-KEY" + i, "MAP-VAL" + i);
		}
		return properties;
	}
}
