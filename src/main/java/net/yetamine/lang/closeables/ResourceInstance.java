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

import java.util.Optional;

/**
 * Represents a particular resource.
 *
 * <p>
 * This interface provides a handle for a resource that encapsulates the two
 * most important operations with any resource: acquiring an instance of the
 * resource when needed and releasing it when finished using it for the time
 * being. The details of acquiring (i.e., usually creating) an instance of the
 * resource and releasing it are hidden from the clients of the resource which
 * enables various strategies for managing the instance, e.g., creating it on
 * demand.
 *
 * <p>
 * Implementations of this interface should be thread-safe. Clients of this
 * interface, however, should coordinate their actions to prevent ownership
 * violation, e.g., closing a resource which another thread might still use.
 *
 * @param <R>
 *            the type of the resource
 * @param <X>
 *            the common type of the exceptions related to acquiring and
 *            releasing a resource instance
 */
public interface ResourceInstance<R, X extends Exception> {

    /**
     * Returns the acquired resource instance.
     *
     * <p>
     * If no resource instance has been acquired so far or since the previous
     * {@link #release()}, an instance shall be acquired at first. The result
     * must remain then same until {@link #release()} which may dispose it.
     *
     * @return the acquired resource instance
     *
     * @throws X
     *             if the resource acquisition fails
     */
    R acquired() throws X;

    /**
     * Returns the resource instance that has been acquired recently.
     *
     * <p>
     * Unlike {@link #acquired()}, this method never attempts to acquire any
     * resource instance, so it throws no exception which could be caused by
     * such an attempt. It rather returns an empty {@link Optional} if no
     * resource instance has been acquired so far or since the previous
     * {@link #release()}.
     *
     * @return the resource instance that has been acquired recently
     */
    Optional<R> available();

    /**
     * Releases the acquired resource instance if any.
     *
     * <p>
     * Implementations may or may not actually release the resource, depending
     * on their strategy. However, after calling this method, the callers must
     * expect the previous result of {@link #acquired()} or {@link #available()}
     * to be invalid and must not use it anymore. When the resource instance is
     * needed again, a valid instance shall be provided by a subsequent call to
     * {@link #acquired()}.
     *
     * @throws X
     *             if the resource release fails
     */
    void release() throws X;
}
