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
 * {@link AutoCloseable} (yet) and therefore try-with-resources can't manage
 * them.
 *
 * @param <T>
 *            the type of the adapted resource
 * @param <X>
 *            the type of the exception that the {@link AutoCloseable#close()}
 *            method may throw for this resource
 */
public interface AutoCloseableResource<T, X extends Exception> extends AutoCloseable {

    /**
     * Provides the adapted resource.
     *
     * <p>
     * Implementations should not return {@code null}, or at least until closing
     * the resource; after closing the resource, implementations should throw an
     * exception (like {@link IllegalStateException}) rather than returning
     * {@code null}. A lightweight implementation may keep returning always the
     * same result, regardless the state.
     *
     * @return the adapted resource
     */
    T resource();

    /**
     * @see java.lang.AutoCloseable#close()
     */
    void close() throws X;
}
