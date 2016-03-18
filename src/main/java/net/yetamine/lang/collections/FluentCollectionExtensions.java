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

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A mixin interface defining extensions of the {@link Collection} interface for
 * making the interface more fluent. See {@link FluentMapExtensions} which is an
 * extended and richer variant of the pattern for maps.
 *
 * @param <E>
 *            the type of values
 * @param <T>
 *            the type of self
 */
public interface FluentCollectionExtensions<E, T> extends FluentContainer<T> {

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
    T include(E value);

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
    T includeMore(@SuppressWarnings("unchecked") E... elements);

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
    T contain(E value);

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
    T containMore(@SuppressWarnings("unchecked") E... elements);

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
    T discard(Object value);

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
    T discardIf(Predicate<? super E> filter);

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
    T discardAll(Collection<? extends E> collection);

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
    T preserveAll(Collection<? extends E> collection);

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
    T includeAll(Collection<? extends E> source);

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
    T forAll(Consumer<? super E> consumer);
}
