package com.mmoehler.sldt;

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

import com.mmoehler.sldt.intern.Indicators;
import com.mmoehler.sldt.utils.SparseCharMatrix;

import java.util.function.Function;
import java.util.function.IntBinaryOperator;

import static com.mmoehler.sldt.intern.IndicatorSigns.*;

public interface Analyzer extends Function<Indicators, Result<String>> {

  SparseCharMatrix ACTION_COMPARISON =
      SparseCharMatrix.newBuilder()
          .indicators(ALPHABET)
          .put(XX, XX, EQ)
          .put(XX, MI, NE)
          .put(MI, XX, NE)
          .put(MI, MI, EQ)
          .build();

  SparseCharMatrix ACTION_COMPARISON_RESULT =
      SparseCharMatrix.newBuilder()
          .indicators(ALPHABET)
          .put(EQ, EQ, EQ)
          .put(EQ, NE, NE)
          .put(NE, EQ, NE)
          .put(NE, NE, NE)
          .build();

  SparseCharMatrix COMBINATION_RESULT =
      SparseCharMatrix.newBuilder()
          .indicators(ALPHABET)
          .put(EQ, EQ, RR)
          .put(EQ, LO, AS)
          .put(EQ, GT, AS)
          .put(EQ, NE, AS)
          .put(EQ, NI, MI)
          .put(EQ, XX, XX)
          .put(NE, EQ, CC)
          .put(NE, LO, LO)
          .put(NE, GT, GT)
          .put(NE, NE, MI)
          .put(NE, NI, MI)
          .put(NE, XX, XX)
          .build();

  SparseCharMatrix CONDITION_COMPARISON =
      SparseCharMatrix.newBuilder()
          .indicators(ALPHABET)
          .put(YY, YY, EQ)
          .put(YY, NN, NE)
          .put(YY, MI, LO)
          .put(NN, YY, NE)
          .put(NN, NN, EQ)
          .put(NN, MI, LO)
          .put(MI, YY, GT)
          .put(MI, NN, GT)
          .put(MI, MI, EQ)
          .build();

  SparseCharMatrix CONDITION_COMPARISON_RESULT =
      SparseCharMatrix.newBuilder()
          .indicators(ALPHABET)
              /*
          .put(EQ, EQ, EQ)
          .put(EQ, LO, LO)
          .put(EQ, GT, GT)
          .put(EQ, NE, NE)

          .put(LO, EQ, LO)
          .put(LO, LO, LO)
          .put(LO, GT, XX)
          .put(LO, NE, NE)

          .put(GT, EQ, GT)
          .put(GT, LO, XX)
          .put(GT, GT, GT)
          .put(GT, NE, NE)

          .put(NE, EQ, NE)
          .put(NE, LO, NE)
          .put(NE, GT, NE)
          .put(NE, NE, NE)

          .put(XX, EQ, XX)
          .put(XX, LO, XX)
          .put(XX, GT, XX)
          .put(XX, NE, NE)
*/

          .put(EQ, EQ, EQ)
          .put(EQ, LO, LO)
          .put(EQ, GT, GT)
          .put(EQ, NE, NE)
          .put(NE, EQ, NE)
          .put(NE, LO, NI)
          .put(NE, GT, NI)
          .put(NE, NE, NI)
          .put(LO, EQ, LO)
          .put(LO, LO, LO)
          .put(LO, GT, XX)
          .put(LO, NE, NI)
          .put(GT, EQ, GT)
          .put(GT, LO, XX)
          .put(GT, GT, GT)
          .put(GT, NE, NI)
          .put(XX, EQ, XX)
          .put(XX, LO, XX)
          .put(XX, GT, XX)
          .put(XX, NE, NI)
          .put(NI, EQ, NI)
          .put(NI, NE, NI)
          .put(NI, LO, NI)
          .put(NI, GT, NI)
          .build();

  default IntBinaryOperator combineActions() {
    return (left, right) -> ACTION_COMPARISON.get((char) left, (char) right);
  }

  default IntBinaryOperator combineConditions() {
    return (left, right) -> CONDITION_COMPARISON.get((char) left, (char) right);
  }

  default IntBinaryOperator combineConditionCombinationResults() {
    return (left, right) -> CONDITION_COMPARISON_RESULT.get((char) left, (char) right);
  }

  default IntBinaryOperator combineActionCombinationResults() {
    return (left, right) -> ACTION_COMPARISON_RESULT.get((char) left, (char) right);
  }

  default IntBinaryOperator combineAllCombinationResults() {
    return (left, right) -> COMBINATION_RESULT.get((char) left, (char) right);
  }
}
