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

package net.yetamine.lang.containers.values;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Box}.
 */
public final class TestBox {

    /**
     * Tests construction methods.
     */
    @Test
    public void testConstruction() {
        Assert.assertNull(Box.empty().get());
        Assert.assertNull(Box.of(null).get());

        final Object o = new Object();
        Assert.assertEquals(Box.of(o).get(), o);
    }

    /**
     * Tests accepting methods.
     */
    @Test
    public void testAccepting() {
        final Object o = new Object();
        Assert.assertEquals(Box.empty().set(o).get(), o);

        final Box<Object> box = Box.empty();
        box.accept(o);
        Assert.assertEquals(box.get(), o);
    }

    /**
     * Tests {@link Box#clear()}.
     */
    @Test
    public void testClear() {
        final Box<?> b1 = Box.empty();
        Assert.assertNull(b1.clear());
        Assert.assertNull(b1.clear());

        final Object o = new Object();
        final Box<?> b2 = Box.of(o);
        Assert.assertSame(b2.clear(), o);
        Assert.assertNull(b2.clear());
    }

    /**
     * Tests {@link Box#equals(Object)} and {@link Box#hashCode()}.
     */
    @Test
    public void testEquals() {
        Assert.assertEquals(Box.empty(), Box.of(null));
        Assert.assertEquals(Box.empty().hashCode(), Box.of(null).hashCode());

        final Object o1 = new Object();
        final Object o2 = new Object();
        Assert.assertEquals(Box.of(o1), Box.of(o1));
        Assert.assertEquals(Box.of(o1).hashCode(), Box.of(o1).hashCode());

        Assert.assertNotEquals(Box.of(o1), Box.of(o2));
        Assert.assertNotEquals(Box.of(o1), Box.empty());

        final String s1 = "Hello";
        final Object s2 = new String(s1);
        Assert.assertEquals(Box.of(s1), Box.of(s2));
        Assert.assertEquals(Box.of(s1).hashCode(), Box.of(s2).hashCode());
    }

    /**
     * Tests {@link Box#stream()}.
     */
    @Test
    public void testStream() {
        final Box<Object> box = Box.empty();
        Assert.assertEquals(box.stream().collect(Collectors.toList()), Arrays.asList((Object) null));

        final Object o = new Object();
        box.set(o);
        Assert.assertEquals(box.stream().collect(Collectors.toList()), Arrays.asList(o));
    }

    /**
     * Tests {@link Box#optional()}.
     */
    @Test
    public void testOptional() {
        Assert.assertFalse(Box.empty().optional().isPresent());
        Assert.assertFalse(Box.of(null).optional().isPresent());

        final Object o = new Object();
        Assert.assertTrue(Box.of(o).optional().isPresent());
        Assert.assertEquals(Box.of(o).optional().get(), o);
    }

    /**
     * Tests {@link Box#map(java.util.function.Function)}.
     */
    @Test
    public void testMap() {
        final Function<Integer, Long> f = i -> i + 1L;
        Assert.assertEquals(Box.of(1).map(f), Long.valueOf(2L));
    }

    /**
     * Tests patching methods.
     */
    @Test
    public void testPatching() {
        final UnaryOperator<Integer> f1 = i -> i + 1;
        Assert.assertEquals(Box.of(1).patch(f1), Box.of(2));
        Assert.assertEquals(Box.of(1).compute(f1), Integer.valueOf(2));

        final BiFunction<Integer, Integer, Integer> f2 = (i, j) -> i + j;
        Assert.assertEquals(Box.of(1).patch(f2, 2), Box.of(3));
        Assert.assertEquals(Box.of(1).compute(f2, 2), Integer.valueOf(3));
    }

    /**
     * Tests {@link Box#use(Consumer)}.
     */
    @Test
    public void testUse() {
        Box.empty().use(value -> Assert.assertNull(value));

        final Object o = new Object();
        Box.of(o).use(value -> Assert.assertEquals(value, o));
    }
}
