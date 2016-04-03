package dev.kkorolyov.simplelogs;

/**
 * Prints a message to {@code System.err}.
 */
public class SysErrPrinter implements Printer {

	@Override
	public void print(String message) {
		System.err.println(message);
	}
}
