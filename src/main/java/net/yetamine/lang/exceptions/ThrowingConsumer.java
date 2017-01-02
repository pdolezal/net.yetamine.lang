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
import java.util.function.Consumer;

/**
 * A variant of {@link Consumer} that throws a specific exception class, which
 * is declared by the type parameter.
 *
 * @param <T>
 *            the type of the argument
 * @param <X>
 *            the type of the exception that the operation may throw
 */
@FunctionalInterface
public interface ThrowingConsumer<T, X extends Exception> {

    /**
     * Executes the operation.
     *
     * @param arg
     *            the argument of the operation
     *
     * @throws X
     *             if the operation fails
     */
    void accept(T arg) throws X;

    /**
     * Returns a {@link Consumer} that invokes this instance to process its
     * argument and uses the given handler to handle any exceptions.
     *
     * <p>
     * This method provides a bridge from methods throwing checked exceptions; a
     * typical use might look like:
     *
     * <pre>
     * collection.forEach(throwingConsumer.handled(UncheckedException::raise))
     * </pre>
     *
     * @param handler
     *            the exception handler. It must not be {@code null}.
     *
     * @return the consumer
     */
    default Consumer<T> guarded(ThrowingConsumer<? super Throwable, ? extends RuntimeException> handler) {
        return arg -> {
            try {
                accept(arg);
            } catch (Throwable t) {
                handler.accept(t);
            }
        };
    }

    /**
     * Returns the given consumer.
     *
     * @param <T>
     *            the type of the argument
     * @param <X>
     *            the type of the exception that the operation may throw
     * @param consumer
     *            the consumer to return. It must not be {@code null}.
     *
     * @return the consumer
     */
    @SuppressWarnings("unchecked")
    static <T, X extends Exception> ThrowingConsumer<T, X> from(ThrowingConsumer<? super T, ? extends X> consumer) {
        return (ThrowingConsumer<T, X>) consumer;
    }

    /**
     * Returns a {@link Consumer} that invokes the given instance to process its
     * argument and relays all unchecked exceptions (and errors) to the caller,
     * while throwing {@link UncheckedException} for any checked exception.
     *
     * @param <T>
     *            the type of the argument
     * @param consumer
     *            the consumer to use. It must not be {@code null}.
     *
     * @return the consumer
     */
    static <T> Consumer<T> rethrowing(ThrowingConsumer<? super T, ?> consumer) {
        Objects.requireNonNull(consumer);

        return arg -> {
            try {
                consumer.accept(arg);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }

    /**
     * Returns a {@link Consumer} that invokes the given instance to process its
     * argument and relays all errors to the caller, while throwing
     * {@link UncheckedException} for any other exception.
     *
     * @param <T>
     *            the type of the argument
     * @param consumer
     *            the consumer to use. It must not be {@code null}.
     *
     * @return the consumer
     */
    static <T> Consumer<T> enclosing(ThrowingConsumer<? super T, ?> consumer) {
        Objects.requireNonNull(consumer);

        return arg -> {
            try {
                consumer.accept(arg);
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }

    /**
     * Returns a {@link Consumer} that invokes the given instance to process its
     * argument and throws {@link UncheckedException} encapsulating any
     * exception or error.
     *
     * @param <T>
     *            the type of the argument
     * @param consumer
     *            the consumer to use. It must not be {@code null}.
     *
     * @return the consumer
     */
    static <T> Consumer<T> guarding(ThrowingConsumer<? super T, ?> consumer) {
        Objects.requireNonNull(consumer);

        return arg -> {
            try {
                consumer.accept(arg);
            } catch (Throwable t) {
                throw new UncheckedException(t);
            }
        };
    }
}
