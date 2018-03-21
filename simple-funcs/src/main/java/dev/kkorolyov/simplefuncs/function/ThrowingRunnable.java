package dev.kkorolyov.simplefuncs.function;

/**
 * A {@link Runnable} which may throw a checked exception.
 * @param <E> exception type
 */
public interface ThrowingRunnable<E extends Exception> extends Runnable {
	/**
	 * Invokes the runnable.
	 * @throws E if an exception of type {@code E} occurs during invocation
	 */
	void runThrowing() throws E;

	@Override
	default void run() {
		try {
			runThrowing();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
