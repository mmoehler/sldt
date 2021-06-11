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

import com.mmoehler.sldt.Result;
import com.mmoehler.sldt.Analyzer;
import com.mmoehler.sldt.intern.Indicators;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mmoehler.test.fixtures.Tests.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StructuralAnalysisTest {

  Analyzer analysis;

  @BeforeEach
  public void setUp() {
    analysis = new DefaultAnalyzer();
  }

  @AfterEach
  public void tearDown() {
    analysis = null;
  }


  @Test
  @DisplayName("Check why this test fails!")
  public void testAnalysis() throws Exception {

    of(analysis)
        .given(Prepare.nothing())
        .when(
            a -> {
              final Indicators indicators =
                  Indicators.newBuilder()
                      .countOfConditions(4)
                      .width(4)
                      .orientation(Indicators.Orientation.ROW)
                      .content(""
                              + "YYY-"
                              + "-NNN"
                              + "---N"
                              + "YYNN"
                              + "XXX-"
                              + "X--X"
                              + "-XX-")
                      .build();
              System.out.println(indicators);
              actual(a.apply(indicators));
              return a;
            })
        .then(
            dt -> {
              Result actual = actual();
              // assertThat(actual.isFailure()).isTrue();
              System.out.println(actual.getCause().getMessage());
            })
        .call();
  }

}
