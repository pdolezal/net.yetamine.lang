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

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An extension of the {@link Collection} interface providing more fluent
 * programming style, which is useful, e.g., for building collections.
 *
 * @param <E>
 *            the type of values
 */
public interface FluentCollection<E> extends Collection<E>, FluentCollectionExtensions<E, FluentCollection<E>> {

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
     * @see net.yetamine.lang.collections.FluentContainer#accept(java.util.function.Consumer)
     */
    default FluentCollection<E> accept(Consumer<? super FluentCollection<E>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentContainer#map(java.util.function.Function)
     */
    default <U> U map(Function<? super FluentCollection<E>, ? extends U> mapping) {
        return mapping.apply(this);
    }

    /**
     * Applies the given function to {@link #collection()}.
     *
     * <p>
     * This method is convenient shortcut for {@link #map(Function)} which would
     * prefer to use the {@link #collection()} anyway, e.g., when this instance
     * acts as a {@link Collection} builder.
     *
     * @param <U>
     *            the type of the result
     * @param mapping
     *            the function which is supposed to remap {@link #collection()}
     *            to the result to return. It must not be {@code null}.
     *
     * @return the result of the mapping function
     */
    default <U> U remap(Function<? super Collection<E>, ? extends U> mapping) {
        return mapping.apply(collection());
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
    Collection<E> collection();

    /**
     * @see net.yetamine.lang.collections.FluentCollectionExtensions#include(java.lang.Object)
     */
    default FluentCollection<E> include(E value) {
        add(value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollectionExtensions#includeMore(java.lang.Object[])
     */
    default FluentCollection<E> includeMore(@SuppressWarnings("unchecked") E... elements) {
        addAll(Arrays.asList(elements));
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollectionExtensions#contain(java.lang.Object)
     */
    default FluentCollection<E> contain(E value) {
        if (!contains(value)) {
            add(value);
        }

        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollectionExtensions#containMore(java.lang.Object[])
     */
    default FluentCollection<E> containMore(@SuppressWarnings("unchecked") E... elements) {
        for (E element : elements) {
            contain(element);
        }

        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentContainer#discard()
     */
    default FluentCollection<E> discard() {
        clear();
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollectionExtensions#discard(java.lang.Object)
     */
    default FluentCollection<E> discard(Object value) {
        remove(value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollectionExtensions#discardIf(java.util.function.Predicate)
     */
    default FluentCollection<E> discardIf(Predicate<? super E> filter) {
        removeIf(filter);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollectionExtensions#discardAll(java.util.Collection)
     */
    default FluentCollection<E> discardAll(Collection<? extends E> collection) {
        removeAll(collection);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollectionExtensions#preserveAll(java.util.Collection)
     */
    default FluentCollection<E> preserveAll(Collection<? extends E> collection) {
        retainAll(collection);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollectionExtensions#includeAll(java.util.Collection)
     */
    default FluentCollection<E> includeAll(Collection<? extends E> source) {
        addAll(source);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentCollectionExtensions#forAll(java.util.function.Consumer)
     */
    default FluentCollection<E> forAll(Consumer<? super E> consumer) {
        forEach(consumer);
        return this;
    }
}
