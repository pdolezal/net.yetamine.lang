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

package net.yetamine.lang.closeables;

import java.util.Arrays;

import net.yetamine.lang.exceptions.Throwing;

/**
 * Represents a strategy that closes a resource.
 *
 * @param <R>
 *            the type of the resource
 * @param <X>
 *            the type of the exception that the attempt to close the resource
 *            may throw
 */
@FunctionalInterface
public interface ResourceClosing<R, X extends Exception> {

    /**
     * Closes the given resource object.
     *
     * @param object
     *            the object to close. It should not be {@code null}, but
     *            implementations may tolerate {@code null} arguments and
     *            perform no operation then; this provides consistency with
     *            non-standard resource creation methods which could return
     *            {@code null} instead of throwing an exception.
     *
     * @throws X
     *             if the operation fails
     */
    void close(R object) throws X;

    /**
     * Closes all given objects with this strategy.
     *
     * <p>
     * The default implementation just invokes {@link #closeAll(Iterable)} like
     * this: {@code closeAll(Arrays.asList(object))}.
     *
     * @param objects
     *            the objects to close. It must not be {@code null}.
     *
     * @throws X
     *             if the operation fails
     */
    @SuppressWarnings("unchecked")
    default void closeAll(R... objects) throws X {
        closeAll(Arrays.asList(objects));
    }

    /**
     * Closes all given objects with this strategy.
     *
     * <p>
     * The default implementation closes all the given resources. If some of
     * them throws an exception, the exception will be thrown after all the
     * resources are closed; any subsequent exceptions are attached as
     * suppressed exceptions of the first one.
     *
     * @param objects
     *            the objects to close. It must not be {@code null}.
     *
     * @throws X
     *             if the operation fails
     */
    default void closeAll(Iterable<? extends R> objects) throws X {
        Throwable exception = null;

        for (R object : objects) {
            if (object == null) {
                continue;
            }

            try {
                close(object);
            } catch (Throwable t) {
                if (exception == null) {
                    exception = t;
                    continue;
                }

                exception.addSuppressed(t);
            }
        }

        if (exception == null) {
            return;
        }

        Throwing.some(exception).throwIfUnchecked();
        @SuppressWarnings("unchecked") // Valid because all unchecked exception have been handled
        final X throwable = (X) exception;
        throw throwable;
    }

    /**
     * Makes an instance from the given strategy.
     *
     * <p>
     * This method is a convenient factory method for fluent making instances:
     *
     * <pre>
     * ResourceClosing.from(MyUtilities::someTest).closeAll(resources)
     * </pre>
     *
     * @param <R>
     *            the type of the resource
     * @param <X>
     *            the type of the exception that the attempt to close the
     *            resource may throw
     * @param closing
     *            the closing method to return. It must not be {@code null}.
     *
     * @return the closing method
     */
    @SuppressWarnings("unchecked")
    static <R, X extends Exception> ResourceClosing<R, X> from(ResourceClosing<? super R, ? extends X> closing) {
        return (ResourceClosing<R, X>) closing;
    }

    /**
     * Returns an instance that does nothing.
     *
     * @param <R>
     *            the type of the resource
     * @param <X>
     *            the type of the exception that the attempt to close the
     *            resource may throw
     *
     * @return an instance that does nothing
     */
    static <R, X extends Exception> ResourceClosing<R, X> none() {
        return r -> {
            // Do nothing
        };
    }
}
