package dev.kkorolyov.simpleprops;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides access to and mutation of long-term properties, which are an indefinite set of key-value pairs.
 * Provides for saving and loading data to/from a file unique to the current instance.
 */
public class Properties {
	private static final Map<String, Properties> instances = new HashMap<>();
	
	private final String filename;
	private final Map<String, String> defaultProperties = new HashMap<>(),
																		properties = new HashMap<>();
	
	/**
	 * Returns a {@code Properties} instance of the specified name.
	 * If an appropriate instance does not yet exist, it is created using the specified filename and no default properties.
	 * @see #getInstance(String, Map)
	 */
	public static Properties getInstance(String filename) {
		return getInstance(filename, (Map<String, String>) null);
	}
	/**
	 * Functions similarly to {@link #getInstance(String, Map)}, but uses a 2-dimensional array instead of a {@code Map} to specify default properties.
	 */
	public static Properties getInstance(String filename, String[][] defaultProperties) {
		return getInstance(filename, convertArrayToMap(defaultProperties));
	}
	/**
	 * Returns a {@code Properties} instance for the specified filename.
	 * If an appropriate instance does not yet exist, it is created using the specified filename and default properties.
	 * @param filename label for the instance, as well as the name of the file written to when saved
	 * @param defaultProperties default properties of new instance; ignored if retrieving an existing instance
	 * @return appropriate instance
	 */
	public static Properties getInstance(String filename, Map<String, String> defaultProperties) {
		Properties instance = null;
		
		while ((instance = instances.get(filename)) == null)
			instances.put(filename, new Properties(filename, defaultProperties));
		
		return instance;
	}
	
	private static Map<String, String> convertArrayToMap(String[][] array) {
		if (array == null)
			return null;
		
		Map<String, String> convertedMap = new HashMap<>();
		
		for (String[] property : array) {
			if (array.length >= 2)
				convertedMap.put(property[0], property[1]);
		}
		return convertedMap;
	}
	
	private Properties(String filename, Map<String, String> defaultProperties) {
		this.filename = filename;
		
		setDefaultProperties(defaultProperties);
		loadDefaults();
		try {
			loadFromFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setDefaultProperties(Map<String, String> newDefaultProperties) {
		defaultProperties.clear();
		
		if (newDefaultProperties != null) {	// If null, no default properties
			for (String key : newDefaultProperties.keySet()) {
				defaultProperties.put(key, newDefaultProperties.get(key));
			}
		}
	}
	
	/**
	 * Resets all properties to default values.
	 */
	public void loadDefaults() {
		properties.clear();
		
		for (String key : defaultProperties.keySet()) {
			properties.put(key, defaultProperties.get(key));
		}
	}
	
	/**
	 * Retrieves the value of a property of the specified key.
	 * If this method is called on an instance which has no properties in memory, the instance will first attempt to load both its default properties and properties from its respective file.
	 * @param key key of property to retrieve
	 * @return property value
	 */
	public String getValue(String key) {
		reloadIfEmpty();
		
		return properties.get(key);
	}
	
	/**
	 * Returns the keys of all properties.
	 * If this method is called on an instance which has no properties in memory, the instance will first attempt to load both its default properties and properties from its respective file.
 	 * @return key of every property
 	 */
	public Set<String> getAllKeys() {
		reloadIfEmpty();
		
		return properties.keySet();
	}
	
	/** 
	 * Returns the total number of properties of this instance.
	 * If this method is called on an instance which has no properties in memory, the instance will first attempt to load both its default properties and properties from its respective file.
	 * @return number of properties
	 */
	public int size() {
		return getAllKeys().size();
	}
	
	/**
	 * Adds the specified property.
	 * If the key matches an existing property's key, then that preexisting property's value is overridden by the specified value instead.
	 * If this method is called on an instance which has no properties in memory, the instance will first attempt to load both its default properties and properties from its respective file.
	 * @param key key of property to add
	 * @param value value of property to add
	 */
	public void addProperty(String key, String value) {
		reloadIfEmpty();

		properties.put(key, value);
	}
	
	private void reloadIfEmpty() {
		if (properties.isEmpty()) {	// Properties were possibly cleared to free memory
			try {
				loadDefaults();
				loadFromFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Clears all properties from memory.
	 * Any properties not saved to disk are lost.
	 */
	public void clear() {
		properties.clear();
	}
	
	/**
	 * Loads all properties found in this instance's respective file.
	 * @throws IOException if an I/O error occurs
	 */
	public void loadFromFile() throws IOException {
		FileReader fileIn;
		try {
			fileIn = new FileReader(new File(filename));
		} catch (FileNotFoundException e) {
			saveToFile();
			fileIn = new FileReader(new File(filename));	// Should open now
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
	public void saveToFile() throws FileNotFoundException, IOException {
		try (	OutputStream fileOut = new FileOutputStream(new File(filename));
					PrintWriter filePrinter = new PrintWriter(fileOut)) {
			for (String key : properties.keySet()) {
				filePrinter.println(key + "=" + properties.get(key));
			}
		}
	}
	
	/** @return name of file attached to this instance */
	public String getFilename() {
		return filename;
	}
}
