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
 * Provides the capability of invalidating some content.
 *
 * <p>
 * This interface targets on concurrent use cases where its implementations must
 * be thread-safe. However, implementations that are not intended for concurrent
 * use, like non-concurrent collections may relax this thread safety requirement
 * and align its use conditions with its own thread safety limitations.
 */
public interface Invalidable {

    /**
     * Invalidates the content related to this instance.
     */
    void invalidate();

    /**
     * Invokes {@link #invalidate()} if the given object implements this
     * interface.
     *
     * @param o
     *            the object to invalidate
     *
     * @return {@code true} if {@link #invalidate()} has been invoked on the
     *         given object successfully
     */
    static boolean invalidate(Object o) {
        if (o instanceof Invalidable) {
            ((Invalidable) o).invalidate();
            return true;
        }

        return false;
    }
}
