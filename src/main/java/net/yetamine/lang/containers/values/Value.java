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

import net.yetamine.lang.concurrent.Invalidable;
import net.yetamine.lang.functional.Producer;

/**
 * Represents a single value and provides means to retrieve it.
 *
 * <p>
 * The intention to represent a single value distinguishes this interface from
 * {@link Producer}, which may, e.g., represent even an output of a generator,
 * that produces always a different value, or other more volatile value source.
 *
 * <p>
 * Implementations may use various strategies to provide the value like
 * computation on demand, caching the value etc. Implementations may be
 * thread-safe, but are not required to.
 *
 * @param <T>
 *            the type of the element
 */
@FunctionalInterface
public interface Value<T> extends Invalidable, Producer<T> {

    /**
     * This method requests releasing the value if it could be retrieved again,
     * so that cached values can be recomputed when their input might change or
     * some resources can be conserved when the value might not be needed for a
     * longer time period.
     *
     * <p>
     * The default implementation does nothing.
     *
     * @see net.yetamine.lang.concurrent.Invalidable#invalidate()
     */
    default void invalidate() {
        // Do nothing
    }
}
