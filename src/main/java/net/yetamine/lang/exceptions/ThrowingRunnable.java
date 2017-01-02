/*
 * Copyright 2017 Yetamine
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

import java.util.Objects;

/**
 * A variant of {@link Runnable} that throws a specific exception class, which
 * is declared by the type parameter.
 *
 * @param <X>
 *            the type of the exception that the operation may throw
 */
@FunctionalInterface
public interface ThrowingRunnable<X extends Exception> {

    /**
     * Executes the operation.
     *
     * @throws X
     *             if the operation fails
     */
    void run() throws X;

    /**
     * Returns a {@link Runnable} that invokes this instance to process its
     * argument and uses the given handler to handle any exceptions.
     *
     * @param handler
     *            the exception handler. It must not be {@code null}.
     *
     * @return the runnable
     */
    default Runnable guarded(ThrowingConsumer<? super Throwable, ? extends RuntimeException> handler) {
        return () -> {
            try {
                run();
            } catch (Throwable t) {
                handler.accept(t);
            }
        };
    }

    /**
     * Returns the given runnable.
     *
     * @param <X>
     *            the type of the exception that the operation may throw
     * @param runnable
     *            the runnable to return. It must not be {@code null}.
     *
     * @return the runnable
     */
    @SuppressWarnings("unchecked")
    static <X extends Exception> ThrowingRunnable<X> from(ThrowingRunnable<? extends X> runnable) {
        return (ThrowingRunnable<X>) runnable;
    }

    /**
     * Returns a {@link Runnable} that invokes the given instance to process its
     * argument and relays all unchecked exceptions (and errors) to the caller,
     * while throwing {@link UncheckedException} for any checked exception.
     *
     * @param <T>
     *            the type of the argument
     * @param runnable
     *            the runnable to use. It must not be {@code null}.
     *
     * @return the runnable
     */
    static <T> Runnable rethrowing(ThrowingRunnable<?> runnable) {
        Objects.requireNonNull(runnable);

        return () -> {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }

    /**
     * Returns a {@link Runnable} that invokes the given instance to process its
     * argument and relays all errors to the caller, while throwing
     * {@link UncheckedException} for any other exception.
     *
     * @param <T>
     *            the type of the argument
     * @param runnable
     *            the runnable to use. It must not be {@code null}.
     *
     * @return the runnable
     */
    static <T> Runnable enclosing(ThrowingRunnable<?> runnable) {
        Objects.requireNonNull(runnable);

        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }

    /**
     * Returns a {@link Runnable} that invokes the given instance to process its
     * argument and throws {@link UncheckedException} encapsulating any
     * exception or error.
     *
     * @param <T>
     *            the type of the argument
     * @param runnable
     *            the runnable to use. It must not be {@code null}.
     *
     * @return the runnable
     */
    static <T> Runnable guarding(ThrowingRunnable<?> runnable) {
        Objects.requireNonNull(runnable);

        return () -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                throw new UncheckedException(t);
            }
        };
    }
}
