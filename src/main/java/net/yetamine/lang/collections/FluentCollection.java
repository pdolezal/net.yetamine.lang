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
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An extension of the {@link Collection} interface providing more fluent
 * programming style, which is useful, e.g., for building collections.
 *
 * @param <E>
 *            the type of values
 */
public interface FluentCollection<E> extends Collection<E> {

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

    // Common fluent extensions support

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
     * Returns a {@link Stream} providing this instance which can be used for
     * pipeline-like processing of this instance then.
     *
     * @return a stream providing this instance
     */
    default Stream<? extends FluentCollection<E>> self() {
        return Stream.of(this);
    }

    /**
     * Applies the given function to {@link #container()} interpreting it as a
     * collection.
     *
     * @param <U>
     *            the type of the result
     * @param mapping
     *            the function which is supposed to remap {@link #container()}
     *            to the result to return. It must not be {@code null}.
     *
     * @return the result of the mapping function
     */
    default <U> U withCollection(Function<? super Collection<E>, ? extends U> mapping) {
        return mapping.apply(container());
    }

    // Fluent extensions for Collection

    /**
     * Adds the given element.
     *
     * <p>
     * The way how the element is added depends on the semantics of the more
     * concrete {@link Collection#add(Object)} contract. However, the element
     * should be contained in this instance at least once if the operation is
     * successful.
     *
     * @param value
     *            the element to add
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentCollection<E> include(E value) {
        add(value);
        return this;
    }

    /**
     * Adds the given elements.
     *
     * <p>
     * This method should behave like {@link Collection#addAll(Collection)} with
     * a list of the given elements.
     *
     * @param elements
     *            the elements to add. It must not be {@code null}.
     *
     * @return this instance
     */
    default FluentCollection<E> includeMore(@SuppressWarnings("unchecked") E... elements) {
        addAll(Arrays.asList(elements));
        return this;
    }

    /**
     * Adds the given element if no such element exists.
     *
     * <p>
     * The way how the element is added depends on the semantics of the more
     * concrete {@link Collection#add(Object)} contract. However, the element
     * should be contained in this instance once if the operation is successful;
     * the mentioned post-condition might not be guaranteed in concurrent cases,
     * because an adapter might not have enough support to ensure the atomicity
     * of test-and-add operation.
     *
     * @param value
     *            the element to add
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentCollection<E> contain(E value) {
        if (!contains(value)) {
            add(value);
        }

        return this;
    }

    /**
     * Adds the given elements that don't exist yet.
     *
     * <p>
     * This method can be based on {@link #contain(Object)} with all its
     * limitations.
     *
     * @param elements
     *            the elements to add. It must not be {@code null}.
     *
     * @return this instance
     */
    default FluentCollection<E> containMore(@SuppressWarnings("unchecked") E... elements) {
        for (E element : elements) {
            contain(element);
        }

        return this;
    }

    /**
     * Removes the given element.
     *
     * @param value
     *            the element to remove
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if the element could not be removed
     */
    default FluentCollection<E> discard(Object value) {
        remove(value);
        return this;
    }

    /**
     * Clears the container.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if clearing operation is not supported
     */
    default FluentCollection<E> discardAll() {
        clear();
        return this;
    }

    /**
     * Removes only the elements from this instance that do not pass the
     * condition of the given filter.
     *
     * @param filter
     *            the condition for the elements. It must not be {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be removed
     */
    default FluentCollection<E> discardIf(Predicate<? super E> filter) {
        removeIf(filter);
        return this;
    }

    /**
     * Removes only the elements from this instance that are contained in the
     * specified collection.
     *
     * @param collection
     *            the collection of elements to remove. It must not be
     *            {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be removed
     */
    default FluentCollection<E> discardAll(Collection<? extends E> collection) {
        removeAll(collection);
        return this;
    }

    /**
     * Retains only the elements in this instance that are contained in the
     * specified collection.
     *
     * @param collection
     *            the collection of elements to retain. It must not be
     *            {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be removed
     */
    default FluentCollection<E> preserveAll(Collection<? extends E> collection) {
        retainAll(collection);
        return this;
    }

    /**
     * Copies all elements from a source to this instance.
     *
     * @param source
     *            the source of the elements to copy. It must not be
     *            {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    default FluentCollection<E> includeAll(Collection<? extends E> source) {
        addAll(source);
        return this;
    }

    /**
     * Applies the given consumer on all elements.
     *
     * <p>
     * This method behaves like {@link Collection#forEach(Consumer)}.
     *
     * @param consumer
     *            the consumer. It must not be {@code null}.
     *
     * @return this instance
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
