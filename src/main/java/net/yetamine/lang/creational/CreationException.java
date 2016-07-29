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

package net.yetamine.lang.creational;

/**
 * Thrown when creating an object is not possible and no better exception is
 * available to indicate the failure, e.g., the underlying implementation of a
 * factory or a builder throws a checked exception that has no better unchecked
 * counterpart to translate to.
 */
public class CreationException extends RuntimeException {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance with no details.
     */
    public CreationException() {
        // Default constructor
    }

    /**
     * Create a new instance with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public CreationException(String message) {
        super(message);
    }

    /**
     * Create a new instance with the specified cause and a detail message
     * constructed from the cause (if not {@code null}).
     *
     * @param cause
     *            the cause
     */
    public CreationException(Throwable cause) {
        super(cause);
    }

    /**
     * Create a new instance with the specified detail message and cause.
     *
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public CreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
