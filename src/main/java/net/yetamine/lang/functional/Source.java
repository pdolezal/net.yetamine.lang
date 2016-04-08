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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An extension of {@link Supplier} that provides additional operations like a
 * direct conversion to {@link Optional} or value mapping.
 *
 * @param <T>
 *            the type of the element
 */
@FunctionalInterface
public interface Source<T> extends Supplier<T> {

    /**
     * Returns the result of the given function applied on the current value of
     * the element.
     *
     * @param <V>
     *            the type of the mapping result
     * @param mapping
     *            the mapping function to apply. It must not be {@code null}.
     *
     * @return the result of the mapping function
     */
    default <V> V map(Function<? super T, ? extends V> mapping) {
        return mapping.apply(get());
    }

    /**
     * Returns the value of the element as an {@link Optional} instance.
     *
     * <p>
     * This method provides a bridge to the standard library and allows using
     * patterns like {@code pointer.nonNull().orElse(fallback)}
     *
     * @return the value as an {@link Optional} instance
     */
    default Optional<T> nonNull() {
        return Optional.ofNullable(get());
    }

    /**
     * Returns a stream using this instance as the element source.
     *
     * @return a stream using this instance as the element source
     */
    default Stream<T> stream() {
        return Stream.of(this).map(Source::get);
    }
}
