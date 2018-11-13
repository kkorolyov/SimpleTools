package dev.kkorolyov.simplestructs.procedure;

/**
 * A stateful command invoking some operation on a data structure.
 * @param <T> associated data structure
 * @param <R> procedure result
 */
public interface Procedure<T, R> {
	/**
	 * Executes this procedure on the current state of its associated data structure.
	 * @return procedure result on current state of associated data structure
	 */
	R execute();
}
