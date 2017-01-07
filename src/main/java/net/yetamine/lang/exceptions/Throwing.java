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
     * Guards the operation and catches any {@link Throwable}.
     *
     * @param operation
     *            the operation to guard. It must not be {@code null}.
     *
     * @return an instance containing the caught exception if any
     *
     * @throws NullPointerException
     *             if the operation is {@code null}; this is the case which is
     *             not intentionally protected as it indicates an error in the
     *             code which needs correction
     */
    public static Throwing<Throwable> sandbox(ThrowingRunnable<?> operation) {
        Objects.requireNonNull(operation);
        try { // Execute in sandbox
            operation.run();
        } catch (Throwable t) {
            return some(t);
        }

        return none();
    }

    /**
     * Guards the operation and catches any {@link Exception} (however, no
     * {@link Error} is caught intentionally).
     *
     * @param operation
     *            the operation to guard. It must not be {@code null}.
     *
     * @return an instance containing the caught exception if any
     *
     * @throws NullPointerException
     *             if the operation is {@code null}; this is the case which is
     *             not intentionally protected as it indicates an error in the
     *             code which needs correction
     */
    public static Throwing<Exception> guard(ThrowingRunnable<?> operation) {
        Objects.requireNonNull(operation);
        try { // Execute in sandbox
            operation.run();
        } catch (Exception e) {
            return some(e);
        }

        return none();
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
    public static <T extends Throwable> Throwing<T> maybe(T throwable) {
        return (throwable != null) ? new Throwing<>(throwable) : none();
    }

    /**
     * Returns an instance that throws nothing.
     *
     * @param <T>
     *            the type of the exception
     *
     * @return an instance throwing nothing
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> Throwing<T> none() {
        return (Throwing<T>) NONE;
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
     * Returns an instance for handling the cause (if any) of the current
     * exception.
     *
     * @return an instance for handling the cause of the current exception
     */
    public Throwing<Throwable> cause() {
        return (throwable != null) ? maybe(throwable.getCause()) : none();
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
     * Throws the exception if unchecked (i.e., {@link RuntimeException} or
     * {@link Error}).
     *
     * @return this instance
     */
    public Throwing<T> throwIfUnchecked() {
        return throwIf(RuntimeException.class).throwIf(Error.class);
    }

    /**
     * Throws the exception that the given mapping function returns.
     *
     * @param mapping
     *            the function to map the current exception to another. It must
     *            not be {@code null}.
     *
     * @param <X>
     *            the type of the exception to possibly throw
     *
     * @return this instance
     *
     * @throws X
     *             if the mapping function returns this exception
     */
    public <X extends Throwable> Throwing<T> throwAs(Function<? super T, ? extends X> mapping) throws X {
        final X t = mapping.apply(throwable);
        if (t != null) {
            throw t;
        }

        return this;
    }

    /**
     * Throws the exception that the given mapping function returns if the
     * current exception is of the given type.
     *
     * @param mapping
     *            the function to map the current exception to another. It must
     *            not be {@code null}.
     * @param clazz
     *            the desired type of the exception
     *
     * @param <X>
     *            the desired type of the current exception
     * @param <Z>
     *            the type of the exception to possibly throw
     * @return this instance
     *
     * @throws Z
     *             if the exception to handle is of the desired type and the
     *             mapping function returns this exception to throw
     */
    public <X, Z extends Throwable> Throwing<T> throwAs(Function<? super X, ? extends Z> mapping, Class<? extends X> clazz) throws Z {
        if (clazz.isInstance(throwable)) {
            final Z t = mapping.apply(clazz.cast(throwable));
            if (t != null) {
                throw t;
            }
        }

        return this;
    }

    /**
     * Maps the exception with the given function to a different one, if any
     * present.
     *
     * @param <X>
     *            the type of the resulting exception
     * @param mapping
     *            the mapping function. It must not be {@code null}.
     *
     * @return an instance representing the mapped exception, possibly
     *         {@link #none()}
     */
    public <X extends Throwable> Throwing<X> map(Function<? super T, ? extends X> mapping) {
        return (throwable != null) ? maybe(mapping.apply(throwable)) : none();
    }

    /**
     * Invokes the given handler with the exception of the desired type if any.
     *
     * @param <Q>
     *            the type of the exception to process
     * @param <X>
     *            the type of the exception that the handler declares to throw
     * @param type
     *            the type of the exception to process. It must not be
     *            {@code null}.
     * @param handler
     *            the handler to use. It must not be {@code null}.
     *
     * @return this instance
     *
     * @throws X
     *             if the handler throws the exception
     */
    public <Q extends Throwable, X extends Exception> Throwing<T> when(Class<? extends Q> type, ThrowingConsumer<Q, X> handler) throws X {
        if (type.isInstance(throwable)) {
            handler.accept(type.cast(throwable));
        }

        return this;
    }

    /**
     * Invokes the given handler when the exception has the desired type.
     *
     * @param <X>
     *            the type of the exception that the handler declares to throw
     * @param type
     *            the type of the exception to process. It must not be
     *            {@code null}.
     * @param handler
     *            the handler to use. It must not be {@code null}.
     *
     * @return this instance
     *
     * @throws X
     *             if the handler throws the exception
     */
    public <X extends Exception> Throwing<T> when(Class<? extends Throwable> type, ThrowingRunnable<X> handler) throws X {
        if (type.isInstance(throwable)) {
            handler.run();
        }

        return this;
    }

    /**
     * Passes the exception to handle, if any, to the given handler.
     *
     * @param <X>
     *            the type of the exception that the handler declares to throw
     * @param handler
     *            the handler to use. It must not be {@code null}.
     *
     * @return this instance
     *
     * @throws X
     *             if the handler throws the exception
     */
    public <X extends Exception> Throwing<T> then(ThrowingConsumer<? super T, ? extends X> handler) throws X {
        if (throwable != null) {
            handler.accept(throwable);
        }

        return this;
    }

    /**
     * Executes the given action regardless of an exception pending to handle.
     *
     * <p>
     * If the action throws an exception, the exception is thrown and any
     * pending exception shall be added as a suppressed one.
     *
     * @param <X>
     *            the type of the exception that the action may throw
     * @param action
     *            the action to execute . It must not be {@code null}.
     *
     * @return this instance
     *
     * @throws X
     *             if the action fails
     */
    public <X extends Exception> Throwing<T> anyway(ThrowingRunnable<X> action) throws X {
        try {
            action.run();
        } catch (Throwable t) {
            if (throwable != null) {
                t.addSuppressed(throwable);
            }

            throw t;
        }

        return this;
    }

    /**
     * Executes the given action if no exception pending to handle.
     *
     * <p>
     * If the action throws an exception, the exception is thrown.
     *
     * @param <X>
     *            the type of the exception that the action may throw
     * @param action
     *            the action to execute . It must not be {@code null}.
     *
     * @return this instance
     *
     * @throws X
     *             if the action fails
     */
    public <X extends Exception> Throwing<T> otherwise(ThrowingRunnable<X> action) throws X {
        if (throwable == null) {
            action.run();
        }

        return this;
    }

    /**
     * Executes the given operation regardless of an exception pending to
     * handle.
     *
     * <p>
     * If the action throws an exception, the exception is thrown and any
     * pending exception shall be added as a suppressed one.
     *
     * @param <R>
     *            the type of the result
     * @param <X>
     *            the type of the exception that the action may throw
     * @param operation
     *            the operation to execute. It must not be {@code null}.
     *
     * @return the result of the operation
     *
     * @throws X
     *             if the action fails
     */
    public <R, X extends Exception> R yield(ThrowingOperation<? super T, ? extends R, X> operation) throws X {
        try {
            return operation.execute(throwable);
        } catch (Throwable t) {
            if (throwable != null) {
                t.addSuppressed(throwable);
            }

            throw t;
        }
    }

    /**
     * Rethrows the exception to handle if any.
     *
     * @return this instance
     *
     * @throws T
     *             if there is any exception to handle
     */
    public Throwing<T> rethrow() throws T {
        if (throwable != null) {
            throw throwable;
        }

        return this;
    }

    /**
     * Returns the exception provided by this instance.
     *
     * @return the exception provided by this instance
     */
    public Optional<T> throwable() {
        return Optional.ofNullable(throwable);
    }

    /**
     * Indicates if an actual exception, which could be thrown, is represented.
     *
     * @return {@code true} iff {@code throwable().isPresent()}
     */
    public boolean couldThrow() {
        return (throwable != null);
    }
}
