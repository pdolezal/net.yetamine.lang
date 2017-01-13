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
 * A utility class for dealing with interruption state and
 * {@link InterruptedException}.
 */
public final class Interruption {

    /**
     * Interrupts the current thread.
     */
    public static void renew() {
        Thread.currentThread().interrupt();
    }

    /**
     * Interrupts the current thread if the given exception is
     * {@link InterruptedException}.
     *
     * @param <X>
     *            the type of the exception
     * @param t
     *            the exception to test
     *
     * @return the given exception
     */
    public static <X extends Throwable> X renew(X t) {
        if (t instanceof InterruptedException) {
            renew();
        }

        return t;
    }

    /**
     * Throws a new {@link InterruptedException} if the current thread is
     * interrupted, clearing its interruption state.
     *
     * @throws InterruptedException
     *             if the current thread is interrupted
     */
    public static void raise() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * Clears the interruption state of the current thread and returns a new
     * {@link InterruptedException}.
     *
     * @return a new {@link InterruptedException}
     */
    public static InterruptedException signal() {
        Thread.interrupted();
        return new InterruptedException();
    }

    private Interruption() {
        throw new AssertionError();
    }
}
