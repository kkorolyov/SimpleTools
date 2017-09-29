package dev.kkorolyov.simplefiles.stream;

import dev.kkorolyov.simplefiles.AccessException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * A collection of standard input stream opening strategies.
 */
public final class StreamStrategies {
	public static StreamStrategy CLASSPATH = path -> {
		InputStream stream = ClassLoader.getSystemResourceAsStream(path);
		if (stream == null) throw new AccessException("No such resource on classpath: " + path);
		return stream;
	};
	public static StreamStrategy PATH = path -> {
		try {
			return java.nio.file.Files.newInputStream(Paths.get(path));
		} catch (IOException e) {
			throw new AccessException("Unable to access resource at path: " + path, e);
		}
	};

	private StreamStrategies() {}
}
