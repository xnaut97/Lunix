package com.github.tezvn.lunix.java;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A type of {@link Range} that represents for integers.
 */
public class IntRange extends Range<Integer> implements Iterable<Integer> {

	/**
	 * Create a new instance of {@link IntRange} with the given {@code min} and
	 * {@code max} value.
	 */
	public static IntRange of(int min, int max) {
		return new IntRange(min, max);
	}

	public IntRange(int min, int max) {
		super(min, max);
	}

	@Override
	public Integer getRandom() {
		Random rand = ThreadLocalRandom.current();
		return rand.nextInt(getMax() - 1) + getMin();
	}

	@Override
	public boolean isInRange(Integer value) {
		return value <= getMax() && value >= getMin();
	}

	@Override
	public Iterator<Integer> iterator() {
		final int[] array = Ints.toArray(ContiguousSet.create(com.google.common.collect.Range.closed(getMin(), getMax()),
				DiscreteDomain.integers()));
		return new Iterator<Integer>() {
			private int currentIndex = 0;
			
			@Override
			public boolean hasNext() {
				return currentIndex < array.length;
			}

			@Override
			public Integer next() {
				return array[currentIndex++];
			}
		};
	}

	/**
	 * Convert the range into a {@link List}.
	 */
	public List<Integer> toList(){
		List<Integer> list = new ArrayList<Integer>();
		for(int i : this)
			list.add(i);
		return list;
	}
	
	/**
	 * Convert the range into a primitive int array.
	 */
	public int[] toArray() {
		return Ints.toArray(toList());
	}
	
}
