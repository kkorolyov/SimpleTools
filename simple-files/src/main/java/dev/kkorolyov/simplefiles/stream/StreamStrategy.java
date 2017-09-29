package dev.kkorolyov.simplefiles.stream;

import dev.kkorolyov.simplefiles.AccessException;

import java.io.InputStream;

/**
 * Derives an {@link InputStream} from a string path.
 */
@FunctionalInterface
public interface StreamStrategy {
	/**
	 * Opens an input stream to a resource at a path.
	 * @param path path to resource
	 * @return input stream to resource, should not be {@code null}
	 * @throws AccessException if a stream is unable to be opened for any reason
	 */
	InputStream open(String path);
}
