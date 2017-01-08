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
import java.util.concurrent.atomic.AtomicReference;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.yetamine.lang.exceptions.Throwables;

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
        Assert.assertEquals(choice.isRight(), valid);
        Assert.assertNotEquals(choice.isWrong(), valid);
        Assert.assertEquals(choice, valid ? Choice.right(o) : Choice.wrong(o));
        Assert.assertNotEquals(choice.swap().isRight(), valid);
        Assert.assertEquals(choice.swap().isWrong(), valid);

        if (valid) {
            Assert.assertEquals(choice.optional(), Optional.ofNullable(o));
            Assert.assertSame(choice.require(RuntimeException::new), o);
            Assert.assertSame(choice.require(), o);
        } else {
            Assert.assertEquals(choice.optional(), Optional.empty());
        }
    }

    /**
     * Tests {@link Choice#ifRight(java.util.function.Consumer)}.
     *
     * @param choice
     *            the instance to test. It must not be {@code null}.
     * @param o
     *            the represented value
     * @param valid
     *            the validity flag
     */
    @Test(dataProvider = "values")
    public void testIfRight(Choice<?> choice, Object o, boolean valid) {
        final AtomicReference<Object> box = new AtomicReference<>(new Object());
        choice.ifRight(value -> box.set(value));
        if (valid) {
            Assert.assertSame(box.get(), o);
        } else {
            Assert.assertNotSame(box.get(), o);
        }
    }

    /**
     * Tests {@link Choice#ifWrong(java.util.function.Consumer)}.
     *
     * @param choice
     *            the instance to test. It must not be {@code null}.
     * @param o
     *            the represented value
     * @param valid
     *            the validity flag
     */
    @Test(dataProvider = "values")
    public void testIfWrong(Choice<?> choice, Object o, boolean valid) {
        final AtomicReference<Object> box = new AtomicReference<>(new Object());
        choice.ifWrong(value -> box.set(value));
        if (valid) {
            Assert.assertNotSame(box.get(), o);
        } else {
            Assert.assertSame(box.get(), o);
        }
    }

    /**
     * Tests using.
     *
     * @param choice
     *            the instance to test. It must not be {@code null}.
     * @param o
     *            the represented value
     * @param valid
     *            the validity flag
     */
    @Test(dataProvider = "values")
    public void testUse(Choice<?> choice, Object o, boolean valid) {
        final AtomicReference<Object> box = new AtomicReference<>();
        final Object o1 = new Object();
        final Object o2 = new Object();

        choice.use(value -> {
            Assert.assertSame(value, o);
            box.set(o1);
        }, value -> {
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

        final Choice<Object> c = choice.map(value -> {
            Assert.assertSame(value, o);
            return o1;
        }, value -> {
            Assert.assertSame(value, o);
            return o2;
        });

        final Choice<?> r = c.mapRight(value -> o2);
        final Choice<?> w = c.mapWrong(value -> o1);

        if (valid) {
            Assert.assertSame(c.get(), o1);
            Assert.assertTrue(c.isRight());
            Assert.assertFalse(c.isWrong());

            Assert.assertSame(w.get(), o1);
            Assert.assertTrue(w.isRight());
            Assert.assertFalse(w.isWrong());

            Assert.assertSame(r.get(), o2);
            Assert.assertTrue(r.isRight());
            Assert.assertFalse(r.isWrong());
        } else {
            Assert.assertSame(c.get(), o2);
            Assert.assertTrue(c.isWrong());
            Assert.assertFalse(c.isRight());

            Assert.assertSame(w.get(), o1);
            Assert.assertTrue(w.isWrong());
            Assert.assertFalse(w.isRight());

            Assert.assertSame(r.get(), o2);
            Assert.assertTrue(r.isWrong());
            Assert.assertFalse(r.isRight());
        }
    }

    /**
     * Tests reconciliation.
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
        if (valid) {
            Assert.expectThrows(IOException.class, () -> {
                choice.resolve(value -> Throwables.raise(new IOException()), value -> value);
            });
        } else {
            Assert.expectThrows(IOException.class, () -> {
                choice.resolve(value -> value, value -> Throwables.raise(new IOException()));
            });
        }

        final Object o1 = new Object();
        final Object o2 = new Object();

        final Object r1 = choice.reconcile(value -> {
            Assert.assertSame(value, o);
            return o1;
        }, value -> {
            Assert.assertSame(value, o);
            return o2;
        });

        if (valid) {
            Assert.assertSame(r1, o1);
        } else {
            Assert.assertSame(r1, o2);
        }

        final Object r2 = choice.resolve(value -> {
            Assert.assertSame(value, o);
            return o1;
        }, value -> {
            Assert.assertSame(value, o);
            return o2;
        });

        if (valid) {
            Assert.assertSame(r2, o1);
        } else {
            Assert.assertSame(r2, o2);
        }
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "values")
    public static Object[][] construction() {
        final Object o = new Object();

        return new Object[][] {
            // @formatter:off
            { Choice.right(o),                  o,      true    },
            { Choice.right(null),               null,   true    },

            { Choice.wrong(o),                  o,      false   },
            { Choice.wrong(null),               null,   false   },

            { Choice.nonNull(o),                o,      true    },
            { Choice.nonNull(null),             null,   false   },

            { Choice.of(o, true),               o,      true    },
            { Choice.of(o, false),              o,      false   },
            { Choice.of(null, true),            null,   true    },
            { Choice.of(null, false),           null,   false   },

            { Choice.from(Optional.of(o)),      o,      true    },
            { Choice.from(Optional.empty()),    null,   false   }
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
        Choice.wrong(o).require();
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
        Choice.wrong(o).require(IOException::new);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "throwing")
    public static Object[][] throwing() {
        return new Object[][] { { new Object() }, { null } };
    }
}
