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

import java.util.Objects;

public interface Result<T> {

  static <U> Result<U> success(U value) {
    return new Success<>(value);
  }

  static <U> Result<U> failure(Throwable throwable) {
    return new Failure<>(throwable);
  }

  boolean isSuccess();

  boolean isFailure();

  Throwable getCause();

  @SuppressWarnings("UnusedReturnValue")
  T get();

  class Success<T> implements Result<T> {

    private final T value;

    Success(final T value) {
      this.value = value;
    }

    @Override
    public boolean isSuccess() {
      return true;
    }

    @Override
    public boolean isFailure() {
      return false;
    }

    @Override
    public Exception getCause() {
      throw new UnsupportedOperationException("No cause on Success");
    }

    @Override
    public T get() {
      return value;
    }

    @Override
    public String toString() {
      return "SUCCESS(" + value + ")";
    }
  }

  class Failure<T> implements Result<T> {
    private final Throwable cause;

    public Failure(final Throwable cause) {
      Objects.requireNonNull(cause, "cause");
      this.cause = cause;
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public boolean isFailure() {
      return true;
    }

    @Override
    public Throwable getCause() {
      return cause;
    }

    @Override
    public T get() {
      return doThrow(cause);
    }

    @Override
    public String toString() {
      return "FAILURE{" + cause + "}";
    }
  }

  static <T extends Throwable, R> R doThrow(Throwable throwable) throws T {
    //noinspection unchecked
    throw (T) throwable;
  }
}
