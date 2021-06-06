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

import com.mmoehler.sldt.intern.Indicator;

import java.util.Comparator;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;

public final class Comparators {
  private Comparators() {}

  public static Comparator<Indicator[]> compareBySign() {
    return (l, r) -> {
      checkState (l.length == r.length, "Different length of arrays to compare!");
      for (var i = 0; i < l.length; i++) {
        int result = l[i].sign() - r[i].sign();
        if (0 != result) {
          return result;
        }
      }
      return 0;
    };
  }

  public static Comparator<Indicator[]> compareWithIgnoringRows(int... rowToBeIgnored) {
    return (l, r) -> {
      checkState(l.length == r.length, "Different length of arrays to compare!");
      int result;
      sort(rowToBeIgnored); // binarySearch requires sorted arrays
      for (var i = 0; i < l.length; i++) {
        if (binarySearch(rowToBeIgnored, i) < 0) { // nothing to ignore
          result = l[i].sign() - r[i].sign();
          if (0 != result) {
            return result;
          }
        }
      }
      return 0;
    };
  }

  public static Comparator<Indicator> rowFirstComparison() {
    return (l, r) -> {
      int tmp;
      return ((tmp = l.row() - r.row()) == 0) ? l.col() - r.col() : tmp;
    };
  }

  public static Comparator<Indicator> colFirstComparison() {
    return (l, r) -> {
      int tmp;
      return ((tmp = l.col() - r.col()) == 0) ? l.row() - r.row() : tmp;
    };
  }
}
