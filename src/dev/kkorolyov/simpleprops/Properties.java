package dev.kkorolyov.simpleprops;

import java.io.*;
import java.util.*;

/**
 * Provides access to and mutation of long-term properties, which are an indefinite set of key-value pairs.
 * Provides for saving and loading data to/from a file.
 */
public class Properties {
	private static final String DELIMETER = "=",
															COMMENT = "#";
	
	private final List<String>	keys = new ArrayList<>(),
															values = new ArrayList<>();
	private final Map<String, Integer> keyPositions = new HashMap<>();
	private File file;
	private Properties defaults;
	
	/**
	 * Constructs a new {@code Properties} instance with no backing file.
	 */
	public Properties() {
		this(null);
	}
	/**
	 * Constructs a new {@code Properties} instance for a specified file.
	 * @see #Properties(File, Properties, boolean)
	 */
	public Properties(File file) {
		this(file, null);
	}
	/**
	 * Constructs a new {@code Properties} instance for a specified file and with specified default values.
	 * @see #Properties(File, Properties, boolean)
	 */
	public Properties(File file, Properties defaults) {
		this(file, defaults, false);
	}
	/**
	 * Constructs a new {@code Properties} instance for a specified file and with specified default values.
	 * This method may optionally create the path to the specified file.
	 * @param file backing filesystem file
	 * @param defaults default properties
	 * @param mkdirs if {@code true}, the path to the specified file is created if it does not exist
	 */
	public Properties(File file, Properties defaults, boolean mkdirs) {
		setFile(file, mkdirs);
		setDefaults(defaults);
		loadDefaults();
		try {
			loadFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the value of the property matching the specified key.
	 * @param key key of property to retrieve
	 * @return property value, or {@code null} if no such property
	 */
	public String get(String key) {
		return contains(key) ? values.get(keyPositions.get(key)) : null;
	}
	
	/**
	 * Adds a property matching the specified key-value pair.
	 * If the specified key matches an existing property's key, the preexisting key's value is overridden by the specified value.
	 * @param key key of property to add
	 * @param value value of property to add
	 */
	public void put(String key, String value) {
		if (keyPositions.containsKey(key))
			setKey(key, value);
		else
			addNewKey(key, value);
	}
	private void setKey(String key, String value) {
		values.set(keyPositions.get(key), value);	// Change value at correct index
	}
	private void addNewKey(String key, String value) {
		keys.add(key);
		values.add(value);
		
		if (key != null)
			keyPositions.put(key, keys.size() - 1);	// New key is at last index
	}
	
	/**
	 * Removes the property matching the specified key.
	 * @param key key of property to remove
	 * @return removed property's value, or {@code null} if no such property
	 */
	public String remove(String key) {
		String removedValue = null;
		
		if (keyPositions.containsKey(key)) {
			int propertyIndex = keyPositions.remove(key);
			
			keys.remove(propertyIndex);
			removedValue = values.remove(propertyIndex);
		}
		
		return removedValue;
	}
	
	/** @return {@code true} if this object contains a property matching the specified key */
	public boolean contains(String key) {
		return keyPositions.containsKey(key);
	}
	
	/** @return list of all property keys, in the order they appear in this object */
	public List<String> keys() {
		List<String> toReturn = new LinkedList<>();
		
		for (String key : keys) {
			if (key != null)
				toReturn.add(key);
		}
		return toReturn;
	}
	
	/** @return {@code true} if this object contains no properties */
	public boolean isEmpty() {
		return size() <= 0;
	}
	
	/** @return number of properties */
	public int size() {
		return keys().size();
	}
	
	/**
	 * Clears all properties from memory.
	 */
	public void clear() {
		keys.clear();
		values.clear();
		keyPositions.clear();
	}
	
	/** @return {@code true} if this object has a backing file and matches it exactly */
	public boolean matchesFile() {
		boolean matches = false;
		
		if (file != null)
			matches = this.equals(new Properties(file, defaults));
		
		return matches;
	}
	
	/**
	 * Resets all properties to default values.
	 * If this object does not have specified default values, clears all properties instead.
	 */
	public void loadDefaults() {
		clear();
		
		if (defaults != null) {
			for (String key : defaults.keys())
				put(key, defaults.get(key));
		}
	}
	
	/**
	 * Loads all properties found in this object's backing file.
	 * If this object does not have a backing file, this method does nothing.
	 * @throws IOException if an I/O error occurs
	 */
	public void loadFile() throws IOException {
		if (file == null)
			return;	// No file, no load
		
		FileReader fileIn;
		try {
			fileIn = new FileReader(file);
		} catch (FileNotFoundException e) {
			saveFile(true);	// Create file
			fileIn = new FileReader(file);	// Open again
		}
		try (BufferedReader fileReader = new BufferedReader(fileIn)) {
		
			String nextLine;
			while ((nextLine = fileReader.readLine()) != null) {
				String[] currentKeyValue = nextLine.split(DELIMETER);
				String 	currentKey = null,
								currentValue = null;
				
				if (currentKeyValue.length > 0 && currentKeyValue[0].length() > 0) {
					currentKey = currentKeyValue[0].trim();
					if (currentKeyValue.length > 1) {
						currentValue = currentKeyValue[1].trim();
					}
				}
				put(currentKey, currentValue);
			}
		}
	}
	/**
	 * Writes all current properties to the backing file.
	 * If there are no new properties to write, this method does nothing.
	 * @throws IOException if an I/O error occurs
	 * @throws FileNotFoundException  if the backing file cannot be accessed for some reason
	 */
	public void saveFile() throws FileNotFoundException, IOException {
		saveFile(false);
	}
	private void saveFile(boolean force) throws FileNotFoundException, IOException {
		if (!force && matchesFile())
			return;
		
		try (	OutputStream fileOut = new FileOutputStream(file);
					PrintWriter filePrinter = new PrintWriter(fileOut)) {
			for (String key : keys) {
				filePrinter.print(key);
				
				if (!key.contains(COMMENT))	// If the current key is not a comment
					filePrinter.print(DELIMETER + get(key));
				
				filePrinter.println();
			}
		}
	}
	
	/** @return backing file */
	public File getFile() {
		return file;
	}
	/**
	 * @param newFile new backing file
	 * @param mkdirs if {@code true}, the path to the specified file is created if it does not exist
	 */
	public void setFile(File newFile, boolean mkdirs) {
		file = newFile;
		if (mkdirs)
			tryMkdirs();
	}
	private void tryMkdirs() {
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
	}
	
	/** @return default properties */
	public Properties getDefaults() {
		return defaults;
	}
	/** @param newDefaults new default properties */
	public void setDefaults(Properties newDefaults) {		
		defaults = newDefaults;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + (keys == null ? 0 : keys.hashCode());
		result = prime * result + (values == null ? 0 : values.hashCode());
		result = prime * result + (keyPositions == null ? 0 : keyPositions.hashCode());
		result = prime * result + (file == null ? 0 : file.hashCode());
		result = prime * result + (defaults == null ? 0 : defaults.hashCode());
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Properties other = (Properties) obj;
		
		if (keys == null) {
			if (other.keys != null)
				return false;
		} else if (!keys.equals(other.keys))
			return false;
		
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		
		if (keyPositions == null) {
			if (other.keyPositions != null)
				return false;
		} else if (!keyPositions.equals(other.keyPositions))
			return false;
		
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		
		if (defaults == null) {
			if (other.defaults != null)
				return false;
		} else if (!defaults.equals(other.defaults))
			return false;
		
		return true;
	}
}
