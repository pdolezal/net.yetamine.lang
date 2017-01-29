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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

import net.yetamine.lang.Types;
import net.yetamine.lang.exceptions.Throwables;

/**
 * Cloning support.
 */
public final class Cloneables {

    /**
     * Prevents creating instances of this class.
     */
    private Cloneables() {
        throw new AssertionError();
    }

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

        final Class<? extends T> clazz = Types.classOf(object);
        if (clazz.isArray()) { // Arrays need special handling!
            return arrayClone(object);
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
        final Class<? extends T> clazz = Types.classOf(template);
        if (clazz.isArray()) { // Optimize for the array case
            return () -> arrayClone(template);
        }

        if (!(template instanceof Cloneable)) {
            throw new IllegalArgumentException("Cloneable object required.");
        }

        try {
            // Get the clone method; it must be public
            final MethodHandle clone = MethodHandles.publicLookup().unreflect(clazz.getMethod("clone"));
            final MethodHandle bound = clone.bindTo(template); // Bind the instance to get rid of all arguments

            // Make the catch handler that must match the actual return type of the clone method
            final MethodHandle never = MethodHandles.constant(clone.type().returnType(), null);
            final MethodHandle catcher = MethodHandles.filterReturnValue(HANDLE_CLONE_NOT_SUPPORTED, never);
            // Install the catch handler to wrap the checked exception according to the usual contract for the factory
            final MethodHandle handler = MethodHandles.catchException(bound, CloneNotSupportedException.class, catcher);

            @SuppressWarnings("unchecked")
            final Factory<T> result = MethodHandleProxies.asInterfaceInstance(Factory.class, handler);
            // If the cast above was dubious, there is still the possibility to
            // insert a return value check that throws an exception if the type
            // of the result does not match the requirements
            return result;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new SecurityException(e);
        }
    }

    /** Exception handler for {@link #prototype(Object)}. */
    private static final MethodHandle HANDLE_CLONE_NOT_SUPPORTED;
    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            final MethodType type = MethodType.methodType(void.class, CloneNotSupportedException.class);
            HANDLE_CLONE_NOT_SUPPORTED = lookup.findStatic(Cloneables.class, "handleCloneNotSupported", type);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Throws an {@link UnsupportedOperationException} wrapping a
     * {@link CloneNotSupportedException}.
     *
     * @param e
     *            the exception to wrap. It must not be {@code null}.
     */
    @SuppressWarnings("unused")
    private static void handleCloneNotSupported(CloneNotSupportedException e) {
        throw new UnsupportedOperationException(e);
    }

    /**
     * Clones an array.
     *
     * @param array
     *            the array to clone. It must be a valid array.
     *
     * @return a clone of the array
     */
    private static <T> T arrayClone(T array) {
        final Class<? extends T> clazz = Types.classOf(array);
        final Class<?> component = clazz.getComponentType();
        final int length = Array.getLength(array);
        final Object result = Array.newInstance(component, length);
        System.arraycopy(array, 0, result, 0, length);
        return clazz.cast(result);
    }
}
