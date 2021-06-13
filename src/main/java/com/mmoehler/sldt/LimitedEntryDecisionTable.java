package com.mmoehler.sldt;

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

import com.mmoehler.sldt.analysis.DefaultAnalyzer;
import com.mmoehler.sldt.compress.Consolidator;
import com.mmoehler.sldt.intern.IndicatorSigns;
import com.mmoehler.sldt.intern.Indicators;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public final class LimitedEntryDecisionTable<T, R> implements DecisionTable<T, R> {
  /** A matrix of indicators which defines the different rules */
  private final Indicators indicators;
  /** An array of conditions as logical part of the defined rules */
  private final Predicate<T>[] conditions;
  /** An array of functions which defines the the action part of the defined rules */
  private final Function<T, R>[] actions;
  /**
   * This is a single decision action that essentially says that if any of the previous rules in
   * table were not triggered, than take this action(s)
   */
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private final Optional<Function<T, R>> elseAction;

  private final int[] decisionMatrix;
  private final int[] maskMatrix;

  /**
   * New builder step 01.
   *
   * @param <I> the type parameter
   * @param <O> the type parameter
   * @return the step 01
   */
  public static <I, O> Step01<I, O> newBuilder() {
    return new Builder<>();
  }

  private LimitedEntryDecisionTable(Builder<T, R> builder) {
    this.indicators = builder.indicators;
    this.conditions = builder.conditions;
    this.actions = builder.actions;
    this.elseAction = Optional.ofNullable(builder.elseAction);
    this.maskMatrix = builder.maskMatrix;
    this.decisionMatrix = builder.decisionMatrix;
  }

  @Override
  public Indicators getIndicators() {
    return indicators;
  }

  @Override
  public Predicate<T>[] getConditions() {
    return conditions;
  }

  @Override
  public Function<T, R>[] getActions() {
    return actions;
  }

  @Override
  public Optional<Function<T, R>> getElseAction() {
    return elseAction;
  }

  @Override
  public R apply(T t) {
    // STEP 1 ---------------------------------------------
    // eval the conditions once! This can be expensive!
    final int conditionTestResult = getConditionTestResult(t);

    // STEP 2 ---------------------------------------------
    // Find the index of a matching column in the columns list and use it as to
    // determine the action
    // to execute. Action to execute = actions[determined index].
    int actionIdx = calculateMatchingRuleIndex(conditionTestResult);

    // If no rules matches, then the decisiontable was incomplete and a
    // corresponding exception is
    // thrown
    return applyAction(t, actionIdx);
  }

  private R applyAction(final T t, final int actionIdx) {
    return (actionIdx < 0)
        ? elseAction
            .orElseThrow(() -> new IllegalStateException("decision table is incomplete!"))
            .apply(t)
        : actions[actionIdx].apply(t);
  }

  private int getConditionTestResult(final T t) {
    @SuppressWarnings("unchecked") final CompletableFuture<Integer>[] futures =
        IntStream.range(0, conditions.length)
            .mapToObj(i -> CompletableFuture.supplyAsync(() -> conditions[i].test(t) ? 1 << i : 0))
            .toArray(CompletableFuture[]::new);

    return CompletableFuture.allOf(futures)
        .thenApply(f -> Arrays.stream(futures).mapToInt(CompletableFuture::join).sum())
        .join();
  }

  private int calculateMatchingRuleIndex(int conditionMask) {
    for (var i = 0; i < decisionMatrix.length; i++) {
      if ((maskMatrix[i] & conditionMask) == decisionMatrix[i]) {
        return i;
      }
    }
    return -1;
  }

  // We use a staged builder because the order of initialization must be
  // respected, since there are
  // dependencies between the individual initialization steps.

  @SuppressWarnings("unchecked")
  interface Step01<I, O> {
    Step02<I, O> conditions(Predicate<I>... conditions);
  }

  @SuppressWarnings("unchecked")
  interface Step02<I, O> {
    Step03<I, O> actions(Function<I, O>... actions);
  }

  interface Step03<I, O> extends Step04<I, O> {
    Step04<I, O> elseAction(Function<I, O> action);
  }

  interface Step04<I, O> {
    Step05<I, O> indicators(String indicatorString);
  }

  interface Step05<I, O> {
    Step06<I, O> enableCompression();
    LimitedEntryDecisionTable<I, O> build();
  }

  interface Step06<I, O> {
    Step07<I, O> enableStructuralCheck();
    LimitedEntryDecisionTable<I, O> build();
  }

  interface Step07<I, O> {
    LimitedEntryDecisionTable<I, O> build();
  }

  static class Builder<I, O>
      implements Step01<I, O>, Step02<I, O>, Step03<I, O>, Step04<I, O>, Step05<I, O>, Step06<I, O>, Step07<I, O> {
    private static final IntUnaryOperator mapToDecision = c -> c == IndicatorSigns.YY ? 1 : 0;
    private static final IntUnaryOperator mapToMask = c -> c == IndicatorSigns.MI ? 0 : 1;

    private Indicators indicators;
    private Predicate<I>[] conditions;
    private Function<I, O>[] actions;
    private Function<I, O> elseAction;
    private String indicatorString;
    private boolean compressionEnabled = false;
    private boolean structuralCheckEnabled = false;

    private int[] decisionMatrix;
    private int[] maskMatrix;

    @SafeVarargs
    @Override
    public final Step02<I, O> conditions(Predicate<I>... conditions) {
      this.conditions = conditions;
      return this;
    }

    @SafeVarargs
    @Override
    public final Step03<I, O> actions(Function<I, O>... actions) {
      this.actions = actions;
      return this;
    }

    @Override
    public Step04<I, O> elseAction(Function<I, O> action) {
      this.elseAction = action;
      return this;
    }

    @Override
    public Step05<I, O> indicators(String indicatorString) {
      this.indicatorString = indicatorString;
      return this;
    }

    @Override
    public Step06<I, O> enableCompression() {
      this.compressionEnabled = true;
      return this;
    }

    @Override
    public Step07<I, O> enableStructuralCheck() {
      this.compressionEnabled = true;
      return this;
    }

    @Override
    public LimitedEntryDecisionTable<I, O> build() {
      final Indicators tmp = Indicators.newBuilder()
              .countOfConditions(conditions.length)
              .countOfActions(actions.length)
              .orientation(Indicators.Orientation.ROW)
              .content(indicatorString)
              .build();

      this.indicators = (compressionEnabled)
              ? Consolidator.consolidate(tmp)
              : tmp;

      if(structuralCheckEnabled) {
        final Result<String> result = new DefaultAnalyzer().apply(this.indicators);
        if(result.isFailure()) {
          result.get(); // in this case the transported exception which contains the description is thrown directly
        }
      }

      this.maskMatrix = createConditionMatrix(mapToMask);
      this.decisionMatrix = createConditionMatrix(mapToDecision);
      return new LimitedEntryDecisionTable<>(this);
    }

    private int[] createConditionMatrix(IntUnaryOperator op) {
      return this.indicators
          .cols()
          .mapToInt(
              col -> {
                final int[] tmp =
                    IntStream.range(0, conditions.length).map(i -> col[i].sign()).map(op).toArray();
                return IntStream.range(0, tmp.length).map(i -> 0 == tmp[i] ? 0 : (1 << i)).sum();
              })
          .toArray();
    }
  }
}
