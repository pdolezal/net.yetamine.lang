package net.yetamine.lang.containers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests {@link Tuple2}.
 */
public final class TestTuple2 {

    /**
     * Tests construction methods.
     */
    @Test
    public void testConstruction() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Tuple2<?, ?> t = Tuple2.of(o1, o2);
        Assert.assertEquals(t.get1(), o1);
        Assert.assertEquals(t.get2(), o2);

        Assert.assertEquals(Tuple2.empty(), Tuple2.of(null, null));
        Assert.assertNull(Tuple2.empty().get1());
        Assert.assertNull(Tuple2.empty().get2());

        final Map<Object, Object> m = Collections.singletonMap(o1, o2);
        final Map.Entry<?, ?> e = m.entrySet().iterator().next();
        final Tuple2<?, ?> u = Tuple2.from(e);
        Assert.assertEquals(o1, u.get1());
        Assert.assertEquals(o2, u.get2());
        Assert.assertEquals(u, t);

        Assert.assertEquals(Tuple2.from(Arrays.asList(o1, o2)), t);
    }

    /**
     * Tests {@link Tuple2#from(Iterable)} with short sources.
     *
     * @param source
     *            the source for test. It must not be {@code null}.
     */
    @Test(expectedExceptions = { NoSuchElementException.class }, dataProvider = "iterables")
    public void testFromFailure(Iterable<?> source) {
        Tuple2.from(source);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "iterables")
    public static Object[][] iterables() {
        return new Object[][] { { Collections.emptyList() }, { Collections.singleton(new Object()) } };
    }

    /**
     * Tests {@link Tuple2#equals(Object)} and {@link Tuple2#hashCode()}.
     */
    @Test
    public void testEquals() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Tuple2<?, ?> t = Tuple2.of(o1, o2);

        Assert.assertEquals(Tuple2.of(o1, o2), t);
        Assert.assertNotEquals(Tuple2.empty(), t);
        Assert.assertNotEquals(t, Tuple2.empty());
        Assert.assertNotEquals(t, Tuple2.of(o2, o1));

        Assert.assertEquals(Tuple2.of(o1, o2).hashCode(), t.hashCode());

        Assert.assertEquals(Tuple2.empty(), Tuple2.of(null, null));
        Assert.assertEquals(Tuple2.of(null, null), Tuple2.empty());

        Assert.assertEquals(Tuple2.of(null, null).hashCode(), Tuple2.empty().hashCode());
    }

    /**
     * Tests {@link Tuple2#set1(Object)} and {@link Tuple2#set2(Object)}.
     */
    @Test
    public void testSet() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Object o3 = new Object();

        final Tuple2<?, ?> t = Tuple2.of(o1, o2);

        Assert.assertEquals(t.set1(o3), Tuple2.of(o3, o2));
        Assert.assertEquals(t.set2(o3), Tuple2.of(o1, o3));

        Assert.assertEquals(t, Tuple2.of(o1, o2));
    }

    /**
     * Tests {@link Tuple2#map1(Function)} and {@link Tuple2#map2(Function)}.
     */
    @Test
    public void testMap() {
        final Object o1 = new Object();
        final Object o2 = new Object();

        final Integer i = 1;
        final Function<Object, Integer> f = o -> i;

        final Tuple2<?, ?> t = Tuple2.of(o1, o2);

        Assert.assertEquals(t.map1(f), Tuple2.of(i, o2));
        Assert.assertEquals(t.map2(f), Tuple2.of(o1, i));

        Assert.assertEquals(t, Tuple2.of(o1, o2));
    }

    /**
     * Tests {@link Tuple2#use1(java.util.function.Consumer)} and
     * {@link Tuple2#use2(java.util.function.Consumer)}.
     */
    @Test
    public void testUse() {
        final Object o1 = new Object();
        final Object o2 = new Object();

        final Tuple2<?, ?> t = Tuple2.of(o1, o2);

        t.use1(o -> Assert.assertEquals(o, o1));
        t.use2(o -> Assert.assertEquals(o, o2));

        Assert.assertEquals(t, Tuple2.of(o1, o2));
    }

    /**
     * Tests {@link Tuple2#reduce(BiFunction)}.
     */
    @Test
    public void testReduce() {
        final Object o1 = new Object();
        final Object o2 = new Object();

        final Tuple2<?, ?> t = Tuple2.of(o1, o2);

        Assert.assertEquals(t.reduce((v1, v2) -> {
            Assert.assertEquals(v1, o1);
            Assert.assertEquals(v2, o2);
            return Integer.valueOf(1);
        }), Integer.valueOf(1));

        Assert.assertEquals(t, Tuple2.of(o1, o2));
    }

    /**
     * Tests {@link Tuple2#swap()}.
     */
    @Test
    public void testSwap() {
        final Object o1 = new Object();
        final Object o2 = new Object();

        final Tuple2<?, ?> t = Tuple2.of(o1, o2);

        Assert.assertEquals(t.swap(), Tuple2.of(o2, o1));
    }

    /**
     * Tests of insertion methods.
     */
    @Test
    public void testInsertions() {
        final Object o1 = new Object();
        final Object o2 = new Object();
        final Object o3 = new Object();

        final Tuple2<?, ?> t = Tuple2.of(o1, o2);

        Assert.assertEquals(t.prepend(o3), Tuple3.of(o3, o1, o2));
        Assert.assertEquals(t.insert(o3), Tuple3.of(o1, o3, o2));
        Assert.assertEquals(t.append(o3), Tuple3.of(o1, o2, o3));
    }

    /**
     * Tests of zipping methods.
     *
     * @param items
     *            the list of items to zip. It must not be {@code null}.
     */
    @Test(dataProvider = "collections")
    public void testZip(List<Integer> items) {
        // Make a list of indices, but make it longer to test sources with different lengths
        final List<Integer> i = Stream.iterate(0, v -> v + 1).limit(items.size() + 1).collect(Collectors.toList());

        // Make the expected list of items
        final List<Tuple2<Integer, Integer>> zipped = new ArrayList<>();
        items.forEach(item -> zipped.add(Tuple2.of(zipped.size(), item)));

        // Test with iterators
        final List<Tuple2<Integer, Integer>> z1 = new ArrayList<>();
        Tuple2.zip(i.iterator(), items.iterator()).forEachRemaining(z1::add);
        Assert.assertEquals(z1, zipped);

        // Test with iterables
        final List<Tuple2<Integer, Integer>> z2 = new ArrayList<>();
        Tuple2.zip(i, items).forEach(z2::add);
        Assert.assertEquals(z2, zipped);

        // Test with streams
        Assert.assertEquals(Tuple2.zip(i.stream(), items.stream()).collect(Collectors.toList()), zipped);
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
