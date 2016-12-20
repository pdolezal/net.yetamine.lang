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

package net.yetamine.lang.containers.values;

import java.util.Objects;

/**
 * A supplier of a constant value that provides identity-based equality.
 *
 * @param <T>
 *            the type of the element
 */
public final class Identical<T> implements Value<T> {

    /** Common shared {@code null} value. */
    private static final Identical<Object> NIL = new Identical<>(null);

    /** Represented value. */
    private final T value;

    /**
     * Creates a new instance.
     *
     * @param object
     *            the object to represent
     */
    private Identical(T object) {
        value = object;
    }

    /**
     * Returns an instance representing the given object.
     *
     * @param <T>
     *            the type of the object
     * @param object
     *            the object to represent
     *
     * @return an instance representing the given object
     */
    public static <T> Identical<T> of(T object) {
        return (object != null) ? new Identical<>(object) : nil();
    }

    /**
     * Returns an instance representing {@code null}.
     *
     * @param <T>
     *            the type of the object
     *
     * @return an instance representing {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> Identical<T> nil() {
        return (Identical<T>) NIL;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("identical[id=%08x, value=%s]", System.identityHashCode(value), value);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return (obj == this) || ((obj instanceof Identical<?>) && (((Identical<?>) obj).value == value));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * @see java.util.function.Supplier#get()
     */
    public T get() {
        return value;
    }
}
