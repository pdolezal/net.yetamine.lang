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
 * Tests {@link ThrowingCallable}.
 */
public final class TestThrowingCallable {

    /**
     * Tests {@link ThrowingCallable#from(ThrowingCallable)}.
     */
    @Test
    public void testFrom() {
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> ThrowingCallable.from(() -> "".charAt(0)).call());

        Assert.expectThrows(IOException.class, () -> ThrowingCallable.from(() -> {
            throw new IOException();
        }).call());
    }

    /**
     * Tests {@link ThrowingCallable#guarding(ThrowingCallable)}.
     */
    @Test
    public void testGuarding() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingCallable.guarding(() -> {
            throw new IOException();
        }).get());

        Assert.assertThrows(UncheckedException.class, () -> ThrowingCallable.guarding(() -> {
            throw new IllegalArgumentException();
        }).get());

        Assert.assertThrows(UncheckedException.class, () -> ThrowingCallable.guarding(() -> {
            throw new AssertionError();
        }).get());
    }

    /**
     * Tests {@link ThrowingCallable#enclosing(ThrowingCallable)}.
     */
    @Test
    public void testEnclosing() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingCallable.enclosing(() -> {
            throw new IOException();
        }).get());

        Assert.assertThrows(UncheckedException.class, () -> ThrowingCallable.enclosing(() -> {
            throw new IllegalArgumentException();
        }).get());

        Assert.assertThrows(AssertionError.class, () -> ThrowingCallable.enclosing(() -> {
            throw new AssertionError();
        }).get());
    }

    /**
     * Tests {@link ThrowingCallable#rethrowing(ThrowingCallable)}.
     */
    @Test
    public void testRethrowing() {
        Assert.assertThrows(UncheckedException.class, () -> ThrowingCallable.rethrowing(() -> {
            throw new IOException();
        }).get());

        Assert.assertThrows(IllegalArgumentException.class, () -> ThrowingCallable.rethrowing(() -> {
            throw new IllegalArgumentException();
        }).get());

        Assert.assertThrows(AssertionError.class, () -> ThrowingCallable.rethrowing(() -> {
            throw new AssertionError();
        }).get());
    }
}
