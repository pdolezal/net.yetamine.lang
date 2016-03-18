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

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A mixin interface defining extensions of the {@link Map} interface for making
 * the interface more fluent, which is suitable, e.g., for map builders.
 *
 * <p>
 * This interface is not meant for regular clients, it should rather provide the
 * common frame for making and implementing fluent map adapters for different
 * flavours of the {@link Map} interface. For that reason the methods are not
 * implemented in most cases, just define the contracts.
 *
 * <p>
 * This interface intentionally defines just extensions for the {@link Map}
 * interface. It should be used for mixin inheritance with a {@code Map} type,
 * so that it acts as an adapter for the inherited interface kind. Notice that
 * the extension methods, in order to achieve the desired fluent interface, do
 * return this instance, but the type definition allows to return even another
 * type; such devitation is possible, but needs proper documentation to prevent
 * confusion. Even returning different instance than this might be possible in
 * some cases, but it is discouraged as it is more likely to confuse users.
 *
 * @param <K>
 *            the type of keys
 * @param <V>
 *            the type of values
 * @param <T>
 *            the type of self
 */
public interface FluentMapExtensions<K, V, T> extends FluentContainer<T> {

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
    Optional<V> find(Object key);

    /**
     * Returns the value associated with the given key, or create a new value,
     * if no value is associated at this moment, and associate it.
     *
     * @param key
     *            the key to use for looking up the value and optionally
     *            defining the association
     *
     * @return the value associated with the given key (possibly the new one);
     *         usually the result should not be {@code null}, unless explicitly
     *         allowed by an implementation or for a particular instance (using
     *         such a strategy for making new values)
     *
     * @throws UnsupportedOperationException
     *             if this instance does not define any way to make new values
     *             on demand or the key does not comply the strategy to apply,
     *             or if the instance does not support inserting (the value)
     */
    V let(K key);

    /**
     * Ensures that an association with the given key exists by creating a new
     * value if no value is associated at this moment.
     *
     * <p>
     * This method behaves like {@link #let(Object)}, but when it creates a new
     * value, it uses the given function to perform any actions on the value and
     * uses its result as the actual value to set up. This allows, e.g.,
     * remapping the blank value with a better one or filling it with custom
     * content. If the function returns {@code null}, the association is not
     * established.
     *
     * @param key
     *            the key to use for looking up the value and optionally
     *            defining the association
     * @param function
     *            the function which gets the newly created value and should
     *            return the value to actually apply. It must not be
     *            {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if this instance does not define any way to make new values
     *             on demand or the key does not comply the strategy to apply,
     *             or if the instance does not support inserting (the value)
     */
    T let(K key, Function<? super V, ? extends V> function);

    /**
     * Ensures that an association with the given key exists by creating a new
     * value if no value is associated at this moment.
     *
     * <p>
     * This method behaves like {@link #let(Object)}, but when it creates a new
     * value, it uses the given consumer to mutate the value before adding it.
     *
     * @param key
     *            the key to use for looking up the value and optionally
     *            defining the association
     * @param mutator
     *            the consumer which gets the newly created value. It must not
     *            be {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if this instance does not define any way to make new values
     *             on demand or the key does not comply the strategy to apply,
     *             or if the instance does not support inserting (the value)
     */
    T let(K key, BiConsumer<? super K, ? super V> mutator);

    /**
     * Merges the current and given values for the given key using the provided
     * function.
     *
     * <p>
     * This method behaves like {@link Map#merge(Object, Object, BiFunction)}.
     *
     * @param key
     *            the key with which the specified value is to be associated
     * @param value
     *            the value to merge the current value with
     * @param function
     *            the merging function. It must not be {@code null}.
     *
     * @return this instance
     */
    T patch(K key, V value, BiFunction<? super V, ? super V, ? extends V> function);

    /**
     * Associates the specified value with the specified key.
     *
     * <p>
     * This method is equivalent to {@link Map#put(Object, Object)}, it just
     * returns this instance instead of the previously associated value. This
     * method is more convenient when the previously associtated value is not
     * interesting and multiple values shall be associated easily. It may be
     * more efficient then.
     *
     * @param key
     *            the key with which the specified value is to be associated
     * @param value
     *            the value to be associated with the specified key
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if the value could not be associated with the specified key
     */
    T set(K key, V value);

    /**
     * Associates the specified value with the specified key if this instance
     * contains no association for the specified key yet.
     *
     * <p>
     * This method is equivalent to {@link Map#putIfAbsent(Object, Object)}, it
     * just returns this instance instead. This method is more convenient if
     * multiple values shall be associated easily and the possibly associated
     * previous values are not interesting. It may be more efficient then.
     *
     * @param key
     *            the key with which the specified value is to be associated
     * @param value
     *            the value to be associated with the specified key
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if the value could not be associated with the specified key
     */
    T add(K key, V value);

    /**
     * Returns the value associated with the given key, or create a new value,
     * if no value is associated at this moment, and associate it. Rather than
     * failing due to a missing strategy to create the new value, it returns an
     * empty container.
     *
     * @param key
     *            the key defining the mapping
     *
     * @return the value associated with the given key (possibly the new one),
     *         or an empty container if the value could not be made
     *
     * @throws UnsupportedOperationException
     *             if the value could not be associated with the specified key,
     *             e.g., because the instance does not allow it or storing any
     *             values. The reason for this exception is not the inability to
     *             create a new value.
     */
    Optional<V> have(K key);

    /**
     * Removes the specified mapping.
     *
     * <p>
     * This method is equivalent to {@link Map#remove(Object)}, it just returns
     * this instance instead of the result of the removing operation, whatever
     * it is. This method is more convenient when the result is not interesting.
     *
     * @param key
     *            the key of the association to remove
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if the association could not be discarded
     */
    T discard(Object key);

    /**
     * Copies all associations from a source to this instance, overwriting the
     * existing ones.
     *
     * @param source
     *            the source of the associations to copy. It must not be
     *            {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an association could not be set
     */
    T setAll(Map<? extends K, ? extends V> source);

    /**
     * Copies all associations from a source to this instance, but retains the
     * existing ones.
     *
     * @param source
     *            the source of the associations to copy. It must not be
     *            {@code null}.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if an association could not be added
     */
    T addAll(Map<? extends K, ? extends V> source);

    /**
     * Replaces all associations with the results of the provided function.
     *
     * <p>
     * This method behaves like {@link Map#replaceAll(BiFunction)}.
     *
     * @param function
     *            the function to return the new value for each association. It
     *            must not be {@code null}.
     *
     * @return this instance
     */
    T patchAll(BiFunction<? super K, ? super V, ? extends V> function);

    /**
     * Applies the given consumer on all mappings.
     *
     * <p>
     * This method behaves like {@link Map#forEach(BiConsumer)}.
     *
     * @param consumer
     *            the consumer. It must not be {@code null}.
     *
     * @return this instance
     */
    T forAll(BiConsumer<? super K, ? super V> consumer);
}
