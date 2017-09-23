package dev.kkorolyov.simplelogs.append;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Provides preset appenders.
 */
public class Appenders {
	/**
	 * @param threshold initial appender threshold
	 * @return new appender which appends to {@code System.err}
	 */
	public static Appender err(int threshold) {
		return new Appender(threshold) {
			@Override
			public void append(String message) {
				System.err.println(message);
			}
		};
	}
	/**
	 * @param threshold initial appender threshold
	 * @return new appender which appends to {@code System.out}
	 */
	public static Appender out(int threshold) {
		return new Appender(threshold) {
			@Override
			public void append(String message) {
				System.out.println(message);
			}
		};
	}

	/**
	 * @param path path to file
	 * @param threshold initial appender threshold
	 * @return new appender which appends to the file at {@code path}
	 * @throws FileNotFoundException if the file at {@code path} does not exist or results in some other issue when opened
	 */
	public static Appender file(Path path, int threshold) throws FileNotFoundException {
		return new Appender(threshold) {
			PrintWriter writer = new PrintWriter(path.toFile());

			@Override
			public void append(String message) {
				writer.println(message);
				writer.flush();
			}
		};
	}
}
