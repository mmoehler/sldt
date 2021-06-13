package com.mmoehler.sldt.intern;

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


import java.util.Objects;

public class Indicator {
  private final char sign;
  private final int row;
  private final int col;

  public static Indicator of(char sign, int row, int col) {
    return new Indicator(sign, row, col);
  }

  private Indicator(char sign, int row, int col) {
    this.sign = sign;
    this.row = row;
    this.col = col;
  }

  public char sign() {
    return sign;
  }

  public int row() {
    return row;
  }

  public int col() {
    return col;
  }

  public boolean isYes() {
    return sign == IndicatorSigns.YY;
  }

  public boolean isNo() {
    return sign == IndicatorSigns.NN;
  }

  public boolean isDontCare() {
    return sign == IndicatorSigns.MI;
  }

  //@Override
  public String toString1() {
    return String.format("%s(%02d:%02d)", sign, row, col);
  }

  @Override
  public String toString() {
    return String.format("%s", sign);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    var indicator = (Indicator) o;
    return sign == indicator.sign && row == indicator.row && col == indicator.col;
  }

  private Integer hc;

  @Override
  public int hashCode() {
    if (hc == null) {
      hc = Objects.hash(sign, row, col);
    }
    return hc;
  }
}
