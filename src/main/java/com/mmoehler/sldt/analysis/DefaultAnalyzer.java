package com.mmoehler.sldt.analysis;

/*-
 * #%L
 * dt
 * %%
 * Copyright (C) 2016 - 2020 Michael Moehler
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

import com.google.common.base.Strings;
import com.mmoehler.sldt.Analyzer;
import com.mmoehler.sldt.Result;
import com.mmoehler.sldt.intern.Indicator;
import com.mmoehler.sldt.intern.IndicatorSigns;
import com.mmoehler.sldt.intern.Indicators;
import com.mmoehler.sldt.utils.IntStreams;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Spliterator;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;

public class DefaultAnalyzer implements Analyzer {

  @Override
  public Result<String> apply(Indicators indicators) {

    var conditions = indicators.conditionIndicators();
    var actions = indicators.actionIndicators();

    // combine all resulting vectors and ...
    final String result =
        IntStreams.zip(
                processActionCombinations(actions),
                processConditionCombinations(conditions),
                combineAllCombinationResults())
            // ... translate them to its character representations and ...
            .mapToObj(i -> String.valueOf((char) i))
            .collect(Collectors.joining());

    // ... prepare and return the result.
    return (Strings.isNullOrEmpty(result) || result.chars().allMatch(c -> c == IndicatorSigns.MI))
        ? Result.success(result)
        : Result.failure(
            new AnalysisException(
                AnalysisResultEmitter.INSTANCE.apply(result, indicators.getWidth()), result));
  }

  private IntStream processConditionCombinations(Indicators conditionIndicators) {
    return processColumnCombination(
        conditionIndicators, combineConditions(), combineConditionCombinationResults());
  }

  private IntStream processActionCombinations(Indicators actionIndicators) {
    return processColumnCombination(
        actionIndicators, combineActions(), combineActionCombinationResults());
  }

  private IntStream processColumnCombination(
      Indicators indicators,
      IntBinaryOperator combineColumns,
      IntBinaryOperator combineColumnsCombinationResults) {

    return Combinations.of(prepareIndicators(indicators)).stream()
            .map( // combine the columns
                    combination ->
                            IntStreams.zip(combination.getLeft(), combination.getRight(), combineColumns))
            .map( // combine the column combination results
                    columnCombinationResults ->
                            columnCombinationResults.reduce(combineColumnsCombinationResults))
            .mapToInt(OptionalInt::orElseThrow);
  }

  private int[][] prepareIndicators(Indicators indicators) {
    return indicators // make the columns combinable
        .cols()
        .map(col1 -> Arrays.stream(col1).mapToInt(Indicator::sign).toArray())
        .toArray(int[][]::new);
  }
}
