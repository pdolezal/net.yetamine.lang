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

import java.util.HashMap;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Mapping}.
 */
public class TestMapping {

    /**
     * Creates a new blank (mutable) table.
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
    protected <K, V> Mapping<K, V> instance() {
        return Mapping.adapting(new HashMap<>());
    }

    /**
     * Tests {@link Mapping#find(Object)}.
     */
    @Test
    public void testFind() {
        final Mapping<Object, Object> f = instance();

        final Object k = new Object();
        Assert.assertFalse(f.find(k).isPresent());

        final Object v = new Object();
        f.put(k, v);
        Assert.assertSame(f.find(k).get(), v);
    }

    /**
     * Tests {@link Mapping#supplyIfAbsent(Object, Supplier)}.
     */
    @Test
    public void testSupplyIfAbsent() {
        final Mapping<String, Object> f = instance();

        final Object o = new Object();
        Assert.assertSame(f.supplyIfAbsent("Hello", () -> o), o);
        Assert.assertSame(f.supplyIfAbsent("Hello", () -> {
            Assert.fail();
            return null;
        }), o);

        Assert.assertSame(f.get("Hello"), o);
    }

    /**
     * Tests {@link Mapping#supplyIfPresent(Object, Supplier)}.
     */
    @Test
    public void testSupplyIfPresent() {
        final Mapping<String, Object> f = instance();

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
