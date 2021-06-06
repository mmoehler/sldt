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
import com.mmoehler.sldt.intern.Indicators;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mmoehler.test.fixtures.Tests.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DefaultStructuralAnalysisTest {

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
  public void apply() throws Exception {

    of(analysis)
        .given(Prepare.nothing())
        .when(
            a -> {
              final Indicators indicators =
                  Indicators.newBuilder()
                      .countOfConditions(3)
                      .width(4)
                      .orientation(Indicators.Orientation.ROW)
                      .content("" + "YYNN" + "-YYN" + "YN--" + "X---" + "-X--" + "--X-" + "---X")
                      .build();
              actual(a.apply(indicators));
              return a;
            })
        .then(
            dt -> {
              Analysis actual = actual();
              assertThat(actual.isSuccess()).isTrue();
            })
        .call();
  }

  @Test
  public void apply0() throws Exception {

    of(analysis)
        .given(Prepare.nothing())
        .when(
            a -> {
              final Indicators indicators =
                  Indicators.newBuilder()
                      .countOfConditions(3)
                      .width(8)
                      .orientation(Indicators.Orientation.ROW)
                      .content(
                          ""
                              + "YYYYNNNN"
                              + "YYNNYYNN"
                              + "YNYNYNYN"
                              + "X-------"
                              + "-X------"
                              + "--X-----"
                              + "---X----"
                              + "----X---"
                              + "-----X--"
                              + "------X-"
                              + "-------X")
                      .build();
              actual(a.apply(indicators));
              return a;
            })
        .then(
            dt -> {
              Analysis actual = actual();
              assertThat(actual.isSuccess()).isTrue();
            })
        .call();
  }

  //@Test
  @DisplayName("Check why this test fails!")
  public void apply1() throws Exception {

    of(analysis)
        .given(Prepare.nothing())
        .when(
            a -> {
              final Indicators indicators =
                  Indicators.newBuilder()
                      .countOfConditions(7)
                      .width(8)
                      .orientation(Indicators.Orientation.ROW)
                      .content(
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
                              + "--XXXX--")
                      .build();
              System.out.println(indicators);
              actual(a.apply(indicators));
              return a;
            })
        .then(
            dt -> {
              Analysis actual = actual();
              assertThat(actual.isFailure()).isTrue();
              System.out.println(actual.getDetailInfo().get());
            })
        .call();
  }

  @Test
  public void applyCleared1() throws Exception {

    of(analysis)
        .given(Prepare.nothing())
        .when(
            a -> {
              final Indicators indicators =
                  Indicators.newBuilder()
                      .countOfConditions(7)
                      .width(7)
                      .orientation(Indicators.Orientation.ROW)
                      .content(
                          "" + "NNYNNNN" + "NNNNNYY" + "NYNYYNN" + "YNNNNNN" + "NNNNYNY" + "NNNYN-N"
                              + "NYNNYNN" + "------X" + "XXX----" + "---XXX-")
                      .build();
              actual(a.apply(indicators));
              return a;
            })
        .then(
            dt -> {
              Analysis actual = actual();
              assertThat(actual.isSuccess()).isTrue();
            })
        .call();
  }

  @Test
  public void testDetectSplitIndex() {
    int width = 7;
    String s =
        "" + "NNYNNNN" + "NNNNNYY" + "NYNYYNN" + "YNNNNNN" + "NNNNYNY" + "NNNYN-N" + "NYNNYNN"
            + "------X" + "XXX----" + "---XXX-";

    String[] expected = {
      "NNYNNNNNNNNNYYNYNYYNNYNNNNNNNNNNYNYNNNYN-NNYNNYNN", "------XXXX-------XXX-"
    };

    String[] result = {};
    for (int i = 0; i < s.length(); i += width) {
      if (s.substring(i, i + width).indexOf('X') >= 0) {
        result = new String[] {s.substring(0, i), s.substring(i)};
        break;
      }
    }
    assertThat(result).isEqualTo(expected);
  }
}
