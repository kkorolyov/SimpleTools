// Copyright (c) 2016, Kirill Korolyov
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer.
// 
// * Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
// 
// * Neither the name of SimpleProps nor the names of its
//   contributors may be used to endorse or promote products derived from
//   this software without specific prior written permission.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package dev.kkorolyov.simpleprops;

import java.io.*;
import java.util.*;

/**
 * Provides access to and mutation of long-term properties, which are an indefinite set of key-value pairs.
 * Provides for saving and loading data to/from a file.
 */
public class Properties {
	private final List<Property> props = new ArrayList<>();
	private final Map<String, Integer> keyPositions = new HashMap<>();
	private File file;
	private boolean mkdirs;
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
	 * @throws UncheckedIOException if an I/O error occurs
	 */
	public Properties(File file, Properties defaults, boolean mkdirs) {
		setFile(file, mkdirs);
		setDefaults(defaults);
		
		try {
			reload();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Retrieves the value of the property matching the specified key.
	 * @param key key of property to retrieve
	 * @return property value, or {@code null} if no such property
	 */
	public String get(String key) {
		return contains(key) ? props.get(keyPositions.get(key)).getValue() : null;
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
		props.get(keyPositions.get(key)).setValue(value);	// Change value at correct index
	}
	private void addNewKey(String key, String value) {
		Property newProperty = new Property(key, value);
		props.add(newProperty);
		
		if (newProperty.isProperty())
			keyPositions.put(key, props.size() - 1);	// New key is at last index
	}
	
	/**
	 * Adds a comment.
	 * Appends the comment marker to the beginning of the specified comment before adding.
	 * @param comment comment to add
	 */
	public void putComment(String comment) {
		Property newComment = new Property(comment);
		
		props.add(newComment);
	}
	
	/**
	 * Removes the property matching the specified key.
	 * @param key key of property to remove
	 * @return removed property's value, or {@code null} if no such property
	 */
	public String remove(String key) {
		String removedValue = null;
		
		if (keyPositions.containsKey(key)) {
			int removeIndex = keyPositions.remove(key);
			
			for (int i = removeIndex + 1; i < props.size(); i++) {	// Shift keyPositions mappings to match resultant props
				String currentRemoveKey = props.get(i).getKey();
				
				if (keyPositions.containsKey(currentRemoveKey))
					keyPositions.put(props.get(i).getKey(), i - 1);
			}
			removedValue = props.remove(removeIndex).getValue();
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
		
		for (Property prop : props) {
			if (prop.isProperty())
				toReturn.add(prop.getKey());
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
		props.clear();
		keyPositions.clear();
	}
	
	/** @return {@code true} if this object has a backing file and matches it exactly */
	public boolean matchesFile() {
		boolean matches = false;
		
		if (file != null)
			matches = this.equals(buildFileProperties(file));
		
		return matches;
	}
	Properties buildFileProperties(File file) {
		return new Properties(file);
	}
	
	/**
	 * Loads all backing file properties and remaining default properties not found in the backing file.
	 * If the backing file is {@code null} or does not exist, this method does nothing.
	 * @throws IOException if an I/O error occurs
	 */
	public void reload() throws IOException {
		clear();
		
		loadFile();
		addRemainingDefaults();
	}
	private void loadFile() throws IOException {
		if (file == null)
			return;	// No file, no load
		
		FileReader fileIn;
		try {
			fileIn = new FileReader(file);
		} catch (FileNotFoundException e) {
			return;	// Nothing to load
		}
		try (BufferedReader fileReader = new BufferedReader(fileIn)) {
			String readBlock = format(readToBlock(fileReader));
			String[] splitBlock = readBlock.split(System.lineSeparator());
			
			for (String nextLine : splitBlock) {				
				String[] splitLine = nextLine.split(Property.DELIMETER);
				String 	currentKey = splitLine.length < 1 ? Property.EMPTY : splitLine[0],
								currentValue = splitLine.length < 2 ? Property.EMPTY : splitLine[1];
				
				put(currentKey, currentValue);
			}
		}
	}
	private void addRemainingDefaults() {
		if (defaults == null)
			return;
		
		for (String key : defaults.keys()) {
			if (!contains(key))
				put(key, defaults.get(key));
		}
	}
	
	private static String readToBlock(BufferedReader reader) throws IOException {
		List<Byte> byteList = new LinkedList<>();
		
		int nextInt;
		while ((nextInt = reader.read()) >= 0)
			byteList.add((byte) nextInt);
		
		byte[] bytes = new byte[byteList.size()];
		int counter = 0;
		for (Byte listByte : byteList) {
			bytes[counter++] = listByte;
		}
		return new String(bytes);
	}
	
	/**
	 * Resets all properties to default values and removes all properties not found in default values.
	 * If this object does not have specified default values, this method does nothing.
	 */
	public void loadDefaults() {
		if (defaults == null)
			return;
		
		for (String key : keys()) {
			if (defaults.contains(key))
				put(key, defaults.get(key));
			else
				remove(key);
		}
		addRemainingDefaults();
	}
	
	/**
	 * Writes all current properties to the backing file.
	 * If there are no new properties to write, this method does nothing.
	 * @throws IOException if an I/O error occurs
	 * @throws FileNotFoundException  if the backing file cannot be accessed for some reason
	 */
	public void saveFile() throws FileNotFoundException, IOException {
		if (matchesFile())
			return;
		
		if (mkdirs)
			tryMkdirs();
		
		try (	OutputStream fileOut = new FileOutputStream(file);
					PrintWriter filePrinter = new PrintWriter(fileOut)) {
			filePrinter.print(format(toString()));
		}
	}
	
	String format(String line) {	// Used to optionally format read/written data
		return line;
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
		this.mkdirs = mkdirs;
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
		
		result = prime * result + (props == null ? 0 : props.hashCode());
		result = prime * result + (keyPositions == null ? 0 : keyPositions.hashCode());
		result = prime * result + (file == null ? 0 : file.hashCode());
		
		return result;
	}
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (!(obj instanceof Properties))
			return false;
		
		Properties other = (Properties) obj;
		
		if (props == null) {
			if (other.props != null)
				return false;
		} else if (!props.equals(other.props))
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
		
		return true;
	}
	
	/**
	 * Returns a formatted string of current properties and comments as they would be printed to a file.
	 */
	@Override
	public String toString() {
		StringBuilder toStringBuilder = new StringBuilder();
		
		for (Property prop : props)
			toStringBuilder.append(prop).append(System.lineSeparator());
		
		return toStringBuilder.toString();
	}
	
	private static class Property {
		private static final char COMMENT = '#';
		private static final String DELIMETER = "=",
																EMPTY = "";
		
		private String	key,
										value;
		
		Property(String comment) {	// Creates a comment property
			this(COMMENT + comment, null);
		}
		Property(String key, String value) {	// Creates a normal property
			setKey(key);
			setValue(value);
		}
		
		boolean isProperty() {
			return !isBlankLine() && !isComment();
		}
		boolean isBlankLine() {
			return key.length() <= 0;
		}
		boolean isComment() {
			return key.charAt(0) == COMMENT;	// Key starts with comment
		}
		
		String getKey() {
			return key;
		}
		void setKey(String newKey) {
			key = (newKey == null ? EMPTY : newKey);
		}
		
		String getValue() {
			return value;
		}
		void setValue(String newValue) {
			value = (newValue == null ? EMPTY : newValue);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			
			result = prime * result + (key == null ? 0 : key.hashCode());
			result = prime * result + (value == null ? 0 : value.hashCode());
			
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
			
			Property other = (Property) obj;
			
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
				
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			
			return true;
		}
		
		@Override
		public String toString() {
			return isProperty() ? key + DELIMETER + value : key;
		}
	}
}
