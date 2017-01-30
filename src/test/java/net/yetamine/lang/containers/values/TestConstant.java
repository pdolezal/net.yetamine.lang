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
 * Tests {@link Constant}.
 */
public final class TestConstant {

    /**
     * Tests construction methods.
     */
    @Test
    public void testConstruction() {
        Assert.assertNull(Constant.nil().get());
        Assert.assertNull(Constant.of(null).get());

        final Object o = new Object();
        Assert.assertEquals(Constant.of(o).get(), o);
    }

    /**
     * Tests {@link Constant#equals(Object)} and {@link Constant#hashCode()}.
     */
    @Test
    public void testEquals() {
        Assert.assertEquals(Constant.nil(), Constant.of(null));
        Assert.assertEquals(Constant.nil().hashCode(), Constant.of(null).hashCode());

        final Object o1 = new Object();
        final Object o2 = new Object();
        Assert.assertEquals(Constant.of(o1), Constant.of(o1));
        Assert.assertEquals(Constant.of(o1).hashCode(), Constant.of(o1).hashCode());

        Assert.assertNotEquals(Constant.of(o1), Constant.of(o2));
        Assert.assertNotEquals(Constant.of(o1), Constant.nil());

        final String s1 = "Hello";
        final Object s2 = new String(s1);
        Assert.assertEquals(Constant.of(s1), Constant.of(s2));
        Assert.assertEquals(Constant.of(s1).hashCode(), Constant.of(s2).hashCode());
    }
}
