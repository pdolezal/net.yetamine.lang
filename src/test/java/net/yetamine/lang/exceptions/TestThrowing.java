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

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
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

        Assert.expectThrows(IllegalArgumentException.class, () -> {
            Throwing.some(new IllegalArgumentException()).then(UncheckedException::rethrow);
        });

        final IOException io = new IOException();
        try {
            Throwing.some(io).then(UncheckedException::rethrow);
            Assert.fail();
        } catch (UncheckedException e) {
            Assert.assertSame(e.getCause(), io);
        }
    }

    /**
     * Test {@link Throwing#maybe(Throwable)}.
     */
    @Test
    public void testMaybe() {
        Throwing.maybe((NullPointerException) null).rethrow();
        Assert.expectThrows(IOException.class, () -> Throwing.some(new IOException()).rethrow());

        Assert.expectThrows(IllegalArgumentException.class, () -> {
            Throwing.maybe(new IllegalArgumentException()).then(UncheckedException::rethrow);
        });

        final IOException io = new IOException();
        try {
            Throwing.maybe(io).then(UncheckedException::rethrow);
            Assert.fail();
        } catch (UncheckedException e) {
            Assert.assertSame(e.getCause(), io);
        }
    }

    /**
     * Test {@link Throwing#none()}.
     */
    @Test
    public void testNone() {
        Throwing.none().map(t -> {
            Assert.fail();
            return new RuntimeException();
        }).rethrow();
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
     * Tests {@link Throwing#throwIfUnchecked()}.
     */
    @Test
    public void testThrowIfUnchecked() {
        Throwing.some(new IOException()).throwIfUnchecked();
        Assert.expectThrows(AssertionError.class, () -> Throwing.some(new AssertionError()).throwIfUnchecked());
        Assert.expectThrows(IllegalArgumentException.class, () -> {
            Throwing.some(new IllegalArgumentException()).throwIfUnchecked();
        });
    }

    /**
     * Tests {@link Throwing#throwAs(java.util.function.Function, Class)}.
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    public void testThrowAs() throws Exception {
        final Function<Object, CloneNotSupportedException> always = o -> new CloneNotSupportedException();
        final Function<Object, RuntimeException> never = o -> null;

        Throwing.some(new IOException()).throwAs(never, RuntimeException.class);
        Throwing.some(new IOException()).throwAs(always, RuntimeException.class);

        Throwing.some(new EOFException()).throwAs(never, IOException.class);
        Assert.expectThrows(CloneNotSupportedException.class, () -> {
            Throwing.some(new EOFException()).throwAs(always, IOException.class);
        });

        Assert.expectThrows(UncheckedIOException.class, () -> {
            Throwing.some(new EOFException()).throwAs(UncheckedIOException::new);
        });

        Throwing.maybe(new IOException()).throwAs(never, RuntimeException.class);
        Throwing.maybe(new IOException()).throwAs(always, RuntimeException.class);
        Throwing.maybe(null).throwAs(never, RuntimeException.class);
        Throwing.maybe(null).throwAs(always, RuntimeException.class);
        Throwing.maybe(new EOFException()).throwAs(never, IOException.class);
        Assert.expectThrows(CloneNotSupportedException.class, () -> {
            Throwing.maybe(new EOFException()).throwAs(always, IOException.class);
        });

        Throwing.cause(new IOException()).throwAs(never, RuntimeException.class);
        Throwing.cause(new IOException()).throwAs(always, RuntimeException.class);
        Throwing.cause(new RuntimeException(new EOFException())).throwAs(never, RuntimeException.class);
        Throwing.cause(new RuntimeException(new EOFException())).throwAs(always, RuntimeException.class);
        Throwing.cause(new RuntimeException(new EOFException())).throwAs(never, IOException.class);
        Assert.expectThrows(CloneNotSupportedException.class, () -> {
            Throwing.cause(new RuntimeException(new EOFException())).throwAs(always, IOException.class);
        });
    }

    /**
     * Tests {@link Throwing#map(Function)}.
     */
    @Test
    public void testMap() {
        final ArithmeticException a = new ArithmeticException();
        try {
            Throwing.some(a).map(IOException::new).rethrow();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertSame(e.getCause(), a);
        }
    }

    /**
     * Tests {@link Throwing#guard(ThrowingRunnable)}.
     */
    @Test
    public void testGuard() {
        final Throwing<?> t = Throwing.guard(() -> {
            throw new IOException();
        });

        Assert.assertThrows(IOException.class, t::rethrow);
        Assert.assertThrows(NullPointerException.class, () -> Throwing.guard(null));
        Assert.assertThrows(AssertionError.class, () -> Throwing.guard(() -> {
            throw new AssertionError();
        }));
    }

    /**
     * Tests {@link Throwing#sandbox(ThrowingRunnable)}.
     */
    @Test
    public void testSandbox() {
        final Throwing<?> s1 = Throwing.sandbox(() -> {
            throw new IOException();
        });

        Assert.assertThrows(IOException.class, s1::rethrow);
        Assert.assertThrows(NullPointerException.class, () -> Throwing.sandbox(null));

        final Throwing<?> s2 = Throwing.sandbox(() -> {
            throw new AssertionError();
        });

        Assert.assertThrows(AssertionError.class, s2::rethrow);
    }

    /**
     * Tests {@link Throwing#anyway(Runnable)}.
     */
    @Test
    public void testAnyway() {
        final IOException io = new IOException();
        try {
            Throwing.some(io).anyway(() -> {
                throw new IllegalStateException();
            }).anyway(Assert::fail);

            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertSame(e.getSuppressed()[0], io);
        }
    }
}
