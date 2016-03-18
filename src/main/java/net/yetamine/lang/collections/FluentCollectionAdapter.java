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
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

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
public final class FluentCollectionAdapter<E> implements Serializable, FluentCollection<E> {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Backing instance. */
    private final Collection<E> collection;

    /**
     * Creates a new instance.
     *
     * @param storage
     *            the backing instance. It must not be {@code null}.
     */
    public FluentCollectionAdapter(Collection<E> storage) {
        collection = Objects.requireNonNull(storage);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return collection.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return collection.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return collection.equals(obj);
    }

    // Collection interface

    /**
     * @see java.util.Collection#size()
     */
    public int size() {
        return collection.size();
    }

    /**
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    /**
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return collection.contains(o);
    }

    /**
     * @see java.util.Collection#iterator()
     */
    public Iterator<E> iterator() {
        return collection.iterator();
    }

    /**
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray() {
        return collection.toArray();
    }

    /**
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    public <T> T[] toArray(T[] a) {
        return collection.toArray(a);
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(E e) {
        return collection.add(e);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        return collection.remove(o);
    }

    /**
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return collection.containsAll(c);
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends E> c) {
        return collection.addAll(c);
    }

    /**
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        return collection.removeAll(c);
    }

    /**
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        return collection.retainAll(c);
    }

    /**
     * @see java.util.Collection#clear()
     */
    public void clear() {
        collection.clear();
    }

    // Fluent interface

    /**
     * @see net.yetamine.lang.collections.FluentCollection#collection()
     */
    public Collection<E> collection() {
        return collection;
    }
}
