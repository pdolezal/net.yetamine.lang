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

package net.yetamine.lang;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Provides a fluent handling of exceptions where catch handlers are not
 * available, e.g., for resolving causes of an exception.
 *
 * @param <T>
 *            the type of the exception to handle
 */
public final class Throwing<T extends Throwable> {

    /** Sole instance of {@code null} exception. */
    private static final Throwing<?> NONE = new Throwing<>(null);

    /** Exception to handle. */
    private final T throwable;

    /**
     * Creates a new instance.
     *
     * @param t
     *            the exception to handle
     */
    private Throwing(T t) {
        throwable = t;
    }

    /**
     * Returns an instance for handling the given exception.
     *
     * @param <T>
     *            the type of the exception
     * @param throwable
     *            the exception to handle. It must not be {@code null}.
     *
     * @return an instance for handling the given exception
     */
    public static <T extends Throwable> Throwing<T> some(T throwable) {
        return new Throwing<>(Objects.requireNonNull(throwable));
    }

    /**
     * Returns an instance for handling the given exception.
     *
     * @param <T>
     *            the type of the exception
     * @param throwable
     *            the exception to handle. It may be {@code null} unlike in the
     *            case of {@link #some(Throwable)}.
     *
     * @return an instance for handling the given exception
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> Throwing<T> maybe(T throwable) {
        return (throwable != null) ? new Throwing<>(throwable) : (Throwing<T>) NONE;
    }

    /**
     * Returns an instance for handling the cause (if any) of the given
     * exception.
     *
     * @param throwable
     *            the throwable whose cause shall be handled. It must not be
     *            {@code null}.
     *
     * @return an instance for handling the cause of the given exception
     */
    public static Throwing<Throwable> cause(Throwable throwable) {
        return maybe(throwable.getCause());
    }

    /**
     * Throws the exception if it is of the given type and satisfies the given
     * predicate.
     *
     * @param <X>
     *            the desired type of the exception
     * @param clazz
     *            the desired type of the exception
     * @param condition
     *            the condition for throwing the exception. It must not be
     *            {@code null}.
     *
     * @return this instance
     *
     * @throws X
     *             if the exception to handle is of the desired type and the
     *             predicate succeeds
     */
    public <X extends Throwable> Throwing<T> throwIf(Class<? extends X> clazz, Predicate<? super X> condition) throws X {
        if (clazz.isInstance(throwable)) {
            final X t = clazz.cast(throwable);
            if (condition.test(t)) {
                throw t;
            }
        }

        assert (condition != null);
        return this;
    }

    /**
     * Throws the exception if it is of the given type and satisfies the given
     * predicate.
     *
     * @param <X>
     *            the desired type of the exception
     * @param clazz
     *            the desired type of the exception
     *
     * @return this instance
     *
     * @throws X
     *             if the exception to handle is of the desired type and the
     *             predicate succeeds
     */
    public <X extends Throwable> Throwing<T> throwIf(Class<? extends X> clazz) throws X {
        if (clazz.isInstance(throwable)) {
            throw clazz.cast(throwable);
        }

        return this;
    }

    /**
     * Throws the exception that the given mapping function returns if the
     * current exception is of the given type.
     *
     * @param <X>
     *            the desired type of the current exception
     * @param <Z>
     *            the type of the exception to possibly throw
     * @param clazz
     *            the desired type of the exception
     * @param mapping
     *            the function to map the current exception to another. It must
     *            not be {@code null}.
     *
     * @return this instance
     *
     * @throws Z
     *             if the exception to handle is of the desired type and the
     *             mapping function returns this exception to throw
     */
    public <X, Z extends Throwable> Throwing<T> throwAs(Class<? extends X> clazz, Function<? super X, ? extends Z> mapping) throws Z {
        if (clazz.isInstance(throwable)) {
            final Z t = mapping.apply(clazz.cast(throwable));
            if (t != null) {
                throw t;
            }
        }

        return this;
    }

    /**
     * Rethrows the exception to handle if any.
     *
     * @throws T
     *             if there is any exception to handle
     */
    public void rethrow() throws T {
        if (throwable != null) {
            throw throwable;
        }
    }

    /**
     * Returns the exception provided by this instance.
     *
     * @return the exception provided by this instance
     */
    public Optional<T> throwable() {
        return Optional.ofNullable(throwable);
    }
}
