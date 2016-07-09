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

package net.yetamine.lang.formatting;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A proxy for an object that has no suitable {@link Object#toString()} method
 * override.
 */
public final class ToString implements Supplier<String> {

    /** Strategy for {@link #toString()}. */
    private final Supplier<String> value;

    /**
     * Creates a new instance.
     *
     * @param representation
     *            the value to present. It must not be {@code null}.
     */
    public ToString(String representation) {
        Objects.requireNonNull(representation);
        value = () -> representation;
    }

    /**
     * Creates a new instance.
     *
     * @param implementation
     *            the implementation of {@link #toString()}. It must not be
     *            {@code null} and it should behave as {@link Object#toString()}
     *            specifies.
     */
    public ToString(Supplier<String> implementation) {
        value = Objects.requireNonNull(implementation);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return value.get();
    }

    /**
     * @see java.util.function.Supplier#get()
     */
    public String get() {
        return toString();
    }
}
