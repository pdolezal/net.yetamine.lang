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

package net.yetamine.lang.introspective;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Extensions}.
 */
public final class TestExtensions {

    /**
     * A helper private exception that can't definitely be used elsewhere.
     */
    private static final class ConfirmingException extends RuntimeException {

        /** Serialization version: 1 */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new instance.
         */
        public ConfirmingException() {
            // Default constructor
        }
    }

    // Testing objects
    private final Object a = new Object();
    private final Object b = new Object();
    private final Object c = new Object();

    /**
     * Tests {@link Extensions#empty()}.
     */
    @Test
    public void testEmpty() {
        Assert.assertEquals(Extensions.empty().known(), Collections.emptySet());
        Assert.assertEquals(Extensions.empty(), Extensions.using(Collections.emptySet()));
    }

    /**
     * Tests {@link Extensions#equals(Object)}.
     */
    @Test
    public void testEquals() {
        final Extensions ab = Extensions.declare(a, b);
        Assert.assertEquals(Extensions.declare(a, b), ab);
        Assert.assertEquals(Extensions.declare(a, b).hashCode(), ab.hashCode());

        final Extensions ao = Extensions.declare(a);
        Assert.assertEquals(Extensions.declare(a, a), ao);
        Assert.assertEquals(Extensions.declare(a, a).hashCode(), ao.hashCode());

        Assert.assertNotEquals(ao, ab);
        Assert.assertNotEquals(ao, Extensions.empty());
        Assert.assertNotEquals(ab, Extensions.empty());
    }

    /**
     * Tests construction methods.
     */
    @Test
    public void testConstruction() {
        Assert.assertEquals(Extensions.declare(a), Extensions.from(Stream.of(a)));
        Assert.assertEquals(Extensions.declare(a, b), Extensions.from(Stream.of(a, b)));

        Assert.assertEquals(Extensions.declare(a), Extensions.declare(Arrays.asList(a)));
        Assert.assertEquals(Extensions.declare(a, b), Extensions.declare(Arrays.asList(a, b)));

        final Set<Object> set = new HashSet<>(Arrays.asList(a, b));
        final Extensions extensions = Extensions.using(set);
        Assert.assertEquals(extensions.known(), set);
        Assert.assertTrue(set.remove(b));
        Assert.assertEquals(extensions.known(), set);
    }

    /**
     * Tests single-element conditionals.
     */
    @Test
    public void testConditionals() {
        final Extensions extensions = Extensions.declare(a);

        final Runnable noop = () -> {
            // Do nothing
        };

        Assert.assertTrue(extensions.notPresent(b, Assert::fail));
        Assert.assertTrue(extensions.notMissing(a, Assert::fail));
        Assert.assertSame(extensions.ifPresent(b, Assert::fail), extensions);
        Assert.assertSame(extensions.ifMissing(a, Assert::fail), extensions);

        Assert.assertFalse(extensions.notPresent(a, noop));
        Assert.assertFalse(extensions.notMissing(b, noop));
        Assert.assertSame(extensions.ifPresent(a, noop), extensions);
        Assert.assertSame(extensions.ifMissing(b, noop), extensions);

        // Throwing test

        final Runnable throwing = () -> {
            throw new ConfirmingException();
        };

        Assert.expectThrows(ConfirmingException.class, () -> {
            extensions.notPresent(a, throwing);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            extensions.notMissing(b, throwing);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            extensions.ifPresent(a, throwing);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            extensions.ifMissing(b, throwing);
        });
    }

    /**
     * Tests {@link Extensions#optional(Object)}.
     */
    @Test
    public void testOptional() {
        final Extensions extensions = Extensions.declare(a);
        Assert.assertFalse(extensions.optional(b).isPresent());
        Assert.assertEquals(extensions.optional(a).get(), a);
    }

    /**
     * Tests {@link Extensions#allPresent(Runnable, Object...)}.
     */
    @Test
    public void testAllPresent() {
        final Extensions extensions = Extensions.declare(a, b);

        final Runnable throwing = () -> {
            throw new ConfirmingException();
        };

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.allPresent(throwing, a, b), extensions);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.allPresent(throwing, a), extensions);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.allPresent(throwing, b), extensions);
        });

        Assert.assertSame(extensions.allPresent(throwing, a, b, c), extensions);
        Assert.assertSame(extensions.allPresent(throwing, a, c), extensions);
        Assert.assertSame(extensions.allPresent(throwing, b, c), extensions);
    }

    /**
     * Tests {@link Extensions#anyPresent(Runnable, Object...)}.
     */
    @Test
    public void testAnyPresent() {
        final Extensions extensions = Extensions.declare(a, b);

        final Runnable throwing = () -> {
            throw new ConfirmingException();
        };

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.anyPresent(throwing, a, b, c), extensions);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.anyPresent(throwing, a, b), extensions);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.anyPresent(throwing, a), extensions);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.anyPresent(throwing, b), extensions);
        });

        Assert.assertSame(extensions.anyPresent(throwing, c), extensions);
    }

    /**
     * Tests {@link Extensions#allMissing(Runnable, Object...)}.
     */
    @Test
    public void testAllMissing() {
        final Extensions extensions = Extensions.declare(a);

        final Runnable throwing = () -> {
            throw new ConfirmingException();
        };

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.allMissing(throwing, b, c), extensions);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.allMissing(throwing, b), extensions);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.allMissing(throwing, c), extensions);
        });

        Assert.assertSame(extensions.allMissing(throwing, a, b, c), extensions);
        Assert.assertSame(extensions.allMissing(throwing, a, b), extensions);
        Assert.assertSame(extensions.allMissing(throwing, a, c), extensions);
    }

    /**
     * Tests {@link Extensions#anyMissing(Runnable, Object...)}.
     */
    @Test
    public void testAnyMissing() {
        final Extensions extensions = Extensions.declare(a, b);

        final Runnable throwing = () -> {
            throw new ConfirmingException();
        };

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.anyMissing(throwing, a, b, c), extensions);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.anyMissing(throwing, a, c), extensions);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.anyMissing(throwing, b, c), extensions);
        });

        Assert.expectThrows(ConfirmingException.class, () -> {
            Assert.assertSame(extensions.anyMissing(throwing, c), extensions);
        });

        Assert.assertSame(extensions.anyMissing(throwing, a, b), extensions);
        Assert.assertSame(extensions.anyMissing(throwing, a), extensions);
        Assert.assertSame(extensions.anyMissing(throwing, b), extensions);
    }

    /**
     * Tests serialization of an empty instance.
     *
     * @throws IOException
     *             if a write fails
     * @throws ClassNotFoundException
     *             if class resolution fails
     */
    @Test
    public void testSerialization_Empty() throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream store = new ByteArrayOutputStream();
        try (ObjectOutputStream os = new ObjectOutputStream(store)) {
            os.writeObject(Extensions.empty());
        }

        try (ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(store.toByteArray()))) {
            Assert.assertSame(is.readObject(), Extensions.empty());
        }
    }

    /**
     * Tests serialization of an empty instance.
     *
     * @throws IOException
     *             if a write fails
     * @throws ClassNotFoundException
     *             if class resolution fails
     */
    @Test
    public void testSerialization_Some() throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream store = new ByteArrayOutputStream();
        try (ObjectOutputStream os = new ObjectOutputStream(store)) {
            os.writeObject(Extensions.using(EnumSet.allOf(TestingExtensions.class)));
        }

        try (ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(store.toByteArray()))) {
            Assert.assertEquals(is.readObject(), Extensions.declare(TestingExtensions.TEST1, TestingExtensions.TEST2));
        }
    }

    /**
     * Testing enum for serialization.
     */
    enum TestingExtensions {
        TEST1, TEST2;
    }
}
