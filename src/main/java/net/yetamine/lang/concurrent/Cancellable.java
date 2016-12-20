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

package net.yetamine.lang.concurrent;

/**
 * Provides the cancellation capability of an operation.
 *
 * <p>
 * Implementations of this interface must be thread-safe.
 */
public interface Cancellable {

    /**
     * Requests cancellation of the task.
     *
     * @param mayInterrupt
     *            if {@code true} and the operation has been started, the
     *            operation may be interrupted in order to terminate (if
     *            possible and supported)
     *
     * @return {@code true} if the task was cancelled before starting
     */
    boolean cancel(boolean mayInterrupt);
}
