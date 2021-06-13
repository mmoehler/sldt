package com.mmoehler.sldt.compress;

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
import com.mmoehler.sldt.Result;
import com.mmoehler.sldt.analysis.DefaultAnalyzer;
import com.mmoehler.sldt.intern.Indicator;
import com.mmoehler.sldt.intern.Indicators;
import com.mmoehler.test.fixtures.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class ConsolidatorTest {

  public static final String INDICATORS =
      ""
          + "YNNNNNNN"
          + "NYYYNNNN"
          + "NNNNYYYN"
          + "NNNNNNNY"
          + "NYNNYNNN"
          + "NNYNNYNN"
          + "NNNNYNYN"
          + "-X------"
          + "X-----XX"
          + "--XXXX--";

  Indicators allIndicators;

  @BeforeEach
  public void setUp() throws Exception {
    allIndicators = TestUtils.allocateIndicators(7,3, INDICATORS);
  }

  @AfterEach
  public void tearDown() throws Exception {
    allIndicators = null;
  }

  @Test
  public void testConditionCompleteness() throws Exception {
    System.out.println(Strings.repeat("-", 70));
    System.out.println(allIndicators);
    System.out.println(Strings.repeat("-", 70));

    final BitSet ruleFlags = Consolidator.indicatorsComplete(allIndicators);
    Assertions.assertThat(ruleFlags.cardinality()).isEqualTo(7);
  }

  @Test
  public void testGroupRulesByActions() {
    Indicators source = allIndicators;
    int countOfConditions = allIndicators.getCountOfConditions();
    final int countOfActions = allIndicators.getCountOfActions();
    final Map<Indicator[], List<Indicator[]>> grouped =
        source
            .cols()
            .collect(
                Collectors.groupingBy(
                    (s) -> (Arrays.copyOfRange(s, countOfConditions, countOfConditions+countOfActions, Indicator[].class)),
                    () -> new TreeMap<>((l1, r) -> Arrays.compare(l1, r, Comparator.comparingInt(Indicator::sign))),
                    Collectors.toUnmodifiableList()));

    grouped.entrySet().stream()
        .forEachOrdered(
            e0 -> {
              System.out.println(Arrays.toString(e0.getKey()));
              e0.getValue().forEach(l -> System.out.println("      " + Arrays.toString(l)));
            });
  }

  @Test
  public void testConsolidateHappyDay() {
    System.out.println(Strings.repeat("-", 70));
    System.out.println(allIndicators);
    System.out.println(Strings.repeat("-", 70));
    final Result<String> result = new DefaultAnalyzer().apply(allIndicators);
    if (result.isFailure()) {
      System.out.println(result.getCause().getMessage());
    }
    System.out.println(Strings.repeat("-", 70));
    Indicators reduced = Consolidator.consolidate(allIndicators);
    System.out.println(Strings.repeat("-", 70));
    System.out.println(reduced);
    System.out.println(Strings.repeat("-", 70));
    final Result<String> result1 = new DefaultAnalyzer().apply(reduced);
    if (result1.isFailure()) {
      System.out.println(result1.getCause().getMessage());
    }
    result1.get();
  }
}
