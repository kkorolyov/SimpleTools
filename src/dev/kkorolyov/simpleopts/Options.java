package dev.kkorolyov.simpleopts;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A set of {@code Option} objects.
 */
public class Options {
	private static final Comparator<Option> optionComparator = new Comparator<Option>() {
		@Override
		public int compare(Option o1, Option o2) {
			return o1.longName().compareTo(o2.longName());
		}
	};
	
	private Set<Option> options = new TreeSet<>(optionComparator);
	
	/**
	 * Constructs an empty {@code Options}.
	 */
	public Options() {
		this(null);
	}
	/**
	 * Constructs a new {@code Options} for the specified set of options.
	 * @param options supported options in this options set
	 */
	public Options(Option[] options) {
		addAll(options);
	}
	
	/**
	 * Checks if this {@code Options} contains the specified option.
	 * @param toCheck option to check
	 * @return {@code true} if this {@code Options} contains the specified option
	 */
	public boolean contains(Option toCheck) {
		return options.contains(toCheck);
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
	 * Removes an option from this options set.
	 * @param toRemove option to remove
	 * @return {@code true} if this options set contained the specified option
	 */
	public boolean remove(Option toRemove) {
		return options.remove(toRemove);
	}
	
	/** @return	all options */
	public Set<Option> getAllOptions() {
		return options;
	}
}
