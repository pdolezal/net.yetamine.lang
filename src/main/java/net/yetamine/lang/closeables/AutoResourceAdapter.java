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

import java.util.Objects;

/**
 * The default implementation of {@link AutoResource} to adapt any resource-like
 * object for use with try-with-resources.
 *
 * @param <T>
 *            the type of the adapted resource
 * @param <X>
 *            the type of the exception that the {@link AutoCloseable#close()}
 *            method may throw for this resource
 */
class AutoResourceAdapter<T, X extends Exception> implements AutoResource<T, X> {

    /** Resource to manage. */
    private final T resource;
    /** Handler for closing the resource. */
    private final Handler<? super T, ? extends X> handler;

    /**
     * Creates a new instance.
     *
     * @param object
     *            the resource to manage. It must not be {@code null}.
     * @param closing
     *            the closing handler. It must not be {@code null}.
     */
    public AutoResourceAdapter(T object, AutoResource.Handler<? super T, ? extends X> closing) {
        // Actually, an assert should be good enough here and now
        handler = Objects.requireNonNull(closing);
        resource = Objects.requireNonNull(object);
    }

    /**
     * @see net.yetamine.lang.closeables.AutoResource#get()
     */
    public T get() {
        return resource;
    }

    /**
     * @see net.yetamine.lang.closeables.AutoResource#close()
     */
    public void close() throws X {
        handler.close(resource);
    }
}
