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
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A subset of the common {@link Map} interface that provides no bulk operations
 * (except for {@link #clear()}) nor content enumeration.
 *
 * <p>
 * Because this interface is more abstract, it is suitable for encapsulating
 * even non-volatile and distributed storages with associative structure. It
 * mimics {@link Map} interface precisely though, so in many cases both
 * interfaces are interchangeable.
 *
 * <p>
 * Implementations may tolerate {@code null} keys and values, but {@code null}
 * value should be understood as missing association to the given key (this is
 * similar to usual {@code Map} handling).
 *
 * @param <K>
 *            the type of keys
 * @param <V>
 *            the type of values
 */
public interface MappingStorage<K, V> {

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

    // Reading interface

    /**
     * Returns the value associated with the given key.
     *
     * @param key
     *            the key whose associated value is to be returned
     *
     * @return the value associated to the key, or {@code null} when no such
     *         value exists
     *
     * @see Map#get(Object)
     */
    V get(Object key);

    /**
     * Returns the value to which the specified key is mapped, or the given
     * default value if this map contains no mapping for the key.
     *
     * <p>
     * This method is a convenient equivalent (which may be more efficient) for
     * {@code find(key).orElse(value)}. The {@link #find(Object)} version might
     * be more descriptive though.
     *
     * @param key
     *            the key whose associated value is to be returned
     * @param defaultValue
     *            the default mapping of the key
     *
     * @return the value to which the specified key is mapped, or the default
     *         mapping if no mapping for the key exists
     *
     * @see Map#getOrDefault(Object, Object)
     */
    default V getOrDefault(Object key, V defaultValue) {
        final V result = get(key);
        return (result != null) ? result : defaultValue;
    }

    /**
     * Returns the value to which the specified key is mapped, or uses the given
     * provider to provide the result instead.
     *
     * <p>
     * This method is a convenient equivalent (which may be more efficient) for
     * {@code find(key).orElseGet(supplier)}. The {@link #find(Object)} version
     * might be more descriptive though.
     *
     * @param key
     *            the key whose associated value is to be returned
     * @param provider
     *            the default value provider. It must not be {@code null}.
     *
     * @return the value to which the specified key is mapped, or the result of
     *         the given provider if no mapping for the key exists
     */
    default V getOrProvide(Object key, Supplier<? extends V> provider) {
        final V result = get(key);
        return (result != null) ? result : provider.get();
    }

    /**
     * Returns the value associated with the given key.
     *
     * <p>
     * This method is a shortcut for {@code Optional.ofNullable(map.get(key))}.
     * It therefore does not work very well for actual {@code null} values, but
     * it is great for simple actions on valid objects using following pattern:
     * {@code map.find(key).ifPresent(consumer)}.
     *
     * @param key
     *            the key to use for looking up the value
     *
     * @return the value associated with the given key, or an empty container if
     *         no such value exists
     */
    default Optional<V> find(Object key) {
        return Optional.ofNullable(get(key));
    }

    // Insertion methods

    /**
     * Associates the specified value with the specified key, or updates the
     * existing association of the key with the given value.
     *
     * @param key
     *            the key with which the specified value is to be associated
     * @param value
     *            the value to be associated with the specified key. For
     *            {@code null} values, the operation outcome may differ for
     *            various implementations and therefore {@code null} values
     *            should not be used with this method.
     *
     * @return the value of the previous association
     *
     * @see Map#put(Object, Object)
     */
    V put(K key, V value);

    /**
     * Associates the specified key with the specified value if the key is not
     * already associated with a value.
     *
     * @param key
     *            the key with which the specified value is to be associated
     * @param value
     *            the value to associate with the key
     *
     * @return the previously associated value (which is retained), or
     *         {@code null} if the given value has been associated with the
     *         given key
     *
     * @see Map#putIfAbsent(java.lang.Object, java.lang.Object)
     */
    V putIfAbsent(K key, V value);

    /**
     * Associates the specified value with the specified key, or removes the
     * existing associations if the value is {@code null}.
     *
     * <p>
     * This method calls {@link #put(Object, Object)} or {@link #remove(Object)}
     * depending on the value, which makes easy to use {@code null} values even
     * with implementations that do not support {@code null} values.
     *
     * @param key
     *            the key with which the specified value is to be associated
     * @param value
     *            the value to be associated with the specified key, or
     *            {@code null} for removing the association for the key
     *
     * @return the value of the previous association
     */
    default V let(K key, V value) {
        return (value != null) ? put(key, value) : remove(key);
    }

    // Replacement methods

    /**
     * Replaces the entry for the specified key only if the key is currently
     * associated with the specified value.
     *
     * @param key
     *            the key with which the specified value is to be associated
     * @param value
     *            the value to be associated with the specified key
     *
     * @return the previous value, or {@code null} if none
     *
     * @see Map#replace(java.lang.Object, java.lang.Object)
     */
    V replace(K key, V value);

    /**
     * Replaces the entry for the specified key only if the key is currently
     * associated with the specified value.
     *
     * @param key
     *            the key with which the specified value is to be associated
     * @param oldValue
     *            the expected currently associated value
     * @param newValue
     *            the value to be associated with the specified key
     *
     * @return {@code true} if the value was replaced, {@code false} if the
     *         expected value didn't match the actual current value
     *
     * @see Map#replace(Object, Object, Object)
     */
    boolean replace(K key, V oldValue, V newValue);

    // Removal methods

    /**
     * Requests clearing all associations.
     *
     * @see Map#clear()
     */
    void clear();

    /**
     * Removes the association for the specified key and returns the previously
     * associated value if any.
     *
     * @param key
     *            the key whose associated value is to be removed and returned
     *
     * @return the value associated to the key, or {@code null} when no such
     *         value existed
     *
     * @see Map#remove(Object)
     */
    V remove(Object key);

    /**
     * Removes the association for the specified key only if it is currently
     * mapped to the specified value.
     *
     * @param key
     *            the key with which the specified value is associated
     * @param value
     *            the value expected to be associated with the specified key
     *
     * @return {@code true} if the value was removed
     *
     * @see Map#remove(java.lang.Object, java.lang.Object)
     */
    boolean remove(Object key, Object value);

    // Composite operations

    /**
     * Associates the specified key, if the key is not already associated with a
     * value, with the given value; otherwise, replaces the associated value
     * with the results of the given remapping function, or removes if the
     * result is {@code null}.
     *
     * @param key
     *            the key with which the specified value is associated
     * @param value
     *            the value to associate the key with
     * @param remapping
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the new value associated with the specified key, or {@code null}
     *         if no value is associated with the key
     *
     * @see java.util.Map#merge(java.lang.Object, java.lang.Object,
     *      java.util.function.BiFunction)
     */
    V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remapping);

    /**
     * Attempts to compute a mapping for the specified key and its current
     * mapped value (or {@code null} if there is no current mapping).
     *
     * @param key
     *            the key with which the specified value is to be associated
     * @param remapping
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the new value associated with the specified key, or {@code null}
     *         if none
     *
     * @see Map#compute(java.lang.Object, java.util.function.BiFunction)
     */
    V compute(K key, BiFunction<? super K, ? super V, ? extends V> remapping);

    /**
     * Returns the value associated with the given key, or uses the given
     * mapping function to compute the result (the result may be stored).
     *
     * @param key
     *            the key whose associated value is to be returned
     * @param mapping
     *            the mapping function for supplying the result for the given
     *            key. It must not be {@code null}.
     *
     * @return the value associated with the given key, or the result of the
     *         mapping function if no such value existed before calling this
     *         method
     *
     * @see Map#computeIfAbsent(Object, Function)
     */
    V computeIfAbsent(K key, Function<? super K, ? extends V> mapping);

    /**
     * Sets the value associated with the given key to the result of the given
     * remapping function.
     *
     * @param key
     *            the key whose associated value is to be returned
     * @param remapping
     *            the remapping function for supplying the result for the given
     *            key. It must not be {@code null}.
     *
     * @return the value associated with the given key
     *
     * @see Map#computeIfPresent(Object, BiFunction)
     */
    V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remapping);

    /**
     * Returns the value associated with the given key, or uses the given
     * supplier to compute the result (the result may be stored).
     *
     * @param key
     *            the key of the association to compute
     * @param valueSupplier
     *            the supplier to provide the value. It must not be
     *            {@code null}.
     *
     * @return the value associated with the given key, or the result of the
     *         supplier if no such value existed before calling this method
     */
    default V supplyIfAbsent(K key, Supplier<? extends V> valueSupplier) {
        return computeIfAbsent(key, k -> valueSupplier.get());
    }

    /**
     * Sets the value if the mapping is present using a supplier.
     *
     * @param key
     *            the key of the association to compute
     * @param valueSupplier
     *            the supplier to provide the value
     *
     * @return the computed value
     */
    default V supplyIfPresent(K key, Supplier<? extends V> valueSupplier) {
        return computeIfPresent(key, (k, v) -> valueSupplier.get());
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
