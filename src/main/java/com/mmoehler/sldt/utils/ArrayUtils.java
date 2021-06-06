package com.mmoehler.sldt.utils;

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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;

public final class ArrayUtils {
  private ArrayUtils() {}

  public static <T> List<T[]> partitioning(T[] array, int chunkSize) {
    int numOfChunks = (int) Math.ceil((double) array.length / chunkSize);
    return IntStream.range(0, numOfChunks)
        .mapToObj(
            i -> {
              final int start = i * chunkSize;
              return copyOfRange(array, start, start + chunkSize);
            })
        .collect(Collectors.toList());
  }

    public static <T> boolean contains(T[] array, T t) {
      return Arrays.asList(array).contains(t);
    }

    public static <T> boolean contains(T[] array, T t, Comparator<T> comparator) {
        return stream(array).anyMatch(e -> comparator.compare(t,e) == 0);
    }
}
