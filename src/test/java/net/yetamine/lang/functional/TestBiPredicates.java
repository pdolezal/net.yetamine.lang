package net.yetamine.lang.functional;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiPredicate;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests {@link BiPredicates}.
 */
public final class TestBiPredicates {

    /**
     * Tests {@link BiPredicates#and(Iterable)}.
     *
     * @param predicates
     *            the predicates
     */
    @Test(dataProvider = "predicates")
    public void testAnd(Iterable<? extends BiPredicate<Object, Object>> predicates) {
        Iterator<? extends BiPredicate<Object, Object>> it = predicates.iterator();
        BiPredicate<Object, Object> a = it.next();
        while (it.hasNext()) {
            a = a.and(it.next());
        }

        final Object o1 = new Object();
        final Object o2 = new Object();
        Assert.assertEquals(BiPredicates.and(predicates).test(o1, o2), a.test(o1, o2));
    }

    /**
     * Tests {@link BiPredicates#or(Iterable)}.
     *
     * @param predicates
     *            the predicates
     */
    @Test(dataProvider = "predicates")
    public void testOr(Iterable<? extends BiPredicate<Object, Object>> predicates) {
        Iterator<? extends BiPredicate<Object, Object>> it = predicates.iterator();
        BiPredicate<Object, Object> a = it.next();
        while (it.hasNext()) {
            a = a.and(it.next());
        }

        final Object o1 = new Object();
        final Object o2 = new Object();
        Assert.assertEquals(BiPredicates.and(predicates).test(o1, o2), a.test(o1, o2));
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "predicates")
    public static Object[][] predicate() {
        final BiPredicate<Object, Object> t = (o1, o2) -> true;
        final BiPredicate<Object, Object> f = (o1, o2) -> false;

        return new Object[][] {
            // @formatter:off
            { Arrays.asList(t, t, t) },
            { Arrays.asList(t, t, f) },
            { Arrays.asList(t, f, f) },
            { Arrays.asList(f, f, f) },
            { Arrays.asList(f, f, t) },
            { Arrays.asList(f, t, t) },
            { Arrays.asList(t, f, t) },
            { Arrays.asList(f, t, f) },
            // @formatter:on
        };
    }
}
