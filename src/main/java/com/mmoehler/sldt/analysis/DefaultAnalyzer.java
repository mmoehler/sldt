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

import com.mmoehler.sldt.Analysis;
import com.mmoehler.sldt.Analyzer;
import com.mmoehler.sldt.intern.Indicator;
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
  public Analysis apply(Indicators indicators) {

    var conditions = indicators.conditionIndicators();
    var actions = indicators.actionIndicators();

    IntStream conditionResults;
    IntStream actionResults;

    try {

      conditionResults = processConditions.apply(conditions);
      actionResults = processActions.apply(actions);

    } catch (Exception e) {
      return DefaultAnalysis.of(e);
    }

    // combine all resulting vectors and ...
    final String result =
        IntStreams.zip(actionResults, conditionResults, combineAllCombinationResults())
            // ... translate them to its character representations.
            .mapToObj(i -> String.valueOf((char) i))
            .collect(Collectors.joining());

    return DefaultAnalysis.of(result, indicators.getWidth());
  }

  private final Function<Indicators, IntStream> processConditions =
      conditions -> {
        final Builder<int[]> outConditions = Stream.builder();
        final Indicators inConditions = conditions.transpose();

        for (var i = 0; i < inConditions.getHeight() - 1; i++) {
          for (var j = 1; j < inConditions.getHeight(); j++) {

            if (j > i) {

              final IntStream leftStream =
                  Arrays.stream(inConditions.row(i)).mapToInt(Indicator::sign);
              final IntStream rightStream =
                  Arrays.stream(inConditions.row(i)).mapToInt(Indicator::sign);

              final int[] collected =
                  IntStreams.zip(leftStream, rightStream, combineConditions()).toArray();

              outConditions.add(collected);
            }
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
        final Indicators inActions = actions.transpose();

        final int height = inActions.getHeight();
        for (var i = 0; i < height - 1; i++) {
          for (var j = 1; j < height; j++) {
            if (j > i) {

              final IntStream leftStream =
                  Arrays.stream(inActions.row(i)).mapToInt(Indicator::sign);

              final IntStream rightStream =
                  Arrays.stream(inActions.row(j)).mapToInt(Indicator::sign);

              final int[] collected =
                  IntStreams.zip(leftStream, rightStream, combineActions()).toArray();

              outActions.add(collected);
            }
          }
        }

        return outActions
            .build()
            .map(c -> Arrays.stream(c).reduce(combineActionCombinationResults()))
            .mapToInt(OptionalInt::orElseThrow);
      };
}
