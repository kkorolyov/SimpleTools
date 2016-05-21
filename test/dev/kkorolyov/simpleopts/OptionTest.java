package dev.kkorolyov.simpleopts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class OptionTest {
	private static final String SHORT_MARKER = "-",
															LONG_MARKER = "--";
	private static final String SHORT_NAME = "h",
															LONG_NAME = "help",
															DESCRIPTION = "Provides help";
	private static final boolean REQUIRES_ARG = true;
	
	private Option option;
	
	@Test
	public void testMatches() {
		option = new Option(SHORT_NAME, LONG_NAME, DESCRIPTION, REQUIRES_ARG);
		
		String 	shortMatch = SHORT_MARKER + SHORT_NAME,
						longMatch = LONG_MARKER + LONG_NAME;
		
		assertTrue(option.matches(shortMatch));
		assertTrue(option.matches(longMatch));
	}

	@Test
	public void testGetShortName() {
		option = new Option(SHORT_NAME, LONG_NAME, DESCRIPTION, REQUIRES_ARG);
		
		String expectedShort = SHORT_MARKER + SHORT_NAME;
		
		assertEquals(expectedShort, option.getShortName());
	}

	@Test
	public void testGetLongName() {
		option = new Option(SHORT_NAME, LONG_NAME, DESCRIPTION, REQUIRES_ARG);

		String expectedLong =	LONG_MARKER + LONG_NAME;

		assertEquals(expectedLong, option.getLongName());
	}

	@Test
	public void testToString() {
		option = new Option(SHORT_NAME, LONG_NAME, DESCRIPTION, REQUIRES_ARG);

		System.out.println(option.toString());
	}
}
