package com.mmoehler.sldt.analysis;

/*-
 * #%L
 * sldt
 * %%
 * Copyright (C) 2021 Michael Moehler
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

/**
 * When the iterator is applied to the following array,
 *
 * <pre>[A,B,C,D]</pre>
 *
 * it returns, on one pass, the following result:
 *
 * <pre>[AB], [AC], [AD], [BC], [BD], [CD]</pre>
 *
 * Where <code>A..D</code> represent the columns of the matrix of indicators to be processed.
 */
class CombinationIterator implements Iterator<CombinationIterator.Combination> {
  private final int[][] content;
  private int i = 0;
  private int j = 1;
  private int l = 0;

  CombinationIterator(final int[][] content) {
    this.content = content;
  }

  @Override
  public boolean hasNext() {
    return (l < content.length * (content.length - 1) / 2);
  }

  @Override
  public Combination next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    final Combination combination = Combination.of(content[i], content[j]);

    if (j == content.length - 1) {
      i++;
      j = i + 1;
    } else {
      j++;
    }
    l++;

    return combination;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("CombinationIterator.remove");
  }

  static class Combination {
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
}
