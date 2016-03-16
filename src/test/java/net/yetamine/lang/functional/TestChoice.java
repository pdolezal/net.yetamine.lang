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

package net.yetamine.lang.functional;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.yetamine.lang.containers.Box;

/**
 * Tests {@link Choice}.
 */
public final class TestChoice {

    /**
     * Tests construction methods and basics.
     *
     * @param choice
     *            the instance to test. It must not be {@code null}.
     * @param o
     *            the represented value
     * @param valid
     *            the validity flag
     */
    @Test(dataProvider = "values")
    public void testConstruction(Choice<?> choice, Object o, boolean valid) {
        Assert.assertSame(choice.get(), o);
        Assert.assertEquals(choice.isAccepted(), valid);
        Assert.assertNotEquals(choice.isRejected(), valid);
        Assert.assertEquals(choice, valid ? Choice.accept(o) : Choice.reject(o));
        Assert.assertNotEquals(choice.flip().isAccepted(), valid);
        Assert.assertEquals(choice.flip().isRejected(), valid);

        if (valid) {
            Assert.assertEquals(choice.optional(), Optional.ofNullable(o));
            Assert.assertSame(choice.require(RuntimeException::new), o);
            Assert.assertSame(choice.require(), o);
        } else {
            Assert.assertEquals(choice.optional(), Optional.empty());
        }
    }

    /**
     * Tests {@link Choice#ifAccepted(java.util.function.Consumer)}.
     *
     * @param choice
     *            the instance to test. It must not be {@code null}.
     * @param o
     *            the represented value
     * @param valid
     *            the validity flag
     */
    @Test(dataProvider = "values")
    public void testIfvalid(Choice<?> choice, Object o, boolean valid) {
        final Box<Object> box = Box.of(new Object());
        choice.ifAccepted(value -> box.set(value));
        if (valid) {
            Assert.assertSame(box.get(), o);
        } else {
            Assert.assertNotSame(box.get(), o);
        }
    }

    /**
     * Tests {@link Choice#ifRejected(java.util.function.Consumer)}.
     *
     * @param choice
     *            the instance to test. It must not be {@code null}.
     * @param o
     *            the represented value
     * @param valid
     *            the validity flag
     */
    @Test(dataProvider = "values")
    public void testIfAbsent(Choice<?> choice, Object o, boolean valid) {
        final Box<Object> box = Box.of(new Object());
        choice.ifRejected(value -> box.set(value));
        if (valid) {
            Assert.assertNotSame(box.get(), o);
        } else {
            Assert.assertSame(box.get(), o);
        }
    }

    /**
     * Tests accepting.
     *
     * @param choice
     *            the instance to test. It must not be {@code null}.
     * @param o
     *            the represented value
     * @param valid
     *            the validity flag
     */
    @Test(dataProvider = "values")
    public void testAccept(Choice<?> choice, Object o, boolean valid) {
        final Box<Object> box = Box.empty();
        final Object o1 = new Object();
        final Object o2 = new Object();

        choice.consume(value -> {
            Assert.assertSame(value, o);
            box.set(o1);
        } , value -> {
            Assert.assertSame(value, o);
            box.set(o2);
        });

        if (valid) {
            Assert.assertSame(box.get(), o1);
        } else {
            Assert.assertSame(box.get(), o2);
        }
    }

    /**
     * Tests mapping.
     *
     * @param choice
     *            the instance to test. It must not be {@code null}.
     * @param o
     *            the represented value
     * @param valid
     *            the validity flag
     */
    @Test(dataProvider = "values")
    public void testMap(Choice<?> choice, Object o, boolean valid) {
        final Object o1 = new Object();
        final Object o2 = new Object();

        final Choice<?> r = choice.map(value -> {
            Assert.assertSame(value, o);
            return o1;
        } , value -> {
            Assert.assertSame(value, o);
            return o2;
        });

        if (valid) {
            Assert.assertSame(r.get(), o1);
            Assert.assertTrue(r.isAccepted());
            Assert.assertFalse(r.isRejected());
        } else {
            Assert.assertSame(r.get(), o2);
            Assert.assertTrue(r.isRejected());
            Assert.assertFalse(r.isAccepted());
        }
    }

    /**
     * Tests reconcile.
     *
     * @param choice
     *            the instance to test. It must not be {@code null}.
     * @param o
     *            the represented value
     * @param valid
     *            the validity flag
     */
    @Test(dataProvider = "values")
    public void testReconcile(Choice<?> choice, Object o, boolean valid) {
        final Object o1 = new Object();
        final Object o2 = new Object();

        final Object r = choice.reconcile(value -> {
            Assert.assertSame(value, o);
            return o1;
        } , value -> {
            Assert.assertSame(value, o);
            return o2;
        });

        if (valid) {
            Assert.assertSame(r, o1);
        } else {
            Assert.assertSame(r, o2);
        }
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "values")
    public static Object[][] construction() {
        final Object o = new Object();

        return new Object[][] {
            // @formatter:off
            { Choice.accept(o),             o,      true    },
            { Choice.accept(null),          null,   true    },

            { Choice.reject(o),             o,      false   },
            { Choice.reject(null),          null,   false   },

            { Choice.nonNull(o),            o,      true    },
            { Choice.nonNull(null),         null,   false   },

            { Choice.of(Optional.of(o)),    o,      true    },
            { Choice.of(Optional.empty()),  null,   false   }
            // @formatter:on
        };
    }

    /**
     * Tests {@link Choice#require()}.
     *
     * @param o
     *            the object to reject
     */
    @Test(dataProvider = "throwing", expectedExceptions = { NoSuchElementException.class })
    public void testRequire1(Object o) {
        Choice.reject(o).require();
    }

    /**
     * Tests {@link Choice#require(java.util.function.Supplier)}.
     *
     * @param o
     *            the object to reject
     *
     * @throws IOException
     *             if test succeeds
     */
    @Test(dataProvider = "throwing", expectedExceptions = { IOException.class })
    public void testRequire2(Object o) throws IOException {
        Choice.reject(o).require(IOException::new);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "throwing")
    public static Object[][] throwing() {
        return new Object[][] { { new Object() }, { null } };
    }
}
