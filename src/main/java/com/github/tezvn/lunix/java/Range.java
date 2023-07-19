package com.github.tezvn.lunix.java;

/**
 * A class represents for a general range. A range is a limit between two
 * objects that varies (The "min" point and the "max" point). Therefore, all
 * methods in this class will work on this range.
 * 
 * @param <T>
 *            The type of the data in the range.
 */
public abstract class Range<T> {

	private T min, max;

	public Range(T min, T max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Return the min value of the range.
	 */
	public T getMin() {
		return min;
	}

	/**
	 * Return the max value of the range.
	 * 
	 * @return
	 */
	public T getMax() {
		return max;
	}

	/**
	 * Get a random value in the range.
	 */
	public abstract T getRandom();

	/**
	 * Check if the given {@code value} is in the range or not.
	 * 
	 * @param value
	 *            The value need checking.
	 * @return {@code true} if the value is in the range, {@code false} otherwise.
	 */
	public abstract boolean isInRange(T value);
}
