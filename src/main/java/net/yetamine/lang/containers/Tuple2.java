package net.yetamine.lang.containers;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A rudimentary tuple implementation consisting of two elements.
 *
 * @param <T1>
 *            the type of element #1
 * @param <T2>
 *            the type of element #2
 */
public final class Tuple2<T1, T2> {

    /** Common shared empty tuple. */
    private static final Tuple2<?, ?> EMPTY = new Tuple2<>(null, null);

    /** Element #1. */
    private final T1 value1;
    /** Element #2. */
    private final T2 value2;

    /**
     * Creates a new instance.
     *
     * @param t1
     *            element #1
     * @param t2
     *            element #2
     */
    private Tuple2(T1 t1, T2 t2) {
        value1 = t1;
        value2 = t2;
    }

    // Core construction methods

    /**
     * Creates a new instance.
     *
     * <p>
     * This method is an alias for {@link #of(Object, Object)} and it is meant
     * mainly as a support for static imports.
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     * @param t1
     *            element #1
     * @param t2
     *            element #2
     *
     * @return the new instance
     */
    public static <T1, T2> Tuple2<T1, T2> tuple2(T1 t1, T2 t2) {
        return of(t1, t2);
    }

    /**
     * Creates a new instance.
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     * @param t1
     *            element #1
     * @param t2
     *            element #2
     *
     * @return the new instance
     */
    public static <T1, T2> Tuple2<T1, T2> of(T1 t1, T2 t2) {
        return new Tuple2<>(t1, t2);
    }

    /**
     * Returns an empty tuple (consisting of {@code null} elements).
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     *
     * @return an empty tuple
     */
    @SuppressWarnings("unchecked")
    public static <T1, T2> Tuple2<T1, T2> empty() {
        return (Tuple2<T1, T2>) EMPTY;
    }

    // Common object methods

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("(%s, %s)", value1, value2);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Tuple2<?, ?>) {
            final Tuple2<?, ?> o = (Tuple2<?, ?>) obj;
            return Objects.equals(value1, o.value1) && Objects.equals(value2, o.value2);
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(value1, value2);
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
     * Returns a tuple with element #1 modified to the given value.
     *
     * @param <V>
     *            the type of the value
     * @param value
     *            the value to set
     *
     * @return a tuple with element #1 modified to the given value
     */
    public <V> Tuple2<V, T2> set1(V value) {
        return of(value, value2);
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
    public <V> Tuple2<T1, V> set2(V value) {
        return of(value1, value);
    }

    /**
     * Returns a tuple with swapped elements.
     *
     * @return a tuple with swapped elements
     */
    public Tuple2<T2, T1> swap() {
        return (this == EMPTY) ? empty() : of(value2, value1);
    }

    // Link to Tuple3

    /**
     * Makes a tuple with the given value prepended to this tuple.
     *
     * @param <V>
     *            the type of the value
     * @param value
     *            the value to prepend
     *
     * @return a tuple with the given value prepended to this tuple
     */
    public <V> Tuple3<V, T1, T2> prepend(V value) {
        return Tuple3.of(value, value1, value2);
    }

    /**
     * Makes a tuple with the given value appended to this tuple.
     *
     * @param <V>
     *            the type of the value
     * @param value
     *            the value to append
     *
     * @return a tuple with the given value appended to this tuple
     */
    public <V> Tuple3<T1, T2, V> append(V value) {
        return Tuple3.of(value1, value2, value);
    }

    /**
     * Makes a tuple with the given value inserted between the elements of this
     * tuple.
     *
     * @param <V>
     *            the type of the value
     * @param value
     *            the value to insert
     *
     * @return a tuple with the given value inserted between the elements of
     *         this tuple
     */
    public <V> Tuple3<T1, V, T2> insert(V value) {
        return Tuple3.of(value1, value, value2);
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
    public <V> Tuple2<V, T2> map1(Function<? super T1, ? extends V> mapping) {
        return of(mapping.apply(value1), value2);
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
    public <V> Tuple2<T1, V> map2(Function<? super T2, ? extends V> mapping) {
        return of(value1, mapping.apply(value2));
    }

    /**
     * Passes element #1 to the specified consumer.
     *
     * @param consumer
     *            the consumer to call. It must not be {@code null}.
     *
     * @return this instance
     */
    public Tuple2<T1, T2> use1(Consumer<? super T1> consumer) {
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
    public Tuple2<T1, T2> use2(Consumer<? super T2> consumer) {
        consumer.accept(value2);
        return this;
    }

    /**
     * Passes the elements of this tuple to the given {@link Consumer}.
     *
     * @param consumer
     *            the consumer to apply on the elements. It must not be
     *            {@code null}.
     */
    public void accept(BiConsumer<? super T1, ? super T2> consumer) {
        consumer.accept(value1, value2);
    }

    /**
     * Applies the given function on the elements of this tuple and returns its
     * result.
     *
     * @param <V>
     *            the type of the result
     * @param reduction
     *            the function to apply on the elements. It must not be
     *            {@code null}.
     *
     * @return the result of the given function
     */
    public <V> V reduce(BiFunction<? super T1, ? super T2, ? extends V> reduction) {
        return reduction.apply(value1, value2);
    }

    // Factory methods for common types

    /**
     * Converts an {@link java.util.Map.Entry} instance into a tuple.
     *
     * @param <K>
     *            the type of the entry's key
     * @param <V>
     *            the type of the entry's value
     * @param entry
     *            the entry to convert. It must not be {@code null}.
     *
     * @return a tuple containing the entry content
     */
    public static <K, V> Tuple2<K, V> from(Map.Entry<? extends K, ? extends V> entry) {
        return of(entry.getKey(), entry.getValue());
    }

    /**
     * Makes a tuple from the first two elements provided by the given source.
     *
     * @param <T>
     *            the type of the source's elements
     * @param source
     *            the source to process. It must provide at least two elements.
     *
     * @return a tuple
     *
     * @throws NoSuchElementException
     *             if the source provides too few elements
     */
    public static <T> Tuple2<T, T> from(Iterable<? extends T> source) {
        return from(source.iterator());
    }

    /**
     * Makes a tuple from the first two elements provided by the given source.
     *
     * @param <T>
     *            the type of the source's elements
     * @param source
     *            the source to process. It must provide at least two elements.
     *
     * @return a tuple
     *
     * @throws NoSuchElementException
     *             if the source provides too few elements
     */
    public static <T> Tuple2<T, T> from(Iterator<? extends T> source) {
        return of(source.next(), source.next());
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
     * @param source1
     *            the source of elements. It must not be {@code null}.
     * @param source2
     *            the source of elements. It must not be {@code null}.
     *
     * @return a zipping iterable
     */
    public static <T1, T2> Iterable<Tuple2<T1, T2>> zip(Iterable<? extends T1> source1, Iterable<? extends T2> source2) {
        Objects.requireNonNull(source1);
        Objects.requireNonNull(source2);

        // Rather not make a lambda, it might change
        return new Iterable<Tuple2<T1, T2>>() {

            /**
             * @see java.lang.Iterable#iterator()
             */
            public Iterator<Tuple2<T1, T2>> iterator() {
                return Tuple2.zip(source1.iterator(), source2.iterator());
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
     * @param source1
     *            the source of elements. It must not be {@code null}.
     * @param source2
     *            the source of elements. It must not be {@code null}.
     *
     * @return a zipping iterator
     */
    public static <T1, T2> Iterator<Tuple2<T1, T2>> zip(Iterator<? extends T1> source1, Iterator<? extends T2> source2) {
        Objects.requireNonNull(source1);
        Objects.requireNonNull(source2);

        return new Iterator<Tuple2<T1, T2>>() {

            /**
             * @see java.util.Iterator#hasNext()
             */
            public boolean hasNext() {
                return source1.hasNext() && source2.hasNext();
            }

            /**
             * @see java.util.Iterator#next()
             */
            public Tuple2<T1, T2> next() {
                return Tuple2.of(source1.next(), source2.next());
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
     * @param source1
     *            the source of elements. It must not be {@code null}.
     * @param source2
     *            the source of elements. It must not be {@code null}.
     *
     * @return a zipping stream
     */
    public static <T1, T2> Stream<Tuple2<T1, T2>> zip(Stream<? extends T1> source1, Stream<? extends T2> source2) {
        final Iterator<Tuple2<T1, T2>> it = zip(source1.iterator(), source2.iterator());
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, 0), false);
    }

    // Link to Map

    /**
     * Returns this instance as an immutable {@link Map}.
     *
     * @return this instance as an immutable {@link Map}
     */
    public Map<T1, T2> asMap() {
        return Collections.singletonMap(value1, value2);
    }

    /**
     * Provides the default {@link Collector} to collect a stream of tuples to a
     * map.
     *
     * <p>
     * This method is just a convenient shortcut for getting the suitable
     * collector using {@link Collectors#toMap(Function, Function)}.
     *
     * @param <T1>
     *            the type of element #1
     * @param <T2>
     *            the type of element #2
     *
     * @return a new collector
     */
    public static <T1, T2> Collector<Tuple2<? extends T1, ? extends T2>, ?, Map<T1, T2>> toMap() {
        return Collectors.toMap(Tuple2::get1, Tuple2::get2);
    }
}
