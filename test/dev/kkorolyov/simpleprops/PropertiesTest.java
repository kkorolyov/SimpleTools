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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class PropertiesTest {
	private static final boolean PRINT_RANDOMIZATION_RESULT = false;
	private static final Map<String, String> properties = generateProperties(100);
	private static final List<String> comments = generateComments(40);
	private static final int BLANK_LINES = 10;
	
	private int fileCounter;
	
	@Test
	public void shouldReturnKeysInInsertionOrder() {
		assertIterablesEquals(properties.keySet(), randomize(new Properties()).keys());
	}
	@Test
	public void shouldReturnPropertiesInInsertionOrder() {
		assertIterablesEquals(properties.entrySet(), randomize(new Properties()).properties());
	}
	@Test
	public void shouldReturnCommentsInInsertionOrder() {
		assertIterablesEquals(comments, randomize(new Properties()).comments());
	}
	
	@Test
	public void shouldMarkCommentsWhenPutUnmarked() {
		assertIterablesEquals(comments, randomize(new Properties(), properties, comments.stream().map(s -> s.substring(1)).collect(Collectors.toList()), 0).comments());
	}
	
	@Test
	public void shouldReturnEqualOnGetWhenValidKey() {
		String 	key = "key",
						value = "val";
		Properties props = new Properties();
		
		props.put(key, value);
		assertEquals(value, props.get(key));
	}
	@Test
	public void shouldReturnEqualArrayStringOnGetWhenValidKey() {
		String key = "key";
		String[] values = {"v1", "v4", " ", UUID.randomUUID().toString()};
		Properties props = new Properties();
		
		props.put(key, values);
		assertEquals(Arrays.toString(values), props.get(key));
	}
	@Test
	public void shouldReturnNullOnGetWhenInvalidKey() {
		assertNull(new Properties().get("key"));
	}
	
	@Test
	public void shouldReturnEqualAsArrayOnGetArrayWhenValidKey() {
		String 	key = "key",
						value = "val";
		Properties props = new Properties();
		
		props.put(key, value);
		assertArrayEquals(Arrays.asList(value).toArray(new String[1]), props.getArray(key));
	}
	@Test
	public void shouldReturnEqualArrayOnGetArrayWhenValidKey() {
		String key = "key";
		String[] values = {"v1", "v4", " ", UUID.randomUUID().toString()};
		Properties props = new Properties();
		
		props.put(key, values);
		assertArrayEquals(values, props.getArray(key));
	}
	@Test
	public void shouldReturnNullOnGetArrayWhenInvalidKey() {
		assertNull(new Properties().getArray("key"));
	}
	
	@Test
	public void shouldBeEmptyWhenInitializedEmpty() {
		assertTrue(new Properties().isEmpty());
	}
	
	@Test
	public void shouldEqualSizeWhenFillerDiffers() {
		Properties 	p1 = randomize(new Properties(), properties, generateComments(1), 5),
								p2 = randomize(new Properties(), properties, generateComments(10), 20);
		assertEquals(p1.size(), p2.size());
	}
	
	@Test
	public void shouldBeEmptyWhenCleared() {
		Properties props = randomize(new Properties());
		props.clear();
		assertTrue(props.isEmpty());
	}
	
	@Test
	public void shouldIdenticalWhenLoadedAfterSave() throws IOException {
		Properties 	original = randomize(new Properties()),
								loaded = new Properties();
		Path file = generateNewFile();
		
		original.save(file);
		loaded.load(file);
		
		assertTrue(original.identical(loaded));
		assertTrue(original.identical(new Properties(file)));
	}
	@Test
	public void shouldEqualsWhenLoadedAfterSave() throws IOException {
		Properties 	original = randomize(new Properties()),
								loaded = new Properties();
		Path file = generateNewFile();
		
		original.save(file);
		loaded.load(file);
		
		assertEquals(original, loaded);
		assertEquals(original, new Properties(file));
	}
	
	@Test
	public void shouldEqualsDefaultsWhenInitialized() {
		Properties defaults = randomize(new Properties());
		assertEquals(defaults, new Properties(defaults));
	}
	@Test
	public void shouldIdenticalDefaultsWhenInitialized() {
		Properties defaults = randomize(new Properties());
		assertTrue(defaults.identical(new Properties(defaults)));
	}
	
	@Test
	public void shouldReflectiveEquals() {
		Properties props = randomize(new Properties());
		assertEquals(props, props);
	}
	@Test
	public void shouldReflexiveEquals() {
		Properties 	p1 = randomize(new Properties()),
								p2 = new Properties(p1);
		assertEquals(p1, p2);
		assertEquals(p2, p1);
	}
	@Test
	public void shouldTransitiveEquals() {
		Properties	p1 = randomize(new Properties()),
								p2 = new Properties(p1),
								p3 = new Properties(p2);
		assertEquals(p1, p2);
		assertEquals(p2, p3);
		assertEquals(p1, p3);
	}
	
	@Test
	public void shouldHashSameWhenEquals() {
		Properties 	p1 = randomize(new Properties()),
								p2 = new Properties(p1);
		assertEquals(p1, p2);
		assertEquals(p1.hashCode(), p2.hashCode());
	}
	
	@Test
	public void shouldEqualsPermutation() {
		Properties 	p1 = randomize(new Properties()),
								p2 = randomize(new Properties());
		assertEquals(p1, p2);
		assertEquals(p2, p1);
	}
	@Test
	public void shouldNotIdenticalPermutation() {
		Properties	p1 = randomize(new Properties()),
								p2 = randomize(new Properties());
		assertFalse(p1.identical(p2));
		assertFalse(p2.identical(p1));
	}
	
	@Test
	public void shouldEqualsWhenFillerDiffers() {
		Properties 	p1 = randomize(new Properties(), properties, generateComments(1), 5),
								p2 = randomize(new Properties(), properties, generateComments(10), 20);
		assertEquals(p1, p2);
		assertEquals(p2, p1);
	}
	@Test
	public void shouldNotIdenticalWhenFillerDiffers() {
		Properties 	p1 = randomize(new Properties(), properties, generateComments(1), 5),
								p2 = randomize(new Properties(), properties, generateComments(10), 20);
		assertFalse(p1.identical(p2));
		assertFalse(p2.identical(p1));
	}
	
	private static <T> void assertIterablesEquals(Iterable<T> expected, Iterable<T> actual) {
		Iterator<T> expectedIt = expected.iterator(),
								actualIt = actual.iterator();
		
		while (expectedIt.hasNext())
			assertEquals(expectedIt.next(), actualIt.next());
		assertEquals(expectedIt.hasNext(), actualIt.hasNext());
	}
	
	private static Properties randomize(Properties props) {
		return randomize(props, properties, comments, BLANK_LINES);
	}
	private static Properties randomize(Properties props, Map<String, String> properties, Iterable<String> comments, int blankLines) {
		Random rand = new Random();
		
		Iterator<Entry<String, String>> propertiesIt = properties.entrySet().iterator();
		Iterator<String> commentIt = comments.iterator();
		int blankLineCounter = blankLines;
		
		while (propertiesIt.hasNext() || commentIt.hasNext() || blankLineCounter > 0) {
			if (rand.nextBoolean()) {
				if (propertiesIt.hasNext() && rand.nextBoolean()) {
					Entry<String, String> property = propertiesIt.next();
					props.put(property.getKey(), property.getValue());
				}
				else if (commentIt.hasNext())
					props.putComment(commentIt.next());
			}
			else if (blankLineCounter-- > 0)
				props.putBlankLine();
		}
		if (PRINT_RANDOMIZATION_RESULT)
			System.out.println("Randomized " + props.getClass().getSimpleName() + ":" + System.lineSeparator() + props);
		return props;
	}
	
	private static Map<String, String> generateProperties(int num) {
		Map<String, String> properties = new LinkedHashMap<>();
		IntStream.range(0, num).forEach(i -> properties.put("Key" + i, "Val" + i));
		
		return properties;
	}
	private static List<String> generateComments(int num) {
		return IntStream.range(0, num).mapToObj(i -> "#Comment" + i).collect(Collectors.toList());
	}
	
	private Path generateNewFile() {
		Path file = Paths.get("PropertiesTestFile" + fileCounter++);
		file.toFile().deleteOnExit();
		return file;
	}
}
