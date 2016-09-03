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
import java.util.function.Function;
import java.util.function.Supplier;

import net.yetamine.lang.functional.Producer;

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
public final class IndirectValue<T> implements Producer<T> {

    /** Supplier to compute the result on demand. */
    private final Supplier<? extends T> supplier;
    /** Factory for making the custom supplier. */
    private final Function<? super T, ? extends Supplier<? extends T>> factory;
    /** Custom supplier for the value to return with {@link #get()}. */
    private volatile Supplier<? extends T> reference;

    /**
     * Creates a new instance.
     *
     * <p>
     * The customization may use, e.g., a {@link java.lang.ref.WeakReference}
     * for caching the value in a smart way:
     *
     * <pre>
     * new IndirectValue&lt;T&gt;(compute, t -&gt; new WeakReference&lt;&gt;(t)::get)
     * </pre>
     *
     * @param compute
     *            the supplier that shall compute the value. It must not be
     *            {@code null}.
     * @param customize
     *            the factory for making the custom supplier. It must not be
     *            {@code null}.
     */
    public IndirectValue(Supplier<? extends T> compute, Function<? super T, ? extends Supplier<? extends T>> customize) {
        factory = Objects.requireNonNull(customize);
        supplier = Objects.requireNonNull(compute);
        invalidate(); // Set an invalid reference
    }

    /**
     * Returns the value supplied by the given supplier.
     *
     * <p>
     * The supplier is invoked only once until {@link #invalidate()} explicitly
     * request to invalidate the result or until the custom supplier returns
     * {@code null}. The update of the value is guarded by this instance, so
     * computing the value or constructing the custom supplier may be unsafe.
     * Exceptions are relayed to the caller.
     *
     * @see java.util.function.Supplier#get()
     */
    public T get() {
        final T current = reference.get();
        if (current != null) {
            return current;
        }

        synchronized (this) { // Well, no valid result retrieved, try to compute it
            final T retry = reference.get();
            if (retry != null) {
                return retry;
            }

            // No success, so recompute it
            final T result = supplier.get();
            reference = factory.apply(result);
            return result;
        }
    }

    /**
     * Invalidates the value held by this instance, hence subsequent
     * {@link #get()} invocation triggers the computation again.
     */
    public void invalidate() {
        reference = () -> null;
    }
}
