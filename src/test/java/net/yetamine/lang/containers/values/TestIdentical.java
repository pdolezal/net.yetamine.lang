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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Identical}.
 */
public final class TestIdentical {

    /**
     * Tests construction methods.
     */
    @Test
    public void testConstruction() {
        Assert.assertNull(Identical.nil().get());
        Assert.assertNull(Identical.of(null).get());

        final Object o = new Object();
        Assert.assertSame(Identical.of(o).get(), o);
    }

    /**
     * Tests {@link Identical#equals(Object)} and {@link Identical#hashCode()}.
     */
    @Test
    public void testEquals() {
        Assert.assertEquals(Identical.nil(), Identical.of(null));
        Assert.assertEquals(Identical.nil().hashCode(), Identical.of(null).hashCode());

        final Object o1 = new Object();
        final Object o2 = new Object();
        Assert.assertEquals(Identical.of(o1), Identical.of(o1));
        Assert.assertEquals(Identical.of(o1).hashCode(), Identical.of(o1).hashCode());

        Assert.assertNotEquals(Identical.of(o1), Identical.of(o2));
        Assert.assertNotEquals(Identical.of(o1), Identical.nil());

        final String s1 = "Hello";
        final Object s2 = new String(s1);
        Assert.assertNotEquals(Identical.of(s1), Identical.of(s2));
    }
}
