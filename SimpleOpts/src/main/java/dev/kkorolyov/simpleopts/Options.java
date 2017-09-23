package dev.kkorolyov.simpleopts;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A set of {@code Option} objects.
 */
public class Options implements Iterable<Option> {
	private Set<Option> options = new HashSet<>();
	
	/**
	 * Constructs an empty {@code Options}.
	 */
	public Options() {
		this(null);
	}
	/**
	 * Constructs a new options set for the specified set of options.
	 * @param options supported options in this options set
	 */
	public Options(Option[] options) {
		if (options != null)
			addAll(options);
	}
	
	/**
	 * Checks if this options set contains an option of the specified name.
	 * @param name short or long name of option
	 * @return {@code true} if this options set contains an option of the specified name
	 */
	public boolean contains(String name) {
		return get(name) != null;
	}
	/**
	 * Checks if this options set contains the specified option.
	 * @param toCheck option to check
	 * @return {@code true} if this options set contains the specified option
	 */
	public boolean contains(Option toCheck) {
		return options.contains(toCheck);
	}
	
	/**
	 * Returns an option from this options set matching the specified name.
	 * @param name short or long name of option
	 * @return matching {@code Option}, or {@code null} if no such option
	 */
	public Option get(String name) {
		Option toReturn = null;
		
		for (Option option : options) {
			if (option.matches(name)) {
				toReturn = option;
				
				break;
			}
		}
		return toReturn;
	}
	
	/**
	 * Adds a new option to this options set.
	 * @param toAdd option to add
	 * @return {@code true} if this options set did not already contain the specified option
	 */
	public boolean add(Option toAdd) {
		return options.add(toAdd);
	}
	/**
	 * Adds an array of options to this options set.
	 * @param toAdd array to add
	 * @return number of options added
	 */
	public int addAll(Option[] toAdd) {
		int addedCounter = 0;
		
		for (Option option : toAdd) {
			if (options.add(option))
				addedCounter++;
		}
		return addedCounter;
	}
	/**
	 * Adds a set of options to this options set.
	 * @param toAdd set to add
	 * @return number of options added
	 */
	public int addAll(Set<Option> toAdd) {
		int addedCounter = 0;
		
		for (Option option : toAdd) {
			if (options.add(option))
				addedCounter++;
		}
		return addedCounter;
	}
	
	/** @return number of options in this options set. */
	public int size() {
		return options.size();
	}
	
	/**
	 * Returns an iterator over all the options in this options set.
	 */
	@Override
	public Iterator<Option> iterator() {
		return options.iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder toStringBuilder = new StringBuilder();
		
		for (Option option : options)
			toStringBuilder.append(option.toString()).append(System.lineSeparator());
		
		return toStringBuilder.toString();
	}
}
