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

import java.io.Closeable;
import java.io.IOException;

/**
 * Provides a few support methods for general {@link AutoCloseable}.
 */
public final class AutoCloseables {

    /**
     * Prevents creating instances of this class.
     */
    private AutoCloseables() {
        throw new AssertionError();
    }

    /**
     * Closes the given resource if not {@code null}.
     *
     * @param resource
     *            the resource to close
     *
     * @throws Exception
     *             if the operation fails
     */
    public static void close(AutoCloseable resource) throws Exception {
        if (resource != null) {
            resource.close();
        }
    }

    /**
     * Closes the given resource if not {@code null}.
     *
     * @param resource
     *            the resource to close
     *
     * @throws IOException
     *             if the operation fails
     */
    public static void close(Closeable resource) throws IOException {
        if (resource != null) {
            resource.close();
        }
    }

    /**
     * Closes the given resource if not {@code null}.
     *
     * @param resource
     *            the resource to close
     *
     * @return {@code true} iff the object is a resource that has been closed
     *
     * @throws Exception
     *             if the operation fails
     */
    public static boolean close(Object resource) throws Exception {
        if (resource instanceof AutoCloseable) {
            ((AutoCloseable) resource).close();
            return true;
        }

        return false;
    }
}
