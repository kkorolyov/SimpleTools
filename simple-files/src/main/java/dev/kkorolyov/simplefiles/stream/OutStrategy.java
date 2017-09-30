package dev.kkorolyov.simplefiles.stream;

import dev.kkorolyov.simplefiles.AccessException;

import java.io.OutputStream;
import java.util.function.Function;

/**
 * Derives an {@link OutputStream} from a string path.
 */
@FunctionalInterface
public interface OutStrategy extends Function<String, OutputStream> {
	/**
	 * Opens an output stream to a resource at a path.
	 * @param path path to resource
	 * @return output stream to resource, should not be {@code null}
	 * @throws AccessException if a stream is unable to be opened for any reason
	 */
	OutputStream apply(String path);
}
