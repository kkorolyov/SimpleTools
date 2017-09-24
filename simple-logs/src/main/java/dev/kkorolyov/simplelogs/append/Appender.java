package dev.kkorolyov.simplelogs.append;

/**
 * Appends log messages to an output.
 */
public abstract class Appender {
	private int threshold;

	public Appender(int threshold) {
		setThreshold(threshold);
	}

	/**
	 * Appends a message to this appender's output if the message's level is within this appender's threshold.
	 * @param level message level
	 * @param message logged message
	 */
	public final void append(int level, String message) {
		if (logs(level)) append(message);
	}
	/**
	 * Appends a message using the implemented appending scheme.
	 * @param message logged message
	 */
	protected abstract void append(String message);

	/**
	 * @param level granularity level
	 * @return {@code true} if this appender accepts messages of a certain level
	 */
	public final boolean logs(int level) {
		return level <= threshold;
	}

	/** @return maximum level of log messages accepted by this appender */
	public final int getThreshold() {
		return threshold;
	}
	/** @param threshold maximum level of log messages accepted by this appender */
	public final void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}
