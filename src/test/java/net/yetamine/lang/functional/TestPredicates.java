package net.yetamine.lang.functional;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests {@link Predicates}.
 */
public final class TestPredicates {

    /**
     * Tests {@link Predicates#and(Iterable)}.
     *
     * @param predicates
     *            the predicates
     */
    @Test(dataProvider = "predicates")
    public void testAnd(Iterable<? extends Predicate<Object>> predicates) {
        Iterator<? extends Predicate<Object>> it = predicates.iterator();
        Predicate<Object> a = it.next();
        while (it.hasNext()) {
            a = a.and(it.next());
        }

        final Object o = new Object();
        Assert.assertEquals(Predicates.and(predicates).test(o), a.test(o));
    }

    /**
     * Tests {@link Predicates#or(Iterable)}.
     *
     * @param predicates
     *            the predicates
     */
    @Test(dataProvider = "predicates")
    public void testOr(Iterable<? extends Predicate<Object>> predicates) {
        Iterator<? extends Predicate<Object>> it = predicates.iterator();
        Predicate<Object> a = it.next();
        while (it.hasNext()) {
            a = a.or(it.next());
        }

        final Object o = new Object();
        Assert.assertEquals(Predicates.or(predicates).test(o), a.test(o));
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "predicates")
    public static Object[][] predicate() {
        final Predicate<Object> t = o -> true;
        final Predicate<Object> f = o -> false;

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
