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

import com.mmoehler.sldt.Result;
import com.mmoehler.sldt.analysis.AnalysisException;
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
    allIndicators = TestUtils.allocateIndicators(7, 3, INDICATORS);
  }

  @AfterEach
  public void tearDown() throws Exception {
    allIndicators = null;
  }

  @Test
  public void testConditionCompleteness() throws Exception {
    final BitSet ruleFlags = Consolidator.indicatorsComplete(allIndicators);
    Assertions.assertThat(ruleFlags.cardinality()).isEqualTo(7);
  }


  @Test
  public void testConsolidateHappyDay() {
    final Result<String> result = new DefaultAnalyzer().apply(allIndicators);
    org.junit.jupiter.api.Assertions.assertThrows(AnalysisException.class, result::get);

    Indicators reduced = Consolidator.consolidate(allIndicators);

    final Result<String> result1 = new DefaultAnalyzer().apply(reduced);
    Assertions.assertThat(result1.isSuccess()).isTrue();
  }
}
