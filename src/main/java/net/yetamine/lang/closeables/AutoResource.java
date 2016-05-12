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

import java.util.function.Supplier;

/**
 * A generic interface for adapting resource-like objects that do not implement
 * {@link AutoCloseable} (yet) and therefore try-with-resources construct can't
 * manage them.
 *
 * @param <T>
 *            the type of the adapted resource
 * @param <X>
 *            the type of the exception that the {@link AutoCloseable#close()}
 *            method may throw for this resource
 */
public interface AutoResource<T, X extends Exception> extends AutoCloseable, Supplier<T> {

    /**
     * Provides the adapted resource.
     *
     * <p>
     * Implementations should return {@code null} if no resource was provided
     * for adapting. The result of this method is not defined after invoking
     * {@link #close()}. Implementations may then return the original value,
     * throw an exception (then {@link IllegalStateException} could be a good
     * choice), or return {@code null} (which is the least preferred option).
     *
     * @see java.util.function.Supplier#get()
     */
    T get();

    /**
     * Closes the adapted resource.
     *
     * @see java.lang.AutoCloseable#close()
     */
    void close() throws X;

    /**
     * Returns an instance of this interface that adapts the given resource with
     * the help of the provided closing handler.
     *
     * @param <T>
     *            the type of the adapted resource
     * @param <X>
     *            the type of the exception that the
     *            {@link AutoCloseable#close()} method may throw for this
     *            resource
     * @param resource
     *            the resource to adapt
     * @param closing
     *            the closing handler. If the resource is {@code null}, the
     *            handler won't be invoked.
     *
     * @return an instance of this interface
     */
    static <T, X extends Exception> AutoResource<T, X> adapt(T resource, Handler<? super T, ? extends X> closing) {
        return (resource != null) ? new AutoResourceAdapter<>(resource, closing) : MissingResource.asAutoResource();
    }

    /**
     * A handler for delegating a closing operation of an {@link AutoCloseable}
     * object.
     *
     * @param <T>
     *            the type of the resource to close
     * @param <X>
     *            the exception that the attempt may fail with
     */
    @FunctionalInterface
    interface Handler<T, X extends Exception> {

        /**
         * Closes the given object.
         *
         * @param object
         *            the object to close. It must not be {@code null}.
         *
         * @throws X
         *             if the operation fails
         */
        void close(T object) throws X;
    }
}
