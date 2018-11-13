package dev.kkorolyov.simplestructs.procedure;

/**
 * A stateful command invoking some operation on a data structure.
 * @param <R> procedure result
 */
@FunctionalInterface
public interface Procedure<R> {
	/**
	 * Executes this procedure on the current state of its associated data structure.
	 * @return result of execution on current state of the associated data structure
	 */
	R execute();

	/**
	 * A procedure which accepts 2 arguments.
	 * @param <T0> first argument type
	 * @param <T1> second argument type
	 * @param <R> result type
	 * @see Procedure
	 */
	@FunctionalInterface
	interface Binary<T0, T1, R> {
		/**
		 * Executes this procedure with the given arguments.
		 * @param arg0 first argument
		 * @param arg1 second argument
		 * @return result of invoking procedure with {@code arg0} and {@code arg1}
		 */
		R execute(T0 arg0, T1 arg1);
	}
}
