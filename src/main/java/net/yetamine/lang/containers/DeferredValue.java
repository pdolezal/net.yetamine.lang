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

import java.util.Objects;
import java.util.function.Supplier;

import net.yetamine.lang.functional.Producer;

/**
 * A value supplier that defers the computation of the value.
 *
 * <p>
 * When {@link #get()} is called, this class uses the given supplier to compute
 * the value and stores the result; the result is returned always then, without
 * recomputing the value, until {@link #invalidate()} is invoked.
 *
 * @param <T>
 *            the type of the element
 */
public final class DeferredValue<T> implements Producer<T> {

    /** Supplier to compute the result on demand. */
    private final Supplier<? extends T> supplier;
    /** Result to return with {@link #get()}. */
    private T value;
    /** Mark for valid {@link #value}. */
    private volatile boolean valid;

    /**
     * Creates a new instance.
     *
     * @param compute
     *            the supplier that shall compute the value. It must not be
     *            {@code null}.
     */
    public DeferredValue(Supplier<? extends T> compute) {
        supplier = Objects.requireNonNull(compute);
    }

    /**
     * Returns the value supplied by the given supplier.
     *
     * <p>
     * The supplier is invoked only once until {@link #invalidate()} explicitly
     * request to invalidate the result and the invocation is guarded by this
     * instance, so that the invocation may be unsafe or have side effects. A
     * {@code null} result is accepted as well, exceptions are relayed to the
     * caller.
     *
     * @see java.util.function.Supplier#get()
     */
    public T get() {
        if (valid) { // Already computed, this access ensures correct memory visibility
            return value;
        }

        final T result;
        synchronized (this) {
            if (valid) { // Double-checked locking applied here
                return value;
            }

            result = supplier.get();
            value = result;
            valid = true; // Write the volatile as last for the memory fence to apply
        }

        return result;
    }

    /**
     * Invalidates the value held by this instance, hence subsequent
     * {@link #get()} invocation triggers the computation again.
     */
    public void invalidate() {
        valid = false;
    }
}
