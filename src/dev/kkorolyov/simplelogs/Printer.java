package dev.kkorolyov.simplelogs;

/**
 * Prints a message to some destination.
 */
public interface Printer {
	/**
	 * Prints a message to a location specified by the printing object.
	 * @param message message to print
	 */
	void print(String message);
}
