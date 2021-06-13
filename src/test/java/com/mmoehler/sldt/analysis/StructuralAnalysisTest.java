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

import com.mmoehler.sldt.Analyzer;
import com.mmoehler.sldt.Result;
import com.mmoehler.sldt.intern.Indicators;
import com.mmoehler.test.fixtures.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mmoehler.test.fixtures.Tests.*;

class StructuralAnalysisTest {

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
  @DisplayName("Check Clash")
  void testClash() throws Exception {
    of(analysis)
        .given(Prepare.nothing())
        .when(
            a -> {
              final Indicators indicators =
                  TestUtils.allocateIndicators(
                      2,
                      1,
                      "-Y" //
                          +"Y-" //
                          + "XX" //
                      );
              actual(a.apply(indicators));
              return a;
            })
        .then(
            dt -> {
              Result actual = actual();
              Assertions.assertThat(actual.isFailure()).isTrue();
              org.junit.jupiter.api.Assertions.assertThrows(
                  AnalysisException.class,
                  () -> {
                    actual.get();
                  });
                Assertions.assertThat(((AnalysisException)actual.getCause()).getRawResult()).isEqualTo("X");
            })
        .call();
  }

    @Test
    @DisplayName("Check Inclusion")
    void testInclusion() throws Exception {
        of(analysis)
                .given(Prepare.nothing())
                .when(
                        a -> {
                            final Indicators indicators =
                                    TestUtils.allocateIndicators(
                                            1,
                                            1,
                                            "-Y" //
                                                    + "-X" //
                                    );
                            actual(a.apply(indicators));
                            return a;
                        })
                .then(
                        dt -> {
                            Result actual = actual();
                            Assertions.assertThat(actual.isFailure()).isTrue();
                            org.junit.jupiter.api.Assertions.assertThrows(
                                    AnalysisException.class,
                                    () -> {
                                        actual.get();
                                    });
                            Assertions.assertThat(((AnalysisException)actual.getCause()).getRawResult()).isEqualTo(">");
                        })
                .call();
    }

    @Test
    @DisplayName("Check Exclusion")
    void testExclusion() throws Exception {
        of(analysis)
                .given(Prepare.nothing())
                .when(
                        a -> {
                            final Indicators indicators =
                                    TestUtils.allocateIndicators(
                                            1,
                                            1,
                                            "YN" //
                                                    + "-X" //
                                    );
                            actual(a.apply(indicators));
                            return a;
                        })
                .then(
                        dt -> {
                            Result actual = actual();
                            Assertions.assertThat(actual.isSuccess()).isTrue();
                            Assertions.assertThat(actual.get()).isEqualTo("-");
                        })
                .call();
    }

    @Test
    @DisplayName("Check Compression Note")
    void testCompressionNote() throws Exception {
        of(analysis)
                .given(Prepare.nothing())
                .when(
                        a -> {
                            final Indicators indicators =
                                    TestUtils.allocateIndicators(
                                            1,
                                            1,
                                            "YN" //
                                                    + "XX" //
                                    );
                            actual(a.apply(indicators));
                            return a;
                        })
                .then(
                        dt -> {
                            Result actual = actual();
                            Assertions.assertThat(actual.isFailure()).isTrue();
                            org.junit.jupiter.api.Assertions.assertThrows(
                                    AnalysisException.class,
                                    () -> {
                                        actual.get();
                                    });
                            Assertions.assertThat(((AnalysisException)actual.getCause()).getRawResult()).isEqualTo("*");
                        })
                .call();
    }

    @SuppressWarnings("SpellCheckingInspection")
  @Test
  @DisplayName("Check Inclusion, Compression Note, Clash")
  void testMultipleIssues() throws Exception {
    of(analysis)
        .given(Prepare.nothing())
        .when(
            a -> {
              final Indicators indicators =
                  TestUtils.allocateIndicators(
                      4,
                      3,
                      "" + "YYY-" //
                          + "-NNN" //
                          + "---N" //
                          + "YYNN" //
                          + "XXX-" //
                          + "X--X" //
                          + "-XX-" //
                      );
              actual(a.apply(indicators));
              return a;
            })
        .then(
            dt -> {
              Result actual = actual();
              Assertions.assertThat(actual.isFailure()).isTrue();
                org.junit.jupiter.api.Assertions.assertThrows(
                        AnalysisException.class,
                        () -> {
                            actual.get();
                        });
                Assertions.assertThat(((AnalysisException)actual.getCause()).getRawResult()).isEqualTo(">--*-X");
            })
        .call();
  }
}
