package dev.kkorolyov.simplelogs;

/**
 * Prints a message to {@code System.out}.
 */
public class SysOutPrinter implements Printer {

	@Override
	public void print(String message) {
		System.out.println(message);
	}
}
