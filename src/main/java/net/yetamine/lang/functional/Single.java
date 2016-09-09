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

package net.yetamine.lang.functional;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.yetamine.lang.Trivalent;
import net.yetamine.lang.formatting.Quoting;

/**
 * A counter-part for {@link Optional} that represents an element together with
 * the information whether the element is the single result of some operation.
 *
 * @param <T>
 *            the type of the represented value
 */
public final class Single<T> implements Supplier<T> {

    /** Common shared instance for the non-single null-value instance. */
    private static final Single<?> NULL = new Single<>(null, Trivalent.FALSE);
    /** Common shared instance for the single null-value instance. */
    private static final Single<?> EMPTY = new Single<>(null, Trivalent.TRUE);
    /** Common shared instance for the no-value instance. */
    private static final Single<?> NONE = new Single<>(null, Trivalent.UNKNOWN);

    /** Represented value. */
    private final T value;
    /** State of the instance. */
    private final Trivalent single;

    /**
     * Creates a new instance with an initial value and specified state.
     *
     * @param initial
     *            the initial value to represent
     * @param state
     *            the state of the new instance. It must not be {@code null}.
     */
    private Single(T initial, Trivalent state) {
        assert (state != null);
        single = state;
        value = initial;
    }

    /**
     * Returns an instance representing a single value.
     *
     * @param <T>
     *            the type of the represented value
     * @param value
     *            the single value to represent
     *
     * @return the new instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Single<T> single(T value) {
        return (value != null) ? new Single<>(value, Trivalent.TRUE) : (Single<T>) EMPTY;
    }

    /**
     * Returns an instance representing a non-single value.
     *
     * @param <T>
     *            the type of the represented value
     * @param value
     *            the non-single value to represent
     *
     * @return the new instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Single<T> some(T value) {
        return (value != null) ? new Single<>(value, Trivalent.FALSE) : (Single<T>) NULL;
    }

    /**
     * Returns an instance representing no value.
     *
     * @param <T>
     *            the type of the represented value
     *
     * @return an instance representing no value
     */
    @SuppressWarnings("unchecked")
    public static <T> Single<T> none() {
        return (Single<T>) NONE;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("single[%s, %s]", single, Quoting.single(value));
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return ((obj instanceof Single<?>) && Objects.equals(((Single<?>) obj).value, value));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns the represented value.
     *
     * @return the represented value; {@code null} if the value is {@code null}
     *         or if no value is represented
     *
     * @see java.util.function.Supplier#get()
     */
    public T get() {
        return value;
    }

    /**
     * Returns the represented value, if single, otherwise throw an exception to
     * be created by the provided supplier.
     *
     * @param <X>
     *            the type of the exception to be thrown
     * @param exceptionSupplier
     *            the supplier which will return the exception to be thrown. It
     *            must not be {@code null}.
     *
     * @return the single value
     *
     * @throws X
     *             if this instance does not represent a single value
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (single.isTrue()) {
            // Use assert to check because of performance reasons and false exceptions
            assert (exceptionSupplier != null) : "Exception supplier must not be null.";
            return value;
        }

        throw exceptionSupplier.get();
    }

    /**
     * Returns the state of this instance.
     *
     * <p>
     * Three values may be returned:
     *
     * <ul>
     * <li>{@link Trivalent#TRUE} if the represented value is single.</li>
     * <li>{@link Trivalent#FALSE} if the represented value is not single.</li>
     * <li>{@link Trivalent#UNKNOWN} if no value is represented.</li>
     * </ul>
     *
     * The state may help to distinguish {@code null} provided by the no-value
     * representation from representations returning {@code null} as a valid
     * outcome.
     *
     * @return the state of this instance
     */
    public Trivalent single() {
        return single;
    }

    /**
     * Returns an {@link Optional} containing the represented value.
     *
     * @return an {@link Optional} containing the represented value
     */
    public Optional<T> optional() {
        return single.isTrue() ? Optional.ofNullable(value) : Optional.empty();
    }

    /**
     * Creates a stream of this instance as the only element of the stream.
     *
     * @return a stream of this instance
     */
    public Stream<Single<T>> stream() {
        return Stream.of(this);
    }

    /**
     * Returns an updated instance representing the value; if this instance
     * represents no value, the result represents the value as single, the
     * result is a non-single representation of the value otherwise.
     *
     * @param update
     *            the value to update
     *
     * @return an updated instance
     */
    public Single<T> revised(T update) {
        return single.isUnknown() ? single(update) : some(update);
    }

    /**
     * Returns an updated instance which represents the given single value if
     * this instance represented no value, or represents the same value as
     * before, but marked as non-single.
     *
     * @param update
     *            the value to update
     *
     * @return an updated instance
     */
    public Single<T> updated(T update) {
        switch (single) {
            case UNKNOWN:
                return single(update);

            case FALSE:
                return this;

            case TRUE:
                return some(value);

            default:
                throw new AssertionError();
        }
    }

    /**
     * Returns an instance that represents the same value, but if this instance
     * represents a single value, the result represents a non-single value.
     *
     * @return an updated instance
     */
    public Single<T> updated() {
        return single.isTrue() ? some(value) : this;
    }

    /**
     * Returns a merged instance representing the same value, but possibly not
     * single anymore depending on the state of the other instance.
     *
     * <p>
     * The result is this instance if it is not single. If this instance
     * represents no value, the other instance is returned instead; when neither
     * case occurs (i.e., this instance represents a single value), new instance
     * is returned representing the same value, but not as single.
     *
     * @param other
     *            the other instance. It must not be {@code null}.
     *
     * @return a merged instance
     */
    public Single<T> merged(Single<? extends T> other) {
        Objects.requireNonNull(other);

        if (single.isFalse()) {
            return this;
        }

        if (single.isUnknown()) {
            @SuppressWarnings("unchecked")
            final Single<T> result = (Single<T>) other;
            return result;
        }

        return some(value);
    }

    /**
     * Returns the representation of the first element from the source,
     * indicating if there is some and if it is the single one element of the
     * source.
     *
     * @param <T>
     *            the type of the represented value
     * @param source
     *            the source to process. It must not be {@code null}.
     *
     * @return the representation of the first element
     */
    public static <T> Single<T> head(Iterator<? extends T> source) {
        if (source.hasNext()) {
            final Single<T> result = single(source.next());
            return source.hasNext() ? result.updated() : result;
        }

        return none();
    }

    /**
     * Returns the representation of the first element from the source,
     * indicating if there is some and if it is the single one element of the
     * source.
     *
     * @param <T>
     *            the type of the represented value
     * @param source
     *            the source to process. It must not be {@code null}.
     *
     * @return the representation of the first element
     */
    public static <T> Single<T> head(Iterable<? extends T> source) {
        return head(source.iterator());
    }

    /**
     * Returns the representation of the first element from the source,
     * indicating if there is some and if it is the single one element of the
     * source.
     *
     * @param <T>
     *            the type of the represented value
     * @param source
     *            the source to process. It must not be {@code null}.
     *
     * @return the representation of the first element
     */
    public static <T> Single<T> head(Stream<? extends T> source) {
        return head(source.iterator());
    }

    /**
     * Returns the representation of the last element from the source,
     * indicating if there is some and if it is the single one element of the
     * source.
     *
     * @param <T>
     *            the type of the represented value
     * @param source
     *            the source to process. It must not be {@code null}.
     *
     * @return the representation of the first element
     */
    public static <T> Single<T> last(Iterator<? extends T> source) {
        Single<T> result = none();
        while (source.hasNext()) {
            result = result.revised(source.next());
        }

        return result;
    }

    /**
     * Returns the representation of the last element from the source,
     * indicating if there is some and if it is the single one element of the
     * source.
     *
     * @param <T>
     *            the type of the represented value
     * @param source
     *            the source to process. It must not be {@code null}.
     *
     * @return the representation of the first element
     */
    public static <T> Single<T> last(Iterable<? extends T> source) {
        return last(StreamSupport.stream(source.spliterator(), false));
    }

    /**
     * Returns the representation of the last element from the source,
     * indicating if there is some and if it is the single one element of the
     * source.
     *
     * @param <T>
     *            the type of the represented value
     * @param source
     *            the source to process. It must not be {@code null}.
     *
     * @return the representation of the first element
     */
    public static <T> Single<T> last(List<? extends T> source) {
        final ListIterator<? extends T> it = source.listIterator(source.size());

        if (it.hasPrevious()) {
            final Single<T> result = single(it.previous());
            return it.hasPrevious() ? result.updated() : result;
        }

        return none();
    }

    /**
     * Returns the representation of the last element from the source,
     * indicating if there is some and if it is the single one element of the
     * source.
     *
     * @param <T>
     *            the type of the represented value
     * @param source
     *            the source to process. It must not be {@code null}.
     *
     * @return the representation of the first element
     */
    public static <T> Single<T> last(Stream<? extends T> source) {
        return source.collect(LastCollector.instance());
    }

    /**
     * Returns a function that can be used with {@link #collector(BiFunction)}
     * or {@link Stream#reduce(Object, BiFunction, BinaryOperator)} for finding
     * an optimum, i.e., the maximal element according to the given comparator.
     *
     * @param <T>
     *            the type of the represented value
     * @param comparator
     *            the comparator to use. It must not be {@code null}.
     *
     * @return the selecting function
     */
    public static <T> BiFunction<Single<T>, T, Single<T>> optimum(Comparator<T> comparator) {
        Objects.requireNonNull(comparator);

        return (s, i) -> {
            if (s.single().isUnknown()) {
                return Single.single(i);
            }

            final T value = s.get();
            final int compare = comparator.compare(value, i);
            if (compare < 0) { // Found the new maximum
                return Single.single(i);
            }

            return (compare == 0) ? s.updated() : s;
        };
    };

    /**
     * Returns a collector for making a {@link Single} instance from a stream.
     *
     * <p>
     * The collector allows to reduce a stream into a {@link Single} instance
     * which is useful when a stream shall produce a single value and it must be
     * verified that it is single indeed, while the value would be needed later.
     * Following pattern is then possible:
     *
     * <pre>
     * // Single item wanted, but if multiple are present, remember the last one
     * // which is greater than zero (useful for, e.g., warnings when more given)
     * singlePositive = stream.collect(Single.collector((s, v) -&gt; (v &gt; 0) ? s.revised(v) : s.updated()));
     * </pre>
     *
     * @param <T>
     *            the type of the represented value
     * @param updater
     *            the function for updating the accumulator. It must not be
     *            {@code null} and it must not return {@code null}.
     *
     * @return a collector for making a {@link Single} instance from a stream
     */
    public static <T> Collector<T, ?, Single<T>> collector(BiFunction<? super Single<T>, ? super T, Single<T>> updater) {
        return new SingleCollector<>(updater);
    }

    /**
     * An implementation of the {@link Collector} interface for collecting to a
     * {@link Single} instance.
     *
     * @param <T>
     *            the type of the stream values
     */
    private static final class SingleCollector<T> implements Collector<T, Box<Single<T>>, Single<T>> {

        /** Accumulator updating function. */
        private final BiFunction<? super Single<T>, ? super T, Single<T>> accumulatorUpdate;

        /**
         * Creates a new instance.
         *
         * @param updater
         *            the function for updating the accumulator. It must not be
         *            {@code null} and it must not return {@code null}.
         */
        public SingleCollector(BiFunction<? super Single<T>, ? super T, Single<T>> updater) {
            accumulatorUpdate = Objects.requireNonNull(updater);
        }

        /**
         * @see Collector#characteristics()
         */
        public Set<Collector.Characteristics> characteristics() {
            return EnumSet.of(Collector.Characteristics.CONCURRENT);
        }

        /**
         * @see Collector#supplier()
         */
        public Supplier<Box<Single<T>>> supplier() {
            return () -> Box.of(Single.none());
        }

        /**
         * @see Collector#accumulator()
         */
        public BiConsumer<Box<Single<T>>, T> accumulator() {
            return (box, value) -> box.set(accumulatorUpdate.apply(box.get(), value));
        }

        /**
         * @see Collector#combiner()
         */
        public BinaryOperator<Box<Single<T>>> combiner() {
            return (box1, box2) -> Box.of(box2.get().merged(box1.get()));
        }

        /**
         * @see Collector#finisher()
         */
        public Function<Box<Single<T>>, Single<T>> finisher() {
            return Box::get;
        }
    }

    /**
     * An implementation of the {@link Collector} interface for getting the last
     * element.
     *
     * @param <T>
     *            the type of the stream values
     */
    private static final class LastCollector<T> implements Collector<T, Box<Single<T>>, Single<T>> {

        /** Common instance of this class. */
        private static final Collector<Object, Box<Single<Object>>, Single<Object>> INSTANCE = new LastCollector<>();

        /**
         * Creates a new instance.
         */
        private LastCollector() {
            // Default constructor
        }

        /**
         * Returns an instance of this class.
         *
         * @param <T>
         *            the type of the represented value
         *
         * @return an instance of this class
         */
        @SuppressWarnings("unchecked")
        public static <T> Collector<T, ?, Single<T>> instance() {
            return (Collector<T, Box<Single<T>>, Single<T>>) (Collector<?, ?, ?>) INSTANCE;
        }

        /**
         * @see Collector#characteristics()
         */
        public Set<Collector.Characteristics> characteristics() {
            return EnumSet.of(Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED);
        }

        /**
         * @see Collector#supplier()
         */
        public Supplier<Box<Single<T>>> supplier() {
            return () -> Box.of(Single.none());
        }

        /**
         * @see Collector#accumulator()
         */
        public BiConsumer<Box<Single<T>>, T> accumulator() {
            return (box, value) -> box.replace(unique -> unique.revised(value));
        }

        /**
         * @see Collector#combiner()
         */
        public BinaryOperator<Box<Single<T>>> combiner() {
            return (box1, box2) -> Box.of(box2.get().merged(box1.get()));
        }

        /**
         * @see Collector#finisher()
         */
        public Function<Box<Single<T>>, Single<T>> finisher() {
            return Box::get;
        }
    }

    /**
     * A mutable container for holding a single value.
     *
     * <p>
     * This implementation is a simplified version of the public version of the
     * class. Although simplification makes it a bit more efficient, the reason
     * for having a special implementation actually is avoiding the circular
     * dependencies between the packages.
     *
     * @param <T>
     *            the type of the stored value
     *
     * @see net.yetamine.lang.containers.Box
     */
    private static final class Box<T> {

        /** Boxed value. */
        private T value;

        /**
         * Creates a new instance.
         *
         * @param initial
         *            the value to store
         */
        private Box(T initial) {
            value = initial;
        }

        /**
         * Creates a new instance.
         *
         * @param <T>
         *            the type of the stored value
         * @param value
         *            the value to store
         *
         * @return the new instance
         */
        public static <T> Box<T> of(T value) {
            return new Box<>(value);
        }

        /**
         * Returns the value.
         *
         * @return the value
         */
        public T get() {
            return value;
        }

        /**
         * Sets the value
         *
         * @param t
         *            the value to set
         */
        public void set(T t) {
            value = t;
        }

        /**
         * Computes the new value, updates this instance and returns it.
         *
         * @param mapping
         *            the value mapping function to apply. It must not be
         *            {@code null}.
         *
         * @return this instance
         */
        public Box<T> replace(Function<? super T, ? extends T> mapping) {
            value = mapping.apply(value);
            return this;
        }
    }
}
