package com.mmoehler.sldt.analysis;

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


import com.mmoehler.sldt.Analysis;
import com.mmoehler.sldt.intern.IndicatorSigns;

import java.util.Optional;

public class DefaultAnalysis implements Analysis {

  private final Exception exception;
  private final String detailInfo;
  private final Integer countOfRules;

  public static Analysis of(Exception exception) {
    return new DefaultAnalysis(exception);
  }

  public static Analysis of(String result, int countOfRules) {
    return new DefaultAnalysis(result, countOfRules);
  }

  public DefaultAnalysis(Exception exception) {
    this.exception = exception;
    this.detailInfo = null;
    this.countOfRules = null;
  }

  public DefaultAnalysis(String result, int countOfRules) {
    this.exception = null;
    this.countOfRules = countOfRules;
    this.detailInfo = (hasIssues(result)) ? describeIssues(result) : null;
  }

  @Override
  public boolean isSuccess() {
    return (null == exception && null == detailInfo);
  }

  @Override
  public boolean isFailure() {
    return !isSuccess();
  }

  @Override
  public Optional<Exception> getCause() {
    return Optional.ofNullable(this.exception);
  }

  @Override
  public Optional<String> getDetailInfo() {
    return Optional.ofNullable(this.detailInfo);
  }

  private String describeIssues(String result) {
    return AnalysisResultEmitter.INSTANCE.apply(result, countOfRules);
  }

  private boolean hasIssues(String result) {
    final var c = result.charAt(0);
    if (c != IndicatorSigns.MI) return false;
    for (var i = 1; i < result.length(); i++) {
      if (c != result.charAt(i)) return true;
    }
    return false;
  }
}
