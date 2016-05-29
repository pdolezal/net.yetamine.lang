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

package net.yetamine.lang.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.functional.BiConsumers;

/**
 * Tests {@link FluentMap}.
 *
 * <p>
 * The implementation is written, so it could be used for both lists and sets if
 * the underlying set respects the insertion order. To run the original tests as
 * a part of the new test set, override {@link #createFluent()}.
 */
public class TestFluentMap {

    /**
     * Creates a new blank (mutable) map.
     *
     * <p>
     * The default implementation uses {@link HashMap} as the underlying
     * implementation.
     *
     * @param <K>
     *            the type of keys
     * @param <V>
     *            the type of values
     *
     * @return the map adapter
     */
    protected <K, V> FluentMap<K, V> createFluent() {
        return FluentMap.adapt(new HashMap<>());
    }

    /**
     * Tests the core map methods.
     */
    @Test
    public void testMap() {
        final Map<Object, Object> c = new HashMap<>();

        final FluentMap<Object, Object> f = FluentMap.adapt(c);
        Assert.assertEquals(f.container(), c);
        Assert.assertEquals(f, c);
        Assert.assertEquals(Collections.singleton(f), f.self().stream().collect(Collectors.toSet()));

        Assert.assertSame(c, f.that().map(Function.identity()));
        Assert.assertSame(f, f.that().map(o -> f));
    }

    /**
     * Tests {@link FluentMap#find(Object)}.
     */
    @Test
    public void testFind() {
        final FluentMap<Object, Object> f = createFluent();

        final Object k = new Object();
        Assert.assertFalse(f.find(k).isPresent());

        final Object v = new Object();
        f.put(k, v);
        Assert.assertSame(f.find(k).get(), v);
    }

    /**
     * Tests {@link FluentMap#supply(Object)}.
     */
    @Test
    public void testSupply() {
        final FluentMap<String, Object> f = createFluent();

        { // Using a supplier
            final String key = "Hello";
            final Object o = new Object();
            final FluentMap<String, Object> sf = f.factory(() -> o);
            Assert.assertNull(sf.get(key));
            Assert.assertSame(sf.supply(key), o);
            Assert.assertSame(sf.get(key), o);

            Assert.expectThrows(UnsupportedOperationException.class, () -> {
                sf.factory((Supplier<?>) null).supply("Dolly");
            });

            Assert.assertNull(sf.get("Dolly"));
            sf.remove(key);
        }

        { // Using a function
            final String key = "World";
            final FluentMap<String, Object> ff = f.factory(String::length);

            final Integer l = key.length();
            Assert.assertNull(ff.get(key));
            Assert.assertEquals(ff.supply(key), l);
            Assert.assertEquals(ff.get(key), l);

            Assert.expectThrows(UnsupportedOperationException.class, () -> {
                ff.factory((Function<Object, ?>) null).supply("Dolly");
            });

            Assert.assertNull(ff.get("Dolly"));
        }
    }

    /**
     * Tests {@link FluentMap#make(Object, Function)}.
     */
    @Test
    public void testMake_Function() {
        final FluentMap<String, Integer> f = createFluent();

        { // Using a supplier
            final String key = "Hello";
            final Integer o = 10;
            final FluentMap<String, Integer> sf = f.factory(() -> o);

            Assert.assertNull(sf.get(key));
            Assert.assertEquals(sf.make(key, Function.identity()).get(key), o);
            Assert.assertEquals(sf.make(key, v -> {
                Assert.fail();
                return null;
            }).get(key), o);

            Assert.assertNull(sf.discard(key).get(key));
            Assert.assertEquals(sf.make(key, i -> i + 1).get(key), Integer.valueOf(o + 1));

            Assert.expectThrows(UnsupportedOperationException.class, () -> {
                sf.factory((Supplier<Integer>) null).make("Dolly", Function.identity());
            });

            Assert.assertNull(sf.get("Dolly"));
            sf.remove(key);
        }

        { // Using a function
            final String key = "World";
            final FluentMap<String, Integer> ff = f.factory(String::length);

            final Integer l = key.length();
            Assert.assertNull(ff.get(key));
            Assert.assertEquals(ff.make(key, Function.identity()).get(key), l);
            Assert.assertEquals(ff.make(key, v -> {
                Assert.fail();
                return null;
            }).get(key), l);

            Assert.assertNull(ff.discard(key).get(key));
            Assert.assertEquals(ff.make(key, i -> i + 1).get(key), Integer.valueOf(l + 1));

            Assert.expectThrows(UnsupportedOperationException.class, () -> {
                ff.factory((Function<Object, Integer>) null).make("Dolly", Function.identity());
            });

            Assert.assertNull(ff.get("Dolly"));
        }
    }

    /**
     * Tests {@link FluentMap#make(Object, BiConsumer)}.
     */
    @Test
    public void testMake_BiConsumer() {
        final FluentMap<String, AtomicInteger> f = createFluent();

        { // Using a supplier
            final String key = "Hello";
            final FluentMap<String, AtomicInteger> sf = f.factory(() -> new AtomicInteger());

            Assert.assertNull(sf.get(key));
            Assert.assertEquals(sf.make(key, BiConsumers.ignoring()).get(key).get(), 0);
            Assert.assertEquals(sf.make(key, (k, v) -> Assert.fail()).get(key).get(), 0);

            Assert.assertNull(sf.discard(key).get(key));
            Assert.assertEquals(sf.make(key, (k, v) -> v.set(k.length())).get(key).get(), key.length());

            Assert.expectThrows(UnsupportedOperationException.class, () -> {
                sf.factory((Supplier<AtomicInteger>) null).make("Dolly", BiConsumers.ignoring());
            });

            Assert.assertNull(sf.get("Dolly"));
            sf.remove(key);
        }

        { // Using a function
            final String key = "World";
            final FluentMap<String, AtomicInteger> ff = f.factory(k -> new AtomicInteger(k.length()));

            final int l = key.length();
            Assert.assertNull(ff.get(key));
            Assert.assertEquals(ff.make(key, BiConsumers.ignoring()).get(key).get(), l);
            Assert.assertEquals(ff.make(key, (k, v) -> Assert.fail()).get(key).get(), l);

            Assert.assertNull(ff.discard(key).get(key));
            Assert.assertEquals(ff.make(key, (k, v) -> v.incrementAndGet()).get(key).get(), key.length() + 1);

            Assert.expectThrows(UnsupportedOperationException.class, () -> {
                ff.factory((Function<Object, AtomicInteger>) null).make("Dolly", Function.identity());
            });

            Assert.assertNull(ff.get("Dolly"));
        }
    }

    /**
     * Tests {@link FluentMap#have(Object)}.
     */
    @Test
    public void testHave() {
        final FluentMap<String, Object> f = createFluent();

        { // Using a supplier
            final String key = "Hello";
            final Object o = new Object();
            final FluentMap<String, Object> sf = f.factory(() -> o);
            Assert.assertNull(sf.get(key));
            Assert.assertSame(sf.have(key).get(), o);
            Assert.assertSame(sf.get(key), o);
            Assert.assertFalse(sf.factory((Supplier<?>) null).have("Dolly").isPresent());
            Assert.assertNull(sf.get("Dolly"));
        }

        { // Using a function
            final String key = "World";
            final FluentMap<String, Object> ff = f.factory(String::length);

            final Integer l = key.length();
            Assert.assertNull(ff.get(key));
            Assert.assertEquals(ff.have(key).get(), l);
            Assert.assertEquals(ff.get(key), l);
            Assert.assertFalse(ff.factory((Function<Object, ?>) null).have("Dolly").isPresent());
            Assert.assertNull(ff.get("Dolly"));
        }
    }

    /**
     * Tests {@link FluentMap#supplyIfAbsent(Object, Supplier)}.
     */
    @Test
    public void testSupplyIfAbsent() {
        final FluentMap<String, Object> f = createFluent();

        final Object o = new Object();
        Assert.assertSame(f.supplyIfAbsent("Hello", () -> o), o);
        Assert.assertSame(f.supplyIfAbsent("Hello", () -> {
            Assert.fail();
            return null;
        }), o);

        Assert.assertSame(f.get("Hello"), o);
    }

    /**
     * Tests {@link FluentMap#supplyIfPresent(Object, Supplier)}.
     */
    @Test
    public void testSupplyIfPresent() {
        final FluentMap<String, Object> f = createFluent();

        final Object o = new Object();
        Assert.assertNull(f.supplyIfPresent("Hello", () -> {
            Assert.fail();
            return null;
        }));

        f.put("Hello", new Object());
        Assert.assertSame(f.supplyIfPresent("Hello", () -> o), o);
        Assert.assertSame(f.get("Hello"), o);
    }
}
