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
 * Tests {@link ThrowingConsumer}.
 */
public final class TestThrowingConsumer {

    /**
     * Tests {@link ThrowingConsumer#from(ThrowingConsumer)}.
     */
    @Test
    public void testFrom() {
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> {
            ThrowingConsumer.from((Integer i) -> "".charAt(i)).accept(0);
        });

        Assert.expectThrows(IOException.class, () -> ThrowingConsumer.from(o -> {
            throw new IOException();
        }).accept(null));
    }

    /**
     * Tests {@link ThrowingConsumer#guarded(ThrowingConsumer)}.
     */
    @Test
    public void testGuarded() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingConsumer.from(o -> {
            throw new IOException();
        }).guarded(UncheckedException::raise).accept(null));

        Assert.assertThrows(UncheckedException.class, () -> ThrowingConsumer.from(o -> {
            throw new IllegalArgumentException();
        }).guarded(UncheckedException::raise).accept(null));

        Assert.assertThrows(UncheckedException.class, () -> ThrowingConsumer.from(o -> {
            throw new AssertionError();
        }).guarded(UncheckedException::raise).accept(null));

        // Test swallowing the exception

        final ThrowingConsumer<Object, RuntimeException> handler = o -> {
            // Do nothing
        };

        ThrowingConsumer.from(o -> {
            throw new IOException();
        }).guarded(handler).accept(null);

        ThrowingConsumer.from(o -> {
            throw new IllegalArgumentException();
        }).guarded(handler).accept(null);

        ThrowingConsumer.from(o -> {
            throw new AssertionError();
        }).guarded(handler).accept(null);
    }

    /**
     * Tests {@link ThrowingConsumer#guarding(ThrowingConsumer)}.
     */
    @Test
    public void testGuarding() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingConsumer.guarding(o -> {
            throw new IOException();
        }).accept(null));

        Assert.assertThrows(UncheckedException.class, () -> ThrowingConsumer.guarding(o -> {
            throw new IllegalArgumentException();
        }).accept(null));

        Assert.assertThrows(UncheckedException.class, () -> ThrowingConsumer.guarding(o -> {
            throw new AssertionError();
        }).accept(null));
    }

    /**
     * Tests {@link ThrowingConsumer#enclosing(ThrowingConsumer)}.
     */
    @Test
    public void testEnclosing() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingConsumer.enclosing(o -> {
            throw new IOException();
        }).accept(null));

        Assert.assertThrows(UncheckedException.class, () -> ThrowingConsumer.enclosing(o -> {
            throw new IllegalArgumentException();
        }).accept(null));

        Assert.assertThrows(AssertionError.class, () -> ThrowingConsumer.enclosing(o -> {
            throw new AssertionError();
        }).accept(null));
    }

    /**
     * Tests {@link ThrowingConsumer#rethrowing(ThrowingConsumer)}.
     */
    @Test
    public void testRethrowing() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingConsumer.rethrowing(o -> {
            throw new IOException();
        }).accept(null));

        Assert.assertThrows(IllegalArgumentException.class, () -> ThrowingConsumer.rethrowing(o -> {
            throw new IllegalArgumentException();
        }).accept(null));

        Assert.assertThrows(AssertionError.class, () -> ThrowingConsumer.rethrowing(o -> {
            throw new AssertionError();
        }).accept(null));
    }
}
