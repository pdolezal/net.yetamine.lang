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
 * Provides the capability of interrupting an asynchronous operation.
 *
 * <p>
 * Implementations of this interface must be thread-safe.
 */
public interface Interruptible {

    /**
     * Interrupts the operation linked to this instance.
     *
     * <p>
     * If the operation has been interrupted already, this method does nothing.
     * This method should throw no exceptions either.
     */
    void interrupt();

    /**
     * Invokes {@link #interrupt()} on the given object if the object implements
     * this interface.
     *
     * @param o
     *            the object to interrupt
     *
     * @return {@code true} if {@link #interrupt()} has been invoked on the
     *         given object successfully
     */
    static boolean interrupt(Object o) {
        if (o instanceof Interruptible) {
            ((Interruptible) o).interrupt();
            return true;
        }

        return false;
    }
}
