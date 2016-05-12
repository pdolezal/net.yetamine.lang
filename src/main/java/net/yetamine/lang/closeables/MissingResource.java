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
 * A singleton implementing {@link SafeResource} for no actual object.
 */
enum MissingResource implements SafeResource<Object> {

    /** Sole instance of this class. */
    INSTANCE;

    /**
     * @see net.yetamine.lang.closeables.AutoResource#get()
     */
    public Object get() {
        return null;
    }

    /**
     * @see net.yetamine.lang.closeables.SafeResource#close()
     */
    public void close() {
        // Do nothing
    }

    /**
     * Returns the instance cast to {@link SafeResource}.
     *
     * @param <T>
     *            the type of the adapted resource
     *
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    public static <T> SafeResource<T> asSafeResource() {
        return (SafeResource<T>) INSTANCE;
    }

    /**
     * Returns the instance cast to {@link AutoResource}.
     *
     * @param <T>
     *            the type of the adapted resource
     * @param <X>
     *            the formal type of the exception which is expected for the
     *            {@link AutoCloseable#close()} method
     *
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends Exception> AutoResource<T, X> asAutoResource() {
        return (AutoResource<T, X>) INSTANCE;
    }
}
