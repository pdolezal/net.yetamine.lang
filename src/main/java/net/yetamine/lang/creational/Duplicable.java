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

package net.yetamine.lang.creational;

import java.util.Objects;

import net.yetamine.lang.Types;

/**
 * A {@link Cloneable} replacement.
 *
 * <p>
 * Because {@code Cloneable} is broken, hostile to classes with final fields and
 * inheriting it does not yet provide a meaningful invocation interface, here is
 * an alternative based on a regular interface.
 */
public interface Duplicable {

    /**
     * Makes a duplicate of this object.
     *
     * <p>
     * The precise meaning of "duplicate" may depend on the class of the object,
     * but in general, no operation on the duplicate should affect the source
     * (although it may affect objects referred from the source).
     *
     * <p>
     * The result must be an instance of the class of this object, i.e.,
     * {@code o.getClass().isInstance(o.duplicate())} must be true. Generally,
     * {@code o.duplicate().getClass() == o.getClass()} should rather be kept.
     *
     * <p>
     * Implementations and overrides of this method should adjust the return
     * type, so that clients are not required to cast the result to the expected
     * type.
     *
     * @return a duplicate of this instance
     *
     * @throws UnsupportedOperationException
     *             if this instance does not support duplicating
     */
    Object duplicate();

    /**
     * Makes a {@link Factory} instance using the given object as a prototype
     * for duplicating.
     *
     * @param <T>
     *            the type of the object
     * @param template
     *            the template object. It must not be {@code null}.
     *
     * @return a factory instance duplicating the given template
     */
    static <T extends Duplicable> Factory<T> factory(T template) {
        Objects.requireNonNull(template);
        return () -> Types.getClass(template).cast(template.duplicate());
    }
}
