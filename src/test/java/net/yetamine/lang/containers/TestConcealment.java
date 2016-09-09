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

import java.util.function.Consumer;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Concealment}.
 */
public final class TestConcealment {

    /**
     * Tests the empty instance.
     */
    @Test
    public void testEmpty() {
        Assert.assertNotNull(Concealment.empty());
        Assert.assertEquals(Concealment.empty(), Concealment.empty());
        Assert.assertEquals(Concealment.empty(), Concealment.of(null));
        Assert.assertNotEquals(Concealment.of(new Object()), Concealment.empty());
    }

    /**
     * Tests {@link Concealment#get()}.
     */
    @Test
    public void testGet() {
        final Object o = new Object();
        Assert.assertSame(Concealment.of(o).get(), o);
    }

    /**
     * Tests {@link Concealment#use(Consumer)}.
     */
    @Test
    public void testUse() {
        final Object o = new Object();

        Concealment.of(o).use(p -> Assert.assertSame(p, o));
        Assert.expectThrows(IllegalArgumentException.class, () -> {
            Concealment.of(o).use(p -> {
                if (p == o) {
                    throw new IllegalArgumentException();
                }
            });
        });
    }

    /**
     * Tests {@link Concealment#equals(Object)}.
     */
    @Test
    public void testEquals() {
        final Object a = new Object();
        final Object b = new Object();

        final Concealment<?> ca = Concealment.of(a);
        final Concealment<?> cb = Concealment.of(b);
        final Concealment<?> ct = Concealment.of(a);

        Assert.assertEquals(ca, ct);
        Assert.assertEquals(ca.hashCode(), ct.hashCode());
        Assert.assertNotEquals(ca, cb);
    }


    /**
     * Tests {@link Concealment#toString()}.
     */
    @Test
    public void testToString() {
        final String v = "Hello";

        Assert.assertNotEquals(Concealment.of(v).toString(), v);
        Assert.assertEquals(Concealment.of(v, o -> "World").toString(), "World");
        Assert.assertEquals(Concealment.of(v, o -> v).toString(), v);
    }
}
