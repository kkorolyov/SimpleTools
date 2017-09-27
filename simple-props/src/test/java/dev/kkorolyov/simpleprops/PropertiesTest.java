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

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PropertiesTest {
	private static final boolean PRINT_RANDOMIZATION_RESULT = false;
	
	private int fileCounter;
	
	@Test
	public void shouldReturnOriginalValueOnGetWhenPutSingleValue() {
		String key = "key", value = "val";
		Properties props = new Properties();
		
		props.put(key, value);
		assertEquals(value, props.get(key));
	}
	@Test
	public void shouldReturnOriginalArrayAsStringOnGetWhenPutMultipleValues() {
		String key = "key";
		String[] values = {"v1", "v4", " ", UUID.randomUUID().toString()};
		Properties props = new Properties();
		
		props.put(key, values);
		assertEquals(Arrays.toString(values), props.get(key));
	}
	@Test
	public void shouldReturnNullOnGetWhenMissingKey() {
		assertNull(new Properties().get("key"));
	}
	
	@Test
	public void shouldReturnOriginalValueAsArrayOnGetArrayWhenPutSingleValue() {
		String key = "key", value = "val";
		Properties props = new Properties();
		
		props.put(key, value);
		assertArrayEquals(Arrays.asList(value).toArray(new String[1]), props.getArray(key));
	}
	@Test
	public void shouldReturnOriginalValueOnGetArrayWhenPutMultipleValues() {
		String key = "key";
		String[] values = {"v1", "v4", " ", UUID.randomUUID().toString()};
		Properties props = new Properties();
		
		props.put(key, values);
		assertArrayEquals(values, props.getArray(key));
	}
	@Test
	public void shouldReturnNullOnGetArrayWhenMissingKey() {
		assertNull(new Properties().getArray("key"));
	}
	
	@Test
	public void shouldReturnKeysInInsertionOrder() {
		Map<String, String> properties = generateProperties(100);
		assertIterablesEquals(properties.keySet(), randomize(new Properties(), properties, generateComments(20), 14).keys());
	}
	@Test
	public void shouldReturnCommentsInInsertionOrder() {
		List<String> comments = generateComments(40);
		assertIterablesEquals(comments, randomize(new Properties(), generateProperties(4), comments, 7).comments());
	}
	
	@Test
	public void shouldIteratePropertiesInInsertionOrder() {
		Map<String, String> properties = generateProperties(100);
		assertIterablesEquals(properties.entrySet(), randomize(new Properties(), properties, generateComments(20), 14));
	}
	@Test
	public void shouldAllowModifiyingIteratedProperties() {
		Properties props = new Properties();
		String 	oldKey = "K1", oldVal = "V1", newVal = "V2";
		
		props.put(oldKey, oldVal);
		assertEquals(oldVal, props.get(oldKey));
		
		props.forEach(e -> e.setValue(newVal));
		assertTrue(props.contains(oldKey));
		assertEquals(newVal, props.get(oldKey));
	}
	@Test
	public void shouldIgnoreRemovingIteratedProperties() {
		Properties props = new Properties();
		String key = "K", val = "V";
		
		props.put(key, val);
		assertTrue(props.contains(key));
		assertEquals(1, props.size());
		
		Iterator<Entry<String, String>> it = props.iterator();
		it.next();
		it.remove();
		
		assertTrue(props.contains(key));
		assertEquals(1, props.size());
	}
	
	@Test
	public void shouldAppendMissingPropertiesOnOverwrite() {
		Properties 	p1 = new Properties(),
								p2 = new Properties();
		String 	key1 = "K1", key2 = "K2", val = "V";
		
		p1.put(key1, val);
		p2.put(key2, val);
		p1.put(p2, true);
		
		assertEquals(2, p1.size());
		assertEquals(val, p1.get(key1));
		assertEquals(val, p1.get(key2));
	}
	
	@Test
	public void shouldNotOverwriteOnPutPropertiesWhenOverwriteFalse() {
		Properties 	p1 = new Properties(),
								p2 = new Properties();
		String key = "K", val1 = "V1", val2 = "V2";
		
		p1.put(key, val1);
		p2.put(key, val2);
		p1.put(p2, false);
		
		assertEquals(val1, p1.get(key));
	}
	@Test
	public void shouldOverwriteOnPutPropertiesWhenOverwriteTrue() {
		Properties 	p1 = new Properties(),
								p2 = new Properties();
		String key = "K", val1 = "V1", val2 = "V2";
		
		p1.put(key, val1);
		p2.put(key, val2);
		p1.put(p2, true);
		
		assertEquals(val2, p1.get(key));
	}
	
	@Test
	public void shouldMarkCommentsWhenPutUnmarked() {
		Map<String, String> properties = generateProperties(100);
		List<String> comments = generateComments(40);
		assertIterablesEquals(comments, randomize(new Properties(), properties, comments.stream().map(s -> s.substring(1)).collect(Collectors.toList()), 0).comments());
	}
	
	@Test
	public void shouldBeEmptyWhenInitializedEmpty() {
		assertTrue(new Properties().isEmpty());
	}
	@Test
	public void shouldNotBeEmptyWhenHasAtLeastOneProperty() {
		Properties props = new Properties();
		props.put("Key", "Val");
		assertFalse(props.isEmpty());
	}
	
	@Test
	public void shouldEqualSizeWhenFillerDiffers() {
		Properties 	p1 = randomize(new Properties(), generateProperties(4), generateComments(1), 5),
								p2 = randomize(new Properties(), generateProperties(4), generateComments(10), 20);
		
		assertNotEquals(count(p1.comments()), count(p2.comments()));
		assertEquals(p1.size(), p2.size());
	}
	private static int count(Iterable<?> iterable) {
		int count = 0;
		
		Iterator<?> it = iterable.iterator();
		while (it.hasNext()) {
			it.next();
			count++;
		}
		return count;
	}
	
	@Test
	public void shouldBeEmptyWhenCleared() {
		Properties props = randomize(new Properties(), generateProperties(34), generateComments(4), 17);
		props.clear();
		assertTrue(props.isEmpty());
	}
	
	@Test
	public void shouldEqualsWhenLoadedAfterSave() throws IOException {
		Properties 	original = randomize(new Properties(), generateProperties(40), generateComments(16), 49),
								loaded = new Properties();
		Path file = generateNewFile();
		
		original.save(file);
		loaded.load(file);
		
		assertEquals(original, loaded);
		assertEquals(original, new Properties(file));
	}
	@Test
	public void shouldIdenticalWhenLoadedAfterSave() throws IOException {
		Properties 	original = randomize(new Properties(), generateProperties(40), generateComments(16), 49),
								loaded = new Properties();
		Path file = generateNewFile();
		
		original.save(file);
		loaded.load(file);
		
		assertTrue(original.identical(loaded));
		assertTrue(original.identical(new Properties(file)));
	}
	
	@Test
	public void shouldEqualsDefaultsWhenInitialized() {
		Properties defaults = randomize(new Properties(), generateProperties(14), generateComments(80), 46);
		assertEquals(defaults, new Properties(defaults));
	}
	@Test
	public void shouldIdenticalDefaultsWhenInitialized() {
		Properties defaults = randomize(new Properties(), generateProperties(14), generateComments(80), 46);
		assertTrue(defaults.identical(new Properties(defaults)));
	}
	
	@Test
	public void shouldReflectiveEquals() {
		Properties props = randomize(new Properties(), generateProperties(80), generateComments(4), 8);
		assertEquals(props, props);
	}
	@Test
	public void shouldReflexiveEquals() {
		Properties 	p1 = randomize(new Properties(), generateProperties(80), generateComments(4), 8),
								p2 = new Properties(p1);
		assertEquals(p1, p2);
		assertEquals(p2, p1);
	}
	@Test
	public void shouldTransitiveEquals() {
		Properties	p1 = randomize(new Properties(), generateProperties(80), generateComments(4), 8),
								p2 = new Properties(p1),
								p3 = new Properties(p2);
		assertEquals(p1, p2);
		assertEquals(p2, p3);
		assertEquals(p1, p3);
	}
	
	@Test
	public void shouldHashSameWhenEquals() {
		Properties 	p1 = randomize(new Properties(), generateProperties(38), generateComments(1), 2),
								p2 = new Properties(p1);
		assertEquals(p1, p2);
		assertEquals(p1.hashCode(), p2.hashCode());
	}
	
	@Test
	public void shouldEqualsPermutation() {
		Map<String, String> properties = generateProperties(400);
		List<String> comments = generateComments(8);
		int blanklines = 4;
		
		Properties 	p1 = randomize(new Properties(), properties, comments, blanklines),
								p2 = randomize(new Properties(), properties, comments, blanklines);
		assertEquals(p1, p2);
		assertEquals(p2, p1);
	}
	@Test
	public void shouldNotIdenticalPermutation() {
		Map<String, String> properties = generateProperties(400);
		List<String> comments = generateComments(8);
		int blanklines = 4;
		
		Properties 	p1 = randomize(new Properties(), properties, comments, blanklines),
								p2 = randomize(new Properties(), properties, comments, blanklines);
		assertFalse(p1.identical(p2));
		assertFalse(p2.identical(p1));
	}
	
	@Test
	public void shouldEqualsWhenFillerDiffers() {
		Map<String, String> properties = generateProperties(15);
		
		Properties 	p1 = randomize(new Properties(), properties, generateComments(1), 5),
								p2 = randomize(new Properties(), properties, generateComments(10), 20);
		assertEquals(p1, p2);
		assertEquals(p2, p1);
	}
	@Test
	public void shouldNotIdenticalWhenFillerDiffers() {
		Map<String, String> properties = generateProperties(15);
		
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
		IntStream.range(0, num).forEach(i -> properties.put("Key-" + UUID.randomUUID().toString(), "Val-" + UUID.randomUUID().toString()));
		
		return properties;
	}
	private static List<String> generateComments(int num) {
		return IntStream.range(0, num).mapToObj(i -> "#Comment-" + UUID.randomUUID()).collect(Collectors.toList());
	}
	
	private Path generateNewFile() {
		Path file = Paths.get("PropertiesTestFile" + fileCounter++);
		file.toFile().deleteOnExit();
		return file;
	}
}
