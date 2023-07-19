package com.github.tezvn.lunix.java;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomID {

    private static final IntRange NUMBER_ASCII_RANGE = IntRange.of(48, 57);
    private static final IntRange UPPERCASE_LETTER_ASCII_RANGE = IntRange.of(65, 90);
    private static final IntRange LOWERCASE_LETTER_ASCII_RANGE = IntRange.of(97, 122);
    private final int length;
    private final List<IDRule> rules = Lists.newArrayList();
    private final List<IntRange> ranges = Lists.newArrayList();

    public static RandomID of(int length, IDRule... rules) {
        return of(length).rule(rules);
    }

    public static RandomID of(int length) {
        return new RandomID(length);
    }

    private RandomID(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("The ID length must be greater than 0 !");
        } else {
            this.length = length;
            this.rule(IDRule.LOWERCASE);
        }
    }

    public RandomID rule(IDRule... rules) {
        this.rules.clear();
        this.rules.addAll(Arrays.asList(rules));
        IDRule[] var2 = rules;
        int var3 = rules.length;
        int var4 = 0;

        while(var4 < var3) {
            IDRule rule = var2[var4];
            switch(rule) {
                case NUMBER:
                    this.ranges.add(NUMBER_ASCII_RANGE);
                case LOWERCASE:
                    this.ranges.add(LOWERCASE_LETTER_ASCII_RANGE);
                default:
                    this.ranges.add(UPPERCASE_LETTER_ASCII_RANGE);
                    ++var4;
            }
        }

        return this;
    }

    public int getLength() {
        return this.length;
    }

    public String generate() {
        StringBuilder builder = new StringBuilder();
        Random random = ThreadLocalRandom.current();

        for(int i = 0; i < this.getLength(); ++i) {
            int position = random.nextInt(this.ranges.size());
            IntRange range = this.ranges.get(position);
            int randByte = range.getRandom();
            builder.append((char)randByte);
        }

        return builder.toString();
    }

    public enum IDRule {
        LOWERCASE,
        UPPERCASE,
        NUMBER
    }

}
