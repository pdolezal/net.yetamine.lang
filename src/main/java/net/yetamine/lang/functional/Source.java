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

package net.yetamine.lang.functional;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents an object that itself acts as a source for typical pipeline-like
 * operations.
 *
 * @param <T>
 *            the type of the object to act as, usually this parameter should be
 *            the type of the descendant that serves as the base for the whole
 *            hierarchy using this capability
 */
public interface Source<T> {

    /**
     * Applies the given predicate to this instance.
     *
     * @param predicate
     *            the predicate to apply. It must not be {@code null}.
     *
     * @return the {@link Optional} instance containing this instance if the
     *         predicate succeeds, otherwise an empty container
     */
    Optional<T> filter(Predicate<? super T> predicate);

    /**
     * Applies the given function to this instance.
     *
     * @param <U>
     *            the type of the result
     * @param mapping
     *            the function which is supposed to map this instance to the
     *            result to return. It must not be {@code null}.
     *
     * @return the result of the mapping function
     */
    <U> U map(Function<? super T, ? extends U> mapping);

    /**
     * Sends this instance to the given consumer.
     *
     * @param consumer
     *            the consumer to accept this instance. It must not be
     *            {@code null}.
     *
     * @return this instance
     */
    T accept(Consumer<? super T> consumer);
}
