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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link SynchronousExecutorService}.
 */
public final class TestSynchronousExecutorService {

    /**
     * Tests {@link SynchronousExecutorService#shutdown()}.
     *
     * @throws InterruptedException
     *             if interrupted waiting on termination
     */
    @Test
    public void testShutdown_Idle() throws InterruptedException {
        final ExecutorService es = new SynchronousExecutorService();
        Assert.assertFalse(es.isShutdown());
        Assert.assertFalse(es.isTerminated());

        es.shutdown();
        Assert.assertTrue(es.isShutdown());

        es.awaitTermination(0, TimeUnit.NANOSECONDS);
        Assert.assertTrue(es.isTerminated());
    }

    /**
     * Tests {@link SynchronousExecutorService#shutdownNow()}.
     *
     * @throws InterruptedException
     *             if interrupted waiting on termination
     */
    @Test
    public void testShutdownNow_Idle() throws InterruptedException {
        final ExecutorService es = new SynchronousExecutorService();
        Assert.assertFalse(es.isShutdown());
        Assert.assertFalse(es.isTerminated());

        Assert.assertTrue(es.shutdownNow().isEmpty());
        Assert.assertTrue(es.isShutdown());

        Assert.assertTrue(es.awaitTermination(0, TimeUnit.NANOSECONDS));
        Assert.assertTrue(es.isTerminated());
    }

    /**
     * Tests {@link SynchronousExecutorService#shutdown()}.
     *
     * @throws InterruptedException
     *             if interrupted waiting on termination
     */
    @Test
    public void testShutdown_Running() throws InterruptedException {
        final ExecutorService es = new SynchronousExecutorService();
        Assert.assertFalse(es.isShutdown());
        Assert.assertFalse(es.isTerminated());

        final AtomicBoolean run = new AtomicBoolean();
        final Semaphore terminating = new Semaphore(0);
        final Semaphore waiting = new Semaphore(0);
        spawn(() -> es.execute(() -> {
            waiting.release();
            terminating.acquireUninterruptibly();
            waiting.release();
            run.set(true);
        }));

        waiting.acquire();
        es.shutdown();
        Assert.assertTrue(es.isShutdown());
        Assert.assertFalse(es.isTerminated());
        Assert.assertFalse(run.get());
        Assert.assertFalse(es.awaitTermination(0, TimeUnit.NANOSECONDS));
        terminating.release();
        waiting.acquire();
        Assert.assertTrue(es.awaitTermination(0, TimeUnit.NANOSECONDS));
        Assert.assertTrue(es.isTerminated());
        Assert.assertTrue(run.get());
    }

    /**
     * Tests {@link SynchronousExecutorService#shutdownNow()}.
     *
     * @throws InterruptedException
     *             if interrupted waiting on termination
     */
    @Test
    public void testShutdownNow_Running() throws InterruptedException {
        final ExecutorService es = new SynchronousExecutorService();
        Assert.assertFalse(es.isShutdown());
        Assert.assertFalse(es.isTerminated());

        final AtomicBoolean run = new AtomicBoolean();
        final Semaphore terminating = new Semaphore(0);
        final Semaphore waiting = new Semaphore(0);
        spawn(() -> es.execute(() -> {
            waiting.release();
            terminating.acquireUninterruptibly();
            waiting.release();
            run.set(true);
        }));

        waiting.acquire();
        Assert.assertEquals(es.shutdownNow(), Collections.emptyList());
        Assert.assertTrue(es.isShutdown());
        Assert.assertFalse(es.isTerminated());
        Assert.assertFalse(run.get());
        Assert.assertFalse(es.awaitTermination(0, TimeUnit.NANOSECONDS));
        terminating.release();
        waiting.acquire();
        Assert.assertTrue(es.awaitTermination(0, TimeUnit.NANOSECONDS));
        Assert.assertTrue(es.isTerminated());
        Assert.assertTrue(run.get());
    }

    /**
     * Tests {@link SynchronousExecutorService#execute(Runnable)}.
     */
    @Test
    public void testExecute() {
        final ExecutorService es = new SynchronousExecutorService();
        final Thread mainThread = Thread.currentThread();
        final AtomicBoolean run = new AtomicBoolean();
        es.execute(() -> {
            Assert.assertSame(Thread.currentThread(), mainThread);
            run.set(true);
        });

        Assert.assertTrue(run.get());

        // Test fail
        Assert.expectThrows(NoSuchElementException.class, () -> es.execute(() -> {
            throw new NoSuchElementException();
        }));
    }

    /**
     * Tests {@link SynchronousExecutorService#submit(Runnable)}.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testSubmit_Runnable1() throws Exception {
        final ExecutorService es = new SynchronousExecutorService();
        final Thread mainThread = Thread.currentThread();
        final AtomicBoolean run = new AtomicBoolean();
        final Future<?> future = es.submit(() -> {
            Assert.assertSame(Thread.currentThread(), mainThread);
            run.set(true);
        });

        Assert.assertTrue(run.get());
        // Actually run already
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
        future.cancel(true); // Not cancellable
        Assert.assertFalse(future.isCancelled());
        future.get(1, TimeUnit.SECONDS);

        // Test fail
        final NoSuchElementException cause = new NoSuchElementException();
        final Future<?> fail = es.submit((Runnable) () -> {
            throw cause;
        });

        try {
            fail.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertSame(e.getCause(), cause);
        }
    }

    /**
     * Tests {@link SynchronousExecutorService#submit(Runnable, Object)}.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testSubmit_Runnable2() throws Exception {
        final ExecutorService es = new SynchronousExecutorService();
        final Thread mainThread = Thread.currentThread();
        final AtomicBoolean run = new AtomicBoolean();
        final Future<Boolean> future = es.submit(() -> {
            Assert.assertSame(Thread.currentThread(), mainThread);
            run.set(true);
        }, true);

        Assert.assertTrue(run.get());
        // Actually run already
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
        future.cancel(true); // Not cancellable
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.get());
        Assert.assertTrue(future.get(1, TimeUnit.SECONDS));

        // Test fail
        final NoSuchElementException cause = new NoSuchElementException();
        final Future<Boolean> fail = es.submit(() -> {
            throw cause;
        }, true);

        try {
            fail.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertSame(e.getCause(), cause);
        }
    }

    /**
     * Tests {@link SynchronousExecutorService#submit(Callable)}.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testSubmit_Callable() throws Exception {
        final ExecutorService es = new SynchronousExecutorService();
        final Thread mainThread = Thread.currentThread();
        final AtomicBoolean run = new AtomicBoolean();
        final Future<Boolean> future = es.submit(() -> {
            Assert.assertSame(Thread.currentThread(), mainThread);
            run.set(true);
            return true;
        });

        Assert.assertTrue(run.get());
        // Actually run already
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
        future.cancel(true); // Not cancellable
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.get());
        Assert.assertTrue(future.get(1, TimeUnit.SECONDS));

        // Test fail
        final NoSuchElementException cause = new NoSuchElementException();
        final Future<Boolean> fail = es.submit(() -> {
            throw cause;
        });

        try {
            fail.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertSame(e.getCause(), cause);
        }
    }

    /**
     * Tests {@link SynchronousExecutorService#invokeAll(java.util.Collection)}.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testInvokeAll() throws Exception {
        final ExecutorService es = new SynchronousExecutorService();
        final Thread mainThread = Thread.currentThread();
        final AtomicBoolean[] run = { new AtomicBoolean(), new AtomicBoolean() };

        final List<Future<Boolean>> futures = es.invokeAll(Arrays.asList(() -> {
            Assert.assertSame(Thread.currentThread(), mainThread);
            run[0].set(true);
            return true;
        }, () -> {
            Assert.assertSame(Thread.currentThread(), mainThread);
            run[1].set(true);
            return true;
        }));

        for (int i = 0; i < run.length; i++) {
            Assert.assertTrue(run[i].get());
            final Future<Boolean> future = futures.get(i);
            Assert.assertTrue(future.isDone());
            Assert.assertFalse(future.isCancelled());
            future.cancel(true); // Not cancellable
            Assert.assertFalse(future.isCancelled());
            Assert.assertTrue(future.get());
            Assert.assertTrue(future.get(1, TimeUnit.SECONDS));
        }
    }

    /**
     * Tests {@link SynchronousExecutorService#invokeAny(java.util.Collection)}.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testInvokeAny() throws Exception {
        final ExecutorService es = new SynchronousExecutorService();
        final Thread mainThread = Thread.currentThread();
        final AtomicBoolean[] run = { new AtomicBoolean(), new AtomicBoolean() };

        final int result = es.invokeAny(Arrays.asList(() -> {
            Assert.assertSame(Thread.currentThread(), mainThread);
            run[0].set(true);
            return 0;
        }, () -> {
            Assert.assertSame(Thread.currentThread(), mainThread);
            run[1].set(true);
            return 1;
        }));

        Assert.assertTrue(run[result].get());
    }

    /**
     * Tests interruption.
     *
     * @throws InterruptedException
     *             if interrupted
     */
    @Test
    public void testInterrupt() throws InterruptedException {
        final ExecutorService es = new SynchronousExecutorService();
        final AtomicBoolean run = new AtomicBoolean();
        final Semaphore waiting = new Semaphore(0);
        final Thread thread = spawn(() -> es.execute(() -> {
            try {
                waiting.release();
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                run.set(true);
            }
        }));

        waiting.acquire();
        Assert.assertFalse(run.get());
        thread.interrupt();
        thread.join();
        Assert.assertTrue(run.get());
    }

    /**
     * Spawns a separate thread.
     *
     * @param r
     *            the {@link Runnable} to start. It must not be {@code null}.
     *
     * @return the thread
     */
    private static Thread spawn(Runnable r) {
        final Thread result = new Thread(r);
        result.setDaemon(true);
        result.start();
        return result;
    }
}
