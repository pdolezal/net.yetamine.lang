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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import net.yetamine.lang.Throwables;
import net.yetamine.lang.functional.Producer;

/**
 * An extension of the {@link List} interface providing more fluent programming
 * style, which is useful, e.g., for building collections.
 *
 * @param <E>
 *            the type of values
 */
public interface FluentList<E> extends FluentCollection<E>, List<E> {

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

    // Common fluent extensions support

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
     * @see net.yetamine.lang.collections.FluentCollection#that()
     */
    default Producer<? extends List<E>> that() {
        return this::container;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#self()
     */
    default Producer<? extends FluentCollection<E>> self() {
        return () -> this;
    }

    // Fluent extensions for List

    /**
     * Returns a new cursor for the list.
     *
     * @return a new cursor for the list
     */
    default Cursor<E> cursor() {
        return ListCursor.create(container());
    }

    /**
     * Returns the index for the specified position.
     *
     * <p>
     * This method uses the position definition that differs from the common
     * index : negative values are understood as offsets from {@link #size()},
     * e.g., -1 refers to the last element.
     *
     * @param position
     *            the position to get the index
     *
     * @return the index
     */
    default int index(int position) {
        return (position < 0) ? size() + position : position;
    }

    /**
     * Returns the element in the list at the given index as an {@link Optional}
     * instance.
     *
     * <p>
     * This method is a shortcut for {@code Optional.ofNullable(get(index))},
     * including all the implications and requirements.
     *
     * @param index
     *            the index of the element
     *
     * @return the element as an {@link Optional} instance
     */
    default Optional<E> see(int index) {
        return Optional.ofNullable(get(index));
    }

    /**
     * Returns the first element in the list as an {@link Optional} instance.
     *
     * <p>
     * This method is a shortcut for {@code Optional.ofNullable(head())}.
     *
     * @return the element as an {@link Optional} instance
     *
     * @throws NoSuchElementException
     *             if the list is empty
     */
    default Optional<E> seeHead() {
        return Optional.ofNullable(head());
    }

    /**
     * Returns the last element in the list as an {@link Optional} instance.
     *
     * <p>
     * This method is a shortcut for {@code Optional.ofNullable(last())}.
     *
     * @return the element as an {@link Optional} instance
     *
     * @throws NoSuchElementException
     *             if the list is empty
     */
    default Optional<E> seeLast() {
        return Optional.ofNullable(last());
    }

    /**
     * Returns the first element in the list as an {@link Optional} instance,
     * while it returns an empty {@code Optional} if the list is empty.
     *
     * @return an {@link Optional} instance with the element (or empty if the
     *         element actually is {@code null}), or an empty instance if the
     *         list is empty
     */
    default Optional<E> peekAtHead() {
        if (isEmpty()) { // Preliminary check to avoid unnecessary exceptions
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(get(0));
        } catch (IndexOutOfBoundsException e) {
            // May happen despite of the preliminary check if a concurrent
            // modification has occurred meanwhile
            return Optional.empty();
        }
    }

    /**
     * Returns the last element in the list as an {@link Optional} instance,
     * while it returns an empty {@code Optional} if the list is empty.
     *
     * @return an {@link Optional} instance with the element (or empty if the
     *         element actually is {@code null}), or an empty instance if the
     *         list is empty
     */
    default Optional<E> peekAtLast() {
        for (int size; (size = size()) != 0;) {
            try {
                return Optional.ofNullable(get(size - 1));
            } catch (IndexOutOfBoundsException e) {
                // Try again: a concurrent modification occurred probably
            }
        }

        return Optional.empty();
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#peekAtSome()
     */
    default Optional<E> peekAtSome() {
        return peekAtHead();
    }

    /**
     * Returns the element in the list at the given index as an {@link Optional}
     * instance, while it returns an empty {@code Optional} if the index is out
     * of bounds.
     *
     * @param index
     *            the index of the element
     *
     * @return an {@link Optional} instance with the element (or empty if the
     *         element actually is {@code null}), or an empty instance if the
     *         index is invalid
     */
    default Optional<E> peekAt(int index) {
        if ((index < 0) || (size() < index)) { // Preliminary check to avoid unnecessary exceptions
            return Optional.empty();
        }

        try {
            return see(index);
        } catch (IndexOutOfBoundsException e) {
            // May happen despite of the preliminary check if a concurrent
            // modification has occurred meanwhile
            return Optional.empty();
        }
    }

    /**
     * Returns the first element in the list.
     *
     * @return the first element in the list
     *
     * @throws NoSuchElementException
     *             if the list is empty
     */
    default E head() {
        try {
            if (isEmpty()) { // Preliminary check to avoid unnecessary exceptions
                throw new NoSuchElementException();
            }

            return get(0);
        } catch (IndexOutOfBoundsException e) {
            // May happen despite of the preliminary check if a concurrent
            // modification has occurred meanwhile
            throw Throwables.init(new NoSuchElementException(), e);
        }
    }

    /**
     * Returns the last element in the list.
     *
     * <p>
     * The default implementation uses the optimistic non-blocking strategy to
     * cope with concurrent scenarios: it computes the position of the last
     * element and tries to get it; if the attempt succeeds, the element is
     * returned, otherwise it tries again, unless the list is empty, which
     * results in throwing {@link NoSuchElementException}.
     *
     * @return the last element in the list
     *
     * @throws NoSuchElementException
     *             if the list is empty
     */
    default E last() {
        for (int size; (size = size()) != 0;) {
            try {
                return get(size - 1);
            } catch (IndexOutOfBoundsException e) {
                // Try again: a concurrent modification occurred probably
            }
        }

        throw new NoSuchElementException();
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#some()
     */
    default E some() {
        return head();
    }

    /**
     * Inserts an element at the specified index.
     *
     * <p>
     * This method behaves like {@link List#add(int, Object)}.
     *
     * @param index
     *            the index
     * @param element
     *            the element to insert
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentList<E> insert(int index, E element) {
        add(index, element);
        return this;
    }

    /**
     * Inserts elements from the given collection at the specified index.
     *
     * <p>
     * This method behaves like {@link List#addAll(int, Collection)}.
     *
     * @param index
     *            the index
     * @param c
     *            the collection to insert. It must not be {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentList<E> insertAll(int index, Collection<? extends E> c) {
        addAll(index, c);
        return this;
    }

    /**
     * Removes the element at the given index.
     *
     * <p>
     * This method behaves like {@link List#remove(int)}.
     *
     * @param index
     *            the index
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentList<E> discard(int index) {
        remove(index);
        return this;
    }

    /**
     * Appends the element at the end of the list.
     *
     * @param element
     *            the element
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentList<E> append(E element) {
        add(element);
        return this;
    }

    /**
     * Sets the element at the specified index.
     *
     * <p>
     * This method behaves like {@link List#set(int, Object)}.
     *
     * @param index
     *            the index
     * @param element
     *            the element
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentList<E> put(int index, E element) {
        set(index, element);
        return this;
    }

    /**
     * Replaces the given element.
     *
     * <p>
     * This method gets the element at the given position and stores the value
     * provided by the given operator instead. Since {@link List} provides no
     * method with atomic guarantees, this operation might not be executed in
     * the atomic fashion.
     *
     * @param index
     *            the index
     * @param operator
     *            the operator to apply. It must not be {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentList<E> patch(int index, Function<? super E, ? extends E> operator) {
        set(index, operator.apply(get(index)));
        return this;
    }

    /**
     * Replaces all elements.
     *
     * <p>
     * This method behaves like {@link List#replaceAll(UnaryOperator)}.
     *
     * @param operator
     *            the operator to apply. It must not be {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentList<E> patchAll(Function<? super E, ? extends E> operator) {
        replaceAll(operator::apply);
        return this;
    }

    /**
     * Sorts this instance.
     *
     * <p>
     * This method behaves like {@link List#sort(Comparator)}.
     *
     * @param c
     *            the comparator to use. It must not be {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentList<E> sorted(Comparator<? super E> c) {
        sort(c);
        return this;
    }

    // Fluent extensions for Collection

    /**
     * @see net.yetamine.lang.collections.FluentCollection#include(java.lang.Object)
     */
    default FluentList<E> include(E value) {
        add(value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#includeMore(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    default FluentList<E> includeMore(E... elements) {
        addAll(Arrays.asList(elements));
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#contain(java.lang.Object)
     */
    default FluentList<E> contain(E value) {
        if (!contains(value)) {
            add(value);
        }

        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#containMore(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    default FluentList<E> containMore(E... elements) {
        for (E element : elements) {
            contain(element);
        }

        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#discard(java.lang.Object)
     */
    default FluentList<E> discard(Object value) {
        remove(value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#discardAll()
     */
    default FluentList<E> discardAll() {
        clear();
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#discardIf(java.util.function.Predicate)
     */
    default FluentList<E> discardIf(Predicate<? super E> filter) {
        removeIf(filter);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#discardAll(java.util.Collection)
     */
    default FluentList<E> discardAll(Collection<? extends E> collection) {
        removeAll(collection);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#preserveAll(java.util.Collection)
     */
    default FluentList<E> preserveAll(Collection<? extends E> collection) {
        retainAll(collection);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#includeAll(java.util.Collection)
     */
    default FluentList<E> includeAll(Collection<? extends E> source) {
        addAll(source);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollection#forAll(java.util.function.Consumer)
     */
    default FluentList<E> forAll(Consumer<? super E> consumer) {
        forEach(consumer);
        return this;
    }

    // List interface default implementation

    /**
     * @see java.util.List#add(int, java.lang.Object)
     */
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
