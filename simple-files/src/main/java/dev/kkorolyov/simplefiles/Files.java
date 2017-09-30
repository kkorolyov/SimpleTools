package dev.kkorolyov.simplefiles;

import dev.kkorolyov.simplefiles.stream.InStrategy;
import dev.kkorolyov.simplefiles.stream.OutStrategy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides methods for quickly working with filesystem resources.
 */
public final class Files {
	/**
	 * Attempts to invoke a consumer with an input stream to a resource.
	 * @param path path to resource
	 * @param streamConsumer consumer invoked with stream if available
	 * @param strategies stream opening strategies to attempt in order
	 * @return {@code true} if stream found and consumed by {@code streamConsumer}
	 */
	public static boolean in(String path, Consumer<InputStream> streamConsumer, InStrategy... strategies) {
		try {
			streamConsumer.accept(in(path, strategies));
			return true;
		} catch (AccessException e) {
			return false;
		}
	}
	/**
	 * Attempts to open an input stream to a resource, throwing an exception only if all strategies are exhausted.
	 * @see #in(String, boolean, InStrategy...)
	 */
	public static InputStream in(String path, InStrategy...  strategies) {
		return in(path, false, strategies);
	}
	/**
	 * Attempts to open an input stream to a resource.
	 * @param path path to resource
	 * @param failFast if {@code true}, will throw an {@link AccessException} on the first failed open strategy
	 * @param strategies all stream opening strategies to attempt, will return the value of the first successful strategy
	 * @return input stream to resource
	 * @throws AccessException if all opening strategies failed
	 */
	public static InputStream in(String path, boolean failFast, InStrategy...  strategies) {
		return stream(path, failFast, strategies);
	}

	/**
	 * Attempts to invoke a consumer with an output stream to a resource.
	 * @param path path to resource
	 * @param streamConsumer consumer invoked with stream if available
	 * @param strategies stream opening strategies to attempt in order
	 * @return {@code true} if stream found and consumed by {@code streamConsumer}
	 */
	public static boolean out(String path, Consumer<OutputStream> streamConsumer, OutStrategy... strategies) {
		try {
			streamConsumer.accept(out(path, strategies));
			return true;
		} catch (AccessException e) {
			return false;
		}
	}
	/**
	 * Attempts to open an output stream to a resource, throwing an exception only if all strategies are exhausted.
	 * @see #out(String, boolean, OutStrategy...)
	 */
	public static OutputStream out(String path, OutStrategy... strategies) {
		return out(path, false, strategies);
	}
	/**
	 * Attempts to open an output stream to a resource.
	 * @param path path to resource
	 * @param failFast if {@code true}, will throw an {@link AccessException} on the first failed open strategy
	 * @param strategies all stream opening strategies to attempt, will return the value of the first successful strategy
	 * @return output stream to resource
	 * @throws AccessException if all opening strategies failed
	 */
	public static OutputStream out(String path, boolean failFast, OutStrategy... strategies) {
		return stream(path, failFast, strategies);
	}

	@SafeVarargs
	private static <T> T stream(String path, boolean failFast, Function<String, T>... strategies) {
		for (Function<String, T> strategy : strategies) {
			try {
				T stream = strategy.apply(path);
				if (stream != null) return stream;
			} catch (Throwable e) {
				if (failFast) throw e;
			}
		}
		throw new AccessException("All strategies failed for path: " + path);
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
	 * @param stream stream to resource
	 * @return buffered reader reading resource
	 */
	public static BufferedReader read(InputStream stream) {
		return new BufferedReader(new InputStreamReader(stream));
	}
	/**
	 * Provides a writer to write a resource.
	 * @param stream stream to resource
	 * @return buffered writer writing resource
	 */
	public static BufferedWriter write(OutputStream stream) {
		return new BufferedWriter(new OutputStreamWriter(stream));
	}

	/**
	 * Reads bytes from a resource stream and then closes the stream.
	 * @param stream stream to resource
	 * @return bytes read from resource
	 */
	public static byte[] bytes(InputStream stream) {
		try(stream) {
			return stream.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	/**
	 * Writes bytes to a resource stream and then closes the stream.
	 * @param stream stream to resource
	 * @param bytes bytes to write
	 * @throws UncheckedIOException if an IO error occurs
	 */
	public static void bytes(OutputStream stream, byte[] bytes) {
		try(stream) {
			stream.write(bytes);
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
