package com.mmoehler.test.fixtures;

import com.mmoehler.sldt.intern.Indicators;

public final class TestUtils {

    private TestUtils() {
        super();
    }

    public static Indicators allocateIndicators(int conditionCount, int actionCount, String content) {
        return Indicators.newBuilder()
                .countOfConditions(conditionCount)
                .countOfActions(actionCount)
                .orientation(Indicators.Orientation.ROW)
                .content(content)
                .build();
    }
}
