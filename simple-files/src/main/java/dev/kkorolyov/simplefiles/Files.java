package dev.kkorolyov.simplefiles;

import dev.kkorolyov.simplefiles.stream.StreamStrategy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides methods for quickly working with filesystem resources.
 */
public final class Files {
	/**
	 * Attempts to open an input stream to a resource, throwing an exception only if all strategies are exhausted.
	 * @see #stream(String, boolean, StreamStrategy...)
	 */
	public static InputStream stream(String path, StreamStrategy...  strategies) {
		return stream(path, false, strategies);
	}
	/**
	 * Attempts to open an input stream to a resource.
	 * @param path path to resource
	 * @param failFast if {@code true}, will throw an {@link AccessException} on the first failed open strategy
	 * @param strategies all stream opening strategies to attempt, will return the value of the first successful strategy
	 * @return input stream to resource
	 * @throws AccessException if all opening strategies failed
	 */
	public static InputStream stream(String path, boolean failFast, StreamStrategy...  strategies) {
		for (StreamStrategy strategy : strategies) {
			try {
				InputStream in = strategy.open(path);
				if (in != null) return in;
			} catch (Throwable e) {
				if (failFast) throw e;
			}
		}
		throw new AccessException("All open strategies failed for path: " + path);
	}

	/**
	 * Returns a {@link Path} to a resource.
	 * @param path string path to resource
	 * @return path to resource
	 */
	public static Path path(String path) {
		return Paths.get(path);
	}

	/**
	 * Provides a reader to read a resource.
	 * @param path path to resource
	 * @return buffered reader reading resource
	 * @throws UncheckedIOException if an IO error occurs
	 */
	public static BufferedReader read(String path) {
		try {
			return java.nio.file.Files.newBufferedReader(path(path));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	/**
	 * Provides a writer to write a resource.
	 * @param path path to resource
	 * @return buffered writer writing resource
	 * @throws UncheckedIOException if an IO error occurs
	 */
	public static BufferedWriter write(String path) {
		try {
			return java.nio.file.Files.newBufferedWriter(path(path));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Reads bytes from a resource.
	 * @param path path to resource
	 * @return bytes read from resource
	 * @throws UncheckedIOException if an IO error occurs
	 */
	public static byte[] bytes(String path) {
		try {
			return java.nio.file.Files.readAllBytes(path(path));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	/**
	 * Writes bytes to a resource.
	 * @param path path to resource
	 * @param bytes bytes to write
	 * @throws UncheckedIOException if an IO error occurs
	 */
	public static void bytes(String path, byte[] bytes) {
		try {
			java.nio.file.Files.write(path(path), bytes);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Deletes a resource if it exists.
	 * @param path path to resource
	 * @return {@code true} if resource exists and was deleted
	 * @throws UncheckedIOException if an IO error occurs
	 */
	public static boolean delete(String path) {
		try {
			return java.nio.file.Files.deleteIfExists(path(path));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private Files() {}
}
