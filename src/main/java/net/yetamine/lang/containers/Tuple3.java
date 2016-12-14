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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A rudimentary tuple implementation consisting of three elements.
 *
 * @param <T1>
 *            the type of element #1
 * @param <T2>
 *            the type of element #2
 * @param <T3>
 *            the type of element #3
 */
public final class Tuple3<T1, T2, T3> implements Tuple {

    /** Common shared empty tuple. */
    private static final Tuple3<?, ?, ?> EMPTY = new Tuple3<>(null, null, null);

    /** Element #1. */
    private final T1 value1;
    /** Element #2. */
    private final T2 value2;
    /** Element #3. */
    private final T3 value3;

    /**
     * Creates a new instance.
     *
     * @param t1
     *            element #1
     * @param t2
     *            element #2
     * @param t3
     *            element #3
     */
    private Tuple3(T1 t1, T2 t2, T3 t3) {
        value1 = t1;
        value2 = t2;
        value3 = t3;
    }

    // Core construction methods

    /**
     * Creates a new instance.
     *
     * <p>
     * This method is an alias for {@link #of(Object, Object, Object)} and it is
     * meant mainly as a support for static imports.
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     * @param <T3>
     *            the type of element #3
     * @param t1
     *            element #1
     * @param t2
     *            element #2
     * @param t3
     *            element #3
     *
     * @return the new instance
     */
    public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple3(T1 t1, T2 t2, T3 t3) {
        return of(t1, t2, t3);
    }

    /**
     * Creates a new instance.
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     * @param <T3>
     *            the type of element #3
     * @param t1
     *            element #1
     * @param t2
     *            element #2
     * @param t3
     *            element #3
     *
     * @return the new instance
     */
    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>(t1, t2, t3);
    }

    /**
     * Returns an empty tuple (consisting of {@code null} elements).
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     * @param <T3>
     *            the type of element #3
     *
     * @return an empty tuple
     */
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> Tuple3<T1, T2, T3> empty() {
        return (Tuple3<T1, T2, T3>) EMPTY;
    }

    /**
     * Narrows a widened type performing a safe type cast (thanks to the safe
     * covariant changes for immutable types).
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     * @param <T3>
     *            the type of element #3
     * @param instance
     *            the instance to narrow
     *
     * @return the narrowed instance
     */
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> Tuple3<T1, T2, T3> narrow(Tuple3<? extends T1, ? extends T2, ? extends T3> instance) {
        return (Tuple3<T1, T2, T3>) instance;
    }

    // Common object methods

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", value1, value2, value3);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Tuple3<?, ?, ?>) {
            final Tuple3<?, ?, ?> o = (Tuple3<?, ?, ?>) obj;
            return Objects.equals(value1, o.value1) && Objects.equals(value2, o.value2)
                    && Objects.equals(value3, o.value3);
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(value1, value2, value3);
    }

    // Inherited methods

    /**
     * @see net.yetamine.lang.containers.Tuple#arity()
     */
    public int arity() {
        return 3;
    }

    /**
     * @see net.yetamine.lang.containers.Tuple#get(int)
     */
    public Object get(int index) {
        switch (index) {
            case 0:
                return get1();

            case 1:
                return get2();

            case 2:
                return get3();

            default:
                throw new IndexOutOfBoundsException();
        }
    }

    /**
     * @see net.yetamine.lang.containers.Tuple#toList()
     */
    public List<?> toList() {
        return Collections.unmodifiableList(Arrays.asList(value1, value2, value3));
    }

    // Core tuple methods

    /**
     * Returns element #1.
     *
     * @return element #1
     */
    public T1 get1() {
        return value1;
    }

    /**
     * Returns element #2.
     *
     * @return element #2
     */
    public T2 get2() {
        return value2;
    }

    /**
     * Returns element #3.
     *
     * @return element #3
     */
    public T3 get3() {
        return value3;
    }

    /**
     * Returns a tuple with element #1 modified to the given value.
     *
     * @param <V>
     *            the type of the value
     * @param value
     *            the value to set
     *
     * @return a tuple with element #1 modified to the given value
     */
    public <V> Tuple3<V, T2, T3> set1(V value) {
        return of(value, value2, value3);
    }

    /**
     * Returns a tuple with element #2 modified to the given value.
     *
     * @param <V>
     *            the type of the value
     * @param value
     *            the value to set
     *
     * @return a tuple with element #2 modified to the given value
     */
    public <V> Tuple3<T1, V, T3> set2(V value) {
        return of(value1, value, value3);
    }

    /**
     * Returns a tuple with element #3 modified to the given value.
     *
     * @param <V>
     *            the type of the value
     * @param value
     *            the value to set
     *
     * @return a tuple with element #3 modified to the given value
     */
    public <V> Tuple3<T1, T2, V> set3(V value) {
        return of(value1, value2, value);
    }

    // Link to Tuple2

    /**
     * Returns the first two elements as a tuple.
     *
     * @return the first two elements as a tuple
     */
    public Tuple2<T1, T2> head() {
        return Tuple2.of(value1, value2);
    }

    /**
     * Returns the last two elements as a tuple.
     *
     * @return the last two elements as a tuple
     */
    public Tuple2<T2, T3> tail() {
        return Tuple2.of(value2, value3);
    }

    /**
     * Returns the first and the last elements as a tuple.
     *
     * @return the first and the last elements as a tuple
     */
    public Tuple2<T1, T3> outer() {
        return Tuple2.of(value1, value3);
    }

    // Functional extensions

    /**
     * Returns a tuple with element #1 mapped with the given function.
     *
     * @param <V>
     *            the type of the function result
     * @param mapping
     *            the function to apply. It must not be {@code null}.
     *
     * @return a tuple with element #1 mapped with the given function
     */
    public <V> Tuple3<V, T2, T3> map1(Function<? super T1, ? extends V> mapping) {
        return of(mapping.apply(value1), value2, value3);
    }

    /**
     * Returns a tuple with element #2 mapped with the given function.
     *
     * @param <V>
     *            the type of the function result
     * @param mapping
     *            the function to apply. It must not be {@code null}.
     *
     * @return a tuple with element #2 mapped with the given function
     */
    public <V> Tuple3<T1, V, T3> map2(Function<? super T2, ? extends V> mapping) {
        return of(value1, mapping.apply(value2), value3);
    }

    /**
     * Returns a tuple with element #3 mapped with the given function.
     *
     * @param <V>
     *            the type of the function result
     * @param mapping
     *            the function to apply. It must not be {@code null}.
     *
     * @return a tuple with element #3 mapped with the given function
     */
    public <V> Tuple3<T1, T2, V> map3(Function<? super T3, ? extends V> mapping) {
        return of(value1, value2, mapping.apply(value3));
    }

    /**
     * Passes element #1 to the specified consumer.
     *
     * @param consumer
     *            the consumer to call. It must not be {@code null}.
     *
     * @return this instance
     */
    public Tuple3<T1, T2, T3> use1(Consumer<? super T1> consumer) {
        consumer.accept(value1);
        return this;
    }

    /**
     * Passes element #2 to the specified consumer.
     *
     * @param consumer
     *            the consumer to call. It must not be {@code null}.
     *
     * @return this instance
     */
    public Tuple3<T1, T2, T3> use2(Consumer<? super T2> consumer) {
        consumer.accept(value2);
        return this;
    }

    /**
     * Passes element #2 to the specified consumer.
     *
     * @param consumer
     *            the consumer to call. It must not be {@code null}.
     *
     * @return this instance
     */
    public Tuple3<T1, T2, T3> use3(Consumer<? super T3> consumer) {
        consumer.accept(value3);
        return this;
    }

    // Factory methods for common types

    /**
     * Makes a tuple from the first three elements provided by the given source.
     *
     * @param <T>
     *            the type of the source's elements
     * @param source
     *            the source to process. It must provide at least three
     *            elements.
     *
     * @return a tuple
     *
     * @throws NoSuchElementException
     *             if the source provides too few elements
     */
    public static <T> Tuple3<T, T, T> from(Iterable<? extends T> source) {
        return from(source.iterator());
    }

    /**
     * Makes a tuple from the first three elements provided by the given source.
     *
     * @param <T>
     *            the type of the source's elements
     * @param source
     *            the source to process. It must provide at least three
     *            elements.
     *
     * @return a tuple
     *
     * @throws NoSuchElementException
     *             if the source provides too few elements
     */
    public static <T> Tuple3<T, T, T> from(Iterator<? extends T> source) {
        return of(source.next(), source.next(), source.next());
    }

    // Zipping

    /**
     * Returns an iterable zipping the elements from given source iterables.
     *
     * <p>
     * The resulting iterables returns tuples from the elements provided by the
     * source iterables and returns as many elements as the shorter of the
     * source iterables.
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     * @param <T3>
     *            the type of element #3
     * @param source1
     *            the source of elements. It must not be {@code null}.
     * @param source2
     *            the source of elements. It must not be {@code null}.
     * @param source3
     *            the source of elements. It must not be {@code null}.
     *
     * @return a zipping iterable
     */
    public static <T1, T2, T3> Iterable<Tuple3<T1, T2, T3>> zip(Iterable<? extends T1> source1, Iterable<? extends T2> source2, Iterable<? extends T3> source3) {
        Objects.requireNonNull(source1);
        Objects.requireNonNull(source2);
        Objects.requireNonNull(source3);

        // Rather not make a lambda, it might change
        return new Iterable<Tuple3<T1, T2, T3>>() {

            /**
             * @see java.lang.Iterable#iterator()
             */
            public Iterator<Tuple3<T1, T2, T3>> iterator() {
                return Tuple3.zip(source1.iterator(), source2.iterator(), source3.iterator());
            }
        };
    }

    /**
     * Returns an iterator zipping the elements from given source iterators.
     *
     * <p>
     * The resulting iterator returns tuples from the elements provided by the
     * source iterators and returns as many elements as the shorter of the
     * source iterators.
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     * @param <T3>
     *            the type of element #3
     * @param source1
     *            the source of elements. It must not be {@code null}.
     * @param source2
     *            the source of elements. It must not be {@code null}.
     * @param source3
     *            the source of elements. It must not be {@code null}.
     *
     * @return a zipping iterator
     */
    public static <T1, T2, T3> Iterator<Tuple3<T1, T2, T3>> zip(Iterator<? extends T1> source1, Iterator<? extends T2> source2, Iterator<? extends T3> source3) {
        Objects.requireNonNull(source1);
        Objects.requireNonNull(source2);
        Objects.requireNonNull(source3);

        return new Iterator<Tuple3<T1, T2, T3>>() {

            /**
             * @see java.util.Iterator#hasNext()
             */
            public boolean hasNext() {
                return source1.hasNext() && source2.hasNext() && source3.hasNext();
            }

            /**
             * @see java.util.Iterator#next()
             */
            public Tuple3<T1, T2, T3> next() {
                return Tuple3.of(source1.next(), source2.next(), source3.next());
            }
        };
    }

    /**
     * Returns a stream zipping the elements from given source streams.
     *
     * <p>
     * The resulting stream returns tuples from the elements provided by the
     * source streams and returns as many elements as the shorter of the source
     * streams.
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     * @param <T3>
     *            the type of element #3
     * @param source1
     *            the source of elements. It must not be {@code null}.
     * @param source2
     *            the source of elements. It must not be {@code null}.
     * @param source3
     *            the source of elements. It must not be {@code null}.
     *
     * @return a zipping stream
     */
    public static <T1, T2, T3> Stream<Tuple3<T1, T2, T3>> zip(Stream<? extends T1> source1, Stream<? extends T2> source2, Stream<? extends T3> source3) {
        final Iterator<Tuple3<T1, T2, T3>> it = zip(source1.iterator(), source2.iterator(), source3.iterator());
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, 0), false);
    }
}
