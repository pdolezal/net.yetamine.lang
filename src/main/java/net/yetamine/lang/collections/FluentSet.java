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

package net.yetamine.lang.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An extension of the {@link Set} interface providing more fluent programming
 * style, which is useful, e.g., for building collections.
 *
 * @param <E>
 *            the type of values
 */
public interface FluentSet<E> extends FluentCollection<E> {

    /**
     * Makes a new instance of the default adapter using {@link HashSet} as the
     * backing implementation.
     *
     * @param <E>
     *            the type of values
     *
     * @return a new instance of the default adapter implementation with an
     *         {@link ArrayList}
     */
    static <E> FluentSet<E> create() {
        return adapt(new HashSet<>());
    }

    /**
     * Makes a new instance of the default adapter implementation.
     *
     * @param <E>
     *            the type of values
     * @param set
     *            the set to adapt. It must not be {@code null}.
     *
     * @return a new instance of the default adapter implementation
     */
    static <E> FluentSet<E> adapt(Set<E> set) {
        return new FluentSetAdapter<>(set);
    }

    // Common fluent extensions support

    /**
     * Returns the pure {@link Set} interface for this instance.
     *
     * <p>
     * Because this interface is supposed to acts as a base for various
     * collection adapters, it may be useful to have more efficient access to
     * the actual content-backing instance. Moreover, this method provides the
     * means for default implementations.
     *
     * <p>
     * This method may return the actual underlying storage, or it may return
     * this instance if all default implementations are overridden in order not
     * to use this method anymore (which might result in an endless recursion);
     * returning this instance always might be necessary when the implementation
     * does not adapt any other instance actually.
     *
     * @return the pure {@link Set} interface for this instance
     */
    Set<E> container();

    /**
     * @see net.yetamine.lang.collections.FluentCollection#self()
     */
    default Stream<? extends FluentSet<E>> self() {
        return Stream.of(this);
    }

    /**
     * Applies the given function to {@link #container()}.
     *
     * @param <U>
     *            the type of the result
     * @param mapping
     *            the function which is supposed to remap {@link #container()}
     *            to the result to return. It must not be {@code null}.
     *
     * @return the result of the mapping function
     */
    default <U> U withSet(Function<? super Set<E>, ? extends U> mapping) {
        return mapping.apply(container());
    }

    // Fluent extensions for Set

    /**
     * @see net.yetamine.lang.collections.FluentCollection#include(java.lang.Object)
     */
    default FluentSet<E> include(E value) {
        add(value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#includeMore(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    default FluentSet<E> includeMore(E... elements) {
        addAll(Arrays.asList(elements));
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#contain(java.lang.Object)
     */
    default FluentSet<E> contain(E value) {
        add(value); // For Set this is better than the default implementation
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#containMore(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    default FluentSet<E> containMore(E... elements) {
        for (E element : elements) {
            contain(element);
        }

        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#discard(java.lang.Object)
     */
    default FluentSet<E> discard(Object value) {
        remove(value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#discardAll()
     */
    default FluentSet<E> discardAll() {
        clear();
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#discardIf(java.util.function.Predicate)
     */
    default FluentSet<E> discardIf(Predicate<? super E> filter) {
        removeIf(filter);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#discardAll(java.util.Collection)
     */
    default FluentSet<E> discardAll(Collection<? extends E> collection) {
        removeAll(collection);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#preserveAll(java.util.Collection)
     */
    default FluentSet<E> preserveAll(Collection<? extends E> collection) {
        retainAll(collection);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#includeAll(java.util.Collection)
     */
    default FluentSet<E> includeAll(Collection<? extends E> source) {
        addAll(source);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#forAll(java.util.function.Consumer)
     */
    default FluentSet<E> forAll(Consumer<? super E> consumer) {
        forEach(consumer);
        return this;
    }

    // Collection interface default implementation

    /**
     * @see java.util.Collection#size()
     */
    default int size() {
        return container().size();
    }

    /**
     * @see java.util.Collection#isEmpty()
     */
    default boolean isEmpty() {
        return container().isEmpty();
    }

    /**
     * @see java.util.Collection#contains(java.lang.Object)
     */
    default boolean contains(Object o) {
        return container().contains(o);
    }

    /**
     * @see java.util.Collection#iterator()
     */
    default Iterator<E> iterator() {
        return container().iterator();
    }

    /**
     * @see java.util.Collection#toArray()
     */
    default Object[] toArray() {
        return container().toArray();
    }

    /**
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    default <T> T[] toArray(T[] a) {
        return container().toArray(a);
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    default boolean add(E e) {
        return container().add(e);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    default boolean remove(Object o) {
        return container().remove(o);
    }

    /**
     * @see java.util.Collection#removeIf(java.util.function.Predicate)
     */
    default boolean removeIf(Predicate<? super E> filter) {
        return container().removeIf(filter);
    }

    /**
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    default boolean containsAll(Collection<?> c) {
        return container().containsAll(c);
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    default boolean addAll(Collection<? extends E> c) {
        return container().addAll(c);
    }

    /**
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    default boolean removeAll(Collection<?> c) {
        return container().removeAll(c);
    }

    /**
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    default boolean retainAll(Collection<?> c) {
        return container().retainAll(c);
    }

    /**
     * @see java.util.Collection#clear()
     */
    default void clear() {
        container().clear();
    }

    /**
     * @see java.lang.Iterable#forEach(java.util.function.Consumer)
     */
    default void forEach(Consumer<? super E> action) {
        container().forEach(action);
    }

    /**
     * @see java.util.Collection#stream()
     */
    default Stream<E> stream() {
        return container().stream();
    }

    /**
     * @see java.util.Collection#parallelStream()
     */
    default Stream<E> parallelStream() {
        return container().parallelStream();
    }

    /**
     * @see java.util.Collection#spliterator()
     */
    default Spliterator<E> spliterator() {
        return container().spliterator();
    }
}

/**
 * The default implementation of the {@link FluentSet} interface which may be
 * used as an adapter.
 *
 * <p>
 * The implementation is suitable even for immutable instances as it delegates
 * all the functionality to the backing instance and holds itself no mutable
 * state.
 *
 * @param <E>
 *            the type of values
 */
final class FluentSetAdapter<E> implements Serializable, FluentSet<E> {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Backing instance. */
    private final Set<E> container;

    /**
     * Creates a new instance.
     *
     * @param set
     *            the backing instance. It must not be {@code null}.
     */
    public FluentSetAdapter(Set<E> set) {
        container = Objects.requireNonNull(set);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return container.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return container.equals(obj);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return container.toString();
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#container()
     */
    public Set<E> container() {
        return container;
    }
}
