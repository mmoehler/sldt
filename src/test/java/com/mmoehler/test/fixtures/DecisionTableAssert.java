package com.mmoehler.test.fixtures;

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
import com.mmoehler.sldt.DecisionTable;
import com.mmoehler.sldt.analysis.DefaultAnalyzer;
import org.assertj.core.api.AbstractAssert;

public class DecisionTableAssert extends AbstractAssert<DecisionTableAssert, DecisionTable> {

  public DecisionTableAssert(DecisionTable decisionTable, Class<?> selfType) {
    super(decisionTable, selfType);
  }

  public static DecisionTableAssert assertThat(DecisionTable actual) {
    return new DecisionTableAssert(actual, DecisionTableAssert.class);
  }

  public DecisionTableAssert isStructuralValid() {
    isNotNull();
    final Analysis analysisResult = new DefaultAnalyzer().apply(actual.getIndicators());
    if (analysisResult.isFailure()) {
      final String detailInfo =
          analysisResult
              .getDetailInfo()
              .orElseThrow(() -> new AssertionError(analysisResult.getCause()));
      failWithMessage(detailInfo);
    }
    return this;
  }
}
