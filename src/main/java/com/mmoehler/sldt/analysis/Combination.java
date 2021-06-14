package com.mmoehler.sldt.analysis;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Combination {
    private final int[] left;
    private final int[] right;

    public static Combination of(final int[] left, final int[] right) {
        return new Combination(left, right);
    }

    public Combination(final int[] left, final int[] right) {
        this.left = left;
        this.right = right;
    }

    public IntStream getLeft() {
        return Arrays.stream(left);
    }

    public IntStream getRight() {
        return Arrays.stream(right);
    }

    @Override
    public String toString() {
        return "{" + Arrays.toString(left) + "-" + Arrays.toString(right) + '}';
    }
}
