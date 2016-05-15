package dev.kkorolyov.simpleopts;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses command line arguments using an {@code Options} object.
 */
public class ArgumentParser {
	private Options validOptions;
	private String[] args;
	private Map<Option, String> options = new HashMap<>();
	
	/**
	 * Constructs a new {@code ArgumentParser} with set valid options for an array of arguments.
	 * The parser will attempt to parse the arguments array upon instantiation.
	 * @param validOptions set of all options recognized by this parser
	 * @param args arguments array to parse
	 * @throws IllegalArgumentException if any option in {@code args} fails to match to an option in {@code validOptions}, or any option requiring an argument does not receive a valid argument
	 */
	public ArgumentParser(Options validOptions, String[] args) {
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
			
			String currentOptionArg = currentOption.requiresArg() ? args[counter++] : null;
			
			if (currentOption.requiresArg() && (currentOptionArg == null || validOptions.contains(currentOptionArg)))
				throw new IllegalArgumentException("Not a valid argument for " + currentOption.getLongName() + ": " + currentOptionArg);
			
			options.put(currentOption, currentOptionArg);
		}
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
	
	/** @return all options obtained by this parser and their respective arguments, if applicable */
	public Map<Option, String> getOptions() {
		return options;
	}
}
