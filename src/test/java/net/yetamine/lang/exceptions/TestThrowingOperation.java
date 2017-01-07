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

package net.yetamine.lang.exceptions;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link ThrowingOperation}.
 */
public final class TestThrowingOperation {

    /**
     * Tests {@link ThrowingOperation#from(ThrowingOperation)}.
     */
    @Test
    public void testFrom() {
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> {
            ThrowingOperation.from((Integer i) -> "".charAt(i)).execute(0);
        });

        Assert.expectThrows(IOException.class, () -> ThrowingOperation.from(o -> {
            throw new IOException();
        }).execute(null));
    }

    /**
     * Tests {@link ThrowingCallable#whenInterrupted(ThrowingConsumer)}.
     *
     * @throws Exception
     *             if the test fails
     */
    @Test
    public void testWhenInterrupted() throws Exception {
        try {
            ThrowingOperation.from(arg -> {
                throw new InterruptedException();
            }).whenInterrupted(InterruptionException::raise).execute(null);

            Assert.fail();
        } catch (UncheckedException e) {
            Assert.assertTrue(e.getCause() instanceof InterruptedException);
            Assert.assertTrue(Thread.interrupted());
        }

        Assert.expectThrows(IOException.class, () -> ThrowingOperation.from(arg -> {
            throw new IOException();
        }).whenInterrupted(InterruptionException::raise).execute(null));
    }

    /**
     * Tests {@link ThrowingOperation#guarding(ThrowingOperation)}.
     */
    @Test
    public void testGuarding() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingOperation.guarding(o -> {
            throw new IOException();
        }).apply(null));

        Assert.assertThrows(UncheckedException.class, () -> ThrowingOperation.guarding(o -> {
            throw new IllegalArgumentException();
        }).apply(null));

        Assert.assertThrows(UncheckedException.class, () -> ThrowingOperation.guarding(o -> {
            throw new AssertionError();
        }).apply(null));
    }

    /**
     * Tests {@link ThrowingOperation#enclosing(ThrowingOperation)}.
     */
    @Test
    public void testEnclosing() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingOperation.enclosing(o -> {
            throw new IOException();
        }).apply(null));

        Assert.assertThrows(UncheckedException.class, () -> ThrowingOperation.enclosing(o -> {
            throw new IllegalArgumentException();
        }).apply(null));

        Assert.assertThrows(AssertionError.class, () -> ThrowingOperation.enclosing(o -> {
            throw new AssertionError();
        }).apply(null));
    }

    /**
     * Tests {@link ThrowingOperation#rethrowing(ThrowingOperation)}.
     */
    @Test
    public void testRethrowing() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingOperation.rethrowing(o -> {
            throw new IOException();
        }).apply(null));

        Assert.assertThrows(IllegalArgumentException.class, () -> ThrowingOperation.rethrowing(o -> {
            throw new IllegalArgumentException();
        }).apply(null));

        Assert.assertThrows(AssertionError.class, () -> ThrowingOperation.rethrowing(o -> {
            throw new AssertionError();
        }).apply(null));
    }
}
