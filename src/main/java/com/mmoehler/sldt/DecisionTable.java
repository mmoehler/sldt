package com.mmoehler.sldt;

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

import com.mmoehler.sldt.intern.Indicators;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface DecisionTable<T, R> extends Function<T, R> {
  Indicators getIndicators();

  Predicate<T>[] getConditions();

  Function<T, R>[] getActions();

  Optional<Function<T, R>> getElseAction();
}
