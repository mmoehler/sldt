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

import java.util.Arrays;
import java.util.stream.IntStream;

public class Combination {
    private final int[] left;
    private final int[] right;

    public static Combination of(final int[] left, final int[] right) {
        return new Combination(left, right);
    }

    public Combination(final int[] left, final int[] right) {
        this.left = left;
        this.right = right;
    }

    public IntStream getLeft() {
        return Arrays.stream(left);
    }

    public IntStream getRight() {
        return Arrays.stream(right);
    }

    @Override
    public String toString() {
        return "{" + Arrays.toString(left) + "-" + Arrays.toString(right) + '}';
    }
}
