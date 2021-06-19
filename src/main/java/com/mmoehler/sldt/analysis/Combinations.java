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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Combinations implements Iterable<Combination> {

  private final int[][] columns;

  private Combinations(final int[][] columns) {
    this.columns = columns;
  }

  public static Combinations of(final int[][] columns) {
    return new Combinations(columns);
  }

  @Override
  public Iterator<Combination> iterator() {
    return new Iterator<Combination>() {
      private int i = 0;
      private int j = 1;
      private int l = 0;

      @Override
      public boolean hasNext() {
        return (l < columns.length * (columns.length - 1) / 2);
      }

      @Override
      public Combination next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        final Combination combination = Combination.of(columns[i], columns[j]);
        if (j == columns.length - 1) {
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
    };
  }

  public Stream<Combination> stream() {
    return StreamSupport.stream(spliterator(), false);
  }
}
