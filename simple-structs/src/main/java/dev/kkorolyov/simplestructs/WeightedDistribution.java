package dev.kkorolyov.simplestructs;

import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A distribution of randomly-selectable weighted values.
 */
public class WeightedDistribution<T> {
	private final NavigableMap<Integer, T> distribution = new TreeMap<>();
	private int total;

	/**
	 * Adds a weighted value to this distribution.
	 * @param value added value
	 * @param weight value weight relative to this distribution's total weight
	 * @return {@code this}
	 */
	public WeightedDistribution<T> add(T value, int weight) {
		distribution.put(total, value);
		total += weight;

		return this;
	}

	/**
	 * @return random value from this distribution
	 * @throws NoSuchElementException if this distribution is empty
	 */
	public T get() {
		if (total == 0) throw new NoSuchElementException("Distribution is empty");

		return distribution.floorEntry(ThreadLocalRandom.current().nextInt(total)).getValue();
	}
}
