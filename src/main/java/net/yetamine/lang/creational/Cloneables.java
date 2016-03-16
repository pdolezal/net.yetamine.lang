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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

import net.yetamine.lang.Throwables;

/**
 * Cloning support.
 */
public final class Cloneables {

    /**
     * Clones an object.
     *
     * @param <T>
     *            the type of the object to clone
     * @param object
     *            the object to clone
     *
     * @return the clone of the source, or {@code null} if the source is
     *         {@code null}
     *
     * @throws CloneNotSupportedException
     *             if the object does not support cloning or cloning fails
     */
    public static <T> T clone(T object) throws CloneNotSupportedException {
        return clone(object, e -> Throwables.init(new CloneNotSupportedException(), e));
    }

    /**
     * Clones an object.
     *
     * @param <T>
     *            the type of the object to clone
     * @param <X>
     *            the type of exception to throw
     * @param object
     *            the object to clone
     * @param exceptionMapping
     *            the function to map exceptions. It must not be {@code null}.
     *
     * @return the clone of the object, or {@code null} if the source is
     *         {@code null} or the exception mapping function returns
     *         {@code null}
     *
     * @throws X
     *             if the cloning operation fails
     */
    public static <T, X extends Throwable> T clone(T object, Function<? super Throwable, ? extends X> exceptionMapping) throws X {
        Objects.requireNonNull(exceptionMapping);
        if (object == null) {
            return null;
        }

        // This is safe due to contract of getClass()
        @SuppressWarnings("unchecked")
        final Class<? extends T> clazz = (Class<? extends T>) object.getClass();

        if (clazz.isArray()) { // Surprisingly, arrays need special handling
            final Class<?> component = clazz.getComponentType();
            final int length = Array.getLength(object);
            final Object result = Array.newInstance(component, length);
            System.arraycopy(object, 0, result, 0, length);
            return clazz.cast(result);
        }

        try {
            return clazz.cast(clazz.getMethod("clone").invoke(object));
        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            final X toThrow = exceptionMapping.apply(e);
            if (toThrow != null) {
                throw toThrow;
            }

            return null;
        }
    }

    /**
     * Makes a factory that clones the template.
     *
     * @param <T>
     *            the type of the object to clone
     * @param template
     *            the template object to clone. It must not be {@code null}.
     *
     * @return a factory that clones the template
     */
    public static <T> Factory<T> prototype(T template) {
        return Factory.prototype(template, o -> clone(o, IllegalArgumentException::new));
    }

    private Cloneables() {
        throw new AssertionError();
    }
}
