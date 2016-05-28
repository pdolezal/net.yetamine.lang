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

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import net.yetamine.lang.containers.Pointer;

/**
 * Extends the {@link Pointer} interface for index-based collections like lists.
 *
 * <p>
 * A cursor maintains an index to the linked collection and provides means to
 * access the colection with an alternative interface. Therefore it acts as a
 * reference to a slot in the collection, not to a value or an actual element.
 *
 * <p>
 * Note that this interface does not provide any thread safety guarantees and
 * implementations, due to technical limitations, are not required to provide
 * them; anyway, using the cursor, due to its nature where most operations are
 * composite, needs some client-side concurrency control, so the limitation is
 * usually acceptable.
 *
 * @param <E>
 *            the type of the elements
 */
public interface Cursor<E> extends Pointer<E> {

    // Position handling

    /**
     * Returns the limit of the index.
     *
     * <p>
     * An element may have the index between zero (inclusive) and the limit
     * (exclusive). The position of the limit itself points to no element,
     * however, it is the position where new elements may be appended (or
     * inserted).
     *
     * @return the limit of the index
     */
    int limit();

    /**
     * Returns the current index.
     *
     * @return the current index
     */
    int index();

    /**
     * Sets the current index.
     *
     * <p>
     * Setting the index even to an invalid value does not cause an immediate
     * problem, but using an instance with an invalid index throws usually an
     * appropriate exception (like {@link IndexOutOfBoundsException}).
     *
     * @param value
     *            the new index
     *
     * @return this instance
     */
    Cursor<E> index(int value);

    /**
     * Sets the current index to zero, i.e., moves the cursor to the position of
     * the first element of the collection (if present).
     *
     * @return this instance
     */
    default Cursor<E> head() {
        return index(0);
    }

    /**
     * Sets the current index to {@link #limit()} - 1, i.e., moves the cursor to
     * the position of the last element of the collection (if present).
     *
     * @return this instance
     */
    default Cursor<E> last() {
        return index(limit() - 1);
    }

    /**
     * Increments the current index, i.e., moves the cursor to the next
     * position.
     *
     * @return this instance
     */
    default Cursor<E> next() {
        return move(1);
    }

    /**
     * Decrements the current index, i.e., moves the cursor to the preceding
     * position.
     *
     * @return this instance
     */
    default Cursor<E> back() {
        return move(-1);
    }

    /**
     * Changes the current index by the specified offset.
     *
     * @param offset
     *            the offset to change the current index of
     *
     * @return this instance
     */
    default Cursor<E> move(int offset) {
        return index(index() + offset);
    }

    /**
     * Changes the current index to the specified position.
     *
     * <p>
     * This method uses the position definition that differs from the values
     * used by {@link #index(int)}: negative values are understood as offsets
     * from the {@link #limit()}, e.g., -1 refers to the last element.
     *
     * @param position
     *            the position to set the index to
     *
     * @return this instance
     */
    default Cursor<E> seek(int position) {
        return index((position < 0) ? limit() + position : position);
    }

    // Conditional operations

    /**
     * Tests if the index is not negative and below {@link #limit()} which is
     * considered valid.
     *
     * @return {@code true} if the index is within the acceptable bounds
     */
    default boolean isValid() {
        final int index = index();
        return (0 <= index) && (index < limit());
    }

    /**
     * Invokes the given action if the index {@link #isValid()}.
     *
     * @param action
     *            the action to invoke. It must not be {@code null}.
     *
     * @return this instance
     */
    default Cursor<E> ifValid(Consumer<? super Cursor<E>> action) {
        if (isValid()) {
            action.accept(this);
        }

        return this;
    }

    // Element modification

    /**
     * Sets the element value.
     *
     * <p>
     * This method is an alias to {@link #accept(Object)}, just returns this
     * instance.
     *
     * @param value
     *            the value to set
     *
     * @return this instance
     */
    default Cursor<E> set(E value) {
        accept(value);
        return this;
    }

    /**
     * Computes the new value of the element, updates the element and returns
     * its new value.
     *
     * @param mapping
     *            the mapping function to apply. It must not be {@code null}.
     *
     * @return this instance
     *
     * @see #compute(Function)
     */
    default Cursor<E> patch(Function<? super E, ? extends E> mapping) {
        accept(mapping.apply(get()));
        return this;
    }

    /**
     * Computes the new value of the element, updates the element and returns
     * its new value.
     *
     * @param mapping
     *            the mapping function to apply; it gets the given value as the
     *            first argument and the current value as the second argument.
     *            It must not be {@code null}.
     * @param value
     *            the value to pass as the first argument to the mapping
     *            function
     *
     * @return this instance
     *
     * @see #compute(BiFunction, Object)
     */
    default Cursor<E> patch(BiFunction<? super E, ? super E, ? extends E> mapping, E value) {
        accept(mapping.apply(value, get()));
        return this;
    }

    // Element insertion

    /**
     * Inserts a new element at the current position. The current position is
     * not changed, therefore the cursor then points to the new element.
     *
     * @param value
     *            the value to insert
     *
     * @return this instance
     */
    Cursor<E> insert(E value);

    /**
     * Inserts a new element after the current position. The current position is
     * not changed, therefore the cursor then points still to the same element.
     *
     * @param value
     *            the value to insert
     *
     * @return this instance
     */
    Cursor<E> add(E value);

    /**
     * Inserts a new element after the current position and moves the cursor to
     * the position of the new element.
     *
     * @param value
     *            the value to insert
     *
     * @return this instance
     */
    Cursor<E> append(E value);

    // Element removal

    /**
     * Removes the element at the current position.
     *
     * @return this instance
     */
    default Cursor<E> discard() {
        remove();
        return this;
    }

    /**
     * Removes the element at the current position.
     *
     * @return the value of the removed element
     */
    E remove();
}
