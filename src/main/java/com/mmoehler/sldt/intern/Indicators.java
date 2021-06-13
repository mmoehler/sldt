package com.mmoehler.sldt.intern;

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

import com.mmoehler.sldt.utils.ArrayUtils;
import com.mmoehler.sldt.utils.Comparators;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.collect.ObjectArrays.concat;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.stream;
public class Indicators {

  /** The enum Orientation. */
  public enum Orientation {
    /** Row orientation. */
    ROW,
    /** Col orientation. */
    COL
  }

  private final int width;
  private final int height;
  private final int countOfConditions;
  private final int countOfActions;
  private final Indicator[] indicators;
  private final Orientation orientation;

  // -- cached, derived properties
  private Collection<Indicator[]> rows;
  private Collection<Indicator[]> cols;
  private Indicators conditionIndicators;
  private Indicators actionIndicators;

  // -- Factory methods -------------------------------------------

  /**
   * New builder step 01.
   *
   * @return the step 01
   */
  public static Step01 newBuilder() {
    return new Builder();
  }

  // -- Constructor -----------------------------------------------

  private Indicators(Builder builder) {
    this.width = builder.width;
    this.height = builder.height;
    this.countOfConditions = builder.countOfConditions;
    this.countOfActions = builder.countOfActions;
    this.indicators = builder.content;
    this.orientation = builder.orientation;
  }

  // -- Getter ----------------------------------------------------

  /**
   * Gets width.
   *
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Gets height.
   *
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Get content indicator [ ].
   *
   * @return the indicator [ ]
   */
  public Indicator[] getIndicators() {
    return copyOf(indicators, indicators.length);
  }

  /**
   * Gets count of conditions.
   *
   * @return the count of conditions
   */
  public int getCountOfConditions() {
    return countOfConditions;
  }

  /**
   * Gets count of actions.
   *
   * @return the count of conditions
   */
  public int getCountOfActions() {
    return countOfActions;
  }


  /**
   * Gets orientation.
   *
   * @return the orientation
   */
  public Orientation getOrientation() {
    return orientation;
  }

  // -- Object overrides ------------------------------------------

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Indicators that = (Indicators) o;
    return width == that.width
        && height == that.height
        && countOfConditions == that.countOfConditions
        && java.util.Arrays.equals(indicators, that.indicators)
        && orientation == that.orientation;
  }

  private Integer result;

  @Override
  public int hashCode() {
    if (result == null) {
      result = Objects.hash(width, height, countOfConditions, orientation);
      result = 31 * result + java.util.Arrays.hashCode(indicators);
    }
    return result;
  }

  @Override
  public String toString() {
    final Indicator[] copy = copyOf(this.indicators, indicators.length);
    return ArrayUtils.partitioning(copy, width).stream()
        .map(Arrays::toString)
        .collect(Collectors.joining(System.lineSeparator()));
  }

  /**
   * Internal state string.
   *
   * @return the string
   */
  public String internalState() {
    return "Indicators{" + "width=" + width +
            ", height=" + height +
            ", countOfConditions=" + countOfConditions +
            ", countOfActions=" + countOfActions +
            ", content=" + Arrays.toString(indicators) +
            ", orientation=" + orientation +
            '}';
  }

  /**
   * As java string string.
   *
   * @return the string
   */
  public String asJavaString() {
    final StringBuilder sb =
        new StringBuilder("final String s = \"\"").append(System.lineSeparator()).append("+ \"");
    for (var i = 0; i < indicators.length; i++) {
      if (i > 0 && (i % width) == 0) {
        sb.append("\"").append(System.lineSeparator()).append("+ \"");
      }
      sb.append(indicators[i].sign());
    }
    sb.append("\";");
    return sb.toString();
  }

  // -- Row and col access ----------------------------------------

  /**
   * Rows stream.
   *
   * @return the stream
   */
  public Stream<Indicator[]> rows() {
    if (rows == null) {
      Indicator[] copy =
          (orientation == Orientation.COL)
              ? stream(indicators).sorted(Comparators.rowFirstComparison()).toArray(Indicator[]::new)
              : stream(indicators).toArray(Indicator[]::new);
      rows = ArrayUtils.partitioning(copy, width);
    }
    return rows.stream();
  }

  public Indicator[] row(int r) {
    return Arrays.copyOfRange(indicators, r * width, r * width + width);
  }

  /**
   * Cols stream.
   *
   * @return the stream
   */
  public Stream<Indicator[]> cols() {
    if (cols == null) {
      Indicator[] copy;
      int size;
      if (orientation == Orientation.ROW) {
        copy = stream(indicators).sorted(Comparators.colFirstComparison()).toArray(Indicator[]::new);
        size = height;
      } else {
        copy = stream(indicators).toArray(Indicator[]::new);
        size = width;
      }
      cols = ArrayUtils.partitioning(copy, size);
    }
    return cols.stream();
  }

  /**
   * Action indicators indicators.
   *
   * @return the indicators
   */
  public Indicators actionIndicators() {
    if (actionIndicators == null) {
      splitIntoConditionsAndActions();
    }
    return actionIndicators;
  }

  /**
   * Condition indicators indicators.
   *
   * @return the indicators
   */
  public Indicators conditionIndicators() {
    if (conditionIndicators == null) {
      splitIntoConditionsAndActions();
    }
    return conditionIndicators;
  }

  private CompletableFuture<Void> createActionIndicators(Map<Boolean, List<Indicator[]>> map) {
    return CompletableFuture.runAsync(
        () -> {
          final Indicator[] content =
              map.get(Boolean.TRUE).stream()
                  .reduce(new Indicator[0], (l, r) -> concat(l, r, Indicator.class));
          actionIndicators =
              Indicators.newBuilder()
                  .countOfConditions(0)
                  .countOfActions(countOfActions)
                  .orientation(Orientation.ROW)
                  .content(content)
                  .build();
        });
  }

  private CompletableFuture<Void> createConditionIndicators(Map<Boolean, List<Indicator[]>> map) {
    return CompletableFuture.runAsync(
        () -> {
          final Indicator[] content =
              map.get(Boolean.FALSE).stream()
                  .reduce(new Indicator[0], (l, r) -> concat(l, r, Indicator.class));
          conditionIndicators =
              Indicators.newBuilder()
                  .countOfConditions(countOfConditions)
                  .countOfActions(0)
                  .orientation(Orientation.ROW)
                  .content(content)
                  .build();
        });
  }

  private void splitIntoConditionsAndActions() {
    final Map<Boolean, List<Indicator[]>> map =
        rows()
            .collect(Collectors.partitioningBy(row -> stream(row).anyMatch(i -> i.sign() == 'X')));

    CompletableFuture.allOf(createConditionIndicators(map), createActionIndicators(map)).join();
  }

  /**
   * Transpose indicators.
   *
   * @return the indicators
   * @deprecated use Indicators#cols() od Indicators#rows() instead
   */
  @Deprecated
  public Indicators transpose() {
    Orientation newOrientation =
        (orientation == Orientation.ROW) ? Orientation.COL : Orientation.ROW;
    Comparator<Indicator> comparator =
        (newOrientation == Orientation.ROW) ? Comparators.rowFirstComparison() : Comparators.colFirstComparison();

    Indicator[] transposed = stream(indicators).sorted(comparator).toArray(Indicator[]::new);

    //noinspection SuspiciousNameCombination
    return Indicators.newBuilder()
        .countOfConditions(countOfConditions)
        .countOfActions(countOfActions)
        .orientation(newOrientation)
        .content(transposed)
        .build();
  }

  /** The interface Step 01. */
  // -- Initialization flow ---------------------------------------
  public interface Step01 {
    /**
     * Count of conditions step 02.
     *
     * @param count the count
     * @return the step 02
     */
    Step02 countOfConditions(int count);
  }

  /** The interface Step 02. */
  public interface Step02 {
    /**
     * Width step 03.
     *
     * @param count the count of actions
     * @return the step 03
     */
    Step03 countOfActions(int count);
  }

  /** The interface Step 04. */
  public interface Step04 {
    /**
     * Content step 05.
     *
     * @param data the data
     * @return the step 05
     */
    Step05 content(String data);

    /**
     * Content step 05.
     *
     * @param data the data
     * @return the step 05
     */
    Step05 content(Indicator[] data);
  }

  /** The interface Step 03. */
  public interface Step03 {
    /**
     * Orientation step 04.
     *
     * @param orientation the orientation
     * @return the step 04
     */
    Step04 orientation(Orientation orientation);
  }

  /** The interface Step 05. */
  public interface Step05 {
    /**
     * Build indicators.
     *
     * @return the indicators
     */
    Indicators build();
  }

  // -- Builder ---------------------------------------------------

  /** The type Builder. */
  public static class Builder implements Step01, Step02, Step03, Step04, Step05 {

    private int width;
    private int height;
    private Indicator[] content;
    private int countOfConditions;
    private int countOfActions;
    private Orientation orientation;

    @Override
    public Step02 countOfConditions(int count) {
      this.countOfConditions = count;
      return this;
    }

    @Override
    public Step05 content(String data) {
      this.height = countOfActions + countOfConditions;
      this.width = data.length() / height;
      this.content =
          IntStream.range(0, data.length())
              .mapToObj(
                  i -> {
                    final int row = (orientation == Orientation.ROW) ? (i / width) : (i % width);
                    final int col = (orientation == Orientation.ROW) ? (i % width) : (i / width);
                    return Indicator.of(data.charAt(i), row, col);
                  })
              .toArray(Indicator[]::new);
      return this;
    }

    @Override
    public Step05 content(Indicator[] data) {
      this.height = countOfActions + countOfConditions;
      this.width = data
              .length / height;

      this.content = (orientation == Orientation.ROW)
              ? stream(data).sorted(Comparators.rowFirstComparison()).toArray(Indicator[]::new)
              : stream(data).sorted(Comparators.colFirstComparison()).toArray(Indicator[]::new);
      return this;
    }

    @Override
    public Step03 countOfActions(int count) {
      countOfActions = count;
      return this;
    }

    @Override
    public Step04 orientation(Orientation orientation) {
      this.orientation = orientation;
      return this;
    }

    @Override
    public Indicators build() {
      return new Indicators(this);
    }
  }
}
