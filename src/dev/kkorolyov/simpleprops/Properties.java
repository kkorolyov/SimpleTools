// Copyright (c) 2017, Kirill Korolyov
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
public final class Properties {
	private static final String PROPERTY_DELIMETER = "=",
															COMMENT_IDENTIFIER = "#";
	
	private final Map<String, String> props = new LinkedHashMap<>();
	private final Set<String> fillers = new HashSet<>();
	
	/**
	 * Constructs a new, empty collection of properties.
	 */
	public Properties() {
		// Nothing special
	}
	/**
	 * Constructs a collection of properties initialized to match {@code defaults}.
	 * @param defaults initial properties
	 */
	public Properties(Properties defaults) {
		put(defaults, true);
	}
	/**
	 * Constructs a collection of properties by parsing a properties file.
	 * @param defaults path to file containing initial properties
	 * @throws IOException if an I/O error occurs
	 */
	public Properties(Path defaults) throws IOException {
		load(defaults);
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
		return value == null ? null : value.replaceFirst("^\\[", "").replaceFirst("\\]$", "").split(",\\s?");	// Trim optional outer brackets and split on array delimiter
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
	 * Copies all properties from another {@code Properties} instance to this instance.
	 * If this instance is completely empty (no properties nor filler), all values from {@code other} are copied to this instance.
	 * @param other instance providing properties
	 * @param overwrite if {@code true}, properties found both in this instance and in {@code other}, will have their values overwritten by those in {@code other}
	 * @return number of appended and overwritten properties
	 */
	public int put(Properties other, boolean overwrite) {
		if (isEmpty()) {
			props.putAll(other.props);
			fillers.addAll(other.fillers);
			
			return size();
		}
		int counter = 0;
		
		for (Entry<String, String> prop : other.properties()) {
			if (overwrite || !contains(prop.getKey())) {
				put(prop.getKey(), prop.getValue());
				counter++;
			}
		}
		return counter;
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
	 * Parses properties and filler from a file and applies them to this instance.
	 * @param file path to properties file to read
	 * @throws IOException if an I/O error occurs
	 */
	public void load(Path file) throws IOException {
		try (BufferedReader in = Files.newBufferedReader(file)) {
			String line;
			while ((line = in.readLine()) != null) {
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
	 * Writes all properties and filler to a file.
	 * @param file path to properties file to write, created if it does not exist
	 * @throws IOException if an I/O error occurs
	 */
	public void save(Path file) throws IOException {
		Path parent = file.getParent();
		if (parent != null)
			Files.createDirectories(parent);
		
		try (BufferedWriter out = Files.newBufferedWriter(file)) {
			for (Entry<String, String> prop : props.entrySet()) {
				out.write(toString(prop));
				out.newLine();
			}
		}
	}
	
	/**
	 * Checks if two properties have identical contents in identical order.
	 * @param other properties to check with
	 * @return {@code true} if both properties are identical
	 */
	public boolean identical(Properties other) {
		return this == other || toString().equals(other.toString());
	}
	
	/**
	 * Compares this object to another for equality. To be equal, {@code obj} must be an instance of {@code Properties} and have a set of properties identical to this instance's set.
	 * @return {@code true} if {@code obj} is a {@code Properties} instance and has identical properties to this instance, regardless of order
	 */
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
		StringBuilder toStringBuilder = new StringBuilder(getClass().getName() + " {");
		
		Iterator<Entry<String, String>> it = props.entrySet().iterator();
		while (it.hasNext()) {
			toStringBuilder.append(toString(it.next()));
			if (it.hasNext())
				toStringBuilder.append(", ");
		}
		return toStringBuilder.append("}").toString();
	}
	private String toString(Entry<String, String> property) {
		return isFiller(property.getKey()) ? property.getValue() : property.getKey() + PROPERTY_DELIMETER + property.getValue();
	}
}
