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

package net.yetamine.lang.introspective;

import java.util.function.Predicate;

/**
 * A convenient interface for implementing extension declarations.
 *
 * <p>
 * This interface serves as a marking interface for types that are intended as
 * extension declarators and it yet adds a few convenient methods for handling
 * {@link Extensible} types. The recommended pattern for implementing extension
 * declarators is to define them as enum constants and let the enum implement
 * this interface.
 */
public interface Extension {

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * <p>
     * Implementations of this interface are required to provide a sensible
     * definition of this method, so that the type could be used as an extension
     * declarator. Of course, {@link #hashCode()} implementation must provide an
     * acceptable behavior then as well.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    boolean equals(Object obj);

    /**
     * @see java.lang.Object#hashCode()
     */
    int hashCode();

    /**
     * Provides a predicate for testing whether an {@link Extensible} instance
     * contains this extension.
     *
     * <p>
     * Use this method instead of {@link #supportTest()} if the instances to
     * deal are {@link Extensible}.
     *
     * @return the testing predicate
     */
    default Predicate<Extensible> presentTest() {
        return o -> Extensions.of(o).contains(this);
    }

    /**
     * Provides a predicate for testing whether any instance contains this
     * extension.
     *
     * <p>
     * This method is useful when the instances to deal with might not be
     * {@link Extensible}.
     *
     * @return the testing predicate
     */
    default Predicate<Object> supportTest() {
        return o -> Extensions.of(o).contains(this);
    }
}
