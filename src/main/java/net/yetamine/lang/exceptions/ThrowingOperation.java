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
import java.util.function.Function;

/**
 * A variant of {@link java.util.function.Function} that throws a specific
 * exception class, which is declared by the type parameter. Since checked
 * exceptions, which this interface aims to, are usual for operations that
 * probably have side-effects, the name indicates a general operation rather
 * than a function.
 *
 * @param <T>
 *            the type of the argument
 * @param <R>
 *            the type of the result
 * @param <X>
 *            the type of the exception that the operation may throw
 */
@FunctionalInterface
public interface ThrowingOperation<T, R, X extends Exception> {

    /**
     * Executes the operation.
     *
     * @param arg
     *            the argument of the operation
     *
     * @return the result of the operation
     *
     * @throws X
     *             if the operation fails
     */
    R execute(T arg) throws X;

    /**
     * Returns an instance that uses a dedicated special handler for taking care
     * of any {@link InterruptedException} which may raise when invoking this
     * instance.
     *
     * @param handler
     *            the handler to use. It must not be {@code null}.
     *
     * @return the guarding instance
     */
    default ThrowingOperation<T, R, X> whenInterrupted(ThrowingConsumer<? super InterruptedException, ? extends X> handler) {
        Objects.requireNonNull(handler);

        return arg -> {
            try {
                return execute(arg);
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    handler.accept((InterruptedException) e);
                }

                throw e;
            }
        };
    }

    /**
     * Returns the given operation.
     *
     * @param <T>
     *            the type of the argument
     * @param <R>
     *            the type of the result
     * @param <X>
     *            the type of the exception that the operation may throw
     * @param operation
     *            the operation to return. It must not be {@code null}.
     *
     * @return the operation
     */
    @SuppressWarnings("unchecked")
    static <T, R, X extends Exception> ThrowingOperation<T, R, X> from(ThrowingOperation<? super T, ? extends R, ? extends X> operation) {
        return (ThrowingOperation<T, R, X>) operation;
    }

    /**
     * Returns a {@link Function} that invokes the given instance and relays all
     * unchecked exceptions (and errors) to the caller, while throwing
     * {@link UncheckedException} for any checked exception.
     *
     * @param <T>
     *            the type of the argument
     * @param <R>
     *            the type of the result
     * @param operation
     *            the operation to use. It must not be {@code null}.
     *
     * @return the function
     */
    static <T, R> Function<T, R> rethrowing(ThrowingOperation<? super T, ? extends R, ?> operation) {
        Objects.requireNonNull(operation);

        return arg -> {
            try {
                return operation.execute(arg);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }

    /**
     * Returns a {@link Function} that invokes the given instance and relays all
     * errors to the caller, while throwing {@link UncheckedException} for any
     * other exception.
     *
     * @param <T>
     *            the type of the argument
     * @param <R>
     *            the type of the result
     * @param operation
     *            the operation to use. It must not be {@code null}.
     *
     * @return the supplier
     */
    static <T, R> Function<T, R> enclosing(ThrowingOperation<? super T, ? extends R, ?> operation) {
        Objects.requireNonNull(operation);

        return arg -> {
            try {
                return operation.execute(arg);
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }

    /**
     * Returns a {@link Function} that invokes the given instance and throws
     * {@link UncheckedException} encapsulating any exception or error.
     *
     * @param <T>
     *            the type of the argument
     * @param <R>
     *            the type of the result
     * @param operation
     *            the operation to use. It must not be {@code null}.
     *
     * @return the supplier
     */
    static <T, R> Function<T, R> guarding(ThrowingOperation<? super T, ? extends R, ?> operation) {
        Objects.requireNonNull(operation);

        return arg -> {
            try {
                return operation.execute(arg);
            } catch (Throwable t) {
                throw new UncheckedException(t);
            }
        };
    }
}
