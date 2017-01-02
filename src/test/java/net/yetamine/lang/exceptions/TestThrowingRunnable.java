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
 * Tests {@link ThrowingRunnable}.
 */
public final class TestThrowingRunnable {

    /**
     * Tests {@link ThrowingRunnable#from(ThrowingRunnable)}.
     */
    @Test
    public void testFrom() {
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> ThrowingRunnable.from(() -> {
            throw new IndexOutOfBoundsException();
        }).run());

        Assert.expectThrows(IOException.class, () -> ThrowingRunnable.from(() -> {
            throw new IOException();
        }).run());
    }

    /**
     * Tests {@link ThrowingRunnable#guarded(ThrowingConsumer)}.
     */
    @Test
    public void testGuarded() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingRunnable.from(() -> {
            throw new IOException();
        }).guarded(UncheckedException::raise).run());

        Assert.assertThrows(UncheckedException.class, () -> ThrowingRunnable.from(() -> {
            throw new IllegalArgumentException();
        }).guarded(UncheckedException::raise).run());

        Assert.assertThrows(UncheckedException.class, () -> ThrowingRunnable.from(() -> {
            throw new AssertionError();
        }).guarded(UncheckedException::raise).run());

        // Test swallowing the exception

        final ThrowingConsumer<Object, RuntimeException> handler = o -> {
            // Do nothing
        };

        ThrowingRunnable.from(() -> {
            throw new IOException();
        }).guarded(handler).run();

        ThrowingRunnable.from(() -> {
            throw new IllegalArgumentException();
        }).guarded(handler).run();

        ThrowingRunnable.from(() -> {
            throw new AssertionError();
        }).guarded(handler).run();
    }

    /**
     * Tests {@link ThrowingRunnable#guarding(ThrowingRunnable)}.
     */
    @Test
    public void testGuarding() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingRunnable.guarding(() -> {
            throw new IOException();
        }).run());

        Assert.assertThrows(UncheckedException.class, () -> ThrowingRunnable.guarding(() -> {
            throw new IllegalArgumentException();
        }).run());

        Assert.assertThrows(UncheckedException.class, () -> ThrowingRunnable.guarding(() -> {
            throw new AssertionError();
        }).run());
    }

    /**
     * Tests {@link ThrowingRunnable#enclosing(ThrowingRunnable)}.
     */
    @Test
    public void testEnclosing() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingRunnable.enclosing(() -> {
            throw new IOException();
        }).run());

        Assert.assertThrows(UncheckedException.class, () -> ThrowingRunnable.enclosing(() -> {
            throw new IllegalArgumentException();
        }).run());

        Assert.assertThrows(AssertionError.class, () -> ThrowingRunnable.enclosing(() -> {
            throw new AssertionError();
        }).run());
    }

    /**
     * Tests {@link ThrowingRunnable#rethrowing(ThrowingRunnable)}.
     */
    @Test
    public void testRethrowing() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingRunnable.rethrowing(() -> {
            throw new IOException();
        }).run());

        Assert.assertThrows(IllegalArgumentException.class, () -> ThrowingRunnable.rethrowing(() -> {
            throw new IllegalArgumentException();
        }).run());

        Assert.assertThrows(AssertionError.class, () -> ThrowingRunnable.rethrowing(() -> {
            throw new AssertionError();
        }).run());
    }
}
