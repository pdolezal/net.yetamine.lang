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
 * The default implementation of {@link SafeResource} to adapt any resource-like
 * object for use with try-with-resources.
 *
 * @param <T>
 *            the type of the adapted resource
 */
class SafeResourceAdapter<T> extends AutoResourceAdapter<T, RuntimeException> implements SafeResource<T> {

    /**
     * Creates a new instance.
     *
     * @param object
     *            the resource to manage. It must not be {@code null}.
     * @param closing
     *            the closing handler. It must not be {@code null}.
     */
    public SafeResourceAdapter(T object, SafeResource.Handler<? super T> closing) {
        super(object, closing);
    }
}
