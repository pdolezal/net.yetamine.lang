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

package net.yetamine.lang;

import java.io.EOFException;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Throwing}.
 */
public final class TestThrowing {

    /**
     * Test {@link Throwing#some(Throwable)}.
     */
    @Test
    public void testSome() {
        Assert.expectThrows(NullPointerException.class, () -> Throwing.some(null));
        Assert.expectThrows(IOException.class, () -> Throwing.some(new IOException()).rethrow());
    }

    /**
     * Test {@link Throwing#maybe(Throwable)}.
     */
    @Test
    public void testMaybe() {
        Throwing.maybe((NullPointerException) null).rethrow();
        Assert.expectThrows(IOException.class, () -> Throwing.some(new IOException()).rethrow());
    }

    /**
     * Test {@link Throwing#cause(Throwable)}.
     *
     * @throws Throwable
     *             if something goes wrong
     */
    @Test
    public void testCause() throws Throwable {
        Assert.expectThrows(NullPointerException.class, () -> Throwing.cause(null));
        Assert.expectThrows(IOException.class, () -> Throwing.cause(new RuntimeException(new IOException())).rethrow());
        Throwing.cause(new RuntimeException()).rethrow(); // No cause, no throw
    }

    /**
     * Tests {@link Throwing#throwIf(Class)}.
     */
    @Test
    public void testThrowIf_1() {
        Throwing.some(new IOException()).throwIf(RuntimeException.class);
        Assert.expectThrows(EOFException.class, () -> Throwing.some(new EOFException()).throwIf(IOException.class));

        Throwing.maybe(new IOException()).throwIf(RuntimeException.class);
        Throwing.maybe(null).throwIf(RuntimeException.class);
        Assert.expectThrows(EOFException.class, () -> Throwing.maybe(new EOFException()).throwIf(IOException.class));

        Throwing.cause(new IOException()).throwIf(RuntimeException.class);
        Throwing.cause(new RuntimeException(new EOFException())).throwIf(RuntimeException.class);
        Assert.expectThrows(EOFException.class, () -> {
            Throwing.cause(new RuntimeException(new EOFException())).throwIf(IOException.class);
        });
    }

    /**
     * Tests {@link Throwing#throwIf(Class, java.util.function.Predicate)}.
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    public void testThrowIf_2() throws Exception {
        final Predicate<Object> always = o -> true;
        final Predicate<Object> never = o -> false;

        Throwing.some(new IOException()).throwIf(RuntimeException.class, never);
        Throwing.some(new IOException()).throwIf(RuntimeException.class, always);

        Throwing.some(new EOFException()).throwIf(IOException.class, never);
        Assert.expectThrows(EOFException.class, () -> {
            Throwing.some(new EOFException()).throwIf(IOException.class, always);
        });

        Throwing.maybe(new IOException()).throwIf(RuntimeException.class, never);
        Throwing.maybe(new IOException()).throwIf(RuntimeException.class, always);
        Throwing.maybe(null).throwIf(RuntimeException.class, never);
        Throwing.maybe(null).throwIf(RuntimeException.class, always);
        Throwing.maybe(new EOFException()).throwIf(IOException.class, never);
        Assert.expectThrows(EOFException.class, () -> {
            Throwing.maybe(new EOFException()).throwIf(IOException.class, always);
        });

        Throwing.cause(new IOException()).throwIf(RuntimeException.class, never);
        Throwing.cause(new IOException()).throwIf(RuntimeException.class, always);
        Throwing.cause(new RuntimeException(new EOFException())).throwIf(RuntimeException.class, never);
        Throwing.cause(new RuntimeException(new EOFException())).throwIf(RuntimeException.class, always);
        Throwing.cause(new RuntimeException(new EOFException())).throwIf(IOException.class, never);
        Assert.expectThrows(EOFException.class, () -> {
            Throwing.cause(new RuntimeException(new EOFException())).throwIf(IOException.class, always);
        });
    }

    /**
     * Tests {@link Throwing#throwAs(Class, java.util.function.Function)}.
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    public void testThrowAs() throws Exception {
        final Function<Object, CloneNotSupportedException> always = o -> new CloneNotSupportedException();
        final Function<Object, RuntimeException> never = o -> null;

        Throwing.some(new IOException()).throwAs(RuntimeException.class, never);
        Throwing.some(new IOException()).throwAs(RuntimeException.class, always);

        Throwing.some(new EOFException()).throwAs(IOException.class, never);
        Assert.expectThrows(CloneNotSupportedException.class, () -> {
            Throwing.some(new EOFException()).throwAs(IOException.class, always);
        });

        Throwing.maybe(new IOException()).throwAs(RuntimeException.class, never);
        Throwing.maybe(new IOException()).throwAs(RuntimeException.class, always);
        Throwing.maybe(null).throwAs(RuntimeException.class, never);
        Throwing.maybe(null).throwAs(RuntimeException.class, always);
        Throwing.maybe(new EOFException()).throwAs(IOException.class, never);
        Assert.expectThrows(CloneNotSupportedException.class, () -> {
            Throwing.maybe(new EOFException()).throwAs(IOException.class, always);
        });

        Throwing.cause(new IOException()).throwAs(RuntimeException.class, never);
        Throwing.cause(new IOException()).throwAs(RuntimeException.class, always);
        Throwing.cause(new RuntimeException(new EOFException())).throwAs(RuntimeException.class, never);
        Throwing.cause(new RuntimeException(new EOFException())).throwAs(RuntimeException.class, always);
        Throwing.cause(new RuntimeException(new EOFException())).throwAs(IOException.class, never);
        Assert.expectThrows(CloneNotSupportedException.class, () -> {
            Throwing.cause(new RuntimeException(new EOFException())).throwAs(IOException.class, always);
        });
    }
}
