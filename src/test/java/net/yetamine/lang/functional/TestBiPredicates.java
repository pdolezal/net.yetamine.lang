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
import java.util.Collections;
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
     * Tests {@link BiPredicates#allOf(Iterable)}.
     *
     * @param predicates
     *            the predicates
     */
    @Test(dataProvider = "predicates")
    public void testAllOf(Iterable<? extends BiPredicate<Object, Object>> predicates) {
        Assert.assertTrue(BiPredicates.allOf(Collections.emptyList()).test(null, null));

        Iterator<? extends BiPredicate<Object, Object>> it = predicates.iterator();
        BiPredicate<Object, Object> a = it.next();
        while (it.hasNext()) {
            a = a.and(it.next());
        }

        final Object o1 = new Object();
        final Object o2 = new Object();
        Assert.assertEquals(BiPredicates.allOf(predicates).test(o1, o2), a.test(o1, o2));
    }

    /**
     * Tests {@link BiPredicates#noneOf(Iterable)}.
     *
     * @param predicates
     *            the predicates
     */
    @Test(dataProvider = "predicates")
    public void testNoneOf(Iterable<? extends BiPredicate<Object, Object>> predicates) {
        Assert.assertTrue(BiPredicates.noneOf(Collections.emptyList()).test(null, null));

        Iterator<? extends BiPredicate<Object, Object>> it = predicates.iterator();
        BiPredicate<Object, Object> a = it.next().negate();
        while (it.hasNext()) {
            a = a.and(it.next().negate());
        }

        final Object o1 = new Object();
        final Object o2 = new Object();
        Assert.assertEquals(BiPredicates.noneOf(predicates).test(o1, o2), a.test(o1, o2));
    }

    /**
     * Tests {@link BiPredicates#anyOf(Iterable)}.
     *
     * @param predicates
     *            the predicates
     */
    @Test(dataProvider = "predicates")
    public void testAnyOf(Iterable<? extends BiPredicate<Object, Object>> predicates) {
        Assert.assertFalse(BiPredicates.anyOf(Collections.emptyList()).test(null, null));

        Iterator<? extends BiPredicate<Object, Object>> it = predicates.iterator();
        BiPredicate<Object, Object> a = it.next();
        while (it.hasNext()) {
            a = a.or(it.next());
        }

        final Object o1 = new Object();
        final Object o2 = new Object();
        Assert.assertEquals(BiPredicates.anyOf(predicates).test(o1, o2), a.test(o1, o2));
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "predicates")
    public static Object[][] predicate() {
        final BiPredicate<Object, Object> f = BiPredicates.alwaysFalse();
        Assert.assertFalse(f.test(null, null));

        final BiPredicate<Object, Object> t = BiPredicates.alwaysTrue();
        Assert.assertTrue(t.test(null, null));

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
