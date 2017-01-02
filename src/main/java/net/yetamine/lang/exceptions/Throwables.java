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

    private Throwables() {
        throw new AssertionError();
    }
}
