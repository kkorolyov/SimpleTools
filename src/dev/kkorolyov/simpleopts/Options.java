package dev.kkorolyov.simpleopts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A set of {@code Option} objects.
 */
public class Options {
	private Set<Option> options = new TreeSet<>();
	
	/**
	 * Constructs a new {@code Options} for the specified set of options.
	 * @param options supported options
	 */
	public Options(Option[] options) {
		
		parse();
	}
	private void parse() {
		int counter = 0;
		while (counter < args.length) {
			Option currentOption = Option.getOption(args[counter]);
			
			if (currentOption.requiresArg()) {
				options.put(currentOption, args[counter + 1]);
				counter += 2;
			}
			else {
				options.put(currentOption, null);
				counter += 1;
			}
		}
	}
	
	/** @return	help string */
	public static String help() {
		StringBuilder toStringBuilder = new StringBuilder("USAGE" + System.lineSeparator());
		
		for (Option option : Option.values()) {
			toStringBuilder.append(option.description()).append(System.lineSeparator());
		}
		return toStringBuilder.toString();
	}
	
	/**
	 * Checks if this {@code Options} contains the specified option.
	 * @param toCheck option to check
	 * @return {@code true} if this {@code Options} contains the specified option
	 */
	public boolean contains(Option toCheck) {
		return options.containsKey(toCheck);
	}
	/**
	 * Retrieves the argument for a specific option.
	 * @param key option to get argument for
	 * @return option's argument, or {@code null} if does not exist
	 */
	public String get(Option key) {
		return options.get(key);
	}
	
	/** @return	all used options */
	public Option[] getAllOptions() {
		return options.keySet().toArray(new Option[options.size()]);
	}
}
