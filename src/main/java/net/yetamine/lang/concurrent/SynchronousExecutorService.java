/*
 * Copyright 2017 Yetamine
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Implements an {@link ExecutorService} that executes the tasks on the calling
 * threads, i.e., synchronously.
 */
public final class SynchronousExecutorService extends AbstractExecutorService implements Interruptible {

    /**
     * Map of running threads with their recursive submission counters.
     *
     * <p>
     * Everytime a thread calls {@link #execute(Runnable)}, its counter
     * increments here, so that a thread may submit a task recursively.
     * Otherwise it might happen that a thread could be forgotten too quickly
     * and a later task interruption request could not reach it.
     *
     * <p>
     * The map acts as a lock for the service structures as well.
     */
    private final Map<Thread, Long> running = new HashMap<>();

    /** Flag marking this instance as shutdown. */
    private boolean shutdown;

    /**
     * Creates a new instance.
     */
    public SynchronousExecutorService() {
        // Default constructor
    }

    /**
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     */
    public void execute(Runnable command) {
        Objects.requireNonNull(command);

        final Thread currentThread = Thread.currentThread();

        synchronized (running) {
            if (shutdown) {
                throw new RejectedExecutionException("Executor shut down.");
            }

            running.compute(currentThread, (t, v) -> (v != null) ? v + 1 : 1);
        }

        try {
            command.run();
        } finally {
            synchronized (running) {
                running.compute(currentThread, (t, v) -> (v == 1) ? null : v - 1);
                if (shutdown && running.isEmpty()) {
                    running.notify();
                }
            }
        }
    }

    /**
     * @see java.util.concurrent.ExecutorService#shutdown()
     */
    public void shutdown() {
        synchronized (running) {
            shutdown = true;
        }
    }

    /**
     * @see java.util.concurrent.ExecutorService#shutdownNow()
     */
    public List<Runnable> shutdownNow() {
        synchronized (running) {
            if (!shutdown) {
                shutdown = true;
                running.keySet().forEach(Thread::interrupt);
            }
        }

        return Collections.emptyList(); // Never queueing any tasks
    }

    /**
     * @see java.util.concurrent.ExecutorService#isShutdown()
     */
    public boolean isShutdown() {
        synchronized (running) {
            return shutdown;
        }
    }

    /**
     * @see java.util.concurrent.ExecutorService#isTerminated()
     */
    public boolean isTerminated() {
        synchronized (running) {
            return shutdown && running.isEmpty();
        }
    }

    /**
     * @see java.util.concurrent.ExecutorService#awaitTermination(long,
     *      java.util.concurrent.TimeUnit)
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long remaining = unit.toNanos(timeout);
        long timestamp = System.nanoTime();

        synchronized (running) {
            while (!shutdown || !running.isEmpty()) {
                if (remaining <= 0) {
                    return false;
                }

                TimeUnit.NANOSECONDS.timedWait(running, remaining);
                remaining -= System.nanoTime() - timestamp;
            }
        }

        return true;
    }

    /**
     * Interrupts all running tasks.
     *
     * @see net.yetamine.lang.concurrent.Interruptible#interrupt()
     */
    public void interrupt() {
        synchronized (running) {
            running.keySet().forEach(Thread::interrupt);
        }
    }
}
