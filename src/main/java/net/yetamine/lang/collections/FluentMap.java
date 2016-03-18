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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An extension of the {@link Map} interface providing more fluent programming
 * style, which is useful, e.g., for building maps.
 *
 * <p>
 * As an example, an adapter for a regular map can be used easily to define
 * {@link Map} constants:
 *
 * <pre>
 * static final Map&lt;TimeUnit, String&gt; UNITS = FluentMap.adapt(new EnumMap&lt;TimeUnit, String&gt;(TimeUnit.class))
 *         .add(TimeUnit.NANOSECONDS, "ns")
 *         .add(TimeUnit.MICROSECONDS, "μs")
 *         .add(TimeUnit.MILLISECONDS, "ms")
 *         .add(TimeUnit.SECONDS, "s")
 *         .add(TimeUnit.MINUTES, "min")
 *         .add(TimeUnit.HOURS, "h")
 *         .add(TimeUnit.DAYS, "d")
 *         .remap(Collections::unmodifiableMap);
 * </pre>
 *
 * Another example allows easier work with multimaps:
 *
 * <pre>
 * final FluentMap m = FluentMap.adapt(new HashMap&lt;String, List&lt;String&gt;&gt;).defaults(ArrayList::new);
 * m.let("greet").add("Hello");
 * m.let("title", l -&gt; {
 *     l.add("Mr.");
 *     l.add("Ms.");
 *     l.add("Mrs.");
 *     l.add("Miss");
 *     return l;
 * });
 * </pre>
 *
 * @param <K>
 *            the type of keys
 * @param <V>
 *            the type of values
 */
public interface FluentMap<K, V> extends Map<K, V>, FluentMapExtensions<K, V, FluentMap<K, V>> {

    /**
     * Makes a new instance of the default adapter implementation.
     *
     * @param <K>
     *            the type of keys
     * @param <V>
     *            the type of values
     * @param map
     *            the map to adapt. It must not be {@code null}.
     *
     * @return a new instance of the default adapter implementation
     */
    static <K, V> FluentMap<K, V> adapt(Map<K, V> map) {
        return new FluentMapAdapter<>(map);
    }

    /**
     * Makes a new instance of the default adapter implementation.
     *
     * <p>
     * This method acts as a shortcut for {@code adapt(map).defaults(factory)}
     * and it may be more efficient.
     *
     * @param <K>
     *            the type of keys
     * @param <V>
     *            the type of values
     * @param map
     *            the map to adapt. It must not be {@code null}.
     * @param defaults
     *            the factory function to be used for making new values, see
     *            {@link #defaults()}
     *
     * @return a new instance of the default adapter implementation
     */
    static <K, V> FluentMap<K, V> adapt(Map<K, V> map, Function<? super K, ? extends V> defaults) {
        return new FluentMapAdapter<>(map, defaults);
    }

    /**
     * Makes a new instance of the default adapter implementation.
     *
     * <p>
     * This method acts as a shortcut for {@code adapt(map).defaults(factory)}
     * and it may be more efficient.
     *
     * @param <K>
     *            the type of keys
     * @param <V>
     *            the type of values
     * @param map
     *            the map to adapt. It must not be {@code null}.
     * @param defaults
     *            the supplier to be used for making new values, see
     *            {@link #defaults()}
     *
     * @return a new instance of the default adapter implementation
     */
    static <K, V> FluentMap<K, V> adapt(Map<K, V> map, Supplier<? extends V> defaults) {
        return new FluentMapAdapter<>(map, defaults);
    }

    /**
     * Returns the pure {@link Map} interface for this instance.
     *
     * <p>
     * Because this interface is supposed to acts as a base for various map
     * adapters, it may be useful to have more efficient access to the actual
     * content-backing instance. Moreover, this method provides the means for
     * default implementations.
     *
     * <p>
     * This method may return the actual underlying storage, or it may return
     * this instance if all default implementations are overridden in order not
     * to use this method anymore (which might result in an endless recursion);
     * returning this instance always might be necessary when the implementation
     * does not adapt any other instance actually.
     *
     * @return the pure {@link Map} interface for this instance
     */
    Map<K, V> map();

    /**
     * Provides the function for making new values.
     *
     * <p>
     * The {@link #let(Object)} implementation should use this method for making
     * new values when needed. Note that this method may return {@code null} and
     * the clients are supposed to check that.
     *
     * @return the function for making new values, or {@code null} if no
     *         strategy is defined
     */
    Function<? super K, ? extends V> defaults();

    /**
     * Sets the function for making new values.
     *
     * @param factory
     *            the function to use. It may be {@code null} if no definition
     *            is requested and the feature of making new values should not
     *            be available
     *
     * @return an instance with the desired setting; the result may be the same
     *         instance, but implementations are free to return a new instance,
     *         e.g., when the implementation wraps another instance and uses
     *         {@code final} fields, so that it may be even immutable
     */
    FluentMap<K, V> defaults(Function<? super K, ? extends V> factory);

    /**
     * Sets the supplier for making new values.
     *
     * @param factory
     *            the supplier to use. It may be {@code null} if no definition
     *            is requested and the feature of making new values should not
     *            be available
     *
     * @return an instance with the desired setting; the result may be the same
     *         instance, but implementations are free to return a new instance,
     *         e.g., when the implementation wraps another instance and uses
     *         {@code final} fields, so that it may be even immutable
     */
    default FluentMap<K, V> defaults(Supplier<? extends V> factory) {
        return defaults((factory != null) ? o -> factory.get() : null);
    }

    /**
     * @see net.yetamine.lang.collections.FluentContainer#accept(java.util.function.Consumer)
     */
    default FluentMap<K, V> accept(Consumer<? super FluentMap<K, V>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentContainer#map(java.util.function.Function)
     */
    default <U> U map(Function<? super FluentMap<K, V>, ? extends U> mapping) {
        return mapping.apply(this);
    }

    /**
     * Applies the given function to {@link #map()}.
     *
     * <p>
     * This method is convenient shortcut for {@link #map(Function)} which would
     * prefer to use the {@link #map()} anyway, e.g., when this instance acts as
     * a {@link Map} builder and the result shall avoid any adaptation overhead,
     * especially when possibly wrapped as an unmodifiable map which shows the
     * motivation example.
     *
     * @param <U>
     *            the type of the result
     * @param mapping
     *            the function which is supposed to remap {@link #map()} to the
     *            result to return. It must not be {@code null}.
     *
     * @return the result of the mapping function
     */
    default <U> U remap(Function<? super Map<K, V>, ? extends U> mapping) {
        return mapping.apply(map());
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#find(java.lang.Object)
     */
    default Optional<V> find(Object key) {
        return Optional.ofNullable(get(key));
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#let(java.lang.Object)
     */
    default V let(K key) {
        final Function<? super K, ? extends V> factory = defaults();
        if (factory == null) { // No factory available!
            throw new UnsupportedOperationException();
        }

        return computeIfAbsent(key, factory);
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#let(java.lang.Object,
     *      java.util.function.Function)
     */
    default FluentMap<K, V> let(K key, Function<? super V, ? extends V> function) {
        final Function<? super K, ? extends V> factory = defaults();
        if (factory == null) { // No factory available!
            throw new UnsupportedOperationException();
        }

        compute(key, (k, v) -> (v != null) ? v : function.apply(factory.apply(k)));
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#let(java.lang.Object,
     *      java.util.function.BiConsumer)
     */
    default FluentMap<K, V> let(K key, BiConsumer<? super K, ? super V> mutator) {
        final Function<? super K, ? extends V> factory = defaults();
        if (factory == null) { // No factory available!
            throw new UnsupportedOperationException();
        }

        compute(key, (k, v) -> {
            if (v != null) {
                return v;
            }

            final V result = factory.apply(k);
            mutator.accept(k, result);
            return result;
        });

        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#patch(java.lang.Object,
     *      java.lang.Object, java.util.function.BiFunction)
     */
    default FluentMap<K, V> patch(K key, V value, BiFunction<? super V, ? super V, ? extends V> function) {
        merge(key, value, function);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#set(java.lang.Object,
     *      java.lang.Object)
     */
    default FluentMap<K, V> set(K key, V value) {
        put(key, value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#add(java.lang.Object,
     *      java.lang.Object)
     */
    default FluentMap<K, V> add(K key, V value) {
        putIfAbsent(key, value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#have(java.lang.Object)
     */
    default Optional<V> have(K key) {
        final Function<? super K, ? extends V> factory = defaults();
        if (factory == null) { // No factory available!
            return Optional.empty();
        }

        return Optional.ofNullable(computeIfAbsent(key, factory));
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#discard(java.lang.Object)
     */
    default FluentMap<K, V> discard(Object key) {
        remove(key);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentContainer#discard()
     */
    default FluentMap<K, V> discard() {
        clear();
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#setAll(java.util.Map)
     */
    default FluentMap<K, V> setAll(Map<? extends K, ? extends V> source) {
        source.forEach(this::put);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#addAll(java.util.Map)
     */
    default FluentMap<K, V> addAll(Map<? extends K, ? extends V> source) {
        source.forEach(this::putIfAbsent);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#patchAll(java.util.function.BiFunction)
     */
    default FluentMap<K, V> patchAll(BiFunction<? super K, ? super V, ? extends V> function) {
        replaceAll(function);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.FluentMapExtensions#forAll(java.util.function.BiConsumer)
     */
    default FluentMap<K, V> forAll(BiConsumer<? super K, ? super V> consumer) {
        forEach(consumer);
        return this;
    }
}
