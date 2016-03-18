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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The default implementation of the {@link FluentMap} interface which may be
 * used as an adapter.
 *
 * <p>
 * The implementation is suitable even for immutable instances as it delegates
 * all the functionality to the backing instance and holds itself no mutable
 * state.
 *
 * @param <K>
 *            the type of keys
 * @param <V>
 *            the type of values
 */
public final class FluentMapAdapter<K, V> implements Serializable, FluentMap<K, V> {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Backing instance. */
    private final Map<K, V> map;
    /** Function for making new values. */
    private final Function<? super K, ? extends V> defaults;

    /**
     * Creates a new instance with no {@link #defaults()}.
     *
     * @param storage
     *            the backing instance. It must not be {@code null}.
     */
    public FluentMapAdapter(Map<K, V> storage) {
        map = Objects.requireNonNull(storage);
        defaults = null; // Explicitly none!
    }

    /**
     * Creates a new instance.
     *
     * @param storage
     *            the backing instance. It must not be {@code null}.
     * @param factory
     *            the function for making new values
     */
    public FluentMapAdapter(Map<K, V> storage, Function<? super K, ? extends V> factory) {
        map = Objects.requireNonNull(storage);
        defaults = factory;
    }

    /**
     * Creates a new instance.
     *
     * @param storage
     *            the backing instance. It must not be {@code null}.
     * @param factory
     *            the function for making new values
     */
    public FluentMapAdapter(Map<K, V> storage, Supplier<? extends V> factory) {
        map = Objects.requireNonNull(storage);
        defaults = (factory != null) ? o -> factory.get() : null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return map.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return map.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return map.equals(obj);
    }

    // Map interface

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        return map.size();
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public V get(Object key) {
        return map.get(key);
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public V put(K key, V value) {
        return map.put(key, value);
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object key) {
        return map.remove(key);
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        map.clear();
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection<V> values() {
        return map.values();
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    // Fluent interface extensions

    /**
     * @see net.yetamine.lang.collections.FluentMap#map()
     */
    public Map<K, V> map() {
        return map;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMap#defaults()
     */
    public Function<? super K, ? extends V> defaults() {
        return defaults;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMap#defaults(java.util.function.Function)
     */
    public FluentMap<K, V> defaults(Function<? super K, ? extends V> factory) {
        return Objects.equals(factory, defaults) ? this : new FluentMapAdapter<>(map, factory);
    }
}
