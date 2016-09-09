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

import java.util.Objects;
import java.util.concurrent.locks.Lock;

import net.yetamine.lang.closeables.SafeCloseable;

/**
 * A utility class allowing to use a {@link Lock} with try-with-resources.
 *
 * <p>
 * This class allows emulate a {@code synchronized} block in similar way:
 *
 * <pre>
 * try (LockRegion lr = LockRegion.open(lock)) { // Locks the lock
 *     // Do something in the critical section
 * } // Automatically releases the lock
 * </pre>
 *
 * This code is effectively same as usual construction with try-finally:
 *
 * <pre>
 * lock.lock();
 * try {
 *     // Do something in the critical section
 * } finally {
 *     lock.unlock();
 * }
 * </pre>
 *
 * <p>
 * One of the main advantages of this resource-like emulation of try-finally is
 * easier use of dynamically ordered locks (that is a technique when lock order
 * is determined dynamically by such a key that prevents deadlock due to wrong
 * lock order) together with {@link net.yetamine.lang.closeables.ResourceGroup}.
 *
 * <p>
 * Note that instances of this class are meant to be used within the scope of
 * the creating thread, therefore this class is not thread-safe, which can be
 * confusing with the respect to its purpose, i.e., ensuring thread safety.
 */
public final class LockRegion implements SafeCloseable {

    /** Managed lock. */
    private Lock lock;

    /**
     * Creates a new instance.
     *
     * @param l
     *            the lock to manage. It must not be {@code null}.
     */
    private LockRegion(Lock l) {
        lock = Objects.requireNonNull(l);
    }

    /**
     * Makes a new instance and acquires the lock.
     *
     * @param lock
     *            the lock to acquire. It must not be {@code null}.
     *
     * @return the new instance
     *
     * @see Lock#lock()
     */
    public static LockRegion open(Lock lock) {
        final LockRegion result = new LockRegion(lock);
        lock.lock();
        return result;
    }

    /**
     * Makes a new instance and acquires the lock interruptibly.
     *
     * @param lock
     *            the lock to acquire. It must not be {@code null}.
     *
     * @return the new instance
     *
     * @throws InterruptedException
     *             if the lock attempt has been interrupted
     *
     * @see Lock#lockInterruptibly()
     */
    public static LockRegion openInterruptibly(Lock lock) throws InterruptedException {
        final LockRegion result = new LockRegion(lock);
        lock.lockInterruptibly();
        return result;
    }

    /**
     * Indicates if the lock is still acquired.
     *
     * @return {@code true} if the lock hasn't been unlocked yet
     */
    public boolean locked() {
        return (lock != null);
    }

    /**
     * Unlocks the lock.
     *
     * <p>
     * This method is idempotent and unlocks the lock only once, hence it is
     * safe to call it earlier if the lock can be released already. However,
     * when the lock might be released before the end of the block, using the
     * classical approach is more recommended, because try-with-resources can
     * suggest more the block nature of the lock.
     *
     * @see net.yetamine.lang.closeables.SafeCloseable#close()
     */
    public void close() {
        if (lock != null) {
            lock.unlock();
            lock = null;
        }
    }
}
