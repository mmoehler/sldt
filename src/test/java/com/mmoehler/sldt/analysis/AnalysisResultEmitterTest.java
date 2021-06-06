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

import org.junit.jupiter.api.Test;

import static com.mmoehler.sldt.intern.IndicatorSigns.*;


public class AnalysisResultEmitterTest {

  @Test
  void testApply() throws Exception {
    final String result = asString(GT, MI, MI, AS, MI, XX);
    System.out.println(result);
    String message = (AnalysisResultEmitter.INSTANCE.apply(result, 4));
    System.out.println(message);
  }

  @Test
  void testApplyOnEmptyResults() throws Exception {
    final String result = asString(MI, MI, MI, MI, MI, MI);
    System.out.println(result);
    String message = (AnalysisResultEmitter.INSTANCE.apply(result, 4));
    System.out.println(message);
  }
}
