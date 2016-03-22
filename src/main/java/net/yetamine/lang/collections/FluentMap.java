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
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.yetamine.lang.functional.Source;

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
public interface FluentMap<K, V> extends Map<K, V>, MapFluency<K, V, FluentMap<K, V>>, Source<FluentMap<K, V>>  {

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

    // Core fluent extensions support

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
    Map<K, V> container();

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

    // Common fluent extensions

    /**
     * @see net.yetamine.lang.functional.Source#filter(java.util.function.Predicate)
     */
    default Optional<FluentMap<K, V>> filter(Predicate<? super FluentMap<K, V>> predicate) {
        return predicate.test(this) ? Optional.of(this) : Optional.empty();
    }

    /**
     * @see net.yetamine.lang.functional.Source#accept(java.util.function.Consumer)
     */
    default FluentMap<K, V> accept(Consumer<? super FluentMap<K, V>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * @see net.yetamine.lang.functional.Source#map(java.util.function.Function)
     */
    default <U> U map(Function<? super FluentMap<K, V>, ? extends U> mapping) {
        return mapping.apply(this);
    }

    /**
     * Applies the given function to {@link #container()}.
     *
     * <p>
     * This method is convenient shortcut for {@link #map(Function)} which would
     * prefer to use the {@link #container()} anyway, e.g., when this instance
     * acts as a {@link Map} builder and the result shall avoid any adaptation
     * overhead, especially when possibly wrapped as an unmodifiable map which
     * shows the motivation example.
     *
     * @param <U>
     *            the type of the result
     * @param mapping
     *            the function which is supposed to remap {@link #container()}
     *            to the result to return. It must not be {@code null}.
     *
     * @return the result of the mapping function
     */
    default <U> U remap(Function<? super Map<K, V>, ? extends U> mapping) {
        return mapping.apply(container());
    }

    // Fluent extensions for Map

    /**
     * @see net.yetamine.lang.collections.MapFluency#find(java.lang.Object)
     */
    default Optional<V> find(Object key) {
        return Optional.ofNullable(get(key));
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#let(java.lang.Object)
     */
    default V let(K key) {
        final Function<? super K, ? extends V> factory = defaults();
        if (factory == null) { // No factory available!
            throw new UnsupportedOperationException();
        }

        return computeIfAbsent(key, factory);
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#let(java.lang.Object,
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
     * @see net.yetamine.lang.collections.MapFluency#let(java.lang.Object,
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
     * @see net.yetamine.lang.collections.MapFluency#patch(java.lang.Object,
     *      java.lang.Object, java.util.function.BiFunction)
     */
    default FluentMap<K, V> patch(K key, V value, BiFunction<? super V, ? super V, ? extends V> function) {
        merge(key, value, function);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#set(java.lang.Object,
     *      java.lang.Object)
     */
    default FluentMap<K, V> set(K key, V value) {
        put(key, value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#add(java.lang.Object,
     *      java.lang.Object)
     */
    default FluentMap<K, V> add(K key, V value) {
        putIfAbsent(key, value);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#have(java.lang.Object)
     */
    default Optional<V> have(K key) {
        final Function<? super K, ? extends V> factory = defaults();
        if (factory == null) { // No factory available!
            return Optional.empty();
        }

        return Optional.ofNullable(computeIfAbsent(key, factory));
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#discard(java.lang.Object)
     */
    default FluentMap<K, V> discard(Object key) {
        remove(key);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#discardAll()
     */
    default FluentMap<K, V> discardAll() {
        clear();
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#setAll(java.util.Map)
     */
    default FluentMap<K, V> setAll(Map<? extends K, ? extends V> source) {
        source.forEach(this::put);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#addAll(java.util.Map)
     */
    default FluentMap<K, V> addAll(Map<? extends K, ? extends V> source) {
        source.forEach(this::putIfAbsent);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#patchAll(java.util.function.BiFunction)
     */
    default FluentMap<K, V> patchAll(BiFunction<? super K, ? super V, ? extends V> function) {
        replaceAll(function);
        return this;
    }

    /**
     * @see net.yetamine.lang.collections.MapFluency#forAll(java.util.function.BiConsumer)
     */
    default FluentMap<K, V> forAll(BiConsumer<? super K, ? super V> consumer) {
        forEach(consumer);
        return this;
    }

    // Map interface default implementation

    /**
     * @see java.util.Map#size()
     */
    default int size() {
        return container().size();
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    default boolean isEmpty() {
        return container().isEmpty();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    default boolean containsKey(Object key) {
        return container().containsKey(key);
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    default boolean containsValue(Object value) {
        return container().containsValue(value);
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    default V get(Object key) {
        return container().get(key);
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    default V put(K key, V value) {
        return container().put(key, value);
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    default V remove(Object key) {
        return container().remove(key);
    }

    /**
     * @see java.util.Map#remove(java.lang.Object, java.lang.Object)
     */
    default boolean remove(Object key, Object value) {
        return container().remove(key, value);
    }

    /**
     * @see java.util.Map#replace(java.lang.Object, java.lang.Object)
     */
    default V replace(K key, V value) {
        return container().replace(key, value);
    }

    /**
     * @see java.util.Map#replace(java.lang.Object, java.lang.Object,
     *      java.lang.Object)
     */
    default boolean replace(K key, V oldValue, V newValue) {
        return container().replace(key, oldValue, newValue);
    }

    /**
     * @see java.util.Map#replaceAll(java.util.function.BiFunction)
     */
    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        container().replaceAll(function);
    }

    /**
     * @see java.util.Map#getOrDefault(java.lang.Object, java.lang.Object)
     */
    default V getOrDefault(Object key, V defaultValue) {
        return container().getOrDefault(key, defaultValue);
    }

    /**
     * @see java.util.Map#putIfAbsent(java.lang.Object, java.lang.Object)
     */
    default V putIfAbsent(K key, V value) {
        return container().putIfAbsent(key, value);
    }

    /**
     * @see java.util.Map#compute(java.lang.Object,
     *      java.util.function.BiFunction)
     */
    default V compute(K key, java.util.function.BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return container().compute(key, remappingFunction);
    }

    /**
     * @see java.util.Map#computeIfAbsent(java.lang.Object,
     *      java.util.function.Function)
     */
    default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return container().computeIfAbsent(key, mappingFunction);
    }

    /**
     * @see java.util.Map#computeIfPresent(java.lang.Object,
     *      java.util.function.BiFunction)
     */
    default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return container().computeIfPresent(key, remappingFunction);
    }

    /**
     * @see java.util.Map#merge(java.lang.Object, java.lang.Object,
     *      java.util.function.BiFunction)
     */
    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return container().merge(key, value, remappingFunction);
    }

    /**
     * @see java.util.Map#forEach(java.util.function.BiConsumer)
     */
    default void forEach(BiConsumer<? super K, ? super V> action) {
        container().forEach(action);
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    default void putAll(Map<? extends K, ? extends V> m) {
        container().putAll(m);
    }

    /**
     * @see java.util.Map#clear()
     */
    default void clear() {
        container().clear();
    }

    /**
     * @see java.util.Map#keySet()
     */
    default Set<K> keySet() {
        return container().keySet();
    }

    /**
     * @see java.util.Map#values()
     */
    default Collection<V> values() {
        return container().values();
    }

    /**
     * @see java.util.Map#entrySet()
     */
    default Set<Map.Entry<K, V>> entrySet() {
        return container().entrySet();
    }
}

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
final class FluentMapAdapter<K, V> implements Serializable, FluentMap<K, V> {

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

    /**
     * @see net.yetamine.lang.collections.FluentMap#container()
     */
    public Map<K, V> container() {
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
