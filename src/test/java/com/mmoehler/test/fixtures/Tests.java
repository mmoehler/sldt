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

import java.util.concurrent.Callable;

/**
 * <h3>
 * USAGE
 * </h3>
 * <pre>
 *
 * </pre>
 *
 * @param <O> Object under test;
 */
public final class Tests<O> implements Callable<O> {

  private final O object;
  private final static ThreadLocal<Object> ACTUAL = new ThreadLocal<>();
  private final static ThreadLocal<Object> EXPECTED = new ThreadLocal<>();
  private Prepare<O> preparation;
  private When<O> execution;
  private Then<O> verification;

  public static void actual(Object o) {
    ACTUAL.set(o);
  }

  public static <T> T actual() {
    return (T) ACTUAL.get();
  }

  public static void expected(Object o) {
    EXPECTED.set(o);
  }

  public static <T> T expected() {
    return (T) EXPECTED.get();
  }


  private Tests(O object) {
    super();
    this.object = object;
  }

  public static <Y> Tests<Y> of(Y subject) {
    return new Tests<>(subject);
  }

  public Tests<O> given(Prepare<O> preparation) {
    this.preparation = preparation;
    return this;
  }

  public Tests<O> when(When<O> execution) {
    this.execution = execution;
    return this;
  }

  public Tests<O> then(Then<O> verification) {
    this.verification = verification;
    return this;
  }

  @Override
  public O call() throws Exception {
    verification.accept(runTest());
    return object;
  }

  private O runTest() throws Exception {
    return execution.apply(preparation.apply(object));
  }

  @FunctionalInterface
  public interface Prepare<X> {
    static <T> Prepare<T> nothing() {
      return t -> t;
    }
    static <T> Prepare<T> nothing(String message) {
      System.out.println(">>> " + message);
      return t -> t;
    }
    X apply(X x) throws Exception;
  }

  @FunctionalInterface
  public interface When<X> {
    static <T> When<T> nothing() {
      return t -> t;
    }
    X apply(X x) throws Exception;
  }

  @FunctionalInterface
  public interface Then<X> {
    void accept(X x) throws Exception;
  }
}
