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
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.yetamine.lang.functional.Producer;

/**
 * Represents a pointer to an element that allows to get and modify the element.
 *
 * <p>
 * Note that any operation may throw an exception appropriate for the underlying
 * storage.
 *
 * @param <T>
 *            the type of the element
 */
public interface Pointer<T> extends Consumer<T>, Producer<T> {

    /**
     * Creates an instance that delegates {@link #get()} to the given
     * {@link Supplier} and {@link #accept(Object)} to the given
     * {@link Consumer}.
     *
     * @param <T>
     *            the type of the element
     * @param supplier
     *            the source of the values. It must not be {@code null}.
     * @param consumer
     *            the target of the values. It must not be {@code null}.
     *
     * @return the delegating instance
     */
    static <T> Pointer<T> from(Supplier<? extends T> supplier, Consumer<? super T> consumer) {
        return new DefaultPointer<>(supplier, consumer);
    }

    /**
     * Returns the element value.
     *
     * @see java.util.function.Supplier#get()
     */
    T get();

    /**
     * Sets the element value.
     *
     * @see java.util.function.Consumer#accept(java.lang.Object)
     */
    void accept(T t);

    /**
     * Computes the new value of the element, updates the element and returns
     * its new value.
     *
     * @param mapping
     *            the mapping function to apply. It must not be {@code null}.
     *
     * @return this instance
     */
    default T compute(Function<? super T, ? extends T> mapping) {
        final T result = mapping.apply(get());
        accept(result);
        return result;
    }

    /**
     * Computes the new value of the element, updates the element and returns
     * its new value.
     *
     * @param mapping
     *            the mapping function to apply; it gets the given value as the
     *            first argument and the current value as the second argument.
     *            It must not be {@code null}.
     * @param value
     *            the value to pass as the first argument to the mapping
     *            function
     *
     * @return this instance
     */
    default T compute(BiFunction<? super T, ? super T, ? extends T> mapping, T value) {
        final T result = mapping.apply(value, get());
        accept(result);
        return result;
    }

    /**
     * Returns the result of the given function applied on the current value of
     * the element.
     *
     * @param <V>
     *            the type of the mapping result
     * @param mapping
     *            the mapping function to apply. It must not be {@code null}.
     *
     * @return the result of the mapping function
     */
    default <V> V map(Function<? super T, ? extends V> mapping) {
        return mapping.apply(get());
    }

    /**
     * Returns the value of the element as an {@link Optional} instance.
     *
     * <p>
     * This method provides a bridge to the standard library and allows using
     * patterns like {@code pointer.nonNull().orElse(fallback)}
     *
     * @return the value as an {@link Optional} instance
     */
    default Optional<T> nonNull() {
        return Optional.ofNullable(get());
    }

    /**
     * Returns a stream using this instance as the element source.
     *
     * @return a stream using this instance as the element source
     */
    default Stream<T> stream() {
        return Stream.of(this).map(Pointer::get);
    }
}

/**
 * An implementation of the {@link Pointer} interface that just delegates.
 *
 * @param <T>
 *            the type of the values
 */
final class DefaultPointer<T> implements Pointer<T> {

    /** Source for {@link #get()}. */
    private final Supplier<? extends T> supplier;
    /** Target for {@link #accept(Object)}. */
    private final Consumer<? super T> consumer;

    /**
     * Creates a new instance.
     *
     * @param source
     *            the source of the values. It must not be {@code null}.
     * @param target
     *            the target of the values. It must not be {@code null}.
     */
    public DefaultPointer(Supplier<? extends T> source, Consumer<? super T> target) {
        supplier = Objects.requireNonNull(source);
        consumer = Objects.requireNonNull(target);
    }

    /**
     * @see net.yetamine.lang.containers.Pointer#get()
     */
    public T get() {
        return supplier.get();
    }

    /**
     * @see net.yetamine.lang.containers.Pointer#accept(java.lang.Object)
     */
    public void accept(T t) {
        consumer.accept(t);
    }
}
