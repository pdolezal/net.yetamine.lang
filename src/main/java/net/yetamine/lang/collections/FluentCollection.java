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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.yetamine.lang.functional.Source;

/**
 * An extension of the {@link Collection} interface providing more fluent
 * programming style, which is useful, e.g., for building collections.
 *
 * @param <E>
 *            the type of values
 */
public interface FluentCollection<E> extends Collection<E>, CollectionFluency<E, FluentCollection<E>>, Source<FluentCollection<E>> {

    /**
     * Makes a new instance of the default adapter implementation.
     *
     * @param <E>
     *            the type of values
     * @param collection
     *            the collection to adapt. It must not be {@code null}.
     *
     * @return a new instance of the default adapter implementation
     */
    static <E> FluentCollection<E> adapt(Collection<E> collection) {
        return new FluentCollectionAdapter<>(collection);
    }

    /**
     * Returns the pure {@link Collection} interface for this instance.
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
     * @return the pure {@link Collection} interface for this instance
     */
    Collection<E> container();

    /**
     * Applies the given function to {@link #container()}.
     *
     * <p>
     * This method is convenient shortcut for {@link #map(Function)} which would
     * prefer to use the {@link #container()} anyway, e.g., when this instance
     * acts as a {@link Collection} builder.
     *
     * @param <U>
     *            the type of the result
     * @param mapping
     *            the function which is supposed to remap {@link #container()}
     *            to the result to return. It must not be {@code null}.
     *
     * @return the result of the mapping function
     */
    default <U> U remap(Function<? super Collection<E>, ? extends U> mapping) {
        return mapping.apply(container());
    }

    // Source interface implementation

    /**
     * @see net.yetamine.lang.functional.Source#filter(java.util.function.Predicate)
     */
    default Optional<FluentCollection<E>> filter(Predicate<? super FluentCollection<E>> predicate) {
        return predicate.test(this) ? Optional.of(this) : Optional.empty();
    }

    /**
     * @see net.yetamine.lang.functional.Source#accept(java.util.function.Consumer)
     */
    default FluentCollection<E> accept(Consumer<? super FluentCollection<E>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * @see net.yetamine.lang.functional.Source#map(java.util.function.Function)
     */
    default <U> U map(Function<? super FluentCollection<E>, ? extends U> mapping) {
        return mapping.apply(this);
    }

    // Fluent extensions for Collection

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#include(java.lang.Object)
     */
    default FluentCollection<E> include(E value) {
        add(value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#includeMore(java.lang.Object[])
     */
    default FluentCollection<E> includeMore(@SuppressWarnings("unchecked") E... elements) {
        addAll(Arrays.asList(elements));
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#contain(java.lang.Object)
     */
    default FluentCollection<E> contain(E value) {
        if (!contains(value)) {
            add(value);
        }

        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#containMore(java.lang.Object[])
     */
    default FluentCollection<E> containMore(@SuppressWarnings("unchecked") E... elements) {
        for (E element : elements) {
            contain(element);
        }

        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#discard(java.lang.Object)
     */
    default FluentCollection<E> discard(Object value) {
        remove(value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#discardAll()
     */
    default FluentCollection<E> discardAll() {
        clear();
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#discardIf(java.util.function.Predicate)
     */
    default FluentCollection<E> discardIf(Predicate<? super E> filter) {
        removeIf(filter);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#discardAll(java.util.Collection)
     */
    default FluentCollection<E> discardAll(Collection<? extends E> collection) {
        removeAll(collection);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#preserveAll(java.util.Collection)
     */
    default FluentCollection<E> preserveAll(Collection<? extends E> collection) {
        retainAll(collection);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#includeAll(java.util.Collection)
     */
    default FluentCollection<E> includeAll(Collection<? extends E> source) {
        addAll(source);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#forAll(java.util.function.Consumer)
     */
    default FluentCollection<E> forAll(Consumer<? super E> consumer) {
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
 * The default implementation of the {@link FluentCollection} interface which
 * may be used as an adapter.
 *
 * <p>
 * The implementation is suitable even for immutable instances as it delegates
 * all the functionality to the backing instance and holds itself no mutable
 * state.
 *
 * @param <E>
 *            the type of values
 */
final class FluentCollectionAdapter<E> implements Serializable, FluentCollection<E> {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Backing instance. */
    private final Collection<E> container;

    /**
     * Creates a new instance.
     *
     * @param collection
     *            the backing instance. It must not be {@code null}.
     */
    public FluentCollectionAdapter(Collection<E> collection) {
        container = Objects.requireNonNull(collection);
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
    public Collection<E> container() {
        return container;
    }
}
