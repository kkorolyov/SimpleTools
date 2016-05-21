package dev.kkorolyov.simpleopts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Parses command line arguments according to an {@code Options} object.
 */
public class ArgParser {
	private Options validOptions;
	private String[] args;
	private Map<Option, String> options = new HashMap<>();
	
	/**
	 * Constructs a new parser with set valid options for an array of arguments.
	 * The parser will attempt to parse the arguments array upon instantiation.
	 * @param validOptions set of all options recognized by this parser
	 * @param args arguments array to parse
	 * @throws IllegalArgumentException if any option in {@code args} fails to match to an option in {@code validOptions}, or any option requiring an argument does not receive a valid argument
	 */
	public ArgParser(Options validOptions, String[] args) {
		setValidOptions(validOptions);
		setArgs(args);
		
		parse();
	}
	private void parse() {
		int counter = 0;
		
		while (counter < args.length) {
			String currentArg = args[counter++];
			Option currentOption = validOptions.get(currentArg);
			
			if (currentOption == null)
				throw new IllegalArgumentException("Not a valid option: " + currentArg);
			
			String currentOptionArg = null;
			if (currentOption.requiresArg() && counter < args.length)
					currentOptionArg = args[counter++];
			
			if (currentOption.requiresArg() && (currentOptionArg == null || validOptions.contains(currentOptionArg)))
				throw new IllegalArgumentException("Not a valid argument for " + currentOption.getLongName() + ": " + currentOptionArg);
			
			options.put(currentOption, currentOptionArg);
		}
	}
	
	/** @return all options found by this parser */
	public Set<Option> getParsedOptions() {
		return options.keySet();
	}
	/**
	 * Checks if this parser found a specified option.
	 * @param toCheck option to check
	 * @return {@code true} if the specified option was found
	 */
	public boolean parsedOption(Option toCheck) {
		return options.containsKey(toCheck);
	}
	/**
	 * Returns the argument parsed for a specified option.
	 * @param key parsed option
	 * @return argument for the specified option, or {@code null} if no argument
	 * @throws IllegalArgumentException if the specified option was not found by this parser
	 */
	public String getArg(Option key) {
		if (!parsedOption(key))
			throw new IllegalArgumentException("Not parsed: " + key.getLongName());
		
		return options.get(key);
	}
	
	/** @return the set of valid options recognized by this parser */
	public Options getValidOptions() {
		return validOptions;
	}
	private void setValidOptions(Options newValidOptions) {
		validOptions = newValidOptions;
	}
	
	/** @return the arguments parsed by this parser */
	public String[] getArgs() {
		return args;
	}
	private void setArgs(String[] newArgs) {
		args = newArgs;
	}
}
