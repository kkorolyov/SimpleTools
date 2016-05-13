package dev.kkorolyov.simpleopts;

/**
 * A single command line option.
 * Any single {@code Option} has
 *<ul>
 *<li>{@code shortName} - a single character preceded by {@code'-'} which uniquely identifies the option, (e.g., {@code -o})</li>
 *<li>{@code longName} - a word or phrase preceded by {@code'--'} which serves as the name of the option, (e.g., {@code --option})</li>
 *<li>{@code description} - a short description of the option, (e.g., {@code Changes value 'x'})</li>
 *<li>{@code requiresArg} - a {@code boolean} specifying whether the option depends on an additional argument</li>
 *</ul>
 */
public class Option {
	private static final String SHORT_MARKER = "-",
															LONG_MARKER = "--";
	private String 	shortName,
									longName;
	private String description;
	private boolean requiresArg;
	
	/**
	 * Constructs a new {@code Option} using the specified parameters.
	 * @param shortName a unique, single-character identifier of this option; if {@code null}, this option will not have a short name
	 * @param longName full identifier of this option
	 * @param requiresArg if {@code true}, this option requires a specified argument directly after it
	 * @param description descriptive statement about this option
	 */
	public Option(String shortName, String longName, boolean requiresArg, String description) {
		setShortName(shortName);
		setLongName(longName);
		setRequiresArg(requiresArg);
		setDescription(description);
	}
	
	/**
	 * @param name option name to match
	 * @return {@code true} if this option's short or long name matches the specified name
	 */
	public boolean matches(String name) {
		return (shortName != null && shortName.equals(name)) || longName.equals(name);
	}
	
	/** @return short identifier of this option */
	public String shortName() {
		return shortName;
	}
	private void setShortName(String newShortName) {
		shortName = newShortName == null ? null : SHORT_MARKER + newShortName.charAt(0);
	}
	
	/** @return long name of this option */
	public String longName() {
		return longName;
	}
	private void setLongName(String newLongName) {
		longName = LONG_MARKER + newLongName.trim();
	}
	
	/** @return {@code true} if this option depends on an additional argument */
	public boolean requiresArg() {
		return requiresArg;
	}
	private void setRequiresArg(boolean newRequiresArg) {
		requiresArg = newRequiresArg;
	}
	
	/** @return description of this option */
	public String description() {
		return description;
	}
	private void setDescription(String newDescription) {
		description = newDescription.trim();
	}
	
	/**
	 * Returns a string spanning 2 lines, in the format:
	 * <pre>
	 * shortName,	longName
	 * 		description
	 * </pre>
	 */
	@Override
	public String toString() {
		String toString = (shortName == null ? "" : shortName + ',') + '\t' + longName + System.lineSeparator()
										+ '\t' + description;
		
		return toString;
	}
}
