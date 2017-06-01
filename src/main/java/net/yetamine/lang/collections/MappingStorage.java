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
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A compatibility-retaining child of {@link Mapping}.
 *
 * @param <K>
 *            the type of keys
 * @param <V>
 *            the type of values
 *
 * @deprecated Use {@link Mapping} instead as this interface adds nothing new
 *             and exists for compatibility in this version.
 */
@Deprecated
public interface MappingStorage<K, V> extends Mapping<K, V> {

    /**
     * Returns an instance that stores nothing and always returns {@code null}.
     *
     * @param <K>
     *            the type of keys
     * @param <V>
     *            the type of values
     *
     * @return the instance of an empty immutable instance
     */
    static <K, V> MappingStorage<K, V> empty() {
        return EmptyMappingStorage.INSTANCE.cast();
    }

    /**
     * Returns a new instance based on the given {@link Map} instance.
     *
     * <p>
     * The result inherits all characteristics (like thread safety) from the
     * given map which is used as the underlying storage.
     *
     * @param <K>
     *            the type of keys
     * @param <V>
     *            the type of values
     * @param map
     *            the map to use as the underlying storage. It must not be
     *            {@code null}.
     *
     * @return the instance using the given map
     */
    static <K, V> MappingStorage<K, V> adapting(Map<K, V> map) {
        return new MappingStorageAdapter<>(map);
    }
}

/**
 * A singleton implementing an empty {@link MappingStorage}.
 */
enum EmptyMappingStorage implements MappingStorage<Object, Object> {

    /** Sole instance of this class. */
    INSTANCE;

    /**
     * Casts this instance to the given type.
     *
     * @param <K>
     *            the type of keys
     * @param <V>
     *            the type of values
     *
     * @return this instance
     */
    @SuppressWarnings("unchecked")
    public <K, V> MappingStorage<K, V> cast() {
        return (MappingStorage<K, V>) this;
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#get(java.lang.Object)
     */
    public Object get(Object key) {
        return null;
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#put(java.lang.Object,
     *      java.lang.Object)
     */
    public Object put(Object key, Object value) {
        return null;
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#putIfAbsent(java.lang.Object,
     *      java.lang.Object)
     */
    public Object putIfAbsent(Object key, Object value) {
        return null;
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#replace(java.lang.Object,
     *      java.lang.Object)
     */
    public Object replace(Object key, Object value) {
        return null;
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public boolean replace(Object key, Object oldValue, Object newValue) {
        return (oldValue == null);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#clear()
     */
    public void clear() {
        // Do nothing
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        return null;
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#remove(java.lang.Object,
     *      java.lang.Object)
     */
    public boolean remove(Object key, Object value) {
        return false;
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#merge(java.lang.Object,
     *      java.lang.Object, java.util.function.BiFunction)
     */
    public Object merge(Object key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> remapping) {
        Objects.requireNonNull(remapping);
        return value;
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#compute(java.lang.Object,
     *      java.util.function.BiFunction)
     */
    public Object compute(Object key, BiFunction<? super Object, ? super Object, ? extends Object> remapping) {
        Objects.requireNonNull(remapping);
        return remapping.apply(key, null);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#computeIfAbsent(java.lang.Object,
     *      java.util.function.Function)
     */
    public Object computeIfAbsent(Object key, Function<? super Object, ? extends Object> mapping) {
        Objects.requireNonNull(mapping);
        return mapping.apply(key);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#computeIfPresent(java.lang.Object,
     *      java.util.function.BiFunction)
     */
    public Object computeIfPresent(Object key, BiFunction<? super Object, ? super Object, ? extends Object> remapping) {
        Objects.requireNonNull(remapping);
        return null;
    }
}

/**
 * The default implementation for {@link MappingStorage#adapting(Map)}.
 *
 * @param <K>
 *            the type of keys
 * @param <V>
 *            the type of values
 */
final class MappingStorageAdapter<K, V> implements Serializable, MappingStorage<K, V> {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Underlying storage. */
    private final Map<K, V> map;

    /**
     * Creates a new instance.
     *
     * @param storage
     *            the underlying storage. It must not be {@code null}.
     */
    public MappingStorageAdapter(Map<K, V> storage) {
        map = Objects.requireNonNull(storage);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#get(java.lang.Object)
     */
    public V get(Object key) {
        return map.get(key);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#put(java.lang.Object,
     *      java.lang.Object)
     */
    public V put(K key, V value) {
        return map.put(key, value);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#putIfAbsent(java.lang.Object,
     *      java.lang.Object)
     */
    public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#replace(java.lang.Object,
     *      java.lang.Object)
     */
    public V replace(K key, V value) {
        return map.replace(key, value);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#clear()
     */
    public void clear() {
        map.clear();
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#remove(java.lang.Object)
     */
    public V remove(Object key) {
        return map.remove(key);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#remove(java.lang.Object,
     *      java.lang.Object)
     */
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#merge(java.lang.Object,
     *      java.lang.Object, java.util.function.BiFunction)
     */
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remapping) {
        return map.merge(key, value, remapping);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#compute(java.lang.Object,
     *      java.util.function.BiFunction)
     */
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remapping) {
        return map.compute(key, remapping);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#computeIfAbsent(java.lang.Object,
     *      java.util.function.Function)
     */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mapping) {
        return map.computeIfAbsent(key, mapping);
    }

    /**
     * @see net.yetamine.lang.collections.MappingStorage#computeIfPresent(java.lang.Object,
     *      java.util.function.BiFunction)
     */
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remapping) {
        return map.computeIfPresent(key, remapping);
    }
}
