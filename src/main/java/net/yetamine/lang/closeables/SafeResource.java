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

/**
 * A generic interface for adapting resource-like objects that do not implement
 * {@link SafeCloseable} (yet) and therefore try-with-resources construct can't
 * manage them.
 *
 * @param <T>
 *            the type of the adapted resource
 */
public interface SafeResource<T> extends SafeCloseable, AutoResource<T, RuntimeException> {

    /**
     * Implementations should rather avoid throwing any exceptions, so that an
     * exception shall be rather a programming error than an actual failure.
     *
     * @see java.lang.AutoCloseable#close()
     */
    void close();

    /**
     * Returns an instance of this interface that adapts the given resource with
     * the help of the provided closing handler.
     *
     * @param <T>
     *            the type of the adapted resource
     * @param resource
     *            the resource to adapt
     * @param closing
     *            the closing handler. If the resource is {@code null}, the
     *            handler won't be invoked.
     *
     * @return an instance of this interface
     */
    static <T> SafeResource<T> adapt(T resource, Handler<? super T> closing) {
        return (resource != null) ? new SafeResourceAdapter<>(resource, closing) : MissingResource.asSafeResource();
    }

    /**
     * A handler for delegating a closing operation of an {@link SafeCloseable}
     * object.
     *
     * @param <T>
     *            the type of the resource to close
     */
    @FunctionalInterface
    interface Handler<T> extends AutoResource.Handler<T, RuntimeException> {

        /**
         * @see net.yetamine.lang.closeables.AutoResource.Handler#close(java.lang.Object)
         */
        void close(T object);
    }
}
