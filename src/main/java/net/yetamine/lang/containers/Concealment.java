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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import net.yetamine.lang.functional.Producer;

/**
 * An immutable container for holding a single value which prevents the value to
 * be exposed via {@link #toString()}.
 *
 * <p>
 * This container is useful for passing sensitive objects like passwords in the
 * contexts where dumping the context in a log might leak some sensitive data,
 * on the other hand to provide at least some troubleshooting support, dumping
 * this class does tell the type and identity of the content.
 *
 * @param <T>
 *            the type of the represented value
 */
public final class Concealment<T> implements Producer<T> {

    /** Common shared empty instance. */
    private static final Concealment<?> EMPTY = new Concealment<>(null, Concealment::toString);

    /** Boxed value. */
    private final T value;
    /** Custom {@link #toString()}. */
    private final Function<? super T, String> toString;

    /**
     * Creates a new instance representing the given value.
     *
     * @param object
     *            the value to represent
     * @param formatter
     *            the {@link #toString} implementation. It must not be
     *            {@code null}.
     */
    private Concealment(T object, Function<? super T, String> formatter) {
        assert (formatter != null);
        toString = formatter;
        value = object;
    }

    /**
     * Returns an instance representing {@code null}.
     *
     * @param <T>
     *            the type of the represented value
     *
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Concealment<T> empty() {
        return (Concealment<T>) EMPTY;
    }

    /**
     * Returns an instance representing the given value.
     *
     * @param <T>
     *            the type of the represented value
     * @param object
     *            the value to represent
     *
     * @return the new instance
     */
    public static <T> Concealment<T> of(T object) {
        return (object != null) ? new Concealment<>(object, Concealment::toString) : empty();
    }

    /**
     * Returns an instance representing the given value.
     *
     * @param <T>
     *            the type of the represented value
     * @param object
     *            the value to represent
     * @param toString
     *            the custom {@link #toString} implementation. It must not be
     *            {@code null}.
     *
     * @return the new instance
     */
    public static <T> Concealment<T> custom(T object, Function<? super T, String> toString) {
        return new Concealment<>(object, Objects.requireNonNull(toString));
    }

    /**
     * Formats a value with the default formatter.
     *
     * @param o
     *            the value to format
     *
     * @return the default formatting
     */
    public static String toString(Object o) {
        if (o == null) {
            return "concealment[empty]";
        }

        return String.format("concealment[%s@%08x]", o.getClass().getTypeName(), System.identityHashCode(o));
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString.apply(value);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return ((obj instanceof Concealment<?>) && Objects.equals(((Concealment<?>) obj).value, value));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * @see net.yetamine.lang.containers.Pointer#get()
     */
    public T get() {
        return value;
    }

    /**
     * Passes the contained value to the specified consumer.
     *
     * @param consumer
     *            the consumer to call. It must not be {@code null}.
     *
     * @return this instance
     */
    public Concealment<T> use(Consumer<? super T> consumer) {
        consumer.accept(value);
        return this;
    }
}
