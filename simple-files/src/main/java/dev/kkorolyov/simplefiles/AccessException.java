package dev.kkorolyov.simplefiles;

/**
 * Exception thrown when a resource is unable to be opened or accessed.
 */
public class AccessException extends RuntimeException {
	/**
	 * Constructs a new access exception.
	 * @param message exception detail message
	 */
	public AccessException(String message) {
		this(message, null);
	}
	/**
	 * Constructs a new access exception.
	 * @param message exception detail message
	 * @param cause exception cause
	 */
	public AccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
