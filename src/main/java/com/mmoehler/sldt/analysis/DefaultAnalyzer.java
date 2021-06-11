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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

public class DefaultAnalyzer implements Analyzer {

  @Override
  public Result<String> apply(Indicators indicators) {

    var conditions = indicators.conditionIndicators();
    var actions = indicators.actionIndicators();
    IntStream conditionResults;
    IntStream actionResults;

    try {
      conditionResults = processConditions.apply(conditions);
      actionResults = processActions.apply(actions);
    } catch (Exception e) {
      return Result.failure(e);
    }

    // combine all resulting vectors and ...
    final String result =
        IntStreams.zip(actionResults, conditionResults, combineAllCombinationResults())
            // ... translate them to its character representations and ...
            .mapToObj(i -> String.valueOf((char) i))
            .collect(Collectors.joining());

    // ... prepare the result.
        return (Strings.isNullOrEmpty(result) || result.chars().allMatch(c -> c == IndicatorSigns.MI))
            ? Result.success(result)
            : Result.failure(
                new IllegalStateException(
                    AnalysisResultEmitter.INSTANCE.apply(result, indicators.getWidth())));
  }

  private final Function<Indicators, IntStream> processConditions =
      conditions -> {
        final Builder<int[]> outConditions = Stream.builder();
        final Indicator[][] inConditions = conditions.cols().toArray(Indicator[][]::new);
        for (int i = 0; i < inConditions.length - 1; i++) {
          for (var j = i + 1; j < inConditions.length; j++) {
            final IntStream leftStream = Arrays.stream(inConditions[i]).mapToInt(Indicator::sign);
            final IntStream rightStream = Arrays.stream(inConditions[j]).mapToInt(Indicator::sign);
            final int[] collected =
                IntStreams.zip(leftStream, rightStream, combineConditions()).toArray();
            outConditions.add(collected);
          }
        }
        return outConditions
            .build()
            .map(c -> Arrays.stream(c).reduce(combineConditionCombinationResults()))
            .mapToInt(OptionalInt::orElseThrow);
      };

  private final Function<Indicators, IntStream> processActions =
      actions -> {
        final Builder<int[]> outActions = Stream.builder();
        final Indicator[][] inActions = actions.cols().toArray(Indicator[][]::new);
        for (var i = 0; i < inActions.length - 1; i++) {
          for (var j = i + 1; j < inActions.length; j++) {
            final IntStream leftStream = Arrays.stream(inActions[i]).mapToInt(Indicator::sign);
            final IntStream rightStream = Arrays.stream(inActions[j]).mapToInt(Indicator::sign);
            final int[] collected =
                IntStreams.zip(leftStream, rightStream, combineActions()).toArray();
            outActions.add(collected);
          }
        }
        return outActions
            .build()
            .map(c -> Arrays.stream(c).reduce(combineActionCombinationResults()))
            .mapToInt(OptionalInt::orElseThrow);
      };
}
