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

package net.yetamine.lang;

import java.util.Objects;

/**
 * Thrown when a checked exception shall be passed through as a boundary that
 * does not allow such checked exception, but the checked exception is desired
 * on a upper level. For instance, a callback code throws copes with a checked
 * exception which should be passed, but the calling code does not allow to,
 * although it relays unchecked exceptions from the callback to the caller.
 *
 * <p>
 * The common practice is wrapping the checked exception in an unchecked one.
 * This exception serves exactly to this one purpose and works especially well
 * when coupled with {@link Throwing} for unwrapping the cause and rethrowing.
 */
public class UncheckedException extends RuntimeException {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance with the specified cause and a detail message
     * constructed from the cause (if not {@code null}).
     *
     * @param cause
     *            the cause. It must not be {@code null}.
     */
    public UncheckedException(Throwable cause) {
        super(Objects.requireNonNull(cause));
    }
}
