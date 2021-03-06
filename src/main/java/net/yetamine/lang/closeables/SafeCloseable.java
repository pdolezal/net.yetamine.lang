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
 * An extension of {@link AutoCloseable} that throws no checked exceptions.
 *
 * <p>
 * Implementations of this interface should avoid throwing any exceptions if
 * possible. Invoking this method on a closed instance should have no effect.
 */
public interface SafeCloseable extends PureCloseable<RuntimeException> {

    /**
     * @see java.lang.AutoCloseable#close()
     */
    void close();

    /**
     * Closes the given resource if not {@code null}.
     *
     * @param resource
     *            the resource to close
     */
    static void close(SafeCloseable resource) {
        PureCloseable.close(resource);
    }

    /**
     * Closes the given resource if it is an instance of this interface.
     *
     * @param resource
     *            the resource to close
     *
     * @return {@code true} iff the object is a resource that has been closed
     */
    static boolean close(Object resource) {
        if (resource instanceof SafeCloseable) {
            ((SafeCloseable) resource).close();
            return true;
        }

        return false;
    }
}
