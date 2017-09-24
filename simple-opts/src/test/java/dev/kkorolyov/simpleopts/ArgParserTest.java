package dev.kkorolyov.simpleopts;

import org.junit.Test;

public class ArgParserTest {	// TODO Assertions
	private static final Option[] VALID_OPTIONS_ARRAY = {	new Option("h", "help", "Provides help", false),
																												new Option("l", "list", "Lists", false),
																												new Option("a", "add", "Adds something", true),
																												new Option("r", "remove", "Removes something", true)};
	private static final Options VALID_OPTIONS = new Options(VALID_OPTIONS_ARRAY);
	private static final String[] ARGS = {"-h",
																				"--list",
																				"-a", "moo",
																				"--remove", "something"};
	
	private ArgParser argParser = new ArgParser(VALID_OPTIONS, ARGS);
			
	@Test
	public void testGetOptions() {		
		for (Option option : argParser.getParsedOptions()) {
			System.out.println(option.getShortName() + '\t' + '-' + '\t' + argParser.getArg(option));
		}
	}
}
