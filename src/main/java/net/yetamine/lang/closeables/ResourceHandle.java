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
 * Represents a particular resource.
 *
 * <p>
 * This interface extends {@link ResourceInstance} with the support useful for
 * <i>try-with-resources</i> by inheriting from {@link AutoCloseable}. Closing
 * the handle releases the acquired resource and invalidates the handle in order
 * to prevent its further use.
 *
 * @param <R>
 *            the type of the resource
 * @param <X>
 *            the type of the exception that the creation or release of the
 *            resource may throw
 */
public interface ResourceHandle<R, X extends Exception> extends PureCloseable<X>, ResourceInstance<R, X> {

    /**
     * Releases the resource instance if any and invalidates this instance.
     *
     * <p>
     * After calling this method, subsequent invocations to {@link #acquired()}
     * should fail with a suitable exception; {@link IllegalStateException} is
     * recommended for that purpose, unless the implementation has a reason to
     * favour another one. Other methods should not throw any exception as this
     * method just makes the "no resource instance" state permanent for them.
     *
     * @see net.yetamine.lang.closeables.PureCloseable#close()
     */
    void close() throws X;
}
