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

package net.yetamine.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Function;

/**
 * A utility for making unmodifiable transformations of collections.
 *
 * <p>
 * The main use case of this class is to make unmodifiable snapshots of existing
 * collections, taking advantage of singletons for empty collections. This class
 * provides a common strategy for making a snapshot while avoiding making empty
 * copies or layering multiple unmodifiable wrappers.
 */
public final class Capture {

    /**
     * Returns the most convenient representation of a frozen collection, which
     * may be an empty instance, an optimized single-element implementation, or
     * an unmodifiable view holding the original collection.
     *
     * @param <E>
     *            the type of the elements
     * @param source
     *            the source collection to be taken as frozen. It must not be
     *            {@code null}.
     *
     * @return the best representation for the given elements
     */
    public static <E> Set<E> frozen(Set<? extends E> source) {
        if (source.isEmpty()) { // Return the usual empty collection
            return Collections.emptySet();
        }

        final Iterator<? extends E> it = source.iterator();
        final E head = it.next();
        // If there are more, just return an unmodifiable wrapper
        return it.hasNext() ? Collections.unmodifiableSet(source) : Collections.singleton(head);
    }

    /**
     * Returns the most convenient representation of a frozen collection, which
     * may be an empty instance, an optimized single-element implementation, or
     * an unmodifiable view holding the original collection.
     *
     * @param <E>
     *            the type of the elements
     * @param source
     *            the source collection to be taken as frozen. It must not be
     *            {@code null}.
     *
     * @return the best representation for the given elements
     */
    public static <E> List<E> frozen(List<? extends E> source) {
        if (source.isEmpty()) { // Return the usual empty collection
            return Collections.emptyList();
        }

        return (source.size() == 1) ? Collections.singletonList(source.get(0)) : Collections.unmodifiableList(source);
    }

    /**
     * Returns the most convenient representation of a frozen collection, which
     * may be an empty instance, an optimized single-element implementation, or
     * an unmodifiable view holding the original collection.
     *
     * @param <K>
     *            the type of the map keys
     * @param <V>
     *            the type of the map values
     * @param source
     *            the source collection to be taken as frozen. It must not be
     *            {@code null}.
     *
     * @return the best representation for the given elements
     */
    public static <K, V> Map<K, V> frozen(Map<? extends K, ? extends V> source) {
        if (source.isEmpty()) { // Return the usual empty collection
            return Collections.emptyMap();
        }

        final Iterator<? extends Map.Entry<? extends K, ? extends V>> it = source.entrySet().iterator();
        final Map.Entry<? extends K, ? extends V> head = it.next();
        if (it.hasNext()) { // If there are more, just return an unmodifiable wrapper
            return Collections.unmodifiableMap(source);
        }

        return Collections.singletonMap(head.getKey(), head.getValue());
    }

    /**
     * Returns an unmodifiable instance that wraps the result returned by the
     * transformation of the provided source collection, or an (unmodifiable)
     * empty instance if the source is empty anyway.
     *
     * @param <E>
     *            the type of the elements
     * @param <C>
     *            the type of the source collection
     * @param source
     *            the source collection. It must not be {@code null}.
     * @param transformation
     *            the transformation on the (non-empty) source. It must not be
     *            {@code null}.
     *
     * @return an unmodifiable instance with result of the transformation of the
     *         source collection
     */
    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<? extends E>> Set<E> set(C source, Function<? super C, ? extends Set<? extends E>> transformation) {
        if (source.isEmpty()) { // Return the usual empty collection
            return Collections.emptySet();
        }

        // It is a good chance that the unmodifiable wrapper would be needed
        // anyway. Moreover the type of the wrapper may differ for various
        // inputs, hence it is better to make it anyway. However, when the
        // result has exactly the same class, wrapping a wrapper can be avoided.
        final Set<? extends E> result = transformation.apply(source);
        final Set<E> unmodifiable = Collections.unmodifiableSet(result);
        return (result.getClass() == unmodifiable.getClass()) ? (Set<E>) result : unmodifiable;
    }

    /**
     * Returns an unmodifiable copy of the source collection.
     *
     * <p>
     * The underlying implementation uses {@link HashSet}. If an alternative
     * {@link Set} implementation provides a better performance (e.g., using
     * {@link java.util.EnumSet} may be better for enums), use the overload
     * which accepts the transformation function.
     *
     * @param <E>
     *            the type of the elements
     * @param source
     *            the source collection. It must not be {@code null}.
     *
     * @return an unmodifiable copy of the source collection
     */
    public static <E> Set<E> set(Collection<? extends E> source) {
        return set(source, HashSet<E>::new);
    }

    /**
     * Returns an unmodifiable instance that wraps the result returned by the
     * transformation of the provided source collection, or an (unmodifiable)
     * empty instance if the source is empty anyway.
     *
     * @param <E>
     *            the type of the elements
     * @param <C>
     *            the type of the source collection
     * @param source
     *            the source collection. It must not be {@code null}.
     * @param transformation
     *            the transformation on the (non-empty) source. It must not be
     *            {@code null}.
     *
     * @return an unmodifiable instance with result of the transformation of the
     *         source collection
     */
    public static <E, C extends Collection<? extends E>> NavigableSet<E> navigableSet(C source, Function<? super C, ? extends NavigableSet<E>> transformation) {
        if (source.isEmpty()) { // Return the usual empty collection
            return Collections.emptyNavigableSet();
        }

        // It is a good chance that the unmodifiable wrapper would be needed
        // anyway. Moreover the type of the wrapper may differ for various
        // inputs, hence it is better to make it anyway. However, when the
        // result has exactly the same class, wrapping a wrapper can be avoided.
        final NavigableSet<E> result = transformation.apply(source);
        final NavigableSet<E> unmodifiable = Collections.unmodifiableNavigableSet(result);
        return (result.getClass() == unmodifiable.getClass()) ? result : unmodifiable;
    }

    /**
     * Returns an unmodifiable instance that wraps the result returned by the
     * transformation of the provided source collection, or an (unmodifiable)
     * empty instance if the source is empty anyway.
     *
     * @param <E>
     *            the type of the elements
     * @param <C>
     *            the type of the source collection
     * @param source
     *            the source collection. It must not be {@code null}.
     * @param transformation
     *            the transformation on the (non-empty) source. It must not be
     *            {@code null}.
     *
     * @return an unmodifiable instance with result of the transformation of the
     *         source collection
     */
    public static <E, C extends Collection<? extends E>> SortedSet<E> sortedSet(C source, Function<? super C, ? extends SortedSet<E>> transformation) {
        if (source.isEmpty()) { // Return the usual empty collection
            return Collections.emptySortedSet();
        }

        // It is a good chance that the unmodifiable wrapper would be needed
        // anyway. Moreover the type of the wrapper may differ for various
        // inputs, hence it is better to make it anyway. However, when the
        // result has exactly the same class, wrapping a wrapper can be avoided.
        final SortedSet<E> result = transformation.apply(source);
        final SortedSet<E> unmodifiable = Collections.unmodifiableSortedSet(result);
        return (result.getClass() == unmodifiable.getClass()) ? result : unmodifiable;
    }

    /**
     * Returns an unmodifiable instance that wraps the result returned by the
     * transformation of the provided source collection, or an (unmodifiable)
     * empty instance if the source is empty anyway.
     *
     * @param <E>
     *            the type of the elements
     * @param <C>
     *            the type of the source collection
     * @param source
     *            the source collection. It must not be {@code null}.
     * @param transformation
     *            the transformation on the (non-empty) source. It must not be
     *            {@code null}.
     *
     * @return an unmodifiable instance with result of the transformation of the
     *         source collection
     */
    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<? extends E>> List<E> list(C source, Function<? super C, ? extends List<? extends E>> transformation) {
        if (source.isEmpty()) { // Return the usual empty collection
            return Collections.emptyList();
        }

        // It is a good chance that the unmodifiable wrapper would be needed
        // anyway. Moreover the type of the wrapper may differ for various
        // inputs, hence it is better to make it anyway. However, when the
        // result has exactly the same class, wrapping a wrapper can be avoided.
        final List<? extends E> result = transformation.apply(source);
        final List<E> unmodifiable = Collections.unmodifiableList(result);
        return (result.getClass() == unmodifiable.getClass()) ? (List<E>) result : unmodifiable;
    }

    /**
     * Returns an unmodifiable copy of the source collection.
     *
     * <p>
     * The underlying implementation uses {@link ArrayList}, which is usually
     * the best option (as it provides the best-performing choice with sparse
     * changes, which is also the case of an unmodifiable collection).
     *
     * @param <E>
     *            the type of the elements
     * @param source
     *            the source collection. It must not be {@code null}.
     *
     * @return an unmodifiable copy of the source collection
     */
    public static <E> List<E> list(Collection<? extends E> source) {
        return list(source, ArrayList<E>::new);
    }

    /**
     * Returns an unmodifiable instance that wraps the result returned by the
     * transformation of the provided source map, or an (unmodifiable) empty
     * instance if the source is empty anyway.
     *
     * @param <K>
     *            the type of the map keys
     * @param <V>
     *            the type of the map values
     * @param <M>
     *            the type of the source map
     * @param source
     *            the source map. It must not be {@code null}.
     * @param transformation
     *            the transformation on the (non-empty) source. It must not be
     *            {@code null}.
     *
     * @return an unmodifiable instance with result of the transformation of the
     *         source map
     */
    @SuppressWarnings("unchecked")
    public static <K, V, M extends Map<? extends K, ? extends V>> Map<K, V> map(M source, Function<? super M, ? extends Map<? extends K, ? extends V>> transformation) {
        if (source.isEmpty()) { // Return the usual empty collection
            return Collections.emptyMap();
        }

        // It is a good chance that the unmodifiable wrapper would be needed
        // anyway. Moreover the type of the wrapper may differ for various
        // inputs, hence it is better to make it anyway. However, when the
        // result has exactly the same class, wrapping a wrapper can be avoided.
        final Map<? extends K, ? extends V> result = transformation.apply(source);
        final Map<K, V> unmodifiable = Collections.unmodifiableMap(result);
        return (result.getClass() == unmodifiable.getClass()) ? (Map<K, V>) result : unmodifiable;
    }

    /**
     * Returns an unmodifiable copy of the source map.
     *
     * <p>
     * The underlying implementation uses {@link HashMap}. If an alternative
     * {@link Map} implementation provides a better performance (e.g., using
     * {@link java.util.EnumMap} may be better for enums), use the overload
     * which accepts the transformation function.
     *
     * @param <K>
     *            the type of the map keys
     * @param <V>
     *            the type of the map values
     * @param source
     *            the source map. It must not be {@code null}.
     *
     * @return an unmodifiable instance with result of the transformation of the
     *         source map
     */
    public static <K, V> Map<K, V> map(Map<? extends K, ? extends V> source) {
        return map(source, HashMap<K, V>::new);
    }

    /**
     * Returns an unmodifiable instance that wraps the result returned by the
     * transformation of the provided source map, or an (unmodifiable) empty
     * instance if the source is empty anyway.
     *
     * @param <K>
     *            the type of the map keys
     * @param <V>
     *            the type of the map values
     * @param <M>
     *            the type of the source map
     * @param source
     *            the source map. It must not be {@code null}.
     * @param transformation
     *            the transformation on the (non-empty) source. It must not be
     *            {@code null}.
     *
     * @return an unmodifiable instance with result of the transformation of the
     *         source map
     */
    public static <K, V, M extends NavigableMap<? extends K, ? extends V>> NavigableMap<K, V> navigableMap(M source, Function<? super M, ? extends NavigableMap<K, V>> transformation) {
        if (source.isEmpty()) { // Return the usual empty collection
            return Collections.emptyNavigableMap();
        }

        // It is a good chance that the unmodifiable wrapper would be needed
        // anyway. Moreover the type of the wrapper may differ for various
        // inputs, hence it is better to make it anyway. However, when the
        // result has exactly the same class, wrapping a wrapper can be avoided.
        final NavigableMap<K, V> result = transformation.apply(source);
        final NavigableMap<K, V> unmodifiable = Collections.unmodifiableNavigableMap(result);
        return (result.getClass() == unmodifiable.getClass()) ? result : unmodifiable;
    }

    /**
     * Returns an unmodifiable instance that wraps the result returned by the
     * transformation of the provided source map, or an (unmodifiable) empty
     * instance if the source is empty anyway.
     *
     * @param <K>
     *            the type of the map keys
     * @param <V>
     *            the type of the map values
     * @param <M>
     *            the type of the source map
     * @param source
     *            the source map. It must not be {@code null}.
     * @param transformation
     *            the transformation on the (non-empty) source. It must not be
     *            {@code null}.
     *
     * @return an unmodifiable instance with result of the transformation of the
     *         source map
     */
    public static <K, V, M extends SortedMap<? extends K, ? extends V>> SortedMap<K, V> sortedMap(M source, Function<? super M, ? extends SortedMap<K, V>> transformation) {
        if (source.isEmpty()) { // Return the usual empty collection
            return Collections.emptySortedMap();
        }

        // It is a good chance that the unmodifiable wrapper would be needed
        // anyway. Moreover the type of the wrapper may differ for various
        // inputs, hence it is better to make it anyway. However, when the
        // result has exactly the same class, wrapping a wrapper can be avoided.
        final SortedMap<K, V> result = transformation.apply(source);
        final SortedMap<K, V> unmodifiable = Collections.unmodifiableSortedMap(result);
        return (result.getClass() == unmodifiable.getClass()) ? result : unmodifiable;
    }

    private Capture() {
        throw new AssertionError();
    }
}
