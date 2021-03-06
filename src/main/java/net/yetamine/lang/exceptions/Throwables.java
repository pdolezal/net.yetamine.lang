/*
 * Copyright 2016 Yetamine
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.yetamine.lang.exceptions;

/**
 * A utility class for dealing with exceptions.
 */
public final class Throwables {

    /**
     * Prevents creating instances of this class.
     */
    private Throwables() {
        throw new AssertionError();
    }

    /**
     * Throws the given exception.
     *
     * @param <X>
     *            the type of the exception
     * @param throwable
     *            the exception to throw. It must not be {@code null}.
     *
     * @return nothing, the exception is thrown
     *
     * @throws X
     *             when the exception is not {@code null}, otherwise JVM should
     *             raise an exception to indicate missing exception instance to
     *             throw
     */
    public static <X extends Throwable> X thrown(X throwable) throws X {
        throw throwable;
    }

    /**
     * Throws the given exception.
     *
     * <p>
     * This method acts as {@link #thrown(Throwable)}, but instead of pretending
     * to return a result of the type of the given exception, it pretends to
     * return a result of any suitable type, so that it could consistute any
     * function-like lambda.
     *
     * @param <T>
     *            the return type
     * @param <X>
     *            the type of the exception
     * @param throwable
     *            the exception to throw. It must not be {@code null}.
     *
     * @return nothing, the exception is thrown
     *
     * @throws X
     *             when the exception is not {@code null}, otherwise JVM should
     *             raise an exception to indicate missing exception instance to
     *             throw
     */
    public static <T, X extends Throwable> T raise(X throwable) throws X {
        throw throwable;
    }

    /**
     * Calls {@link Throwable#initCause(Throwable)} on the given exception and
     * returns the exception then.
     *
     * <p>
     * This method is suitable for compact initialization of the exceptions that
     * have no cause-chaining constructor:
     *
     * <pre>
     * try {
     *     // Some code that might raise an IOException
     * } catch (IOException e) {
     *     throw Throwables.init(new NoSuchElementException(), e);
     * }
     * </pre>
     *
     * To detect wrong callers that supply {@code null} exception to initialize,
     * causing an unexpected {@link NullPointerException}, the method checks the
     * argument with an {@code assert}.
     *
     * @param <X>
     *            the type of the exception to process
     * @param t
     *            the exception to process. It must not be {@code null}.
     * @param cause
     *            the cause to be set
     *
     * @return the given exception
     */
    public static <X extends Throwable> X init(X t, Throwable cause) {
        assert (t != null);
        t.initCause(cause);
        return t;
    }
}
