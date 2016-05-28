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

package net.yetamine.lang.containers;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import net.yetamine.lang.functional.Acceptor;

/**
 * A mutable container for holding a single value.
 *
 * @param <T>
 *            the type of the stored value
 */
public final class Box<T> implements Serializable, Pointer<T> {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Boxed value. */
    private T value;

    /**
     * Creates a new instance filled with {@code null}.
     */
    private Box() {
        // Default constructor
    }

    /**
     * Creates a new instance filled with the initial value.
     *
     * @param initial
     *            the initial value
     */
    private Box(T initial) {
        value = initial;
    }

    /**
     * Creates a new instance filled with the initial value.
     *
     * @param <T>
     *            the type of the stored value
     * @param value
     *            the initial value
     *
     * @return the new instance
     */
    public static <T> Box<T> of(T value) {
        return new Box<>(value);
    }

    /**
     * Creates a new instance filled with {@code null}.
     *
     * @param <T>
     *            the type of the stored value
     *
     * @return the new instance
     */
    public static <T> Box<T> empty() {
        return new Box<>();
    }

    /**
     * Makes a box that accepts a value only once.
     *
     * <p>
     * This method fills the given box with an empty {@link Optional}; the
     * returned consumer then ignores {@code null} arguments and once it stores
     * the first non-{@code null} argument as a non-empty {@code Optional}, all
     * subsequent attempts to store anything are ignored as well.
     *
     * <p>
     * This method provides no concurrency guarantees.
     *
     * @param <T>
     *            the type of the stored value
     * @param box
     *            the box to use. It must not be {@code null}.
     *
     * @return a consumer storing the first non-{@code null} value only
     */
    public static <T> Acceptor<T> once(Box<Optional<T>> box) {
        box.accept(Optional.empty());

        return o -> {
            if ((o == null) || box.get().isPresent()) {
                return;
            }

            box.accept(Optional.ofNullable(o));
        };
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new StringBuilder("box[").append(value).append(']').toString();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return ((obj instanceof Box<?>) && Objects.equals(((Box<?>) obj).value, value));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * @see java.util.function.Consumer#accept(java.lang.Object)
     */
    public void accept(T t) {
        value = t;
    }

    /**
     * @see net.yetamine.lang.containers.Pointer#get()
     */
    public T get() {
        return value;
    }

    /**
     * Sets the element value.
     *
     * <p>
     * This method is an alias to {@link #accept(Object)}, just returns this
     * instance.
     *
     * @param t
     *            the value to set
     *
     * @return this instance
     */
    public Box<T> set(T t) {
        value = t;
        return this;
    }

    /**
     * Sets the element value.
     *
     * @param t
     *            the value to set
     *
     * @return the previous value
     */
    public T put(T t) {
        final T result = value;
        value = t;
        return result;
    }

    /**
     * Sets the element value to {@code null} and returns its original value.
     *
     * @return the original value
     */
    public T clear() {
        return put(null);
    }

    /**
     * Computes the new value of the element, updates the element and returns
     * its new value.
     *
     * @param mapping
     *            the mapping function to apply. It must not be {@code null}.
     *
     * @return this instance
     *
     * @see #compute(Function)
     */
    public Box<T> patch(Function<? super T, ? extends T> mapping) {
        value = mapping.apply(value);
        return this;
    }

    /**
     * Computes the new value of the element, updates the element and returns
     * its new value.
     *
     * @param mapping
     *            the mapping function to apply; it gets the given value as the
     *            first argument and the current value as the second argument.
     *            It must not be {@code null}.
     * @param t
     *            the value to pass as the first argument to the mapping
     *            function
     *
     * @return this instance
     *
     * @see #compute(BiFunction, Object)
     */
    public Box<T> patch(BiFunction<? super T, ? super T, ? extends T> mapping, T t) {
        value = mapping.apply(t, value);
        return this;
    }

    /**
     * Passes the contained value to the specified consumer.
     *
     * @param consumer
     *            the consumer to call. It must not be {@code null}.
     *
     * @return this instance
     */
    public Box<T> pass(Consumer<? super T> consumer) {
        consumer.accept(value);
        return this;
    }
}
