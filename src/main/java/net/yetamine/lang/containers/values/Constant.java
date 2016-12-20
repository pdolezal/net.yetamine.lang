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

import java.io.Serializable;
import java.util.Objects;

/**
 * A supplier of a constant value.
 *
 * @param <T>
 *            the type of the element
 */
public final class Constant<T> implements Serializable, Value<T> {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Singleton representing {@code null}. */
    private static final Value<Object> EMPTY = new Constant<>(null);

    /** Stored value. */
    private final T value;

    /**
     * Creates a new instance.
     *
     * @param constant
     *            the value to store
     */
    private Constant(T constant) {
        value = constant;
    }

    /**
     * Returns an instance for the given value.
     *
     * @param <T>
     *            the type of the element
     * @param constant
     *            the value to store
     *
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Value<T> of(T constant) {
        return (constant != null) ? new Constant<>(constant) : (Constant<T>) EMPTY;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return (obj instanceof Constant) && Objects.equals(value, ((Constant<?>) obj).value);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("constant[%s]", value);
    }

    /**
     * @see java.util.function.Supplier#get()
     */
    public T get() {
        return value;
    }
}
