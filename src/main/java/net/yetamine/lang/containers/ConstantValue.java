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

package net.yetamine.lang.containers;

/**
 * A value supplier that defers the computation of the value and uses a custom
 * supplier for holding it, so that it can adapt a custom strategy for caching
 * or invalidating a value.
 *
 * <p>
 * When {@link #get()} is called, this class uses the given supplier to compute
 * the value and lets the given factory make the custom supplier for the value.
 * The implementation considers {@code null} results of the custom supplier as
 * the indication that the value has been lost and must be recomputed. Although
 * {@code null} values are tolerated, handling them is very inefficient, and
 * therefore {@code null} values should not be used.
 *
 * @param <T>
 *            the type of the element
 */
public final class ConstantValue<T> implements InvalidableValue<T> {

    /** Singleton representing {@code null}. */
    private static final InvalidableValue<Object> EMPTY = new ConstantValue<>(null);

    /** Stored value. */
    private final T value;

    /**
     * Creates a new instance.
     *
     * @param constant
     *            the value to store
     */
    private ConstantValue(T constant) {
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
    public static <T> InvalidableValue<T> of(T constant) {
        return (constant != null) ? new ConstantValue<>(constant) : (ConstantValue<T>) EMPTY;
    }

    /**
     * @see java.util.function.Supplier#get()
     */
    public T get() {
        return value;
    }

    /**
     * @see net.yetamine.lang.concurrent.Invalidable#invalidate()
     */
    public void invalidate() {
        // Do nothing
    }
}
