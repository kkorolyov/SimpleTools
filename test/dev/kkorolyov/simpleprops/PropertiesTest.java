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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class PropertiesTest {
	private static final int ITERATIONS = Byte.MAX_VALUE;
	private static final String COMMENT = "#";
	private static final String BASE_FILENAME = "TestPropFile";
	private static int testFilesCounter = 0;
	private static final Random rand = new Random();
	
	@Test
	public void testPut_Contains_Get_Remove() throws IOException {
		Properties props = new Properties();
		
		assertEquals(0, props.size());
		
		String 	keyBase = "PutKey",
						valueBase = "PutValue";
		
		for (int i = 0; i < ITERATIONS; i++) {
			String 	key = keyBase + i,
							value = valueBase + i;
			props.put(key, value);
			
			assertEquals(i + 1, props.size());
			
			for (int j = i; j >= 0; j--) {
				assertTrue(props.contains(key));
				assertEquals(value, props.get(key));
			}
		}
		for (int i = props.size() - 1; i >= 0; i--) {
			assertEquals(valueBase + i, props.remove(keyBase + i));
			assertEquals(i, props.size());
		}
	}
	
	@Test
	public void testGetArray() throws IOException {
		File file = buildFile();
		Properties props = new Properties(file);
		
		assertEquals(0, props.size());
		
		String key = "PutArrayKey";
		String[] value = {"A", "   BB", "C CC"};
		
		props.put(key, value);
		
		assertEquals(Arrays.toString(value), props.get(key));
		assertArrayEquals(value, props.getArray(key));
		
		props.saveFile();
		props.reload();
		
		assertEquals(Arrays.toString(value), props.get(key));
		assertArrayEquals(value, props.getArray(key));
	}
	
	@Test
	public void testPutComment() {
		Properties props = new Properties();
		StringBuilder expectedCommentsBuilder = new StringBuilder();

		String commentBase = "PutCommentComment";
		
		for (int i = 0; i < ITERATIONS; i++) {
			String comment = commentBase + i;
			
			props.putComment(comment);
			expectedCommentsBuilder.append(COMMENT).append(comment).append(System.lineSeparator());
			
			assertEquals(0, props.size());
			assertTrue(props.isEmpty());
		}
		assertEquals(expectedCommentsBuilder.toString(), props.toString());
	}
	
	@Test
	public void testKeys() {
		Properties props = new Properties();
		List<String> expectedKeys = new LinkedList<>();
		
		String 	keyBase = "KeysKey",
						valueBase = "KeysValue",
						commentBase = "KeysComment";
		
		for (int i = 0; i < ITERATIONS; i++) {
			String 	key = keyBase + i,
							value = valueBase + i,
							comment = commentBase;
			
			props.put(key, value);
			props.putComment(comment);
			
			expectedKeys.add(key);
		}
		String[] 	expectedKeysArray = expectedKeys.toArray(new String[expectedKeys.size()]),
							actualKeysArray = props.keys().toArray(new String[props.keys().size()]);
		assertArrayEquals(expectedKeysArray, actualKeysArray);
	}
	
	@Test
	public void testClear_Size_IsEmpty() {
		Properties props = new Properties();
		
		String 	keyBase = "ClearKey",
						valueBase = "ClearValue",
						commentBase = "ClearComment";
		
		for (int i = 0; i < ITERATIONS; i++) {
			String 	key = keyBase + i,
							value = valueBase + i,
							comment = commentBase + i;
			
			props.put(key, value);
			props.putComment(comment);
			
			assertEquals(i + 1, props.size());
			assertFalse(props.isEmpty());
		}
		props.clear();
		assertEquals(0, props.size());
		assertTrue(props.isEmpty());
	}
	
	@Test
	public void testMatchesFile() throws FileNotFoundException, IOException {
		File file = buildFile();
		Properties props = new Properties(file);
		
		props.saveFile();
		assertTrue(props.matchesFile());
		
		String	key = "MatchesFileKey",
						value = "MatchesFileValue",
						comment = "MatchesFileComment";
		
		props.put(key, value);
		assertFalse(props.matchesFile());
		
		props.clear();
		assertTrue(props.matchesFile());
		
		props.putComment(comment);
		assertFalse(props.matchesFile());
		
		props.clear();
		assertTrue(props.matchesFile());
		
		props.put(key, value);
		props.putComment(comment);
		props.saveFile();
		assertTrue(props.matchesFile());
		
		props.clear();
		assertFalse(props.matchesFile());
	}
	
	@Test
	public void testSaveFileMkdirs() throws IOException {	// TODO Meh
		File 	fileDir = new File("testDir/"),
					file = new File("testDir/testFileInDir");
		fileDir.deleteOnExit();
		file.deleteOnExit();
		
		Properties props = new Properties(file);
		
		props.putComment("Comment");
		try {
			props.saveFile();
			
			fail("Failed to throw FileNotFoundException");
		} catch (FileNotFoundException e) {
			// Success
		}
		try {
			props.saveFile(true);
		} catch (FileNotFoundException e) {
			fail("Failed to mkdirs");
		}
	}
	
	@Test
	public void testReload() throws IOException {
		File file = buildFile();
		Properties 	defaults = buildDefaults(1),
								props = new Properties(file, defaults);
		
		String 	key = defaults.keys().iterator().next(),
						value = "ReloadNewValue",
						comment = "ReloadComment";
		
		String 	expectedDefaultValue = defaults.get(key),
						expectedNewValue = value;
		
		assertEquals(defaults.size(), props.size());
		assertEquals(expectedDefaultValue, props.get(key));
		
		props.put(key, value);
		props.putComment(comment);
		assertEquals(defaults.size(), props.size());
		assertFalse(props.get(key).equals(expectedDefaultValue));
		assertEquals(expectedNewValue, props.get(key));
		
		props.reload();
		assertEquals(defaults.size(), props.size());
		assertFalse(props.get(key).equals(expectedNewValue));
		assertEquals(expectedDefaultValue, props.get(key));
		
		props.put(key, value);
		props.putComment(comment);
		props.saveFile();
		props.reload();
		assertEquals(defaults.size(), props.size());
		assertFalse(props.get(key).equals(expectedDefaultValue));
		assertEquals(expectedNewValue, props.get(key));
	}
	
	@Test
	public void testLoadDefaults() throws IOException {
		Properties 	defaults = buildDefaults(),
								props = new Properties();
		
		props.setDefaults(defaults);
		props.loadDefaults();
		
		assertEquals(defaults.size(), props.size());
		assertEquals(defaults.keys(), props.keys());
		for (String key : defaults.keys())
			assertEquals(defaults.get(key), props.get(key));
		
		String 	keyBase = "LoadDefaultsNewKey",
						valueBase = "LoadDefaultsNewValue",
						commentBase = "LoadDefaultsComment";
		
		for (int i = 0; i < ITERATIONS; i++) {
			String 	key = keyBase + i,
							value = valueBase + i,
							comment = commentBase + i;
			
			props.put(key, value);
			props.putComment(comment);
			assertFalse(props.equals(defaults));
		}
		props.loadDefaults();
		assertEquals(defaults.size(), props.size());
		assertEquals(defaults.keys(), props.keys());
		for (String key : defaults.keys())
			assertEquals(defaults.get(key), props.get(key));
	}
	
	@Test
	public void testToString() throws FileNotFoundException, IOException {
		File file = buildFile();
		Properties props = new Properties(file);
		
		System.out.println("Clean EncryptedProperties:");
		System.out.println(props.toString());
		
		String 	keyBase = "ToStringKey",
						valueBase = "ToStringValue",
						commentBase = "ToStringComment";
		
		for (int i = 0; i < 10; i++) {
			String 	key = keyBase + i,
							value = valueBase + i,
							comment = commentBase + i;
			
			props.put(key, value);
			props.putComment(comment);
		}
		System.out.println("Properties with " + props.size() + " properties:");
		System.out.println(props.toString());
		
		props.saveFile();
		props.reload();
		
		System.out.println("Properties with " + props.size() + " properties post-reload():");
		System.out.println(props.toString());
	}

	private static Properties buildDefaults() {
		return buildDefaults(rand.nextInt(ITERATIONS + 1));
	}
	private static Properties buildDefaults(int numProperties) {
		Properties defaults = new Properties();
		
		String 	keyBase = "DefaultKey",
						valueBase = "DefaultValue";
		for (int i = 0; i < numProperties; i++) {
			String 	key = keyBase + i,
							value = valueBase + i;
			
			defaults.put(key, value);
		}
		return defaults;
	}
	
	private static File buildFile() {
		String filename = BASE_FILENAME + testFilesCounter++;
		File file = new File(filename);
		file.deleteOnExit();
		
		System.out.println("Created testFile: " + file.toString());
		
		return file;
	}
}
