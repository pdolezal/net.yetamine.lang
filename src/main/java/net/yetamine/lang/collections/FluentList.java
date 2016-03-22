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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import net.yetamine.lang.functional.Source;

/**
 * An extension of the {@link List} interface providing more fluent programming
 * style, which is useful, e.g., for building collections.
 *
 * @param <E>
 *            the type of values
 */
public interface FluentList<E> extends List<E>, ListFluency<E, FluentList<E>>, Source<FluentList<E>> {

    /**
     * Makes a new instance of the default adapter using {@link ArrayList} as
     * the backing implementation.
     *
     * @param <E>
     *            the type of values
     *
     * @return a new instance of the default adapter implementation with an
     *         {@link ArrayList}
     */
    static <E> FluentList<E> create() {
        return adapt(new ArrayList<>());
    }

    /**
     * Makes a new instance of the default adapter implementation.
     *
     * @param <E>
     *            the type of values
     * @param list
     *            the collection to adapt. It must not be {@code null}.
     *
     * @return a new instance of the default adapter implementation
     */
    static <E> FluentList<E> adapt(List<E> list) {
        return new FluentListAdapter<>(list);
    }

    // Core and common fluent extensions support

    /**
     * Returns the pure {@link List} interface for this instance.
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
     * @return the pure {@link List} interface for this instance
     */
    List<E> container();

    /**
     * @see net.yetamine.lang.functional.Source#filter(java.util.function.Predicate)
     */
    default Optional<FluentList<E>> filter(Predicate<? super FluentList<E>> predicate) {
        return predicate.test(this) ? Optional.of(this) : Optional.empty();
    }

    /**
     * @see net.yetamine.lang.functional.Source#accept(java.util.function.Consumer)
     */
    default FluentList<E> accept(Consumer<? super FluentList<E>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * @see net.yetamine.lang.functional.Source#map(java.util.function.Function)
     */
    default <U> U map(Function<? super FluentList<E>, ? extends U> mapping) {
        return mapping.apply(this);
    }

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
    default <U> U remap(Function<? super List<E>, ? extends U> mapping) {
        return mapping.apply(container());
    }

    // Fluent extensions for List

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#include(java.lang.Object)
     */
    default FluentList<E> include(E value) {
        add(value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#includeMore(java.lang.Object[])
     */
    default FluentList<E> includeMore(@SuppressWarnings("unchecked") E... elements) {
        addAll(Arrays.asList(elements));
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#contain(java.lang.Object)
     */
    default FluentList<E> contain(E value) {
        if (!contains(value)) {
            add(value);
        }

        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#containMore(java.lang.Object[])
     */
    default FluentList<E> containMore(@SuppressWarnings("unchecked") E... elements) {
        for (E element : elements) {
            contain(element);
        }

        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#discard(java.lang.Object)
     */
    default FluentList<E> discard(Object value) {
        remove(value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#discardAll()
     */
    default FluentList<E> discardAll() {
        clear();
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#discardIf(java.util.function.Predicate)
     */
    default FluentList<E> discardIf(Predicate<? super E> filter) {
        removeIf(filter);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#discardAll(java.util.Collection)
     */
    default FluentList<E> discardAll(Collection<? extends E> collection) {
        removeAll(collection);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#preserveAll(java.util.Collection)
     */
    default FluentList<E> preserveAll(Collection<? extends E> collection) {
        retainAll(collection);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#includeAll(java.util.Collection)
     */
    default FluentList<E> includeAll(Collection<? extends E> source) {
        addAll(source);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.CollectionFluency#forAll(java.util.function.Consumer)
     */
    default FluentList<E> forAll(Consumer<? super E> consumer) {
        forEach(consumer);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.ListFluency#insert(int,
     *      java.lang.Object)
     */
    default FluentList<E> insert(int index, E element) {
        add(index, element);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.ListFluency#insertAll(int,
     *      java.util.Collection)
     */
    default FluentList<E> insertAll(int index, Collection<? extends E> c) {
        addAll(index, c);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.ListFluency#discard(int)
     */
    default FluentList<E> discard(int index) {
        remove(index);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.ListFluency#put(int,
     *      java.lang.Object)
     */
    default FluentList<E> put(int index, E element) {
        set(index, element);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.ListFluency#patch(int,
     *      java.util.function.Function)
     */
    default FluentList<E> patch(int index, Function<? super E, ? extends E> operator) {
        set(index, operator.apply(get(index)));
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.ListFluency#patchAll(java.util.function.Function)
     */
    default FluentList<E> patchAll(Function<? super E, ? extends E> operator) {
        replaceAll(operator::apply);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.ListFluency#sorted(java.util.Comparator)
     */
    default FluentList<E> sorted(Comparator<? super E> c) {
        sort(c);
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
     * @see java.util.Set#iterator()
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

    // List interface default implementation

    default void add(int index, E element) {
        container().add(index, element);
    }

    /**
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    default boolean addAll(int index, Collection<? extends E> c) {
        return container().addAll(index, c);
    }

    /**
     * @see java.util.List#get(int)
     */
    default E get(int index) {
        return container().get(index);
    }

    /**
     * @see java.util.List#indexOf(java.lang.Object)
     */
    default int indexOf(Object o) {
        return container().indexOf(o);
    }

    /**
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    default int lastIndexOf(Object o) {
        return container().lastIndexOf(o);
    }

    /**
     * @see java.util.List#listIterator()
     */
    default ListIterator<E> listIterator() {
        return container().listIterator();
    }

    /**
     * @see java.util.List#listIterator(int)
     */
    default ListIterator<E> listIterator(int index) {
        return container().listIterator(index);
    }

    /**
     * @see java.util.List#remove(int)
     */
    default E remove(int index) {
        return container().remove(index);
    }

    /**
     * @see java.util.List#set(int, java.lang.Object)
     */
    default E set(int index, E element) {
        return container().set(index, element);
    }

    /**
     * @see java.util.List#replaceAll(java.util.function.UnaryOperator)
     */
    default void replaceAll(UnaryOperator<E> operator) {
        container().replaceAll(operator);
    }

    /**
     * @see java.util.List#sort(java.util.Comparator)
     */
    default void sort(Comparator<? super E> c) {
        container().sort(c);
    }

    /**
     * @see java.util.List#subList(int, int)
     */
    default FluentList<E> subList(int fromIndex, int toIndex) {
        return adapt(container().subList(fromIndex, toIndex));
    }
}

/**
 * The default implementation of the {@link FluentList} interface which may be
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
final class FluentListAdapter<E> implements Serializable, FluentList<E> {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Backing instance. */
    private final List<E> container;

    /**
     * Creates a new instance.
     *
     * @param list
     *            the backing instance. It must not be {@code null}.
     */
    public FluentListAdapter(List<E> list) {
        container = Objects.requireNonNull(list);
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
    public List<E> container() {
        return container;
    }
}
