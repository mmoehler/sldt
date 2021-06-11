package com.mmoehler.sldt.intern;

import com.google.common.base.Strings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IndicatorsTest {

  Indicators indicators;

  @BeforeEach
  void setUp() {
    indicators =
        Indicators.newBuilder()
            .countOfConditions(4)
            .width(4)
            .orientation(Indicators.Orientation.ROW)
            .content("" + "YYY-" + "-NNN" + "---N" + "YYNN" + "XXX-" + "X--X" + "-XX-")
            .build();
    System.out.println(indicators);
  }

  @AfterEach
  void tearDown() {
    indicators = null;
  }

  @Test
  void transpose() {
    System.out.println(Strings.repeat("-", 70));
    final Indicators transposed = indicators.transpose();
    System.out.println(transposed);
  }
}
