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

package net.yetamine.lang.introspective;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.yetamine.lang.collections.Capture;

/**
 * Declares additional abilities and traits extending the basic contract of an
 * interface to help dynamic adaptation in order to achieve better efficiency.
 *
 * <p>
 * An extension is declared by including a descriptor, known to both the client
 * of the interface and the interface provider, in the extension set. A client,
 * which is aware of the extensions, may then run an optimized action easily if
 * the extension is available.
 *
 * <p>
 * The fluent interface allows employing several patterns. The most usual is
 * just running a specific action that may exploit a particular extension:
 *
 * <pre>
 * extensions.ifPresent(extension1, action1).ifPresent(extension2, action2);
 * </pre>
 *
 * When an alternative action needs executing, following code snippet is often a
 * good and fluent way to express such a need:
 *
 * <pre>
 * if (extensions.notMissing(extension, fallback)) {
 *     // Executes if the extension is not missing
 * }
 * </pre>
 *
 * The snippet above is basically equivalent to:
 *
 * <pre>
 * if (extensions.known().contains(extension)) {
 *     // Executes if the extension is not missing
 * } else {
 *     // Executes if the extension is missing
 * }
 * </pre>
 *
 * The direct test with {@link #known()} is rarely needed, perhaps when the set
 * of required extensions is ready, so that {@link Set#containsAll(Collection)}
 * could be used immediately. Otherwise following pattern might be used:
 *
 * <pre>
 * // The result is an Optional with the extensions, to that the action may need
 * // a conversion to match Optional::ifPresent signature:
 * extensions.allPresent(action, extension1, extension2);
 * </pre>
 *
 * <p>
 * The implementation is immutable with one outstanding exception: when the
 * instance is created with {@link #using(Set)}, the result just wraps the given
 * set as an unmodifiable instance and provides it as {@link #known()}. This may
 * be useful for more efficient constructions and for the cases when the set may
 * change over time, which is generally not recommended though.
 */
public final class Extensions implements Serializable {

    /** Shared instance for an immutable empty extension set. */
    private static final Extensions EMPTY = new Extensions(Collections.emptySet());

    /** Known extensions. */
    private final Set<?> known;

    /**
     * Creates a new instance.
     *
     * @param extensions
     *            the extensions set. It must not be {@code null}.
     */
    Extensions(Set<?> extensions) {
        known = extensions;
    }

    /**
     * Returns an empty instance.
     *
     * @return an empty instance
     */
    public static Extensions empty() {
        return EMPTY;
    }

    /**
     * Creates a new instance.
     *
     * @param extension
     *            the extension to provide
     *
     * @return the new instance
     */
    public static Extensions list(Object extension) {
        return new Extensions(Collections.singleton(extension));
    }

    /**
     * Creates a new instance.
     *
     * @param extensions
     *            the extensions to provide. It must not be {@code null}.
     *
     * @return the new instance
     */
    public static Extensions list(Object... extensions) {
        return from(Arrays.asList(extensions));
    }

    /**
     * Creates a new instance.
     *
     * @param extensions
     *            the extensions to provide. It must not be {@code null}.
     *
     * @return the new instance
     */
    public static Extensions from(Collection<?> extensions) {
        final Set<?> capture = Capture.set(extensions);
        return capture.isEmpty() ? empty() : new Extensions(capture);
    }

    /**
     * Creates a new instance.
     *
     * @param extensions
     *            the extensions to provide. It must not be {@code null}.
     *
     * @return the new instance
     */
    public static Extensions from(Stream<?> extensions) {
        final Set<?> values = extensions.collect(Collectors.toSet());
        return values.isEmpty() ? empty() : new Extensions(Capture.frozen(values));
    }

    /**
     * Creates a new instance.
     *
     * @param extensions
     *            the extensions to combine. It must not be {@code null}.
     *
     * @return the new instance
     */
    public static Extensions combined(Extensions... extensions) {
        return combined(Stream.of(extensions));
    }

    /**
     * Creates a new instance.
     *
     * @param extensions
     *            the stream of extensions to combine. It must not be
     *            {@code null}.
     *
     * @return the new instance
     */
    public static Extensions combined(Stream<Extensions> extensions) {
        final Set<?> values = extensions.flatMap(extension -> extension.known().stream()).collect(Collectors.toSet());
        return values.isEmpty() ? empty() : new Extensions(Capture.frozen(values));
    }

    /**
     * Creates a new instance.
     *
     * <p>
     * Unlike {@link #from(Collection)}, this method does not make an immutable
     * copy of the collection, it rather uses it as it is, therefore the caller
     * may influence the content of the instance through the original reference.
     * It is strongly recommended to use a concurrent set then.
     *
     * @param extensions
     *            the extensions to provide. It must not be {@code null}.
     *
     * @return the new instance
     */
    public static Extensions using(Set<?> extensions) {
        final Set<?> set = Collections.unmodifiableSet(extensions);
        // Not using Capture intentionally, keeping the possibility to modify the original
        return new Extensions((set.getClass() == extensions.getClass()) ? extensions : set);
    }

    /**
     * Gets the extensions of the given object.
     *
     * @param o
     *            the object to query
     *
     * @return the extensions of the object, or empty extensions if the object
     *         is not an instance of {@link Extensible} (including the case of
     *         {@code null} argument)
     */
    public static Extensions of(Object o) {
        return Extensible.query(o).extensions();
    }

    /**
     * Gets the extensions of the given object.
     *
     * @param o
     *            the object to query
     *
     * @return the extensions of the object, or empty extensions if the argument
     *         is {@code null}
     */
    public static Extensions of(Extensible o) {
        return (o != null) ? o.extensions() : empty();
    }

    /**
     * Returns an instance with an additional extension.
     *
     * @param more
     *            the extension to add. It must not be {@code null}.
     *
     * @return an instance combining {@link #known()} and the given extension
     */
    public Extensions with(Object more) {
        return from(Stream.concat(known().stream(), Stream.of(more)));
    }

    /**
     * Returns an instance with additional extensions.
     *
     * @param more
     *            the extensions to add. It must not be {@code null}.
     *
     * @return an instance combining {@link #known()} and the given extensions
     */
    public Extensions with(Object... more) {
        return from(Stream.concat(known().stream(), Stream.of(more)));
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return known.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return known.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Extensions) && known.equals(((Extensions) obj).known);
    }

    /**
     * Returns the set of provided extension guarantees.
     *
     * @return the set of provided extension guarantees
     */
    public Set<?> known() {
        return known;
    }

    /**
     * Tests if the extension is known.
     *
     * <p>
     * This method is a shortcut for {@code known().contains(extension)}.
     *
     * @param extension
     *            the extension to check
     *
     * @return {@code true} if the extension is known
     */
    public boolean contains(Object extension) {
        return known.contains(extension);
    }

    /**
     * Tests if the given extension is not present and runs the given action if
     * the extension is present.
     *
     * @param extension
     *            the extension to test
     * @param fallback
     *            the action to run. It must not be {@code null}.
     *
     * @return {@code true} if the extension is not present (i.e., is missing)
     */
    public boolean notPresent(Object extension, Runnable fallback) {
        if (known.contains(extension)) {
            fallback.run();
            return false;
        }

        return true;
    }

    /**
     * Tests if the given extension is not missing and runs the given action if
     * the extension is missing.
     *
     * @param extension
     *            the extension to test
     * @param fallback
     *            the action to run. It must not be {@code null}.
     *
     * @return {@code true} if the extension is not missing (i.e., is present)
     */
    public boolean notMissing(Object extension, Runnable fallback) {
        if (known.contains(extension)) {
            return true;
        }

        fallback.run();
        return false;
    }

    /**
     * Runs an action if the given extension is present.
     *
     * @param extension
     *            the extension to check
     * @param action
     *            the action to run. It must not be {@code null}.
     *
     * @return this instance
     */
    public Extensions ifPresent(Object extension, Runnable action) {
        notPresent(extension, action);
        return this;
    }

    /**
     * Runs an action if the given extension is missing.
     *
     * @param extension
     *            the extension to check
     * @param action
     *            the action to run. It must not be {@code null}.
     *
     * @return this instance
     */
    public Extensions ifMissing(Object extension, Runnable action) {
        notMissing(extension, action);
        return this;
    }

    /**
     * Runs an action if all of the given extension are present.
     *
     * @param action
     *            the action to run. It must not be {@code null}.
     * @param extensions
     *            the extensions to check. It must not be {@code null}.
     *
     * @return this instance
     */
    public Extensions allPresent(Runnable action, Object... extensions) {
        if (Stream.of(extensions).allMatch(known::contains)) {
            action.run();
        }

        return this;
    }

    /**
     * Runs an action if any of the given extension is present.
     *
     * @param action
     *            the action to run. It must not be {@code null}.
     * @param extensions
     *            the extensions to check. It must not be {@code null}.
     *
     * @return this instance
     */
    public Extensions anyPresent(Runnable action, Object... extensions) {
        if (Stream.of(extensions).anyMatch(known::contains)) {
            action.run();
        }

        return this;
    }

    /**
     * Runs an action if all of the given extension are missing.
     *
     * @param action
     *            the action to run. It must not be {@code null}.
     * @param extensions
     *            the extensions to check. It must not be {@code null}.
     *
     * @return this instance
     */
    public Extensions allMissing(Runnable action, Object... extensions) {
        if (Stream.of(extensions).noneMatch(known::contains)) {
            action.run();
        }

        return this;
    }

    /**
     * Runs an action if any of the given extension is missing.
     *
     * @param action
     *            the action to run. It must not be {@code null}.
     * @param extensions
     *            the extensions to check. It must not be {@code null}.
     *
     * @return this instance
     */
    public Extensions anyMissing(Runnable action, Object... extensions) {
        Stream.of(extensions).filter(e -> !known.contains(e)).findAny().ifPresent(e -> action.run());
        return this;
    }

    /**
     * Returns an {@link Optional} containing the given extension if the
     * extension is present.
     *
     * @param <T>
     *            the type of the extension
     * @param extension
     *            the extension to check. It must not be {@code null}.
     *
     * @return this instance
     */
    public <T> Optional<T> optional(T extension) {
        return known.contains(extension) ? Optional.of(extension) : Optional.empty();
    }

    // Serialization support

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /**
     * Makes the serialization proxy to resolve the empty singleton more
     * efficiently.
     *
     * @return the serialization proxy
     *
     * @see Serializable
     */
    private Object writeReplace() {
        return (this == EMPTY) ? new SerializationProxy() : new SerializationProxy(known);
    }

    /**
     * Serialization proxy.
     *
     * <p>
     * The proxy stores the set of known extensions, or {@code null} for the
     * empty singleton.
     */
    private static final class SerializationProxy implements Serializable {

        /** Class providing the singleton via an access point. */
        private final Set<?> known;

        /**
         * Creates a new instance.
         *
         * @param set
         *            the set to store. It must not be {@code null}.
         */
        public SerializationProxy(Set<?> set) {
            assert (set != null);
            known = set;
        }

        /**
         * Creates a new instance for the empty singleton.
         */
        public SerializationProxy() {
            known = null;
        }

        // Serialization support

        /** Serialization version: 1 */
        private static final long serialVersionUID = 1L;

        /**
         * Returns the resolved instance.
         *
         * @return the resolved instance
         *
         * @see Serializable
         */
        private Object readResolve() {
            return (known != null) ? new Extensions(known) : Extensions.empty();
        }
    }
}
