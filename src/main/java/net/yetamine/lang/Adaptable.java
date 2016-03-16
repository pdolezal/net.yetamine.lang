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

package net.yetamine.lang;

import java.util.Optional;

/**
 * A type that can be adapted safely to other types.
 */
public interface Adaptable {

    /**
     * Adapts this instance to the given type if possible.
     *
     * <p>
     * The default implementation attempts to cast this instance, however, an
     * alternative implementation may return a different instance, possibly a
     * view on the same data that is not related by inheritance.
     *
     * @param <T>
     *            the desired type
     * @param type
     *            the desired type. It must not be {@code null}.
     *
     * @return this instance adapted to the given type, or an empty container if
     *         not possible
     */
    default <T> Optional<T> adapt(Class<? extends T> type) {
        return type.isInstance(this) ? Optional.of(type.cast(this)) : Optional.empty();
    }

    /**
     * Adapts an instance to the given type if possible.
     *
     * @param <T>
     *            the desired type
     * @param type
     *            the desired type. It must not be {@code null}.
     * @param o
     *            the instance to adapt
     *
     * @return the instance adapted to the given type, or an empty container if
     *         not possible or the instance argument is {@code null}
     */
    static <T> Optional<T> adapt(Class<? extends T> type, Adaptable o) {
        return (o != null) ? o.adapt(type) : Optional.empty();
    }
}
