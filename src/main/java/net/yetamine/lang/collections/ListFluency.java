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
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A mixin interface defining extensions of the {@link List} interface for
 * making the interface more fluent.
 *
 * @param <E>
 *            the type of values
 * @param <T>
 *            the type of self
 */
public interface ListFluency<E, T> extends CollectionFluency<E, T> {

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
    T insert(int index, E element);

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
    T insertAll(int index, Collection<? extends E> c);

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
    T discard(int index);

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
    T put(int index, E element);

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
    T patch(int index, Function<? super E, ? extends E> operator);

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
    T patchAll(Function<? super E, ? extends E> operator);

    /**
     *
     * @param c
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an element could not be added
     */
    T sorted(Comparator<? super E> c);
}
