package dev.kkorolyov.simpleopts;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OptionsTest {
	private static final Option[] OPTIONS_ARRAY = {	new Option("h", "help", "Provides help", false),
																									new Option("l", "list", "Lists", false),
																									new Option("a", "add", "Adds something", true),
																									new Option("r", "remove", "Removes something", true)};
	private static final Set<Option> OPTIONS_SET = new HashSet<>();
	
	private Options options = new Options();
	
	@BeforeClass
	public static void setUpBeforeClass() {
		for (Option option : OPTIONS_ARRAY)
			OPTIONS_SET.add(option);
	}
	@Before
	public void setUp() {
		for (Option option : OPTIONS_ARRAY)
			options.add(option);
	}
	
	@Test
	public void testContainsString() {
		for (Option option : OPTIONS_ARRAY) {
			assertTrue(options.contains(option.getShortName()));
			assertTrue(options.contains(option.getLongName()));
		}
	}

	@Test
	public void testContainsOption() {
		for (Option option : OPTIONS_ARRAY) {
			assertTrue(options.contains(option));
		}
	}

	@Test
	public void testGet() {
		for (Option option : OPTIONS_ARRAY) {
			assertEquals(option, options.get(option.getShortName()));
			assertEquals(option, options.get(option.getLongName()));
		}
	}

	@Test
	public void testAddAllOptionArray() {
		options = new Options();	// Clear
		
		options.addAll(OPTIONS_ARRAY);
		
		assertEquals(OPTIONS_ARRAY.length, options.size());
		for (Option option : OPTIONS_ARRAY)
			assertTrue(options.contains(option));
	}

	@Test
	public void testAddAllSetOfOption() {
		options = new Options();	// Clear
		
		options.addAll(OPTIONS_SET);
		
		assertEquals(OPTIONS_SET.size(), options.size());
		for (Option option : OPTIONS_SET)
			assertTrue(options.contains(option));
	}
	
	@Test
	public void testToString() {
		System.out.println(options.toString());
	}
}
