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

import java.util.PrimitiveIterator.OfInt;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public enum AnalysisResultEmitter implements BiFunction<String, Integer, String> {

  INSTANCE;

  private static final String STRUCTURE_ANALYSIS_RULE = "STRUCTURE ANALYSIS RULE %2s ";
  private static final String STR_EMPTY = "";
  private static final String STR_SPACE = " ";
  private static final char CHR_SPACE = ' ';
  private static final String STR_FILLER = ". ";

  @Override
  public String apply(String analysisResult, Integer countRules) {

    var p = new Printer();

    String header =
        IntStream.rangeClosed(1, countRules)
            .mapToObj(
                i ->
                    Strings.padStart(
                        (i > 9 && i % 10 == 0) ? (String.valueOf(i / 10)) : STR_SPACE,
                        2,
                        CHR_SPACE))
            .reduce(STR_EMPTY, (a, b) -> a + b);

    p.printp(header, STRUCTURE_ANALYSIS_RULE.length() + header.length() - 2);

    final OfInt indices = analysisResult.chars().iterator();
    header =
        IntStream.rangeClosed(1, countRules)
            .mapToObj(i -> Strings.padStart(String.valueOf(i % 10), 2, CHR_SPACE))
            .reduce(STR_EMPTY, (a, b) -> a + b);

    p.printp(header, STRUCTURE_ANALYSIS_RULE.length() + header.length() - 2);

    for (var i = 0; i < countRules - 1; i++) {
      p.printf(((i + 1) % 10));
      for (var j = 0; j < countRules; j++) {
        if (j > i) {
          final char c = (char) indices.nextInt();
          p.print(c + STR_SPACE);
        } else {
          p.print(STR_FILLER);
        }
      }
      p.crlf();
    }
    p.println("-----------------------------------------------");
    p.println("R   Redundancy");
    p.println("C   Contradiction");
    p.println("<   Inclusion");
    p.println(">   Inclusion");
    p.println("-   Exclusion");
    p.println("X   Clash");
    p.println("*   Compression Note");
    p.println("-----------------------------------------------");
    p.crlf();

    return String.valueOf(p);
  }

  static class Printer {

    private final StringBuilder sb = new StringBuilder();

    void print(String s) {
      sb.append(s);
    }

    void printf(Object... args) {
      sb.append(String.format(AnalysisResultEmitter.STRUCTURE_ANALYSIS_RULE, args));
    }

    void println(String s) {
      sb.append(s).append(System.lineSeparator());
    }

    void printp(String s, int minLen) {
      sb.append(Strings.padStart(s, minLen, AnalysisResultEmitter.CHR_SPACE)).append(System.lineSeparator());
    }

    void crlf() {
      crlf(1);
    }

    void crlf(int numReps) {
      sb.append(String.valueOf(System.lineSeparator()).repeat(Math.max(0, numReps)));
    }

    @Override
    public String toString() {
      return sb.toString();
    }
  }
}
