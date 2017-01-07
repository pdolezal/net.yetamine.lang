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

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * An extension of {@link Callable} that throws a specific exception class,
 * which is declared by a type parameter for the interface as well.
 *
 * @param <V>
 *            the type of the result
 * @param <X>
 *            the type of the exception that the resource bound to this
 *            interface may throw
 */
@FunctionalInterface
public interface ThrowingCallable<V, X extends Exception> extends Callable<V> {

    /**
     * @see java.util.concurrent.Callable#call()
     */
    V call() throws X;

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
    default ThrowingCallable<V, X> whenInterrupted(ThrowingConsumer<? super InterruptedException, ? extends X> handler) {
        Objects.requireNonNull(handler);

        return () -> {
            try {
                return call();
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    handler.accept((InterruptedException) e);
                }

                throw e;
            }
        };
    }

    /**
     * Returns the given callable.
     *
     * @param <V>
     *            the type of the result
     * @param <X>
     *            the type of the exception that the operation may throw
     * @param callable
     *            the callable to return. It must not be {@code null}.
     *
     * @return the callable
     */
    @SuppressWarnings("unchecked")
    static <V, X extends Exception> ThrowingCallable<V, X> from(ThrowingCallable<? extends V, ? extends X> callable) {
        return (ThrowingCallable<V, X>) callable;
    }

    /**
     * Returns a {@link Supplier} that invokes the given instance and relays all
     * unchecked exceptions (and errors) to the caller, while throwing
     * {@link UncheckedException} for any checked exception.
     *
     * @param <V>
     *            the type of the result
     * @param callable
     *            the callable to use. It must not be {@code null}.
     *
     * @return the supplier
     */
    static <V> Supplier<V> rethrowing(ThrowingCallable<? extends V, ?> callable) {
        Objects.requireNonNull(callable);

        return () -> {
            try {
                return callable.call();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }

    /**
     * Returns a {@link Supplier} that invokes the given instance and relays all
     * errors to the caller, while throwing {@link UncheckedException} for any
     * other exception.
     *
     * @param <V>
     *            the type of the result
     * @param callable
     *            the callable to use. It must not be {@code null}.
     *
     * @return the supplier
     */
    static <V> Supplier<V> enclosing(ThrowingCallable<? extends V, ?> callable) {
        Objects.requireNonNull(callable);

        return () -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }

    /**
     * Returns a {@link Supplier} that invokes the given instance and throws
     * {@link UncheckedException} encapsulating any exception or error.
     *
     * @param <V>
     *            the type of the result
     * @param callable
     *            the callable to use. It must not be {@code null}.
     *
     * @return the supplier
     */
    static <V> Supplier<V> guarding(ThrowingCallable<? extends V, ?> callable) {
        Objects.requireNonNull(callable);

        return () -> {
            try {
                return callable.call();
            } catch (Throwable t) {
                throw new UncheckedException(t);
            }
        };
    }
}
