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

import java.util.List;
import java.util.Objects;

/**
 * An implementation of the {@link Cursor} interface for the {@link List}
 * collection.
 *
 * @param <E>
 *            the type of the elements
 */
public final class ListCursor<E> implements Cursor<E> {

    /** Underlying collection. */
    private final List<E> container;
    /** Current index. */
    private int index;

    /**
     * Creates a new instance.
     *
     * @param list
     *            the list to use. It must not be {@code null}.
     */
    private ListCursor(List<E> list) {
        container = Objects.requireNonNull(list);
    }

    /**
     * Creates a new instance.
     *
     * @param <E>
     *            the type of the elements
     * @param list
     *            the list to use. It must not be {@code null}.
     *
     * @return the new instance
     */
    public static <E> Cursor<E> create(List<E> list) {
        return new ListCursor<>(list);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("{%d @ %s}", index, container);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ListCursor<?>) {
            final ListCursor<?> o = (ListCursor<?>) obj;
            return (index == o.index) && (container == o.container);
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(index, System.identityHashCode(container));
    }

    /**
     * @see net.yetamine.lang.containers.Pointer#get()
     */
    public E get() {
        return container.get(index);
    }

    /**
     * @see java.util.function.Consumer#accept(java.lang.Object)
     */
    public void accept(E value) {
        container.set(index, value);
    }

    /**
     * @see net.yetamine.lang.collections.Cursor#limit()
     */
    public int limit() {
        return container.size();
    }

    /**
     * @see net.yetamine.lang.collections.Cursor#index()
     */
    public int index() {
        return index;
    }

    /**
     * @see net.yetamine.lang.collections.Cursor#index(int)
     */
    public Cursor<E> index(int value) {
        index = value;
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.Cursor#insert(java.lang.Object)
     */
    public Cursor<E> insert(E value) {
        container.add(index, value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.Cursor#add(java.lang.Object)
     */
    public Cursor<E> add(E value) {
        container.add(index + 1, value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.Cursor#append(java.lang.Object)
     */
    public Cursor<E> append(E value) {
        final int position = index + 1;
        container.add(position, value);
        index = position;
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.Cursor#remove()
     */
    public E remove() {
        return container.remove(index);
    }
}
