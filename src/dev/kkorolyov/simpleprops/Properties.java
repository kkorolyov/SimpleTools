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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * A collection of key-value pairs, comments, and blank lines which maintain original insertion order.
 */
public class Properties {
	private static final String PROPERTY_DELIMETER = "=",
															COMMENT_IDENTIFIER = "#";
	
	private final Map<String, String> props = new LinkedHashMap<>();
	private final Set<String> fillers = new HashSet<>();
	private Path file;
	private Properties defaults;
	
	/**
	 * Constructs a new {@code Properties} instance residing solely in memory.
	 */
	public Properties() {
		this(null);
	}
	/**
	 * Constructs a new {@code Properties} instance from a backing file.
	 * @see #Properties(Path, Properties)
	 */
	public Properties(Path file) {
		this(file, null);
	}
	/**
	 * Constructs a new {@code Properties} instance from both a backing file and default properties.
	 * @param file backing file on filesystem
	 * @param defaults default properties
	 * @throws UncheckedIOException if an I/O error occurs
	 */
	public Properties(Path file, Properties defaults) {
		setFile(file);
		setDefaults(defaults);
		
		try {
			reload();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Retrieves the value of the property identified by {@code key}.
	 * @param key identifier of property to retrieve
	 * @return property value, or {@code null} if no such property
	 */
	public String get(String key) {
		return props.get(key);
	}
	/**
	 * Retrieves a property value as an array.
	 * @param key identifier of property to retrieve
	 * @return property value parsed as an array, or {@code null} if no such property
	 */
	public String[] getArray(String key) {
		String value = get(key);
		return value == null ? null : value.replaceFirst("^\\[", "").replaceFirst("\\]$", "").split(",\\s*");	// Trim optional outer brackets and split on array delimiter
	}
	
	/** @return {@code true} if this object contains a property identified by {@code key} */
	public boolean contains(String key) {
		return props.containsKey(key);
	}
	
	/** @return all properties, in order */
	public Iterable<Entry<String, String>> properties() {
		return propertyList(false);
	}
	private List<Entry<String, String>> propertyList(boolean sort) {
		List<Entry<String, String>> list = props.entrySet().stream().filter(e -> !isFiller(e.getKey())).collect(Collectors.toList()); // Filters out comments, blank lines
		
		if (sort)
			list.sort((e1, e2) -> e1.getKey().compareTo(e2.getKey()));
		
		return list;
	}
	
	/** @return all property identifiers, in order */
	public Iterable<String> keys() {
		return props.keySet().stream().filter(k -> !isFiller(k)).collect(Collectors.toList());
	}
	
	/** @return all comments, in order */
	public Iterable<String> comments() {
		return props.values().stream().filter(v -> isComment(v)).collect(Collectors.toList());
	}
	
	/**
	 * Appends a new property or updates an existing property identified by {@code key}.
	 * @param key identifier of property to add or update
	 * @param value property value
	 * @return previous value associated with {@code key}, or {@code null} if no such value
	 */
	public String put(String key, String value) {
		return props.put(key, value);
	}
	/**
	 * Appends a new property or updates an existing property identified by {@code key} with multiple values.
	 * @param key identifier of property to add or update
	 * @param values property values
	 * @return previous value associated with {@code key}, or {@code null} if no such value
	 */
	public String put(String key, String... values) {
		return put(key, Arrays.toString(values));
	}
	
	/**
	 * Removes the property identified by {@code key}.
	 * @param key identifier of property to remove
	 * @return removed property's value, or {@code null} if no such property
	 */
	public String remove(String key) {
		return props.remove(key);
	}
	
	/**
	 * Appends a comment.
	 * @param comment comment to append, is ensured to begin with a comment identifier
	 */
	public void putComment(String comment) {
		put(isComment(comment) ? comment : (COMMENT_IDENTIFIER + comment));
	}
	private static boolean isComment(String value) {
		return value != null && value.length() > 0 && value.substring(0, 1).equals(COMMENT_IDENTIFIER);
	}
	
	/**
	 * Appends a blank line.
	 */
	public void putBlankLine() {
		put("");
	}
	
	private void put(String value) {
		String filler;
		while (fillers.contains((filler = UUID.randomUUID().toString())));	// Randomize until unique filler
		
		fillers.add(filler);
		put(filler, value);
	}
	private boolean isFiller(String key) {
		return fillers.contains(key);
	}
	
	/** @return {@code true} if this object contains zero properties */
	public boolean isEmpty() {
		return size() <= 0;
	}
	
	/** @return number of properties */
	public int size() {
		return propertyList(false).size();
	}
	
	/**
	 * Clears all properties and comments from memory.
	 */
	public void clear() {
		props.clear();
		fillers.clear();
	}
	
	/**
	 * Loads all backing file properties and remaining default properties not found in the backing file.
	 * If the backing file is {@code null} or does not exist, this method is equivalent to {@link #loadDefaults()}.
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
		
		try (BufferedReader fileReader = Files.newBufferedReader(file)) {
			String line;
			while ((line = format(fileReader.readLine())) != null) {
				String[] splitLine = line.split("\\s*" + PROPERTY_DELIMETER + "\\s*");	// Trim whitespace around delimiter
				
				if (splitLine.length < 1)
					putBlankLine();
				else if (splitLine.length < 2) {
					if (splitLine[0].length() < 1)
						putBlankLine();
					else
						putComment(splitLine[0]);
				}
				else
					put(splitLine[0], splitLine[1]);
			}
		}
	}
	
	/**
	 * Resets all properties to match defaults, if default properties are set.
	 * @return {@code true} if this instance has default properties and successfully loaded them
	 */
	public boolean loadDefaults() {
		if (defaults == null)
			return false;
		
		for (String key : keys()) {
			String defaultVal = defaults.get(key);
			
			if (defaultVal == null)
				remove(key);
			else
				put(key, defaultVal);
		}
		addRemainingDefaults();
		
		return true;
	}
	private void addRemainingDefaults() {
		if (defaults == null)
			return;
		
		for (String key : defaults.keys()) {
			if (!contains(key))
				put(key, defaults.get(key));
		}
	}
	
	/**
	 * Writes all current properties to the backing file.
	 * This method does not attempt to create the path to the backing file if it does not exist.
	 * @see #saveFile(boolean)
	 */
	public boolean saveFile() throws FileNotFoundException, IOException {
		return saveFile(false);
	}
	/**
	 * Writes all current properties to the backing file.
	 * If there are no new properties to write, this method does nothing.
	 * @param mkdirs if {@code true}, will create the path to the backing file if it does not exist
	 * @throws FileNotFoundException  if the backing file cannot be accessed for some reason
	 * @throws IOException if an I/O error occurs
	 */
	public boolean saveFile(boolean mkdirs) throws FileNotFoundException, IOException {
		if (file == null || toString().equals(buildFileProperties(file).toString()))
			return false;
		
		if (mkdirs) {
			Path parent = file.getParent();
			if (parent != null)
				Files.createDirectories(parent);
		}
		try (BufferedWriter out = Files.newBufferedWriter(file)) {
			for (Entry<String, String> prop : props.entrySet()) {
				out.write(format(toString(prop)));
				out.newLine();
			}
		}
		return true;
	}
	protected Properties buildFileProperties(Path file) {
		return new Properties(file);
	}
	
	protected String format(String string) {
		return string;
	}
	
	private String toString(Entry<String, String> prop) {
		return isFiller(prop.getKey()) ? prop.getValue() : prop.getKey() + PROPERTY_DELIMETER + prop.getValue();
	}
	
	/** @return path to backing file */
	public Path getFile() {
		return file;
	}
	/** @param newFile new path to backing file */
	public void setFile(Path newFile) {
		file = newFile;
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null || !(obj instanceof Properties))
			return false;
		
		Properties o = (Properties) obj;
		
		return Objects.equals(propertyList(true), o.propertyList(true));
	}
	@Override
	public int hashCode() {
		return Objects.hash(propertyList(true));
	}
	
	/**
	 * Returns a string containing all properties, comments, and blank lines in original insertion order.
	 * @return string consisting of properties and filler in original insertion order
	 */
	@Override
	public String toString() {
		StringBuilder toStringBuilder = new StringBuilder(getClass().getName() + "{");
		
		Iterator<Entry<String, String>> it = props.entrySet().iterator();
		while (it.hasNext()) {
			toStringBuilder.append(toString(it.next()));
			if (it.hasNext())
				toStringBuilder.append(", ");
		}
		return toStringBuilder.append("}").toString();
	}
}
