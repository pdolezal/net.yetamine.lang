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

package net.yetamine.lang.containers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests {@link Tuple3}.
 */
public final class TestTuple3 {

    /**
     * Tests construction methods.
     */
    @Test
    public void testConstruction() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Object o3 = new Object();

        final Tuple3<?, ?, ?> t = Tuple3.of(o1, o2, o3);
        Assert.assertEquals(t.get1(), o1);
        Assert.assertEquals(t.get2(), o2);
        Assert.assertEquals(t.get3(), o3);

        Assert.assertEquals(Tuple3.empty(), Tuple3.of(null, null, null));
        Assert.assertNull(Tuple3.empty().get1());
        Assert.assertNull(Tuple3.empty().get2());
        Assert.assertNull(Tuple3.empty().get3());

        Assert.assertEquals(Tuple3.from(Arrays.asList(o1, o2, o3)), t);
    }

    /**
     * Tests {@link Tuple3#from(Iterable)} with short sources.
     *
     * @param source
     *            the source for test. It must not be {@code null}.
     */
    @Test(expectedExceptions = { NoSuchElementException.class }, dataProvider = "iterables")
    public void testFromFailure(Iterable<?> source) {
        Tuple3.from(source);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "iterables")
    public static Object[][] iterables() {
        return new Object[][] {
// @formatter:off
            { Collections.emptyList()               },
            { Collections.singleton(new Object())   },
            { Arrays.asList(1, 2)                   }
            // @formatter:on
        };
    }

    /**
     * Tests {@link Tuple3#equals(Object)} and {@link Tuple3#hashCode()}.
     */
    @Test
    public void testEquals() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Object o3 = new Object();

        final Tuple3<?, ?, ?> t = Tuple3.of(o1, o2, o3);

        Assert.assertEquals(Tuple3.of(o1, o2, o3), t);
        Assert.assertNotEquals(Tuple3.empty(), t);
        Assert.assertNotEquals(t, Tuple3.empty());
        Assert.assertNotEquals(t, Tuple3.of(o2, o1, o3));

        Assert.assertEquals(Tuple3.of(o1, o2, o3).hashCode(), t.hashCode());

        Assert.assertEquals(Tuple3.empty(), Tuple3.of(null, null, null));
        Assert.assertEquals(Tuple3.of(null, null, null), Tuple3.empty());

        Assert.assertEquals(Tuple3.of(null, null, null).hashCode(), Tuple3.empty().hashCode());
    }

    /**
     * Tests "setting" methods.
     */
    @Test
    public void testSet() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Object o3 = new Object();
        final Object o4 = new Object();

        final Tuple3<?, ?, ?> t = Tuple3.of(o1, o2, o3);

        Assert.assertEquals(t.set1(o4), Tuple3.of(o4, o2, o3));
        Assert.assertEquals(t.set2(o4), Tuple3.of(o1, o4, o3));
        Assert.assertEquals(t.set3(o4), Tuple3.of(o1, o2, o4));

        Assert.assertEquals(t, Tuple3.of(o1, o2, o3));
    }

    /**
     * Tests mapping methods.
     */
    @Test
    public void testMap() {
        final Function<Integer, Long> f = i -> Long.valueOf(i + 10);

        final Tuple3<Integer, Integer, Integer> t = Tuple3.of(1, 2, 3);
        Assert.assertEquals(t.map1(f), Tuple3.of(11L, 2, 3));
        Assert.assertEquals(t.map2(f), Tuple3.of(1, 12L, 3));
        Assert.assertEquals(t.map3(f), Tuple3.of(1, 2, 13L));

        Assert.assertEquals(t, Tuple3.of(1, 2, 3));
    }

    /**
     * Tests "using" methods.
     */
    @Test
    public void testUse() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Object o3 = new Object();

        final Tuple3<?, ?, ?> t = Tuple3.of(o1, o2, o3);

        t.use1(o -> Assert.assertEquals(o, o1));
        t.use2(o -> Assert.assertEquals(o, o2));
        t.use3(o -> Assert.assertEquals(o, o3));

        Assert.assertEquals(t, Tuple3.of(o1, o2, o3));
    }

    /**
     * Tests projectsion to {@link Tuple2}.
     */
    @Test
    public void testProjects() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Object o3 = new Object();

        final Tuple3<?, ?, ?> t = Tuple3.of(o1, o2, o3);

        Assert.assertEquals(t.head(), Tuple2.of(o1, o2));
        Assert.assertEquals(t.tail(), Tuple2.of(o2, o3));
        Assert.assertEquals(t.outer(), Tuple2.of(o1, o3));
    }

    /**
     * Tests of zipping methods.
     *
     * @param items
     *            the list of items to zip. It must not be {@code null}.
     */
    @Test(dataProvider = "collections")
    public void testZip(List<Integer> items) {
        // Make a list of indices (shifted), but make it longer to test sources with different lengths
        final List<Integer> i = Stream.iterate(+1, v -> v + 1).limit(items.size() + 1).collect(Collectors.toList());
        final List<Integer> j = Stream.iterate(-1, v -> v - 1).limit(items.size() + 1).collect(Collectors.toList());

        // Make the expected list of items
        final List<Tuple3<Integer, Integer, Integer>> zipped = new ArrayList<>();
        items.forEach(item -> zipped.add(Tuple3.of(+1 + zipped.size(), -1 - zipped.size(), item)));

        // Test with iterators
        final List<Tuple3<Integer, Integer, Integer>> z1 = new ArrayList<>();
        Tuple3.zip(i.iterator(), j.iterator(), items.iterator()).forEachRemaining(z1::add);
        Assert.assertEquals(z1, zipped);

        // Test with iterables
        final List<Tuple3<Integer, Integer, Integer>> z2 = new ArrayList<>();
        Tuple3.zip(i, j, items).forEach(z2::add);
        Assert.assertEquals(z2, zipped);

        // Test with streams
        Assert.assertEquals(Tuple3.zip(i.stream(), j.stream(), items.stream()).collect(Collectors.toList()), zipped);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "collections")
    public static Object[][] collections() {
        return new Object[][] {
// @formatter:off
            { Collections.emptyList()       },
            { Collections.singletonList(10) },
            { Arrays.asList(10, 11, 12)     }
            // @formatter:on
        };
    }
}
