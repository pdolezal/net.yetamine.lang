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
 * {@link SafeCloseable} (yet) and therefore try-with-resources can't manage
 * them, or require yet another catch block to deal with exceptions possibly
 * thrown from the {@link AutoCloseable#close()} method.
 *
 * @param <T>
 *            the type of the adapted resource
 */
public interface SafeCloseableResource<T> extends SafeCloseable, AutoCloseableResource<T, RuntimeException> {

    /**
     * Implementations should rather avoid throwing any exceptions, so that an
     * exception shall be rather a programming error than an actual failure.
     *
     * @see java.lang.AutoCloseable#close()
     */
    void close();
}
