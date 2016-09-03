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
