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
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An interface for factories and builders.
 *
 * <p>
 * For convenience this interface inherits from {@link Supplier} and the
 * inherited {@link #get()} method should be an alias to {@link #build()}.
 *
 * @param <T>
 *            the type of the product
 */
@FunctionalInterface
public interface Factory<T> extends Supplier<T> {

    /**
     * Provides a new instance of the product.
     *
     * <p>
     * Technically, the result does not have to be a brand new instance if the
     * result is immutable and may be shared. However, the result must be such
     * an instance that it can be used by multiple clients without their mutual
     * interference. This is an additional requirement that the {@link #get()}
     * method does not impose.
     *
     * @return a new instance of the product
     */
    T build();

    /**
     * @see java.util.function.Supplier#get()
     */
    default T get() {
        return build();
    }

    /**
     * Makes a factory which uses a template object as the prototype for making
     * more instances.
     *
     * @param <U>
     *            the type of the template
     * @param <T>
     *            the type of the product
     * @param template
     *            the template for the builder. It must not be {@code null}.
     * @param builder
     *            the builder which takes a template object and returns an
     *            independent copy of it. It must not be {@code null}.
     *
     * @return a factory using a template object
     */
    static <U, T> Factory<T> prototype(U template, Function<? super U, ? extends T> builder) {
        Objects.requireNonNull(template);
        Objects.requireNonNull(builder);

        return () -> builder.apply(template);
    }
}
