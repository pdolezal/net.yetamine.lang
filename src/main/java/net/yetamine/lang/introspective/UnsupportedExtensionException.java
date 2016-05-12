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

package net.yetamine.lang.introspective;

import java.util.Objects;

/**
 * Indicates that some requested extensions are not supported for the operation.
 */
public class UnsupportedExtensionException extends UnsupportedOperationException {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Extensions to report. */
    private final Extensions extensions;

    /**
     * Creates a new instance with no details.
     */
    public UnsupportedExtensionException() {
        extensions = Extensions.empty();
    }

    /**
     * Create a new instance with the specified extensions as the detail.
     *
     * @param report
     *            the extensions to report. It must not be {@code null}.
     */
    public UnsupportedExtensionException(Extensions report) {
        extensions = Objects.requireNonNull(report);
    }

    /**
     * Create a new instance with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public UnsupportedExtensionException(String message) {
        super(message);
        extensions = Extensions.empty();
    }

    /**
     * Create a new instance with the specified cause and a detail message
     * constructed from the cause (if not {@code null}).
     *
     * @param cause
     *            the cause
     */
    public UnsupportedExtensionException(Throwable cause) {
        super(cause);
        extensions = Extensions.empty();
    }

    /**
     * Create a new instance with the specified detail message and cause.
     *
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public UnsupportedExtensionException(String message, Throwable cause) {
        super(message, cause);
        extensions = Extensions.empty();
    }

    /**
     * Create a new instance with the specified detail message and extensions.
     *
     * @param message
     *            the detail message
     * @param report
     *            the extensions to report. It must not be {@code null}.
     */
    public UnsupportedExtensionException(String message, Extensions report) {
        super(message);
        extensions = Objects.requireNonNull(report);
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        final String result = super.getMessage();
        return (result != null) ? result : extensions.toString();
    }

    /**
     * Returns the reported extensions.
     *
     * <p>
     * If no extensions were given, the result is an empty extension set, hence
     * the result is never {@code null}.
     *
     * @return the reported extensions
     */
    public Extensions getExtensions() {
        return extensions;
    }
}
