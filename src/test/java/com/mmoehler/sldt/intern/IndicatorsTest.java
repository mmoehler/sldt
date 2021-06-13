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

import com.google.common.base.Strings;
import com.mmoehler.test.fixtures.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("SpellCheckingInspection")
class IndicatorsTest {

  Indicators indicators;

  @BeforeEach
  void setUp() {
    indicators =
            TestUtils.allocateIndicators(4,3,""
                    + "YYY-"
                    + "-NNN"
                    + "---N"
                    + "YYNN"
                    + "XXX-"
                    + "X--X"
                    + "-XX-");
    System.out.println(indicators);
  }

  @AfterEach
  void tearDown() {
    indicators = null;
  }

  //@Test
  void transpose() {
    System.out.println(Strings.repeat("-", 70));
    final Indicators transposed = indicators.transpose();
    Assertions.assertNotNull(transposed);
    System.out.println(transposed);
  }
}
