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
public final class IndicatorSigns {

  private IndicatorSigns() {}
  /* @formatter::off */
  public static final char ZZ = '\u0023'; /* # */
  public static final char AS = '\u002A'; /* * */
  public static final char MI = '\u002D'; /* - */
  public static final char LO = '\u003C'; /* < */
  public static final char EQ = '\u003D'; /* = */
  public static final char GT = '\u003E'; /* > */
  public static final char CC = '\u0043'; /* C */
  public static final char NN = '\u004E'; /* N */
  public static final char RR = '\u0052'; /* R */
  public static final char XX = '\u0058'; /* X */
  public static final char YY = '\u0059'; /* Y */
  public static final char TL = '\u007E'; /* ~ */
  public static final char NE = '\u2260'; /* ≠ */
  public static final char NI = '\u2262'; /* ≢ */
  /* @formatter:on */

  public static final String ALPHABET = "NY<>X≢*≠=-RC#~";
}
