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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.yetamine.lang.Trivalent;

/**
 * Tests {@link Single}.
 */
public final class TestSingle {

    /**
     * Tests construction methods.
     */
    @Test
    public void testConstruction() {
        Assert.assertNull(Single.none().get());
        Assert.assertNull(Single.some(null).get());
        Assert.assertNull(Single.single(null).get());

        Assert.assertEquals(Single.none().single(), Trivalent.UNKNOWN);
        Assert.assertEquals(Single.some(null).single(), Trivalent.FALSE);
        Assert.assertEquals(Single.single(null).single(), Trivalent.TRUE);

        final Object o = new Object();

        Assert.assertSame(Single.single(o).get(), o);
        Assert.assertSame(Single.some(o).get(), o);

        Assert.assertEquals(Single.some(o).single(), Trivalent.FALSE);
        Assert.assertEquals(Single.single(o).single(), Trivalent.TRUE);
    }

    /**
     * Tests {@link Single#optional()}.
     */
    @Test
    public void testOptional() {
        final Object o = new Object();

        Assert.assertFalse(Single.none().optional().isPresent());
        Assert.assertFalse(Single.some(null).optional().isPresent());
        Assert.assertFalse(Single.some(o).optional().isPresent());

        Assert.assertFalse(Single.single(null).optional().isPresent());
        Assert.assertSame(Single.single(o).optional().get(), o);
    }

    /**
     * Tests {@link Single#update(Object)}.
     */
    @Test
    public void testUpdateValue() {
        final Object o = new Object();

        final Single<Object> single = Single.none().update(o);
        Assert.assertEquals(single, Single.single(o));

        final Single<Object> some = single.update(new Object());
        Assert.assertEquals(some, Single.some(o));
        Assert.assertEquals(some.update(new Object()), Single.some(o));
    }

    /**
     * Tests {@link Single#update()}.
     */
    @Test
    public void testUpdate() {
        final Single<Object> single = Single.none().update();
        Assert.assertEquals(single, Single.single(null));

        final Single<Object> some = single.update();
        Assert.assertEquals(some, Single.some(null));
        Assert.assertEquals(some.update(), Single.some(null));
    }

    /**
     * Tests {@link Single#accept(Object)}.
     */
    @Test
    public void testAccept() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Object o3 = new Object();

        final Single<Object> single = Single.none().accept(o1);
        Assert.assertEquals(single, Single.single(o1));

        final Single<Object> some = single.accept(o2);
        Assert.assertEquals(some, Single.some(o2));
        Assert.assertEquals(some.accept(o3), Single.some(o3));
    }

    /**
     * Tests {@link Single#merge(Single)}.
     *
     * @param that
     *            the object to merge. It must not be {@code null}.
     * @param operand
     *            the object to merge with. It must not be {@code null}.
     * @param expected
     *            the expected outcome. It must not be {@code null}.
     */
    @Test(dataProvider = "merging")
    public void testMerge(Single<Object> that, Single<?> operand, Single<Object> expected) {
        Assert.assertEquals(that.merge(operand), expected);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "merging")
    public static Object[][] merging() {
        return new Object[][] {
            // @formatter:off
            { Single.none(),    Single.none(),      Single.none()       },
            { Single.none(),    Single.single(1),   Single.single(1)    },
            { Single.none(),    Single.some(1),     Single.some(1)      },

            { Single.single(1), Single.none(),      Single.single(1)    },
            { Single.single(1), Single.single(2),   Single.some(1)      },
            { Single.single(1), Single.some(2),     Single.some(1)      },

            { Single.some(1),   Single.none(),      Single.some(1)      },
            { Single.some(1),   Single.single(2),   Single.some(1)      },
            { Single.some(1),   Single.some(2),     Single.some(1)      }
            // @formatter:on
        };
    }

    /**
     * Tests "head" methods.
     *
     * @param source
     *            the source of elements. It must not be {@code null}.
     * @param expected
     *            the expected result
     */
    @Test(dataProvider = "head")
    public void testHead(Collection<?> source, Single<?> expected) {
        Assert.assertEquals(Single.head(source.iterator()), expected);
        Assert.assertEquals(Single.head(source.stream()), expected);
        Assert.assertEquals(Single.head(source), expected);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "head")
    public static Object[][] head() {
        return new Object[][] {
            // @formatter:off
            { Collections.emptyList(),  Single.none()       },
            { Collections.singleton(1), Single.single(1)    },
            { Arrays.asList(1, 2),      Single.some(1)      },
            { Arrays.asList(1, 2, 3),   Single.some(1)      }
            // @formatter:on
        };
    }

    /**
     * Tests "last" methods.
     *
     * @param source
     *            the source of elements. It must not be {@code null}.
     * @param expected
     *            the expected result
     */
    @Test(dataProvider = "last")
    public void testLast(List<?> source, Single<?> expected) {
        Assert.assertEquals(Single.last(source.iterator()), expected);
        Assert.assertEquals(Single.last(source.stream()), expected);
        Assert.assertEquals(Single.last((Iterable<?>) source), expected);
        Assert.assertEquals(Single.last(source), expected);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "last")
    public static Object[][] last() {
        return new Object[][] {
            // @formatter:off
            { Collections.emptyList(),      Single.none()       },
            { Collections.singletonList(1), Single.single(1)    },
            { Arrays.asList(1, 2),          Single.some(2)      },
            { Arrays.asList(1, 2, 3),       Single.some(3)      }
            // @formatter:on
        };
    }

    /**
     * Tests {@link Single#collector(java.util.function.BiFunction)} by finding
     * the maximum of the source, indicating if this the only occurrence.
     *
     * <p>
     * This test demonstrates a more general pattern; actually, this case could
     * be implemented with the reduce operation as shown below, but it requires
     * more effort.
     *
     * @param <T>
     *            the type of the elements
     * @param source
     *            the source of elements. It must not be {@code null}.
     * @param expected
     *            the expected result
     */
    @Test(dataProvider = "numbers")
    public <T extends Comparable<T>> void testCollector(Collection<T> source, Single<T> expected) {
        final BiFunction<Single<T>, T, Single<T>> max = Single.optimum(Comparator.<T> naturalOrder());
        Assert.assertEquals(source.stream().collect(Single.collector(max)), expected);
        Assert.assertEquals(source.stream().reduce(Single.none(), max, Single::merge), expected);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "numbers")
    public static Object[][] numbers() {
        return new Object[][] {
            // @formatter:off
            { Collections.emptyList(),  Single.none()       },
            { Collections.singleton(1), Single.single(1)    },
            { Arrays.asList(1, 1),      Single.some(1)      },

            { Arrays.asList(2, 1, 1),   Single.single(2)    },
            { Arrays.asList(1, 2, 1),   Single.single(2)    },
            { Arrays.asList(1, 1, 2),   Single.single(2)    },

            { Arrays.asList(2, 2, 1),   Single.some(2)      },
            { Arrays.asList(1, 2, 2),   Single.some(2)      },
            { Arrays.asList(2, 1, 2),   Single.some(2)      }
            // @formatter:on
        };
    }
}
