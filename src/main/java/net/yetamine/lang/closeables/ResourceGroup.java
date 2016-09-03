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
 * A resource group providing means for collective and individual resource
 * management.
 *
 * <p>
 * Implementations of this interface should be thread-safe.
 *
 * @param <X>
 *            the type of the exception that the creation or release of the
 *            resource may throw
 */
public interface ResourceGroup<X extends Exception> extends PureCloseable<X> {

    /**
     * Adopts the given resource instance.
     *
     * <p>
     * This method adds the given resource instance in the group, returning a
     * handle to manage the resource. The resource instance must be closed if
     * the handle is closed, which includes closing the whole group. Calling
     * {@link ResourceHandle#release()} does nothing in this case.
     *
     * @param <R>
     *            the type of the resource
     * @param resource
     *            the resource instance to adopt
     * @param destructor
     *            the strategy for closing the resource instance. It must not be
     *            {@code null}.
     *
     * @return a handle to manage the resource
     */
    <R> ResourceHandle<R, X> adopted(R resource, ResourceClosing<? super R, ? extends X> destructor);

    /**
     * Adopts the given resource instance.
     *
     * <p>
     * This method behaves like {@link #adopted(Object, ResourceClosing)}, but
     * the resource given for managing can't be closed by any means by the
     * returned handle. This is useful when the handle should provide some
     * resource without actually owning it, e.g., when some code expects a
     * handle, but the resource can't be managed in such a dynamic way and must
     * remain steady for the code.
     *
     * @param <R>
     *            the type of the resource
     * @param resource
     *            the resource instance to adopt
     *
     * @return a handle to manage the resource
     */
    default <R> ResourceHandle<R, X> adopted(R resource) {
        return adopted(resource, ResourceClosing.none());
    }

    /**
     * Adds a new resource in this stack.
     *
     * <p>
     * This method adds a placeholder for the resource in the group, returning a
     * handle to manage it. The resource instance is created and released as the
     * handle invocations require: {@link ResourceHandle#acquired()} should make
     * another resource instance on demand and {@link ResourceHandle#release()}
     * should free the instance.
     *
     * @param <R>
     *            the type of the resource
     * @param constructor
     *            the strategy for opening a resource instance. It must not be
     *            {@code null}.
     * @param destructor
     *            the strategy for closing a resource instance. It must not be
     *            {@code null}.
     *
     * @return a handle to manage the resource
     */
    <R> ResourceHandle<R, X> managed(ResourceOpening<? extends R, ? extends X> constructor, ResourceClosing<? super R, ? extends X> destructor);

    /**
     * Releases all resources.
     *
     * @throws X
     *             if the operation fails
     */
    void release() throws X;

    /**
     * Closes this instance and all managed resources.
     *
     * <p>
     * After calling this method, no resources may be added and such attempts
     * should fail with a suitable exception; {@link IllegalStateException} is
     * recommended for that purpose, unless the implementation has a reason to
     * favour another one.
     *
     * @see java.lang.AutoCloseable#close()
     */
    void close() throws X;

    /**
     * Indicates whether this instance is closed already.
     *
     * @return {@code true} after calling {@link #close()}
     */
    boolean closed();
}
