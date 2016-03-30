package dev.kkorolyov.simpleprops;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A property file implementation accessible through static calls.
 */
public class Properties {	
	private static final Map<String, String> defaultProperties = new HashMap<>();
	
	private static String fileName = "SimpleProps.txt";
	private static Map<String, String> properties = new HashMap<>();
	
	/**
	 * Initializes all necessary properties in this class.
	 * <ol>
	 * <li>Default, hardcoded properties are loaded.</li>
	 * <li>The properties file is read, all current properties are updated with those read from the file.</li>
	 * </ol>
	 * @throws IOException if an I/O error occurs
	 */
	public static void init() throws IOException {
		loadDefaults();
		loadFile();
	}
	
	/**
	 * Clears all current properties and loads the hard-coded defaults.
	 */
	public static void loadDefaults() {
		properties.clear();
		
		for (String key : defaultProperties.keySet()) {
			properties.put(key, defaultProperties.get(key));
		}
	}
	
	/**
	 * Sets the name of the file to which properties will be saved.
	 * The default name is {@code SimpleProps.txt}.
	 * @param newFileName new file name to write to
	 */
	public static void setFileName(String newFileName) {
		fileName = newFileName;
	}
	/**
	 * Overwrites the default properties with the specified values.
	 * The specified array is expected to be an array of 2-element {@code String} arrays, where the String at {@code newDefaults[0]} is the key of a property, and the String at {@code newDefaults[1]} is the corresponding value.
	 * @param newDefaults 2-Dimensional array of key-value pairs
	 */
	public static void setDefaults(String[][] newDefaults) {
		defaultProperties.clear();
		
		for (String[] property : newDefaults) {
			if (newDefaults.length >= 2)
				defaultProperties.put(property[0], property[1]);
		}
	}
	/**
	 * Overwrites the default properties using the specified map
	 * @param newDefaults map of new default properties
	 */
	public static void setDefaults(Map<String, String> newDefaults) {
		defaultProperties.clear();
		
		for (String key : newDefaults.keySet()) {
			defaultProperties.put(key, newDefaults.get(key));
		}
	}
	
	/**
	 * Loads all properties specified in the properties file
	 * @throws IOException if an I/O error occurs
	 */
	public static void loadFile() throws IOException {
		FileReader fileIn;
		try {
			fileIn = new FileReader(new File(fileName));
		} catch (FileNotFoundException e) {
			saveToFile();
			fileIn = new FileReader(new File(fileName));	// Should open now
		}
		try (BufferedReader fileReader = new BufferedReader(fileIn)) {
		
			String nextLine;
			while ((nextLine = fileReader.readLine()) != null) {
				String[] currentKeyValue = nextLine.split("=");	// Line should be "<KEY>=<VALUE>";
				String currentKey = "", currentValue = "";
				
				if (currentKeyValue.length > 0) {
					currentKey = currentKeyValue[0].trim();
					if (currentKeyValue.length > 1) {
						currentValue = currentKeyValue[1].trim();
					}
				}
				properties.put(currentKey, currentValue);
			}
		}
	}
	
	/**
	 * Writes all properties currently loaded in memory to the properties file.
	 * @throws IOException if an I/O error occurs
	 * @throws FileNotFoundException  if the properties file cannot be accessed for some reason
	 */
	public static void saveToFile() throws FileNotFoundException, IOException {
		try (	OutputStream fileOut = new FileOutputStream(new File(fileName));
					PrintWriter filePrinter = new PrintWriter(fileOut)) {
			for (String key : properties.keySet()) {
				filePrinter.println(key + "=" + properties.get(key));
			}
		}
	}
	
	/**
	 * Retrieves the value of a property of the specified key.
	 * @param key key of property to retrieve
	 * @return property value
	 */
	public static String getValue(String key) {
		checkInit();
		
		return properties.get(key);
	}
	
	/** @return key of every property */
	public static Set<String> getAllKeys() {
		checkInit();
		
		return properties.keySet();
	}
	
	/**
	 * Adds the specified property.
	 * If the key matches an existing property's key, then that preexisting property's value is overridden by the specified value.
	 * @param key key of property to add
	 * @param value value of property to add
	 */
	public static void addProperty(String key, String value) {
		checkInit();

		properties.put(key, value);
	}
	
	/**
	 * Clears all properties from memory.
	 * Any properties not saved to disk are lost.
	 */
	public static void clear() {
		properties.clear();
	}
	
	private static void checkInit() {
		if (properties.isEmpty()) {
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
