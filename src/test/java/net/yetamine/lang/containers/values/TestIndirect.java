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

import net.yetamine.lang.containers.values.Deferred;
import net.yetamine.lang.containers.values.Indirect;

/**
 * Tests {@link Deferred}.
 */
public final class TestIndirect {

    /**
     * Tests caching and invalidation of the value.
     */
    @Test
    public void test() {
        final Box<Object> box = Box.empty();
        final Indirect<?> v = new Indirect<>(Object::new, box::set);

        final Object o = v.get();
        Assert.assertSame(v.get(), o);
        Assert.assertSame(box.get(), o);

        final Object p = new Object();
        box.set(p); // Override internally
        Assert.assertSame(v.get(), p);

        box.clear();
        final Object q = v.get();
        Assert.assertNotSame(q, o);
        Assert.assertNotSame(q, p);
        Assert.assertSame(v.get(), q);
        Assert.assertSame(box.get(), q);

        v.invalidate();
        final Object r = v.get();
        Assert.assertNotSame(r, o);
        Assert.assertNotSame(r, p);
        Assert.assertNotSame(r, q);
        Assert.assertSame(v.get(), r);
    }
}
