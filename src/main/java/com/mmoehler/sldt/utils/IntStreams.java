package com.mmoehler.sldt.utils;

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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterator.OfInt;
import java.util.Spliterators;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public final class IntStreams {

  private IntStreams() {
  }

  public static IntStream zip(IntStream streamA, IntStream streamB, IntBinaryOperator function) {
    checkNotNull(streamA);
    checkNotNull(streamB);
    checkNotNull(function);
    boolean isParallel = streamA.isParallel() || streamB.isParallel(); // same as Stream.concat
    final OfInt splitterA = streamA.spliterator();
    final OfInt splitterB = streamB.spliterator();
    int characteristics =
        splitterA.characteristics()
            & splitterB.characteristics()
            & (Spliterator.SIZED | Spliterator.ORDERED);

    final PrimitiveIterator.OfInt itrA = Spliterators.iterator(splitterA);
    final PrimitiveIterator.OfInt itrB = Spliterators.iterator(splitterB);
    return StreamSupport.intStream(
        new Spliterators.AbstractIntSpliterator(
            Math.min(splitterA.estimateSize(), splitterB.estimateSize()), characteristics) {
          @Override
          public boolean tryAdvance(IntConsumer action) {
            if (itrA.hasNext() && itrB.hasNext()) {
              action.accept(function.applyAsInt(itrA.next(), itrB.next()));
              return true;
            }
            return false;
          }
        },
        isParallel)
        .onClose(streamA::close)
        .onClose(streamB::close);
  }
}
