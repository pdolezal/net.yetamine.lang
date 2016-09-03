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

package net.yetamine.lang.closeables;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Represents a strategy that opens a resource.
 *
 * @param <R>
 *            the type of the resource
 * @param <X>
 *            the type of the exception that the attempt to open the resource
 *            may throw
 */
@FunctionalInterface
public interface ResourceOpening<R, X extends Exception> {

    /**
     * Opens a resource and returns its instance.
     *
     * <p>
     * Implementations must either succeed and return a valid result, or fail
     * with an exception. Implementations may limit the method to a single
     * invocation or allow calling the method only for not-opened resources.
     *
     * <p>
     * Theoretically, an implementation may explicitly assert that returning
     * {@code null} is possible and legal outcome of the operation, e.g., an
     * encapsulation of {@link Class#getResourceAsStream(String)} might do that,
     * but the result should not be {@code null} in general.
     *
     * @return the resource instance
     *
     * @throws X
     *             if opening the resource fails
     */
    R open() throws X;

    /**
     * Returns an instance that creates a resource with the given instance once
     * only.
     *
     * @param <R>
     *            the type of the resource
     * @param <X>
     *            the type of the exception that the attempt to open the
     *            resource may throw
     * @param opening
     *            the actual resource constructor. It must not be {@code null}.
     * @param exception
     *            the supplier of the exception to throw if the resource has
     *            been required once than more. It must not be {@code null}.
     *
     * @return an instance that creates a resource with the given instance once
     *         only
     */
    static <R, X extends Exception> ResourceOpening<R, X> unique(ResourceOpening<? extends R, ? extends X> opening, Supplier<? extends X> exception) {
        Objects.requireNonNull(exception);
        Objects.requireNonNull(opening);

        // Make a flag to ensure single use only
        final AtomicBoolean used = new AtomicBoolean();

        return () -> {
            if (used.compareAndSet(false, true)) {
                return opening.open();
            }

            throw exception.get();
        };
    }
}
