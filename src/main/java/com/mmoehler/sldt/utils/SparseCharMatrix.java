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

import it.unimi.dsi.fastutil.chars.Char2CharMap;
import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.util.LinkedList;
import java.util.List;

public class SparseCharMatrix {

  private final Char2ObjectMap<Char2CharMap> data;

  private SparseCharMatrix(Builder builder) {
    CharSequence s = builder.indicators;
    data = new Char2ObjectOpenHashMap<>(s.length());
    builder.buffer.forEach(
        c -> {
          if (!data.containsKey(c[0])) {
            data.put(c[0], new Char2CharOpenHashMap());
          }
          data.get(c[0]).put(c[1], c[2]);
        });
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public char get(char i, char j) {
    if (data.containsKey(i) && data.get(i).containsKey(j)) {
      return data.get(i).get(j);
    }
    return (char) 0;
  }

  public String toString() {
    StringBuilder s = new StringBuilder("{").append(System.lineSeparator());
    for (char k1 : data.keySet()) {
      Char2CharMap data2 = data.get(k1);
      for (char k2 : data2.keySet()) {
        s.append(String.format("   { %s:%s -> %s }", k1, k2, data2.get(k2)))
            .append(System.lineSeparator());
      }
    }
    s.append("}");
    return s.toString();
  }

  public static final class Builder {

    private CharSequence indicators;
    private final List<char[]> buffer = new LinkedList<>();

    private Builder() {
    }

    public Builder indicators(CharSequence indicators) {
      this.indicators = indicators;
      return this;
    }

    public Builder put(char i, char j, char val) {
      this.buffer.add(new char[]{i, j, val});
      return this;
    }

    public SparseCharMatrix build() {
      return new SparseCharMatrix(this);
    }
  }
}
