/*
 * Copyright 2018 Celeral.
 *
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
 */
package com.celeral.utils;

import java.lang.reflect.Constructor;

import static org.slf4j.helpers.MessageFormatter.arrayFormat;

/**
 * Collection of helper methods to ensure that exceptions are
 * properly thrown. If the cause is of type Error or
 * RuntimeException then the cause it thrown as it is.
 * Otherwise the cause is wrapped in a RuntimeException and
 * the later is thrown.
 */
public class Throwables
{

  public static RuntimeException wrapIfChecked(Throwable cause)
  {
    if (cause instanceof Error) {
      throw (Error)cause;
    }

    if (cause instanceof RuntimeException) {
      throw (RuntimeException)cause;
    }

    throw new RuntimeException(cause);
  }

  public static RuntimeException wrapIfChecked(Exception exception)
  {
    if (exception instanceof RuntimeException) {
      throw (RuntimeException)exception;
    }

    throw new RuntimeException(exception);
  }

  /**
   *
   * @param error the error which needs to be wrapped
   *
   * @return the error passed as an argument as it is
   *
   * @deprecated Error does not need to be wrapped; Instead of "throw DTThrowable.wrapIfChecked(error);" use "throw error;" directly.
   */
  @Deprecated
  public static RuntimeException wrapIfChecked(Error error)
  {
    throw error;
  }

  /**
   *
   * @param exception the runtime exception which needs to be wrapped
   *
   * @return the runtime exception passed as an argument as it is
   *
   * @deprecated Unchecked exception (subclass of RuntimeException) does not need to be wrapped; Instead of "DTThrowable.rethrow(runtime_exception);" use "throw runtime_exception;" directly.
   */
  @Deprecated
  public static RuntimeException wrapIfChecked(RuntimeException exception)
  {
    throw exception;
  }

  @SuppressWarnings("UseSpecificCatch")
  public static <T extends Throwable> T throwFormatted(Class<T> clazz, String messagePattern, Object... args) throws T
  {
    String message = arrayFormat(messagePattern, args).getMessage();

    T instance = null;
    try {
      Constructor<T> constructor = clazz.getConstructor(String.class);
      instance = constructor.newInstance(message);
    }
    catch (Exception ex) {
      throw throwFormatted(ex, RuntimeException.class,
                           "Couldn't throw exception of type {} with message {} as constructor that takes only message String was not found!",
                           clazz.getName(), message);
    }

    throw instance;
  }

  @SuppressWarnings({"UseSpecificCatch", "InfiniteRecursion"})
  public static <T extends Throwable> T throwFormatted(Throwable cause, Class<T> clazz, String messagePattern, Object... args) throws T
  {
    String message = arrayFormat(messagePattern, args).getMessage();

    T instance = null;
    try {
      Constructor<T> constructor = clazz.getConstructor(String.class, Throwable.class);
      instance = constructor.newInstance(message, cause);
    }
    catch (Exception ex) {
      ex.addSuppressed(cause);
      throw throwFormatted(ex, RuntimeException.class,
                           "Couldn't throw exception of type {} with message {} as constructor that takes only message String was not found!",
                           clazz.getName(), message);
    }

    throw instance;
  }

}
